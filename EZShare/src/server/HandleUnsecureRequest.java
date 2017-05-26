package server;


import variable.resourceList;
import variable.serverList;
import variable.secureServerList;
import variable.subscribeList;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
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

    private resourceList newResourceList;

    private resourceList newResourceList_copy;

    private serverList serverList;

    private Boolean debug = true;

    private Logger log ;

    private String hostname;

    private int intervalLimit;

    public HandleUnsecureRequest(int serverPort, String secret , resourceList resourceList,
                                 resourceList newResourceList, resourceList newResourceList_copy,
                                         serverList serverList, Boolean debug, Logger log, String hostname, int intervalLimit){
        this.serverPort = serverPort;
        this.secret = secret;
        this.resourceList = resourceList;
        this.newResourceList = newResourceList;
        this.newResourceList_copy = newResourceList_copy;
        this.serverList = serverList;
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
        }


    }

    public void OpenServer(ServerSocket server, int port){
        //Thread pool to increase efficiency
        try{

            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();


            //Single thread executor for the relay thread, added by yankun
            subscribeList subscribeList = new subscribeList();
            subscribeList readyToSend = new subscribeList();
            serverList serverDeleteList = new serverList();
            serverDeleteList.initialserverList();
            serverList serverAddList = new serverList();
            serverAddList.initialserverList();

            //Single thread executor for the subscription thread, added by yankun
            //ExecutorService ThreadExecutor = Executors.newCachedThreadPool();
            subscribeThread subscriptionThread = new subscribeThread(newResourceList_copy, debug,
                    intervalLimit , subscribeList, readyToSend);
            //ThreadExecutor.execute(subscriptionThread);
            new Thread(subscriptionThread).start();

            // relay monitor
            //ExecutorService relayThreadExecutor = Executors.newCachedThreadPool();
            relayThread relayThread = new relayThread(newResourceList_copy, debug,
                    subscribeList, serverList, serverDeleteList, serverAddList, readyToSend, false);
            //ThreadExecutor.execute(relayThread);
            new Thread(relayThread).start();

            //create multiple threads to build connections
            Boolean f = true ;
            while(f){
                Socket client = server.accept();
                if(client.isConnected()){
                    ServerThread s = new ServerThread(client, getSecret(),
                            resourceList, newResourceList, newResourceList_copy, serverList, serverAddList,
                             getDebug(), getHostname(), serverPort, getIntervalLimit(),
                            subscribeList,readyToSend);

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
