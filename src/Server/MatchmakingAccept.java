package Server;

import Main.LoggedIn;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.StringWriter;
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

class MatchmakingAccept extends HTTPSHandle {
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
            extractChallenge();

            // Checks if the argument lengths are valid
            checkLength(username);
            checkLength(password);
            checkLength(challengeUsername);

            code = Server.acceptChallenge(username, password, challengeUsername);            
            if (code == 200) { // OK: Sending information
                LoggedIn challengerLogin = Server.getLogin(challengeUsername);
                body = buildResponse(challengerLogin);
            }
        }

        private String buildResponse(LoggedIn challengerLogin) {
            String result = "";
            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

                // List
                Document doc = docBuilder.newDocument();
                Element match = doc.createElement("match");
                doc.appendChild(match);

                // Name
                Element nameElem = doc.createElement("name");
                match.appendChild(nameElem);
                nameElem.appendChild(doc.createTextNode(challengerLogin.getAccount().getName()));

                // IP Address
                Element ipElem = doc.createElement("ip");
                match.appendChild(ipElem);
                ipElem.appendChild(doc.createTextNode(challengerLogin.getIPAddress()));

                // Port number
                Element portElem = doc.createElement("port");
                match.appendChild(portElem);
                portElem.appendChild(doc.createTextNode(Integer.toString(challengerLogin.getPortNumber())));

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
                System.out.println("Error building the response XML (Matchmaking Accept).");
            }
            return result;
        }
    }
}
