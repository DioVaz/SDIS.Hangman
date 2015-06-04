package Server;

import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class HTTPMessage {
    protected String username, name, password, challengeUsername, body;
    protected int port_number;
    protected int code = 500;
    private Document doc;
        
    protected HTTPMessage(String str) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        db.setErrorHandler(new Handler());
        InputSource is = new InputSource(new StringReader(str));
        doc = db.parse(is);
        doc.getDocumentElement().normalize();
    }

    public void setBody(String body) {
        this.body = body;
    }

    protected void checkLength(String str) {
        int length = str.length();
        if (length<3 || length>32) {
            System.out.println("Argument(s) has wrong size.");
            throw new IllegalArgumentException();
        }
    }
    
    protected void extractUsername() {
        username = getElementByTagName("username");
    }
    
    protected void extractName() {
        name = getElementByTagName("name");
    }
    
    protected void extractPassword() {
        password = getElementByTagName("password");
    }
    
    protected void extractPortNumber() {
        port_number = Integer.parseInt(getElementByTagName("port"));
    }
    
    protected void extractChallenge() {
        challengeUsername = getElementByTagName("challenge");
    }
    
    private String getElementByTagName(String str) {
        if (doc.getElementsByTagName(str).getLength()==0)
            return "";
        return doc.getElementsByTagName(str).item(0).getTextContent();
    }

    private class Handler implements ErrorHandler {
        @Override public void warning(SAXParseException exception) throws SAXException {}
        @Override public void error(SAXParseException exception) throws SAXException {}
        @Override public void fatalError(SAXParseException exception) throws SAXException {}
    }
}
