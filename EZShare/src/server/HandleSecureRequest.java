package server;

import com.sun.net.ssl.internal.ssl.Provider;
import variable.resourceList;
import variable.serverList;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.PrivilegedActionException;
import java.security.Security;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;

/**
 * Created by xutianyu on 5/14/17.
 */
public class HandleSecureRequest implements Runnable{

    private SSLServerSocket server;
    private int serverPort ;

    private String secret;

    private variable.resourceList resourceList;

    private variable.serverList serverList;

    private Boolean debug = true;

    private Logger log ;

    private String hostname;

    private int intervalLimit;

    public HandleSecureRequest(int serverPort, String secret , resourceList resourceList,
                                 serverList serverList, Boolean debug, Logger log, String hostname, int intervalLimit ){
        this.serverPort = serverPort;
        this.secret = secret;
        this.resourceList = resourceList;
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

            //Specifying the Keystore details
            System.setProperty("javax.net.ssl.keyStore", "serverKeystore/server.jks");
            System.setProperty("javax.net.ssl.keyStorePassword", "comp90015");


            SSLServerSocketFactory sslServerSocketfactory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
            server= (SSLServerSocket)sslServerSocketfactory.createServerSocket(getServerPort());

            //create multiple threads to build connections
            Boolean f = true ;
            while(f){
                SSLSocket client = (SSLSocket)server.accept();
                if(client.isConnected()){
                    ServerThread s = new ServerThread(client, getSecret(),
                            resourceList, serverList, getDebug(), getHostname(), getServerPort(), getIntervalLimit());

                    executor.execute(s);
                }
                else{
                    client.close();
                }
            }
            executor.shutdown();
            server.close();

        }catch(Exception e){
            PrivilegedActionException priexp = new PrivilegedActionException(e);
            System.out.println(" Priv exp --- " + priexp.getMessage());

            System.out.println(" Exception occurred .... " +e);
            e.printStackTrace();        }

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
