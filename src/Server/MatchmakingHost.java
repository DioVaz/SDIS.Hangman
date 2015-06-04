package Server;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

class MatchmakingHost extends HTTPSHandle {
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
            
            int availabilityCode = Server.checkMatchAvailability(username, password);
            if (availabilityCode==401) {
                code = 401; // Unauthorized: Authentication failed or not logged in
            } else if (availabilityCode==0) {
                code = 200; // OK
                body = "no"; // The user is not open for proposals
            } else if (availabilityCode==1) {
                code = 200; // OK
                body = "yes"; // The user is open for proposals
            }
        }
    }
}
