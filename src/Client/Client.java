package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    public static void main(String[] arg) {
        
        PrintWriter os = null;
        BufferedReader is = null; // is: input stream
        Socket smtpSocket = null; // smtpClient: our client socket
        
        String address = arg[0];
        int port = Integer.parseInt(arg[1]);
        
        try {
            InetAddress thisIp = InetAddress.getByName(address);
            System.out.println(thisIp.getHostAddress());
            smtpSocket = new Socket(thisIp, port);
            os = new PrintWriter(smtpSocket.getOutputStream(), true);
            
            is = new BufferedReader(new InputStreamReader(smtpSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + address + ":" + port);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + address + ":" + port);
        }
        
        if (smtpSocket != null && os != null && is != null) {
            try {
                // write to server
                os.println("STARTMATCH");
                if (os.checkError()) {
                    System.out.println("ERROR writing data to socket !!!");
                    System.exit(-1);
                }
                os.close();
                is.close();
                smtpSocket.close();   
            } catch (UnknownHostException e) {
                System.err.println("Trying to connect to unknown host: " + e);
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            }
        }
        
        
        
        
        

        /*while (true) {
            System.out.println("Starting!");
            try ( 
                ServerSocket serverSocket = new ServerSocket(portNumber);
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            ) {
                String outputLine;
                int listSize = list.size();
                boolean check = true;
                while(check) {
                    for (int i=0; i<listSize; i++) {
                        try { Thread.sleep(1000); } catch (InterruptedException ex) {}
                        out.println(list.get(i));
                        if (out.checkError()) {
                            System.out.println("ERROR writing data to socket !!!");
                            check = false;
                            break;
                        }
                    }
                }
            }
            System.out.println("Starting over!");
            if ("hello".equals("abc"))
                break;
        }*/
        
        
        
        
        /*if (Integer.parseInt(args[0])==1) {
            
            int port_number = Integer.parseInt(args[1]);

            // Starts a thread for the socket that is going to listen
            // waiting for server notifications or accepted matches
            ClientThread clientThread = new ClientThread(port_number);
            clientThread.start();

            System.out.println("Hello world!");

            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {}
            clientThread.close();
            
        } else if (Integer.parseInt(args[0])==2) {
            
            
                
        }*/
            
    }
}