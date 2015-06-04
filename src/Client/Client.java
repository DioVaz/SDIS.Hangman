package Client;

import java.io.IOException;

public class Client {
    public static void main(String[] args) throws IOException {
        /*if (args.length < 1 || args.length > 2) {
             System.out.println("Usage: java Client <port_number> <oper>");
             System.out.println("oper: register/login/logout");
             System.out.println("oper matchmaking hosts: view/add/remove");
             System.out.println("oper matchmaking self host: proposals/accept");
             System.out.println("oper matchmaking proposals: propose/unpropose");
             return;
        }*/
        
        int port_number = Integer.parseInt(args[0]);
        
        // Starts a thread for the socket that is going to listen
        // waiting for server notifications or accepted matches
        ClientThread clientThread = new ClientThread(port_number);
        clientThread.start();
        
        System.out.println("Hello world!");
        
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {}
        clientThread.close();
            
            
            
            
            
            
            /*
            // A server socket because the client is the receiving end of the notifications
            try (ServerSocket serverSocket = new ServerSocket(port_number)) {
            new ClientThread(serverSocket.accept()).start();
            } catch (IOException e) {
            System.err.println("Could not listen on port " + port_number + ".");
            System.exit(-1);
            }
            
            if (args.length==1)
            return;*/
            
            /*System.out.println("Answer Code:"+ClientHTTPS.register("utilizador", "password", "nome"));
            System.out.println("Answer Code:"+ClientHTTPS.register("novo utlizador", "password da pinta", "nome completo"));*/
            
            /*System.out.println("Answer Code:"+ClientHTTPS.login("novo utlizador", "password da pinta", 9898));
            System.out.println("Answer Code:"+ClientHTTPS.login("novo utlizador", "password", 9898));
            System.out.println("Answer Code:"+ClientHTTPS.login("novo", "password da pinta", 9898));
            System.out.println("Answer Code:"+ClientHTTPS.login("jacksone", "123456", 9800));*/
            
            /*System.out.println("Answer Code:"+ClientHTTPS.logout("novo", "password da pinta"));
            System.out.println("Answer Code:"+ClientHTTPS.logout("novo utlizador", "password"));
            System.out.println("Answer Code:"+ClientHTTPS.logout("novo utlizador", "password da pinta"));*/
            
            /*System.out.println("Answer Code:"+ClientHTTPS.matchmakingAdd("novo", "password da pinta"));
            System.out.println("Answer Code:"+ClientHTTPS.matchmakingAdd("novo utlizador", "password"));
            System.out.println("Answer Code:"+ClientHTTPS.matchmakingAdd("novo utlizador", "password da pinta"));*/
            
            /*System.out.println("Answer Code:"+ClientHTTPS.matchmakingRemove("novo", "password da pinta"));
            System.out.println("Answer Code:"+ClientHTTPS.matchmakingRemove("novo utlizador", "password"));
            System.out.println("Answer Code:"+ClientHTTPS.matchmakingRemove("novo utlizador", "password da pinta"));*/
            
            /*System.out.println("Answer Code:"+ClientHTTPS.login("diogo", "qwerty", 9701));
            System.out.println("Answer Code:"+ClientHTTPS.login("drexx", "123456", 9702));
            System.out.println("Answer Code:"+ClientHTTPS.login("jacksone", "123456", 9703));
            System.out.println("Answer Code:"+ClientHTTPS.login("oli", "123456", 9704));
            System.out.println("Answer Code:"+ClientHTTPS.login("ybra1992", "asd", 9705));
            
            System.out.println("\nAnswer Code:"+ClientHTTPS.matchmakingAdd("diogo", "qwerty"));
            System.out.println("Answer Code:"+ClientHTTPS.matchmakingAdd("drexx", "123456"));
            System.out.println("Answer Code:"+ClientHTTPS.matchmakingAdd("jacksone", "123456"));
            System.out.println("Answer Code:"+ClientHTTPS.matchmakingAdd("oli", "123456"));
            System.out.println("Answer Code:"+ClientHTTPS.matchmakingAdd("ybra1992", "asd"));
            
            System.out.println("\nAnswer Code:"+ClientHTTPS.matchmakingRemove("diogo", "qwerty"));
            System.out.println("Answer Code:"+ClientHTTPS.matchmakingRemove("drexx", "123456"));
            System.out.println("Answer Code:"+ClientHTTPS.matchmakingRemove("jacksone", "123456"));
            System.out.println("Answer Code:"+ClientHTTPS.matchmakingRemove("oli", "123456"));
            System.out.println("Answer Code:"+ClientHTTPS.matchmakingRemove("ybra1992", "asd"));
            
            System.out.println("\nAnswer List:");
            User.print(ClientHTTPS.matchmakingView("ybra1992", "asd"));*/
            
            /*System.out.println("Answer Code:"+ClientHTTPS.login("diogo", "qwerty", 9701));
            System.out.println("Answer Code:"+ClientHTTPS.login("drexx", "123456", 9702));
            System.out.println("Answer Code:"+ClientHTTPS.login("jacksone", "123456", 9703));
            System.out.println("Answer Code:"+ClientHTTPS.login("oli", "123456", 9704));
            System.out.println("Answer Code:"+ClientHTTPS.login("ybra1992", "asd", 9705));
            
            System.out.println("\nAnswer Code:"+ClientHTTPS.matchmakingAdd("diogo", "qwerty"));
            
            System.out.println("\nAnswer List:");
            User.print(ClientHTTPS.matchmakingView("drexx", "123456"));
            
            System.out.println("\nAnswer List:");
            User.print(ClientHTTPS.matchmakingView("diogo", "qwerty"));
            
            System.out.println("\nAnswer List:");
            User.print(ClientHTTPS.matchmakingProposals("diogo", "qwerty"));
            
            System.out.println("\nAnswer Code:"+ClientHTTPS.matchmakingPropose("drexx", "123456", "diogo"));
            System.out.println("Answer Code:"+ClientHTTPS.matchmakingPropose("jacksone", "123456", "diogo"));
            System.out.println("Answer Code:"+ClientHTTPS.matchmakingPropose("oli", "123456", "diogo"));
            System.out.println("Answer Code:"+ClientHTTPS.matchmakingPropose("ybra1992", "asd", "diogo"));
            
            System.out.println("\nAnswer List:");
            User.print(ClientHTTPS.matchmakingProposals("diogo", "qwerty"));
            
            System.out.println("\nAnswer Code:"+ClientHTTPS.matchmakingUnpropose("drexx", "123456", "diogo"));
            System.out.println("Answer Code:"+ClientHTTPS.matchmakingUnpropose("jacksone", "123456", "diogo"));
            System.out.println("Answer Code:"+ClientHTTPS.matchmakingUnpropose("oli", "123456", "diogo"));
            System.out.println("Answer Code:"+ClientHTTPS.matchmakingUnpropose("ybra1992", "asd", "diogo"));
            
            System.out.println("\nAnswer List:");
            User.print(ClientHTTPS.matchmakingProposals("diogo", "qwerty"));*/
            
            /*System.out.println("\nAnswer Code:"+ClientHTTPS.login("diogo", "qwerty", 9701));
            System.out.println("Answer Code:"+ClientHTTPS.login("drexx", "123456", 9702));
            System.out.println("Answer Code:"+ClientHTTPS.login("jacksone", "123456", 9703));
            
            System.out.println("\nAnswer Code:"+ClientHTTPS.matchmakingAdd("diogo", "qwerty"));
            System.out.println("\nAnswer Code:"+ClientHTTPS.matchmakingAdd("drexx", "123456"));
            System.out.println("\nAnswer Code:"+ClientHTTPS.matchmakingAdd("jacksone", "123456"));
            
            System.out.println("\nAnswer Code:"+ClientHTTPS.matchmakingPropose("drexx", "123456", "diogo"));*/
            /*System.out.println("\nAnswer Code:"+ClientHTTPS.matchmakingPropose("jacksone", "123456", "diogo"));
            
            Match match = ClientHTTPS.matchmakingAccept("diogo", "qwerty", "drexx");
            if (match!=null)
            match.print();*/
            
            
            
            
            
            
            /*String xml = HTTPSConnection.buildXML("utilizador", "password", "name", "nome");
            System.out.println("xml:" + xml);
            HttpsURLConnection con = HTTPSConnection.send("https://localhost:9000/hangman/register", xml);
            //HTTPSConnection.printInfo(con);
            HTTPSConnection.printContent(con);
            
            xml = HTTPSConnection.buildXML("utilizador", "password", "port", "9876");
            System.out.println("xml:" + xml);
            con = HTTPSConnection.send("https://localhost:9000/hangman/login", xml);
            //HTTPSConnection.printInfo(con);
            HTTPSConnection.printContent(con);
            
            xml = HTTPSConnection.buildXML("utilizador", "password", null, null);
            System.out.println("xml:" + xml);
            con = HTTPSConnection.send("https://localhost:9000/hangman/matchmaking/add", xml);
            //HTTPSConnection.printInfo(con);
            HTTPSConnection.printContent(con);
            
            xml = HTTPSConnection.buildXML("utilizador", "password", null, null);
            System.out.println("xml:" + xml);
            con = HTTPSConnection.send("https://localhost:9000/hangman/matchmaking/view", xml);
            //HTTPSConnection.printInfo(con);
            HTTPSConnection.printContent(con);
            
            xml = HTTPSConnection.buildXML("jacksone", "123456", "port", "8765");
            System.out.println("xml:" + xml);
            con = HTTPSConnection.send("https://localhost:9000/hangman/login", xml);
            //HTTPSConnection.printInfo(con);
            HTTPSConnection.printContent(con);
            
            xml = HTTPSConnection.buildXML("jacksone", "123456", null, null);
            System.out.println("xml:" + xml);
            con = HTTPSConnection.send("https://localhost:9000/hangman/matchmaking/add", xml);
            //HTTPSConnection.printInfo(con);
            HTTPSConnection.printContent(con);
            
            xml = HTTPSConnection.buildXML("utilizador", "password", null, null);
            System.out.println("xml:" + xml);
            con = HTTPSConnection.send("https://localhost:9000/hangman/matchmaking/view", xml);
            //HTTPSConnection.printInfo(con);
            HTTPSConnection.printContent(con);*/
    }

}