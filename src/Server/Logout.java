package Server;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

class Logout extends HTTPSHandle {
    public static void handle(HttpExchange he) {
        int code = 500;
        try {
            Post p = new Post(he, read(he));
            code = p.code;
        } catch (ParserConfigurationException | SAXException | IOException | IllegalArgumentException ex) {
            System.out.println("Error when parsing a received message.");
            code = 400;
        } finally {
            sendCodeOnly(he, code);
        }
    }
    
    private static class Post extends HTTPMessage {
        private Post(HttpExchange he, String str) throws ParserConfigurationException, SAXException, IOException, IllegalArgumentException {
            super(str);
            
            // Get the arguments from the sent XML
            extractUsername();
            extractPassword();

            // Checks if the argument lengths are valid
            checkLength(username);
            checkLength(password);

            if (Server.logout(username, password))
                code = 200; // OK: Removed login
            else
                code = 401; // Unauthorized: Logout failed
        }
    }
}
