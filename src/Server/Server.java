package Server;

import Main.LoggedIn;
import Main.Account;
import Main.Constants;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpsServer;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class Server {
    private static final String usage_string = "Usage: java HTTPServer <port_number>";
    private static int port_number;
    
    private static ArrayList<Account> accounts;
    private static ArrayList<LoggedIn> logins;
    
    public static void main(String[] args) throws Exception {
        accounts = Replication.load();
        logins = new ArrayList<LoggedIn>();
        checkArgs(args);
        start();
    }
    
    private static void start() {
        try {
            // Initialise the HTTPS server
            HttpsServer httpsServer = HttpsServer.create(new InetSocketAddress(port_number), 0);

            // Initialise the keystore
            SSLContext sslContext = SSLContext.getInstance("SSL");
            KeyStore ks = KeyStore.getInstance("JKS");
            FileInputStream fis = new FileInputStream(Constants.server_keystore_file);
            ks.load(fis, Constants.keystore_password);

            // Initialise the key manager factory
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, Constants.keystore_password);

            // Initialise the trust manager factory
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);

            // Initialise the HTTPS context and parameters
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            httpsServer.setHttpsConfigurator(new HTTPSConfig(sslContext));
            HttpContext httpsContext = httpsServer.createContext("/hangman", new HTTPSHandler());
            httpsContext.setAuthenticator(null);
            httpsServer.setExecutor(null); // Creates a default executor
            httpsServer.start();            
        
        } catch (IOException | NoSuchAlgorithmException | KeyStoreException | CertificateException | UnrecoverableKeyException | KeyManagementException ex) {
            System.out.println("Error when starting the server.");
            System.exit(-2);
        }
    }
    
    // Checks if the args have the required port_number
    private static void checkArgs(String[] args) {
        if (args.length != 1) {
            System.out.println(usage_string);
            System.exit(-1);
        }
        try {
            port_number = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            System.out.println("<port_number> needs to be a integer.\n" + usage_string);
            System.exit(-1);
        }
    }
    
    // Checks if the account username sent already exists. If not, adds the account.
    static boolean addAccount(Account ac) {
        int num_accounts = accounts.size();
        for (int i=0; i<num_accounts; i++) {
            if (accounts.get(i).equals(ac)) {
                return false;
            }
        }
        accounts.add(ac);
        Replication.save(accounts);
        return true;
    }

    private static Account getAccount(String username, String password) {
        int num_accounts = accounts.size();
        for (int i=0; i<num_accounts; i++) {
            if (accounts.get(i).checkUsername(username)) {
                if (accounts.get(i).checkPassword(password))
                    return accounts.get(i);
                else
                    return null;
            }
        }
        return null;
    }

    protected static LoggedIn getLogin(String username) {
        int num_logins = logins.size();
        for (int i=0; i<num_logins; i++) {
            if (logins.get(i).getAccount().checkUsername(username)) {
                return logins.get(i);
            }
        }
        return null;
    }

    static boolean login(String username, String password, String ip_address, int port_number) {
        Account account = getAccount(username, password);
        if (account == null)
            return false;
        int num_logins = logins.size();
        for (int i=0; i<num_logins; i++) {
            if (logins.get(i).getAccount().equals(account)) {
                if (ip_address.equals(logins.get(i).getIPAddress()) && port_number==logins.get(i).getPortNumber())
                    return true;
                Notification.doubleLogin(logins.get(i).getIPAddress(), logins.get(i).getPortNumber(), ip_address, port_number);
                logins.get(i).setIPAddress(ip_address);
                logins.get(i).setPortNumber(port_number);
                return true;
            }
        }
        LoggedIn l = new LoggedIn(account, ip_address, port_number);
        logins.add(l);
        return true;
    }

    static boolean logout(String username, String password) {
        Account account = getAccount(username, password);
        if (account == null)
            return false;
        int num_logins = logins.size();
        for (int i=0; i<num_logins; i++) {
            if (logins.get(i).getAccount().equals(account)) {
                logins.remove(i);
                removeMatchJoins(username);
                return true;
            }
        }
        removeMatchJoins(username);
        return true;
    }

    private static void removeMatchJoins(String username) {
        int num_logins = logins.size();
        for (int i=0; i<num_logins; i++) {
            logins.get(i).removeProposal(username);
        }
    }
    
    static void printAccountList() {
        int accountsSize = accounts.size();
        for (int i=0; i<accountsSize; i++) {
            System.out.print("[Account " + i + "]");
            accounts.get(i).println();
        }
    }
    
    static void printLoginList() {
        int loginsSize = logins.size();
        for (int i=0; i<loginsSize; i++)
            logins.get(i).print();
    }
    
    static boolean setMatchAvailability(String username, String password, boolean availability) {
        Account account = getAccount(username, password);
        if (account == null)
            return false;
        int num_logins = logins.size();
        for (int i=0; i<num_logins; i++) {
            if (logins.get(i).getAccount().equals(account)) {
                logins.get(i).setAvailable(availability);
                return true;
            }
        }
        return false;
    }

    static int checkMatchAvailability(String username, String password) {
        Account account = getAccount(username, password);
        if (account == null)
            return 401;
        int num_logins = logins.size();
        for (int i=0; i<num_logins; i++) {
            if (logins.get(i).getAccount().equals(account)) {
                if (logins.get(i).isAvailable())
                    return 1;
                else
                    return 0;
            }
        }
        return 401;
    }

    static ArrayList<LoggedIn> viewMatchesAvailable(String username, String password) {
        Account account = getAccount(username, password);
        if (account == null)
            return null;
        int num_logins = logins.size();
        boolean logged_in = false;        
        ArrayList<LoggedIn> availablePlayers = new ArrayList<LoggedIn>();
        for (int i=0; i<num_logins; i++) {
            if (logins.get(i).getAccount().equals(account)) {
                logged_in = true;
            } else if (logins.get(i).isAvailable()) {
                availablePlayers.add(logins.get(i));
            }
        }
        if (logged_in)
            return availablePlayers;
        return null;
    }

    static int challengeUser(String username, String password, String challengeUsername) {
        Account account = getAccount(username, password);
        if (account == null)
            return 401; // Unauthorized: Account does not exist or authentication failed
        if (account.checkUsername(challengeUsername))
            return 403; // Forbidden: Cannot challenge itself
        
        int num_logins = logins.size();
        LoggedIn login = null;
        for (int i=0; i<num_logins; i++) {
            if (logins.get(i).getAccount().equals(account)) {
                login = logins.get(i);
                break;
            }
        }
        if (login == null)
            return 401; // Unauthorized: Not logged in
        
        for (int i=0; i<num_logins; i++) {
            if (logins.get(i).getAccount().checkUsername(challengeUsername)) {
                int response = logins.get(i).addProposal(login);
                if (response==1) {
                    return 403; // Forbidden: The challenged user does not allow challenges
                } else if (response==2) {
                    return 200; // OK: Proposal was already there
                } else if (response==3) {
                    Notification.proposal(logins.get(i), login, " challenged you.");
                    return 200; // OK: Proposal added
                }
            }
        }
        return 404; // Challenged user not found
    }

    static int unchallengeUser(String username, String password, String challengeUsername) {
        Account account = getAccount(username, password);
        if (account == null)
            return 401; // Unauthorized: Account does not exist or authentication failed
        if (account.checkUsername(challengeUsername))
            return 403; // Forbidden: Cannot unchallenge itself
        
        int num_logins = logins.size();
        LoggedIn login = null;
        for (int i=0; i<num_logins; i++) {
            if (logins.get(i).getAccount().equals(account)) {
                login = logins.get(i);
                break;
            }
        }
        if (login == null)
            return 401; // Unauthorized: Not logged in
        
        for (int i=0; i<num_logins; i++) {
            if (logins.get(i).getAccount().checkUsername(challengeUsername)) {
                if (logins.get(i).removeProposal(login.getAccount().getUsername())) // Proposal removed if true, proposal wasn't already there if false
                    Notification.proposal(logins.get(i), login, " removed its challenge proposal.");
                return 200; // OK
            }
        }
        return 404; // User to unchallenge not found
    }

    static int acceptChallenge(String username, String password, String challengeUsername) {
        Account account = getAccount(username, password);
        if (account == null)
            return 401; // Unauthorized: Account does not exist or authentication failed
        if (account.checkUsername(challengeUsername))
            return 403; // Forbidden: Cannot challenge itself
        
        int num_logins = logins.size();
        LoggedIn login = null;
        for (int i=0; i<num_logins; i++) {
            if (logins.get(i).getAccount().equals(account)) {
                login = logins.get(i);
                break;
            }
        }
        if (login == null)
            return 401; // Unauthorized: Not logged in
        
        for (int i=0; i<num_logins; i++) {
            if (logins.get(i).getAccount().checkUsername(challengeUsername)) {
                if (login.getProposals().contains(logins.get(i))) {
                    login.acceptedMatch();
                    logins.get(i).acceptedMatch();
                    return 200; // OK: Challenge accepted
                } else {
                    return 403; // Forbidden: Wasn't challenged by that user
                }
            }
        }
        return 404; // Challenger not found
    }

    static LoggedIn viewMatchProposals(String username, String password) {
        Account account = getAccount(username, password);
        if (account == null)
            return null;
        int num_logins = logins.size();
        for (int i=0; i<num_logins; i++) {
            if (logins.get(i).getAccount().equals(account)) {
                return logins.get(i);
            }
        }
        return null;
    }
}