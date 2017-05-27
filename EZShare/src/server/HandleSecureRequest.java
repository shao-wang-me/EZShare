package server;

import com.sun.net.ssl.internal.ssl.Provider;
import support.SetSecureSocket;
import variable.resourceList;
import variable.serverList;
import variable.secureServerList;
import variable.subscribeList;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.KeyStore;
import java.security.PrivilegedActionException;
import java.security.Security;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by xutianyu on 5/14/17.
 */
public class HandleSecureRequest implements Runnable{

    private SSLServerSocket server;

    private int serverPort ;

    private String secret;

    private resourceList resourceList;

    private resourceList newResourceList;

    private resourceList newResourceList_copy;


    private serverList serverList;

    private Boolean debug = true;

    private Logger log ;

    private String hostname;

    private int intervalLimit;

    public HandleSecureRequest(int serverPort, String secret , resourceList resourceList, resourceList newResourceList
                               ,resourceList newResourceList_copy, secureServerList serverList,
                               Boolean debug, Logger log, String hostname, int intervalLimit){
        this.serverPort = serverPort;
        this.secret = secret;
        this.resourceList = resourceList;
        this.newResourceList = newResourceList;
        this.newResourceList_copy = newResourceList_copy; // copy resourcelist
        this.serverList = serverList;
        this.debug = debug;
        this.log = log;
        this.hostname = hostname;
        this.intervalLimit = intervalLimit;

    }

    @Override
    public void run() {
        try {
            //Thread pool to increase efficiency
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

            // Initialize the Server Socket
            Security.addProvider(new Provider());

            InputStream keystoreInput = getClass()
                    .getResourceAsStream("/serverKeystore/server.jks");
            InputStream truststoreInput = getClass()
                    .getResourceAsStream("/clientKeystore/client.jks");
            support.SetSecureSocket.setSSLFactories(keystoreInput, "comp90015", truststoreInput);
            keystoreInput.close();
            truststoreInput.close();

            //Specifying the Keystore details
            //System.setProperty("javax.net.ssl.keyStore", buffer.lines().collect(Collectors.joining("\n")));
            //System.setProperty("javax.net.ssl.keyStorePassword", "comp90015");

            //Single thread executor for the relay thread, added by yankun
            subscribeList subscribeList = new subscribeList();
            subscribeList readyToSend = new subscribeList();
            secureServerList serverDeleteList = new secureServerList();
            serverDeleteList.initialserverList();
            secureServerList serverAddList = new secureServerList();
            serverAddList.initialserverList();

            //Single thread executor for the subscription thread, added by yankun
            //ExecutorService ThreadExecutor = Executors.newCachedThreadPool();
            subscribeThread subscriptionThread = new subscribeThread(newResourceList, debug,
                    intervalLimit , subscribeList, readyToSend);
            //ThreadExecutor.execute(subscriptionThread);
            new Thread(subscriptionThread).start();


            // relay monitor
            //ExecutorService relayThreadExecutor = Executors.newCachedThreadPool();
            relayThread relayThread = new relayThread(newResourceList, debug,
                    subscribeList, serverList, serverDeleteList, serverAddList, readyToSend, true);
            //ThreadExecutor.execute(relayThread);
            new Thread(relayThread).start();



            SSLServerSocketFactory sslServerSocketfactory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
            server= (SSLServerSocket)sslServerSocketfactory.createServerSocket(getServerPort());

            //create multiple threads to build connections
            Boolean f = true ;
            while(f){
                SSLSocket client = (SSLSocket)server.accept();
                if(client.isConnected()){
                    ServerThread s = new ServerThread(client, getSecret(),
                            resourceList, newResourceList, newResourceList_copy, serverList, serverAddList,
                            getDebug(), getHostname(), getServerPort(), getIntervalLimit(),
                             subscribeList,  readyToSend);

                    executor.execute(s);
                }
                else{
                    client.close();
                }
            }
            executor.shutdown();
            server.close();

        }catch(Exception e){
        }

        }


    public int getServerPort() {
        return serverPort;
    }


    public String getSecret() {
        return secret;
    }


    public Boolean getDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public String getHostname() {
        return hostname;
    }


    public int getIntervalLimit() {
        return intervalLimit;
    }


}
