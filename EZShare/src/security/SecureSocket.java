package security;

import com.sun.net.ssl.internal.ssl.Provider;
import variable.Host;

import javax.net.ssl.*;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

/**
 * Created by xutianyu on 5/14/17.
 *
 * * This program simulates an SSL Server listening on a specific port for client requests
 *
 * Algorithm:
 * 1. Regsiter the JSSE provider
 * 2. Set System property for keystore by specifying the keystore which contains the server certificate
 * 3. Set System property for the password of the keystore which contains the server certificate
 * 4. Create an instance of SSLServerSocketFactory
 * 5. Create an instance of SSLServerSocket by specifying the port to which the SSL Server socket needs to bind with
 * 6. Initialize an object of SSLSocket
 * 7. Create InputStream object to read data sent by clients
 * 8. Create an OutputStream object to write data back to clients.
 *
 */

public class SecureSocket {

    /**
     * @param args
     *
     *
     */

    public static SSLServerSocket serverSecure(Host host) throws Exception {
        {
            // Registering the JSSE provider
            Security.addProvider(new Provider());

            //Specifying the Keystore details
            InputStream keystoreInput = SecureSocket.class
                    .getResourceAsStream("/serverKeystore/server.jks");
            InputStream truststoreInput = SecureSocket.class
                    .getResourceAsStream("/clientKeystore/client.jks");
            support.SetSecureSocket.setSSLFactories(keystoreInput, "comp90015", truststoreInput);
            keystoreInput.close();
            truststoreInput.close();

            // Enable debugging to view the handshake and communication which happens between the SSLClient and the SSLServer
            // System.setProperty("javax.net.debug","all");
        }

        SSLServerSocket sslServerSocket = null;
        try {
            // Initialize the Server Socket
            SSLServerSocketFactory sslServerSocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            sslServerSocket = (SSLServerSocket) sslServerSocketfactory.createServerSocket(host.getPort());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sslServerSocket;
    }

    public static SSLSocket clientSecure(Host host){

        SSLSocket sslSocket = null;
        
        try{
            // initialize
            SSLContext context = SSLContext.getInstance("SSL");

            // Creating Client Sockets
            SSLSocketFactory sslsocketfactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
            sslSocket = (SSLSocket)sslsocketfactory.createSocket(host.getHostname(),host.getPort());


        }catch(Exception e){

        }

        return sslSocket;


    }




}
