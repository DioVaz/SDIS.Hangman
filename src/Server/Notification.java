package Server;

import Main.LoggedIn;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

class Notification {
    static void proposal(LoggedIn challenged, LoggedIn challenger, String text) {
        try {
            new ProposalThread(challenged, challenger, text).start();
        } catch (IOException ex) {
            System.out.println("Couldn't connect to " + challenged.getIPAddress() + ":" + challenged.getPortNumber() + ". (Proposal Notification)");
        }
    }

    static void doubleLogin(String old_ip, int old_port, String new_ip, int new_port) {
        try {
            new LoginThread(old_ip, old_port, new_ip, new_port).start();
        } catch (IOException ex) {
            System.out.println("Couldn't connect to " + old_ip + ":" + old_port + ". (Double Login Notification)");
        }
    }

    private static class LoginThread extends Thread {
        String old_ip, new_ip;
        int old_port, new_port;
        
        public LoginThread(String old_ip, int old_port, String new_ip, int new_port) throws IOException {
            super("LoginThread");
            this.old_ip = old_ip;
            this.old_port = old_port;
            this.new_ip = new_ip;
            this.new_port = new_port;
        }
        
        @Override
        public void run() {
            try {
                Socket socket = new Socket(old_ip, old_port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println("The machine " + new_ip + ":" + new_port + " logged in using the same account.");
                out.close();
            } catch (UnknownHostException e) {
                System.out.println("Don't know about host " + old_ip + ":" + old_port + ". (Double Login Notification)");
            } catch (IOException e) {
                System.out.println("Couldn't connect to " + old_ip + ":" + old_port + ". (Double Login Notification)");
            }
        }
    }

    private static class ProposalThread extends Thread {
        LoggedIn challenged, challenger;
        String text;
        
        public ProposalThread(LoggedIn challenged, LoggedIn challenger, String text) throws IOException {
            super("ProposalThread");
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
