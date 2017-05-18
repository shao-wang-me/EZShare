package server;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
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

    public relayThread(resourceList newResourceList, Boolean debug, subscribeList subscribeList, serverList serverList, serverList serverDeleteList, serverList serverAddList) {
        this.newResourceList = newResourceList;
        this.debug = debug;
        this.subscribeList = subscribeList;
        this.serverList = serverList;
        this.serverDeleteList = serverDeleteList;
        this.serverAddList = serverAddList;
        this.relayList = new subscribeList();
        sockets = new HashMap<String,Socket>();
    }

    public void run() {
        try {
            Thread.sleep(1000);

            while (true) {
                try {
                    boolean ifUpdate = false;
                    /**
                     * search the serverAddlist if its size is greater than one. If so, create socket connecting to the new
                     * server and add to the HashMap, send all the subscriptions in relayList, delete the record from serverAddList
                     */

                    while (serverAddList.getServerList().size() > 0) {
                        Host h = serverAddList.getServerList().get(0);
                        Socket s = new Socket(h.getHostname(), h.getPort());
                        sockets.put(h.getHostname(), s);
                        DataOutputStream out = new DataOutputStream(s.getOutputStream());
                        for (JSONObject j : relayList.getList()) {
                            Resource re = (Resource) j.get("resourceTemplate");
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
                            send.put("id", j.getString("actualID"));


                            out.writeUTF(send.toString());
                            out.flush();
                        }
                        serverAddList.delete(h);
                        ifUpdate = true;
                    }
                    /**
                     * search the serverDeleteList, if its size is greater than one. If so, find the socket in the HashMap,
                     * close the socket and delete it from the HashMap
                     */
                    while (serverDeleteList.getServerList().size() > 0) {
                        Host h = serverDeleteList.getServerList().get(0);
                        Socket s = sockets.get(h.getHostname());
                        s.close();
                        sockets.remove(h.getHostname());
                        serverDeleteList.delete(h);
                        ifUpdate = true;
                    }


                    if (ifUpdate) {
                        continue;
                    }


                    /**
                     * search the subscribe List, put records with the relay field true but not in the relay list into the relay list
                     */
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

                                DataOutputStream out = new DataOutputStream(sockets.get(h.getHostname()).getOutputStream());

                                out.writeUTF(send.toString());
                                out.flush();


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
                                Socket s = sockets.get(h.getHostname());
                                DataOutput out = new DataOutputStream(s.getOutputStream());
                                out.writeUTF(send.toString());
                            }
                        }
                    }

                    for (variable.Host h : serverList.getServerList()) {
                        DataInputStream in = new DataInputStream(sockets.get(h.getHostname()).getInputStream());

                        if (in.available() > 0) {

                            JsonElement root = new JsonParser().parse(in.readUTF());
                            Resource resource = new Resource("", "", new ArrayList<String>(), "", "", "", "");
                            Gson gson = new Gson();
                            Resource r = gson.fromJson(root.getAsJsonObject(), Resource.class);
                            newResourceList.add(r);


                        }

                    }


                } catch (IOException e) {
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }



}
