package Server;

import Main.LoggedIn;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

class MatchmakingView extends HTTPSHandle {
    public static void handle(HttpExchange he) {
        int code = 500;
        String body = "";
        try {
            Post p = new Post(read(he));
            code = p.code;
            body = p.body;
        } catch (ParserConfigurationException | SAXException | IOException | IllegalArgumentException ex) {
            System.out.println("Error when parsing a received message.");
            code = 400;
        } finally {
            if (code==200)
                sendResponse(he, code, body);
            else
                sendCodeOnly(he, code);
        }
    }
    
    private static class Post extends HTTPMessage {
        private Post(String str) throws ParserConfigurationException, SAXException, IOException, IllegalArgumentException {
            super(str);
            
            // Get the arguments from the sent XML
            extractUsername();
            extractPassword();

            // Checks if the argument lengths are valid
            checkLength(username);
            checkLength(password);

            ArrayList<LoggedIn> availablePlayers = Server.viewMatchesAvailable(username, password);
            if (availablePlayers == null) {
                code = 401; // Unauthorized: Authentication failed or not logged in
            } else {
                code = 200; // OK: Sending list
                body = buildResponse(availablePlayers);
            }
        }

        private String buildResponse(ArrayList<LoggedIn> availablePlayers) {
            String result = "";
            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

                // List
                Document doc = docBuilder.newDocument();
                Element list = doc.createElement("list");
                doc.appendChild(list);

                int numAvailablePlayers = availablePlayers.size();
                for (int i=0; i<numAvailablePlayers; i++) {
                    // Account
                    Element userElem = doc.createElement("user");
                    list.appendChild(userElem);

                    // Name
                    Element nameElem = doc.createElement("name");
                    userElem.appendChild(nameElem);
                    nameElem.appendChild(doc.createTextNode(availablePlayers.get(i).getAccount().getName()));

                    // Username
                    Element usernameElem = doc.createElement("username");
                    userElem.appendChild(usernameElem);
                    usernameElem.appendChild(doc.createTextNode(availablePlayers.get(i).getAccount().getUsername()));

                    // IP Address
                    Element ipElem = doc.createElement("ip");
                    userElem.appendChild(ipElem);
                    ipElem.appendChild(doc.createTextNode(availablePlayers.get(i).getIPAddress()));
                    
                    // Was proposed
                    Element proposedElem = doc.createElement("proposed");
                    userElem.appendChild(proposedElem);
                    
                    ArrayList<LoggedIn> proposals = availablePlayers.get(i).getProposals();
                    int numProposals = proposals.size();
                    boolean didntPropose = true;
                    for (int j=0; j<numProposals; j++) {
                        if (proposals.get(j).getAccount().checkUsername(username)) {
                            proposedElem.appendChild(doc.createTextNode("true"));
                            didntPropose = false;
                            break;
                        }
                    }
                    if (didntPropose) {
                        proposedElem.appendChild(doc.createTextNode("false"));
                    }
                }                

                // Write the content into xml file
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);

                // Output to console for testing
                StringWriter writer = new StringWriter();
                StreamResult streamResult = new StreamResult(writer);
                transformer.transform(source,streamResult);
                result = writer.toString();
            }
            catch (ParserConfigurationException | TransformerException ex) {
                System.out.println("Error building the response XML (Matchmaking View).");
            }
            return result;
        }
    }
}
