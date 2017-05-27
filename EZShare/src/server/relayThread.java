package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONObject;
import variable.*;
import variable.Host;
import variable.Resource;
import variable.resourceList;
import variable.serverList;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by qiuyankun on 17/5/17.
 */
public class relayThread implements Runnable {
    private variable.resourceList newResourceList;
    private Boolean debug;
    private subscribeList subscribeList;
    private serverList serverList;
    private HashMap<String,Socket> sockets;
    private subscribeList relayList;
    private serverList serverDeleteList;
    private serverList serverAddList;
    private subscribeList readyToSend;
    private boolean secure;

    public relayThread(resourceList newResourceList, Boolean debug, subscribeList subscribeList, serverList serverList,
                       serverList serverDeleteList, serverList serverAddList, subscribeList readyToSend, boolean secure) {
        this.newResourceList = newResourceList;
        this.debug = debug;
        this.subscribeList = subscribeList;
        this.serverList = serverList;
        this.serverDeleteList = serverDeleteList;
        this.serverAddList = serverAddList;
        this.readyToSend = readyToSend;
        this.relayList = new subscribeList();
        this.secure = secure;
    }

    public void run() {
        try {

            Thread.sleep(1000);

            sockets = new HashMap<String, Socket>();


                while (true) {

                    Thread.sleep(1000);
                    synchronized(serverList){
                    try {

                        //Thread.sleep(500);

                        boolean ifUpdate = false;

                        /**
                         * search the serverAddlist if its size is greater than one. If so, create socket connecting to the new
                         * server and add to the HashMap, send all the subscriptions in relayList, delete the record from serverAddList
                         */

                        if(serverList.getServerList() != null){

                            for(variable.Host h : serverList.getServerList()) {
                                Socket s ;
                                SocketAddress socketaddr = new InetSocketAddress(h.getHostname(), h.getPort());

                                if (secure) {

                                    InputStream keystoreInput = getClass()
                                            .getResourceAsStream("/serverKeystore/server.jks");
                                    InputStream truststoreInput = getClass()
                                            .getResourceAsStream("/clientKeystore/client.jks");
                                    support.SetSecureSocket.setSSLFactories(keystoreInput, "comp90015", truststoreInput);
                                    keystoreInput.close();
                                    truststoreInput.close();
                                    //System.setProperty("javax.net.ssl.trustStore",
                                    //getClass().getResource("/clientKeystore/client.jks").getFile());
                                    SSLSocketFactory sslsocketfactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
                                    s = (SSLSocket)sslsocketfactory.createSocket();
                                    s.connect(socketaddr, 5000);
                                } else {
                                    s = new Socket();
                                    s.connect(socketaddr, 5000);
                                }

                                //sockets.put(h.getHostname(), s);
                                DataOutputStream out = new DataOutputStream(s.getOutputStream());
                                for (JSONObject j : relayList.getList()) {
                                    if (!sockets.containsKey(j.getString("actualID") + h.getHostname())) {
                                        sockets.put(j.getString("actualID") + h.getHostname(), s);
                                        Resource re = (Resource) j.get("resourceTemplate");
                                        JSONObject send = new JSONObject("{}");

                                        sockets.put(j.getString("actualID") + h.getHostname(), s);

                                        send.put("command", "SUBSCRIBE");
                                        send.put("relay", false);
                                        JSONObject r = new JSONObject("{}");
                                        r.put("name", re.getName());
                                        r.put("tags", re.getTags());
                                        r.put("description", re.getDescription());
                                        r.put("uri", re.getUri());
                                        r.put("channel", re.getChannel());
                                        r.put("owner", re.getOwner());
                                        String ez = null;
                                        r.put("ezserver", ez);
                                        send.put("resourceTemplate", r);
                                        send.put("id", j.getString("actualID"));


                                        out.writeUTF(send.toString());
                                        out.flush();
                                    }
                                }
                            }
                        }


                        while (serverAddList.getServerList().size() > 0) {
                            Host h = serverAddList.getServerList().get(0);
                            Socket s = null ;
                            SocketAddress socketaddr = new InetSocketAddress(h.getHostname(), h.getPort());
                            if (secure) {
                                SSLSocketFactory sslsocketfactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
                                s = (SSLSocket)sslsocketfactory.createSocket();
                                s.connect(socketaddr, 5000);
                            } else {
                                s = new Socket();
                                s.connect(socketaddr, 5000);
                            }
                            //Socket s = new Socket(h.getHostname(), h.getPort());
                            // sockets.put(h.getHostname(), s);
                            DataOutputStream out = new DataOutputStream(s.getOutputStream());
                            for (JSONObject j : relayList.getList()) {
                                Resource re = (Resource) j.get("resourceTemplate");
                                JSONObject send = new JSONObject("{}");

                                sockets.put(j.getString("actualID") + h.getHostname(),s);

                                send.put("command", "SUBSCRIBE");
                                send.put("relay", false);
                                JSONObject r = new JSONObject("{}");
                                r.put("name", re.getName());
                                r.put("tags", re.getTags());
                                r.put("description", re.getDescription());
                                r.put("uri", re.getUri());
                                r.put("channel", re.getChannel());
                                r.put("owner", re.getOwner());
                                String ez = null;
                                r.put("ezserver", ez);
                                send.put("resourceTemplate", r);
                                send.put("id", j.getString("actualID"));


                                out.writeUTF(send.toString());
                                out.flush();
                            }
                            System.out.println(serverAddList.getServerList().size());
                            serverAddList.delete(h);
                            ifUpdate = true;
                        }
                        /**
                         * search the serverDeleteList, if its size is greater than one. If so, find the socket in the HashMap,
                         * close the socket and delete it from the HashMap
                         */
                        if(serverDeleteList.getServerList() != null){
                            while (serverDeleteList.getServerList().size() > 0) {
                                Host h = serverDeleteList.getServerList().get(0);

                                for (JSONObject j : relayList.getList()) {

                                    Socket s = sockets.get(j.getString("actualID") + h.getHostname());
                                    s.close();
                                    sockets.remove(j.getString("actualID") + h.getHostname());

                                }
                                serverDeleteList.delete(h);
                                ifUpdate = true;
                            }
                        }


                        if (ifUpdate) {
                            continue;
                        }


                        /**
                         * search the subscribe List, put records with the relay field true but not in the relay list into the relay list
                         */
                        if( subscribeList.getList()  != null){
                            for (JSONObject subscribeRecord : subscribeList.getList()) {
                                if (subscribeRecord.getBoolean("relay") == true && !relayList.contain(subscribeRecord)) {
                                    relayList.add(subscribeRecord);

                                    Resource re = (Resource) subscribeRecord.get("resourceTemplate");
                                    JSONObject send = new JSONObject("{}");


                                    send.put("command", "SUBSCRIBE");
                                    send.put("relay", false);
                                    JSONObject r = new JSONObject("{}");
                                    r.put("name", re.getName());
                                    r.put("tags", re.getTags());
                                    r.put("description", re.getDescription());
                                    r.put("uri", re.getUri());
                                    r.put("channel", re.getChannel());
                                    r.put("owner", re.getOwner());
                                    String ez = null;
                                    r.put("ezserver", ez);
                                    send.put("resourceTemplate", r);
                                    send.put("id", subscribeRecord.getString("actualID"));


                                    for (variable.Host h : serverList.getServerList()) {

                                        if(sockets.get(subscribeRecord.getString("actualID") + h.getHostname()) != null) {
                                            Socket s = sockets.get(subscribeRecord.getString("actualID") + h.getHostname());
                                            DataOutputStream out = new DataOutputStream(s.getOutputStream());
                                            out.writeUTF(send.toString());
                                            out.flush();
                                        }


                                    }


                                }
                            }
                        }


                        /**
                         * search the relay list, find the records in relay list but not in subscibe list, which means this has been
                         * unsubscribed, so we need to remove it from the relay list and send unsubscribe to all the servers
                         */
                        for (JSONObject relayRecord : relayList.getList()) {
                            if (!subscribeList.contain(relayRecord)) {
                                //The relayRecord should be unsubscribed
                                JSONObject send = new JSONObject("{}");
                                send.put("command", "UNSUBSCRIBE");
                                send.put("id", relayRecord.getString("actualID"));

                                for (variable.Host h : serverList.getServerList()) {
                                    Socket s = sockets.get(relayRecord.getString("actualID") + h.getHostname());
                                    DataOutputStream out = new DataOutputStream(s.getOutputStream());
                                    out.writeUTF(send.toString());
                                    out.flush();
                                }
                            }
                        }

                        for (variable.Host h : serverList.getServerList()) {

                            for (JSONObject relayRecord : relayList.getList()) {

                                Socket socket = sockets.get(relayRecord.getString("actualID") + h.getHostname());
                                if(socket != null){
                                    DataInputStream in = new DataInputStream(socket.getInputStream());

                                    String s = null;
                                    if ( ( s = in.readUTF() )!= null) {

                                        /**
                                         * need to check if this is really a resource
                                         */

                                        //String s = in.readUTF();



                                        JsonElement root = new JsonParser().parse(s);
                                        if (!(root.getAsJsonObject().has("response") || root.getAsJsonObject().has("resultSize"))) {
                                            Resource resource = new Resource("", "", new ArrayList<String>(), "", "", "", "");
                                            Gson gson = new Gson();
                                            resource = gson.fromJson(root.getAsJsonObject(), Resource.class);
                                            // newResourceList.add(r);
                                            readyToSend.add(relayRecord.getString("userID"), relayRecord.getString("actualID"), false, resource);

                                        }

                                    }

                                }


                            }

                        }


                    } catch (IOException e) {
                    }
                    catch (Exception e) {
                        //throw exception
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }



}