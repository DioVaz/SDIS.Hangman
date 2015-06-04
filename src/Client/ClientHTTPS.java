package Client;

import Main.Constants;
import Main.Match;
import Main.User;
import java.io.IOException;
import java.util.ArrayList;
import javax.net.ssl.HttpsURLConnection;

public class ClientHTTPS {
    private static final String server_url = Constants.server_url;
    
    public static int register(String username, String password, String name) {
        String xml = HTTPSConnection.buildXML(username, password, "name", name);
        HttpsURLConnection con = HTTPSConnection.send(server_url + "register", xml);
        int code;
        try {
            code = con.getResponseCode();
        } catch (IOException ex) {
            System.out.println("Error when connecting to the server (HTTPS Client).");
            return 0;
        }
        return code;
    }
    
    public static int login(String username, String password, int port) {
        String xml = HTTPSConnection.buildXML(username, password, "port", Integer.toString(port));
        HttpsURLConnection con = HTTPSConnection.send(server_url + "login", xml);
        int code;
        try {
            code = con.getResponseCode();
        } catch (IOException ex) {
            System.out.println("Error when connecting to the server (HTTPS Client).");
            return 0;
        }
        return code;
    }
    
    private static int authenticationOnly(String username, String password, String page) {
        String xml = HTTPSConnection.buildXML(username, password, null, null);
        HttpsURLConnection con = HTTPSConnection.send(server_url + page, xml);
        int code;
        try {
            code = con.getResponseCode();
        } catch (IOException ex) {
            System.out.println("Error when connecting to the server (HTTPS Client).");
            return 0;
        }
        return code;
    }
    
    public static int logout(String username, String password) {
        return authenticationOnly(username, password, "logout");
    }
    
    public static int matchmakingAdd(String username, String password) {
        return authenticationOnly(username, password, "matchmaking/add");
    }
    
    public static int matchmakingRemove(String username, String password) {
        return authenticationOnly(username, password, "matchmaking/remove");
    }
    
    private static ArrayList<User> authenticationWithReturn(String username, String password, String page, boolean proposedField) {
        String xml = HTTPSConnection.buildXML(username, password, null, null);
        HttpsURLConnection con = HTTPSConnection.send(server_url + page, xml);
        int code;
        ArrayList<User> list = new ArrayList<User>();
        try {
            code = con.getResponseCode();
        } catch (IOException ex) {
            System.out.println("Error when connecting to the server (HTTPS Client).");
            return list;
        }
        if (code!=200)
            return list;
        return HTTPSConnection.getUsers(con, proposedField);
    }
    
    public static ArrayList<User> matchmakingView(String username, String password) {
        return authenticationWithReturn(username, password, "matchmaking/view", true);
    }
    
    public static ArrayList<User> matchmakingProposals(String username, String password) {
        return authenticationWithReturn(username, password, "matchmaking/proposals", false);
    }
    
    private static int authenticationPropose(String username, String password, String challenge, String page) {
        String xml = HTTPSConnection.buildXML(username, password, "challenge", challenge);
        HttpsURLConnection con = HTTPSConnection.send(server_url + page, xml);
        int code;
        try {
            code = con.getResponseCode();
        } catch (IOException ex) {
            System.out.println("Error when connecting to the server (HTTPS Client).");
            return 0;
        }
        return code;
    }
    
    public static int matchmakingPropose(String username, String password, String challenge) {
        return authenticationPropose(username, password, challenge, "matchmaking/propose");
    }
    
    public static int matchmakingUnpropose(String username, String password, String challenge) {
        return authenticationPropose(username, password, challenge, "matchmaking/unpropose");
    }

    static Match matchmakingAccept(String username, String password, String challenge) {
        String xml = HTTPSConnection.buildXML(username, password, "challenge", challenge);
        HttpsURLConnection con = HTTPSConnection.send(server_url + "matchmaking/accept", xml);
        int code;
        try {
            code = con.getResponseCode();
        } catch (IOException ex) {
            System.out.println("Error when connecting to the server (HTTPS Client).");
            return null;
        }
        if (code!=200)
            return null;
        return HTTPSConnection.getMatch(con);
    }
    
    public static boolean matchmakingHost(String username, String password) {
        String xml = HTTPSConnection.buildXML(username, password, null, null);
        HttpsURLConnection con = HTTPSConnection.send(server_url + "matchmaking/host", xml);
        int code;
        try {
            code = con.getResponseCode();
        } catch (IOException ex) {
            System.out.println("Error when connecting to the server (HTTPS Client).");
            return false;
        }
        if (code!=200) {
            System.out.println("Error code received: " + code + " (HTTPS Client).");
            return false;
        }
        
        String response = HTTPSConnection.getContent(con);
        if (null != response)
            switch (response) {
            case "yes":
                return true;
            case "no":
                return false;
        }
        System.out.println("Unexpected format in response (HTTPS Client).");
        return false;
    }
    
    
    
    
    
}