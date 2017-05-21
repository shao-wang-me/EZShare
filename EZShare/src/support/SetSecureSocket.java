package support;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;

/**
 * Created by xutianyu on 5/21/17.
 */
public class SetSecureSocket {

    public static void setSSL() throws Exception {

        try{

            InputStream keystoreInput = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("/serverKeystore/server.jks");
            InputStream truststoreInput = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("/clientKeystore/client.jks");
            setSSLFactories(keystoreInput, "comp90015", truststoreInput);
            keystoreInput.close();
            truststoreInput.close();

        }catch(Exception e){
            e.printStackTrace();
        }

    }


    public static void setSSLFactories(InputStream keyStream, String keyStorePassword,
                                        InputStream trustStream) throws Exception
    {
        // Get keyStore
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

        // if your store is password protected then declare it (it can be null however)
        char[] keyPassword = keyStorePassword.toCharArray();

        // load the stream to your store
        keyStore.load(keyStream, keyPassword);

        // initialize a trust manager factory with the trusted store
        KeyManagerFactory keyFactory =
                KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyFactory.init(keyStore, keyPassword);

        // get the trust managers from the factory
        KeyManager[] keyManagers = keyFactory.getKeyManagers();

        // Now get trustStore
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

        // if your store is password protected then declare it (it can be null however)
        //char[] trustPassword = password.toCharArray();

        // load the stream to your store
        trustStore.load(trustStream, null);

        // initialize a trust manager factory with the trusted store
        TrustManagerFactory trustFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustFactory.init(trustStore);

        // get the trust managers from the factory
        TrustManager[] trustManagers = trustFactory.getTrustManagers();

        // initialize an ssl context to use these managers and set as default
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(keyManagers, trustManagers, null);
        SSLContext.setDefault(sslContext);
    }
}
