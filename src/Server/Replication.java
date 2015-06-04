package Server;

import Main.Account;
import Main.Constants;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Scanner;
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

public class Replication {
    private static final String accountsFilename = Constants.accounts_file;
    
    public static ArrayList<Account> load() {
	StringBuilder result = new StringBuilder("");
	File file = new File(accountsFilename);
        if(!file.exists()) {
            System.out.println("There is no accounts file to import (Replication).");
            return new ArrayList<Account>();
        }
        
	try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line);
            }
            scanner.close();
	} catch (IOException e) {
            System.out.println("Error when reading the accounts file (Replication).");
            return new ArrayList<Account>();
	}
        
        ArrayList<Account> accounts = new ArrayList<Account>();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            db.setErrorHandler(new Handler());
            InputSource is = new InputSource(new StringReader(result.toString()));
            Document doc = db.parse(is);
            doc.getDocumentElement().normalize();
            
            NodeList accountsXML = doc.getElementsByTagName("account");
            int numAccounts = accountsXML.getLength();
            for (int i=0; i<numAccounts; i++) {
                NodeList children = accountsXML.item(i).getChildNodes();
                if (children.getLength()!=3) {
                    System.out.println("Wrong number of children in a account element (Replication).");
                    throw new IOException();
                }
                accounts.add(new Account(children.item(0).getTextContent(), children.item(1).getTextContent(), children.item(2).getTextContent()));
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            System.out.println("Error when parsing the read accounts file (Replication).");
            return new ArrayList<Account>();
        }
        System.out.println("Accounts file imported (Replication).");
        return accounts;
    }
    
    
    public static void save(ArrayList<Account> accounts) {
        try (PrintWriter writer = new PrintWriter(accountsFilename, "UTF-8")) {
            writer.print(getXML(accounts));
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            System.out.println("Error when saving a file (Replication).");
        }
    }
    
    private static String getXML(ArrayList<Account> accounts) {
            String result = "";
            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

                // List
                Document doc = docBuilder.newDocument();
                Element list = doc.createElement("accounts");
                doc.appendChild(list);

                int numAccounts = accounts.size();
                for (int i=0; i<numAccounts; i++) {
                    // Account
                    Element accountElem = doc.createElement("account");
                    list.appendChild(accountElem);

                    // Name
                    Element usernameElem = doc.createElement("username");
                    accountElem.appendChild(usernameElem);
                    usernameElem.appendChild(doc.createTextNode(accounts.get(i).getUsername()));

                    // Username
                    Element nameElem = doc.createElement("name");
                    accountElem.appendChild(nameElem);
                    nameElem.appendChild(doc.createTextNode(accounts.get(i).getName()));

                    // Password
                    Element passwordElem = doc.createElement("password");
                    accountElem.appendChild(passwordElem);
                    passwordElem.appendChild(doc.createTextNode(accounts.get(i).getPassword()));
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
                System.out.println("Error building the saving XML (Replication).");
            }
            return result;
    }

    private static class Handler implements ErrorHandler {
        @Override public void warning(SAXParseException exception) throws SAXException {}
        @Override public void error(SAXParseException exception) throws SAXException {}
        @Override public void fatalError(SAXParseException exception) throws SAXException {}
    }
}
