package Server;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;

class HTTPSConfig extends HttpsConfigurator {
    public HTTPSConfig(SSLContext c) {
        super(c);
    }
    @Override
    public void configure(HttpsParameters params) {
        try {
            // Initialise the SSL context
            SSLContext c = SSLContext.getDefault ();
            SSLEngine engine = c.createSSLEngine ();
            params.setNeedClientAuth(false);
            params.setCipherSuites(engine.getEnabledCipherSuites());
            params.setProtocols(engine.getEnabledProtocols());

            // Get the default parameters
            SSLParameters defaultSSLParameters = c.getDefaultSSLParameters();
            params.setSSLParameters ( defaultSSLParameters );

        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Error when starting the server.");
            System.exit(-3);
        }
    }
}