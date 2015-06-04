package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

class ClientThread extends Thread {
    protected int port_number;
    protected ServerSocket serverSocket;
    
    public ClientThread(int port_number) throws IOException {
        super("ClientThread");
        this.port_number = port_number;
        serverSocket = new ServerSocket(port_number);
    }
    
    public void close() {
        // Closes the socket
        try {
            serverSocket.close();
        } catch (IOException ex) {}
    }

    @Override
    public void run() {
        try {
            while(true) {
                Socket socket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println(inputLine);
                    if (inputLine.contains("logged in using the same account"))
                        System.exit(0);
                    if (inputLine.contains("STARTMATCH")) {
                        
                    }
                }
                in.close();
                socket.close();
            }
        } catch (IOException ex) { // The socket was closed using the close() function above.
            //System.out.println("Socket closed.");
        }
    }
    
}
