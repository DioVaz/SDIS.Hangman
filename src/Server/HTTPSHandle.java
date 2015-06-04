package Server;

import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

class HTTPSHandle {        
    protected static String read(HttpExchange he) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(he.getRequestBody()));
        String line;
        StringBuffer buffer = new StringBuffer();
        while ((line = br.readLine()) != null) {
            buffer.append(line);
        }
        br.close();
        return buffer.toString();
    }
    
    protected static void sendCodeOnly(HttpExchange he, int code) {
        try {
            he.sendResponseHeaders(code, 0);
            OutputStream os = he.getResponseBody();
            os.close();
        } catch (IOException ex) {
            System.out.println("Error when sending a response message (HTTP code only).");
        }
    }
    
    protected static void sendResponse(HttpExchange he, int code, String body) {
        try {
            byte[] bodyBytes = body.getBytes();
            he.sendResponseHeaders(code, bodyBytes.length);
            OutputStream os = he.getResponseBody();
            os.write(bodyBytes);
            os.close();
        } catch (IOException ex) {
            System.out.println("Error when sending a response message (Full response: HTTP code + body).");
        }
    
    }
}
