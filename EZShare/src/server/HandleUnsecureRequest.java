package server;


import variable.resourceList;
import variable.serverList;
import variable.secureServerList;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;

/**
 * Created by xutianyu on 5/14/17.
 */
public class HandleUnsecureRequest implements Runnable{

    private ServerSocket server;

    private int serverPort ;

    private String secret;

    private resourceList resourceList;

    private serverList serverList;

    private secureServerList secureServerList;

    private Boolean debug = true;

    private Logger log ;

    private String hostname;

    private int intervalLimit;

    public HandleUnsecureRequest(int serverPort, String secret , resourceList resourceList,
                    serverList serverList, secureServerList secureServerList,
                                 Boolean debug, Logger log, String hostname, int intervalLimit ){
        this.serverPort = serverPort;
        this.secret = secret;
        this.resourceList = resourceList;
        this.serverList = serverList;
        this.secureServerList = secureServerList;
        this.debug = debug;
        this.log = log;
        this.hostname = hostname;
        this.intervalLimit = intervalLimit;

    }

    @Override
    public void run() {
        try{
            ServerSocket server = new ServerSocket(serverPort);
            OpenServer(server, serverPort);
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    public void OpenServer(ServerSocket server, int port){
        //Thread pool to increase efficiency
        try{

            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

            //create multiple threads to build connections
            Boolean f = true ;
            while(f){
                Socket client = server.accept();
                if(client.isConnected()){
                    ServerThread s = new ServerThread(client, getSecret(),
                            resourceList, serverList, secureServerList ,getDebug(), getHostname(), serverPort, getIntervalLimit());

                    executor.execute(s);
                }
                else{
                    client.close();
                }
            }
            executor.shutdown();
            server.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public String getSecret() {
        return secret;
    }


    public Boolean getDebug() {
        return debug;
    }

    public void setDebug(Boolean debug) {
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
