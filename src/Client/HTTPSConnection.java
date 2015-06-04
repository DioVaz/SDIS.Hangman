package Client;

import Main.Constants;
import Main.Match;
import Main.User;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
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
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class HTTPSConnection {    
    // Builds the XML to be sent to the server
    public static String buildXML(String username, String password, String otherTitle, String otherText) {
            String result = "";
            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

                // Account
                Document doc = docBuilder.newDocument();
                Element account = doc.createElement("account");
                doc.appendChild(account);

                // Username
                Element usernameElem = doc.createElement("username");
                account.appendChild(usernameElem);
                usernameElem.appendChild(doc.createTextNode(username));

                // Password
                Element passwordElem = doc.createElement("password");
                account.appendChild(passwordElem);
                passwordElem.appendChild(doc.createTextNode(password));
                
                if (otherTitle != null && otherText != null && (!"".equals(otherTitle))) {
                    // Other Title
                    Element otherTitleElem = doc.createElement(otherTitle);
                    account.appendChild(otherTitleElem);
                    otherTitleElem.appendChild(doc.createTextNode(otherText));
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
                System.out.println("Error building the XML to be sent (HTTPS Client).");
            }
            return result;
    }
    
    // Reads the XML list of users sent by the server
    public static ArrayList<User> getUsers(HttpsURLConnection con, boolean proposedField) {
        ArrayList<User> list = new ArrayList<User>();
        try {
            String content = getContent(con);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            db.setErrorHandler(new Handler());
            InputSource is = new InputSource(new StringReader(content));
            Document doc = db.parse(is);
            doc.getDocumentElement().normalize();
            
            NodeList users = doc.getElementsByTagName("user");
            int listSize = users.getLength();
            if (listSize == 0)
                return list;
            
            int expectedNumChildren=3;
            if (proposedField)
                expectedNumChildren++;
            
            for (int i=0; i<listSize; i++) {
                NodeList children = users.item(i).getChildNodes();
                if (children.getLength()!=expectedNumChildren) {
                    System.out.println("Wrong number of children in a user element (HTTPS Client).");
                    throw new IOException();
                }
                if (proposedField)
                    list.add(new User(children.item(0).getTextContent(), children.item(1).getTextContent(), children.item(2).getTextContent(), children.item(3).getTextContent()));
                else
                    list.add(new User(children.item(0).getTextContent(), children.item(1).getTextContent(), children.item(2).getTextContent()));
            }
            
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            System.out.println("Error reading the XML sent by the server (HTTPS Client).");
            return list;
        }
        return list;
    }
    
    public static String getContent(HttpsURLConnection con) {
        String content = "";
        try {
            String input;
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            while ((input = br.readLine()) != null){
                content += input;
            }
            br.close();
        } catch (IOException ex) {
            System.out.println("Error getting the content sent by the server (HTTPS Client).");
            return "";
        }
        return content;
    }
    
    // Sends 'content' to the 'https_url' through a HTTPS connection and returns that connection
    public static HttpsURLConnection send(String https_url, String content) {
        try {            
            // First initialize the key and trust material.
            KeyStore ksKeys = KeyStore.getInstance("JKS");
            ksKeys.load(new FileInputStream(Constants.client_keystore_file), Constants.keystore_password);
            KeyStore ksTrust = KeyStore.getInstance("JKS");
            ksTrust.load(new FileInputStream(Constants.truststore_file), Constants.keystore_password);
            
            // KeyManager's decide which key material to use.
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ksKeys, Constants.keystore_password);
            
            // TrustManager's decide whether to allow connections.
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ksTrust);
            
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            
            URL url = new URL(https_url);
            HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
            con.setSSLSocketFactory(sslContext.getSocketFactory());
            
            // Send post request
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(content);
            wr.flush();
            wr.close();
            
            return con;
            
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | KeyManagementException ex) {
            System.out.println("Error when configuring the connection security or connecting (HTTPS Client).");
            System.exit(-1);
        }
        return null;
    }
    
    // Prints the secure connection information
    public static void printInfo(HttpsURLConnection con){
        if(con!=null){
            try {
                System.out.println("Response Code: " + con.getResponseCode());
                System.out.println("Cipher Suite: " + con.getCipherSuite());
                Certificate[] certs = con.getServerCertificates();
                int numCerts = certs.length;
                for(int i=0; i<numCerts; i++){
                    System.out.println("[Certificate " + (i+1) + "] ");
                    System.out.println("     Type: " + certs[i].getType());
                    System.out.println("     Hash Code: " + certs[i].hashCode());
                    System.out.println("     Public Key Algorithm: " + certs[i].getPublicKey().getAlgorithm());
                    System.out.println("     Public Key Format: "  + certs[i].getPublicKey().getFormat());
                }
            } catch (IOException ex) {
                System.out.println("Error when printing the HTTPS information. (HTTPS Client)");
            }
        }
    }
 
    // Prints the content returned by the server (or just the response code if it's not 200 [OK])
    public static void printContent(HttpsURLConnection con){
        if(con!=null){
            try {
                int responseCode = con.getResponseCode();
                System.out.println("Response Code: " + responseCode);
                if (responseCode==200) {
                    System.out.print("Content of the URL: ");
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String input;
                    while ((input = br.readLine()) != null){
                        System.out.println(input);
                    }
                    br.close();
                }
            } catch (IOException e) {
                System.out.println("Error when printing the content of a HTTPS connection (HTTPS Client).");
            }
        }
    }

    // Reads the XML match information sent by the server
    static Match getMatch(HttpsURLConnection con) {
        Match match;
        try {
            String content = getContent(con);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            db.setErrorHandler(new Handler());
            InputSource is = new InputSource(new StringReader(content));
            Document doc = db.parse(is);
            doc.getDocumentElement().normalize();
            
            // Get the opponent Name
            NodeList nameNodeList = doc.getElementsByTagName("name");
            if (nameNodeList.getLength()==0)
                return null;
            String name = nameNodeList.item(0).getTextContent();
            
            // Get the opponent IP Address
            NodeList ipNodeList = doc.getElementsByTagName("ip");
            if (ipNodeList.getLength()==0)
                return null;
            String ip_address = ipNodeList.item(0).getTextContent();
            
            // Get the opponent Port Number
            NodeList portNodeList = doc.getElementsByTagName("port");
            if (portNodeList.getLength()==0)
                return null;
            int port_number = Integer.parseInt(portNodeList.item(0).getTextContent());
            
            match = new Match(name, ip_address, port_number);
            
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            System.out.println("Error reading the XML sent by the server (HTTPS Client).");
            return null;
        }
        return match;
    }
    
    private static class Handler implements ErrorHandler {
        @Override public void warning(SAXParseException exception) throws SAXException {}
        @Override public void error(SAXParseException exception) throws SAXException {}
        @Override public void fatalError(SAXParseException exception) throws SAXException {}
    }
}
