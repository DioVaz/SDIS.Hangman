package Server;

import Main.LoggedIn;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

class Notification {
    static void proposal(LoggedIn challenged, LoggedIn challenger, String text) {
        try {
            new ServerThread(challenged, challenger, text).start();
        } catch (IOException ex) {
            System.out.println("Couldn't connect to " + challenged.getIPAddress() + ":" + challenged.getPortNumber() + ". (Proposal Notification)");
        }
    }

    private static class ServerThread extends Thread {
        LoggedIn challenged, challenger;
        String text;
        
        public ServerThread(LoggedIn challenged, LoggedIn challenger, String text) throws IOException {
            super("ServerThread");
            this.challenged = challenged;
            this.challenger = challenger;
            this.text = text;
        }
        
        @Override
        public void run() {
            try {
                Socket socket = new Socket(challenged.getIPAddress(), challenged.getPortNumber());
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println("User " + challenger.getAccount().getName() + text);
                out.close();
            } catch (UnknownHostException e) {
                System.out.println("Don't know about host " + challenged.getIPAddress() + ":" + challenged.getPortNumber() + ". (Proposal Notification)");
            } catch (IOException e) {
                System.out.println("Couldn't connect to " + challenged.getIPAddress() + ":" + challenged.getPortNumber() + ". (Proposal Notification)");
            }
        }
    }
}
