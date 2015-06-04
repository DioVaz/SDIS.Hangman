package Client;

import Main.Match;
import Main.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ClientCLI {
    private static String username, password;
    private static int port;
    private static boolean isHost = false;
    private static ClientThread clientThread;
    
    public static void main(String[] args) {
        mainMenu();
    }
    
    public static void mainMenu() {
        while(true) {
            System.out.println("\n[Hangman.net] Main Menu");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("9. Sair");
            
            Scanner reader = new Scanner(System.in);
            System.out.print("Input:");
            int option = 0;
            try {
                option = reader.nextInt();
            } catch (InputMismatchException ex) {}
            if (option==1) {
                register();
            } else if (option==2) {
                if (login())
                    loggedInMenu();
            } else if (option==9) {
                System.exit(0);
            } else {
                System.out.println("Invalid option.");
            }
        }
    }
    
    private static void loggedInMenu() {
        while(true) {
            System.out.println("\n[Hangman.net] Main Menu");
            System.out.println("\n[Logged in] Username: " + username + " (Port: " + port + ")");
            System.out.println("1. View host list");
            
            isHost = ClientHTTPS.matchmakingHost(username, password);
            
            if (isHost) {
                System.out.println("2. View received proposals");
                System.out.println("3. Remove me as host");
            } else {
                System.out.println("2. Add me as host");
            }
            System.out.println("9. Logout");
            
            Scanner reader = new Scanner(System.in);
            System.out.print("Input:");
            int option = 0;
            try {
                option = reader.nextInt();
            } catch (InputMismatchException ex) {}
            if (option==1) {
                matchmakingView();
            } else if (option==2) {
                if (isHost) {
                    matchmakingProposals();
                } else {
                    System.out.println("\nAnswer Code: " + ClientHTTPS.matchmakingAdd(username, password));
                }
            } else if (option==3 && isHost) {
                System.out.println("\nAnswer Code: " + ClientHTTPS.matchmakingRemove(username, password));
            } else if (option==9) {
                int answer_code = ClientHTTPS.logout(username, password);
                System.out.println("\nAnswer Code: " + answer_code);
                if (answer_code!=200)
                    System.out.println("An HTTP error that should not occur has occurred (HTTP Code: " + answer_code + ")");
                clientThread.close();                
                return;
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    private static boolean login() {
        String usernameLogin, passwordLogin;
        int portLogin;
        try {
            while(true) {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("\n[Hangman.net] Login");
                System.out.print("Username:");
                usernameLogin = br.readLine();
                System.out.print("Password:");
                passwordLogin = br.readLine();
            
                Scanner reader = new Scanner(System.in);
                System.out.print("My port number:");
                try {
                    portLogin = reader.nextInt();
                } catch (InputMismatchException ex) {
                    return false;
                }

                int answer_code = ClientHTTPS.login(usernameLogin, passwordLogin, portLogin);
                System.out.println("\nAnswer Code: " + answer_code);
                if (answer_code==400) {
                    System.out.println("Username and password must have at least 3 characters and port number must be valid.");
                    return false;
                } else if (answer_code==401) {
                    System.out.println("Incorrect login details or account doesn't exist.");
                    return false;
                } else if (answer_code==200) {
                    username = usernameLogin;
                    password = passwordLogin;
                    port = portLogin;
                    if (clientThread!=null) {
                        if (clientThread.isAlive()) {
                            clientThread.close();
                        }
                    }
                    clientThread = new ClientThread(port);
                    clientThread.start();
                    return true;
                } else {
                    System.out.println("Unexpected error.");
                    return false;
                }
            }
        } catch (IOException ex) { return false; }
    }

    private static boolean register() {
            String usernameReg, passwordReg, nameReg;
            
            try {
                while(true) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                    System.out.println("\n[Hangman.net] Register");
                    System.out.print("Username:");
                    usernameReg = br.readLine();
                    System.out.print("Password:");
                    passwordReg = br.readLine();
                    System.out.print("Name:");
                    nameReg = br.readLine();

                    int answer_code = ClientHTTPS.register(usernameReg, passwordReg, nameReg);
                    System.out.println("\nAnswer Code: " + answer_code);
                    if (answer_code==400) {
                        System.out.println("Every field must have at least 3 characters.");
                        return false;
                    } else if (answer_code==409) {
                        System.out.println("Username already registered.");
                        return false;
                    } else if (answer_code==201) {
                        System.out.println("User '" + usernameReg + "' created.");
                        return true;
                    } else {
                        System.out.println("Unexpected error.");
                        return false;
                    }
                }
            } catch (IOException ex) { return false; }
    }

    private static void matchmakingView() {
        while(true) {
            System.out.println("\n[Hangman.net] View host list");
            
            ArrayList<User> users = ClientHTTPS.matchmakingView(username, password);
            User.print(users);
            
            System.out.println("1. Propose");
            System.out.println("2. Unpropose");
            System.out.println("9. Return");
            
            Scanner reader = new Scanner(System.in);
            System.out.print("Input:");
            int option = 0;
            try {
                option = reader.nextInt();
            } catch (InputMismatchException ex) {}
            if (option==1) {
                matchmakingPropose();
            } else if (option==2) {
                matchmakingUnpropose();
            } else if (option==9) {
                return;
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    private static void matchmakingPropose() {
        while(true) {
            System.out.println("\n[Hangman.net] Propose");
            
            ArrayList<User> users = ClientHTTPS.matchmakingView(username, password);
            User.printIndexed(users);
            
            Scanner reader = new Scanner(System.in);
            System.out.print("Input user index (negative number to return):");
            int option = 0;
            try {
                option = reader.nextInt();
            } catch (InputMismatchException ex) {}
            if (option<0) {
                return;
            } else if (option>0 && option<=users.size()) {
                System.out.println("\nAnswer Code: " + ClientHTTPS.matchmakingPropose(username, password, users.get(option-1).getUsername()));
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    private static void matchmakingUnpropose() {
        while(true) {
            System.out.println("\n[Hangman.net] Unpropose");
            
            ArrayList<User> users = ClientHTTPS.matchmakingView(username, password);
            User.printIndexed(users);
            
            Scanner reader = new Scanner(System.in);
            System.out.print("Input user index (negative number to return):");
            int option = 0;
            try {
                option = reader.nextInt();
            } catch (InputMismatchException ex) {}
            if (option<0) {
                return;
            } else if (option>0 && option<=users.size()) {
                System.out.println("\nAnswer Code: " + ClientHTTPS.matchmakingUnpropose(username, password, users.get(option-1).getUsername()));
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    private static void matchmakingProposals() {
        while(true) {
            System.out.println("\n[Hangman.net] View received proposals");
            
            ArrayList<User> users = ClientHTTPS.matchmakingProposals(username, password);
            User.print(users);
            
            System.out.println("1. Accept");
            System.out.println("9. Return");
            
            Scanner reader = new Scanner(System.in);
            System.out.print("Input:");
            int option = 0;
            try {
                option = reader.nextInt();
            } catch (InputMismatchException ex) {}
            if (option==1) {
                matchmakingAccept();
            } else if (option==9) {
                return;
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    private static void matchmakingAccept() {
        while(true) {
            System.out.println("\n[Hangman.net] Accept received proposal");
            
            ArrayList<User> users = ClientHTTPS.matchmakingProposals(username, password);
            User.printIndexed(users);
            
            Scanner reader = new Scanner(System.in);
            System.out.print("Input user index (negative number to return):");
            int option = 0;
            try {
                option = reader.nextInt();
            } catch (InputMismatchException ex) {}
            if (option<0) {
                return;
            } else if (option>0 && option<=users.size()) {
                Match match = ClientHTTPS.matchmakingAccept(username, password, users.get(option-1).getUsername());
                if (match==null) {
                    System.out.println("\nMatch is null.");
                } else {
                    System.out.println("");
                    match.print();
                }
            } else {
                System.out.println("Invalid option.");
            }
        }
    }
}
