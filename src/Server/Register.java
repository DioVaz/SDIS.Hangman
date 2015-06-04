package Server;

import Main.Account;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

class Register extends HTTPSHandle {
    public static void handle(HttpExchange he) throws IOException {
        int code = 500;
        try {
            Post p = new Post(read(he));
            code = p.code;
        } catch (ParserConfigurationException | SAXException | IOException | IllegalArgumentException ex) {
            System.out.println("Error when parsing a received message.");
            code = 400;
        } finally {
            sendCodeOnly(he, code);
        }
    }
    
    private static class Post extends HTTPMessage {
        private Post(String str) throws ParserConfigurationException, SAXException, IOException, IllegalArgumentException {
            super(str);
            
            // Get the arguments from the sent XML
            extractUsername();
            extractName();
            extractPassword();

            // Checks if the argument lengths are valid
            checkLength(username);
            checkLength(name);
            checkLength(password);

            Account ac = new Account(username,name,password);
            if (Server.addAccount(ac))
                code = 201; // Created: Added to the server account list
            else
                code = 409; // Conflict: Username already exists
        }
    }
}
