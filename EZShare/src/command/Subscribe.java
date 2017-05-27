package command;

import com.google.gson.*;
import org.json.JSONObject;
import server.Function;
import server.unsubThread;
import support.Debug;
import variable.Host;
import variable.Resource;
import variable.resourceList;
import variable.serverList;
import variable.subscribeList;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.ArrayList;

/**
 * Created by qiuyankun on 15/5/17.
 */
public class Subscribe {

    public static void subscribe(JsonElement root, DataOutputStream out, DataInputStream in, String clientID, resourceList resourceList,
                                 subscribeList subscribeList, subscribeList readyToSend, serverList serverList, Host h, boolean debug , Logger log) {
        ArrayList<String> tags = new ArrayList<String>();
        Resource resource = new Resource("", "", tags, "", "", "", "");
        String userID = null;
        String actualID = null;
        Gson gson = new Gson();
        JSONObject reply = new JSONObject();
        Boolean relay = false;

        try{

            if(root.getAsJsonObject().has("resourceTemplate")){
                //resource valid check
                try{
                    JsonObject object = root.getAsJsonObject().get("resourceTemplate").getAsJsonObject();
                    if(root.getAsJsonObject().has("relay")){
                        relay = root.getAsJsonObject().get("relay").getAsBoolean();
                    }
                    /**
                     * This block of code can add subscription record to the
                     * subscribe list, and send the first response to the client
                     * to indicate that the command is successfully received
                     *
                     */
                    resource = gson.fromJson(object, Resource.class);
                    userID = root.getAsJsonObject().get("id").getAsString();
                    actualID = userID + clientID;

                    subscribeList.add(userID, actualID, relay, resource);


                    reply.put("response","success");
                    reply.put("id",userID);
                    out.writeUTF(reply.toString());
                    out.flush();
                    if (debug) {
                        Debug.printDebug('s', reply.toString(), debug, log);
                    }

                    int resultSize = 0;

                    unsubThread ut = new unsubThread(debug, log, out, in, clientID, actualID,subscribeList);

                    Thread thread = new Thread(ut);
                    thread.start();

                    while(true) {

                        Thread.sleep(1000);

                        if(!thread.isAlive()) {
                            JSONObject rep = new JSONObject("{}");
                            rep.put("resultSize", resultSize);
                            Debug.printDebug('s', rep.toString(), debug, log);
                            out.writeUTF(rep.toString());
                            out.flush();
                        }

                        if (readyToSend.size() > 0) {
                            for (Iterator<JSONObject> iterator = readyToSend.getList().iterator(); iterator.hasNext();) {
                                JSONObject temp = iterator.next();
                                if (temp.getString("actualID").equals(actualID)) {
                                    Resource r = (Resource) temp.get("resourceTemplate");
                                    JSONObject re = new JSONObject();
                                    re.put("channel", r.getChannel());
                                    re.put("owner", r.getOwner().isEmpty()?"":"*");
                                    re.put("name", r.getName());
                                    re.put("uri", r.getUri());
                                    re.put("description", r.getDescription());
                                    re.put("tags", r.getTags());
                                    String ezserver = r.getEzServer();
                                    if (ezserver == null) {
                                        re.put("ezserver", h.getHostname() + ":" + h.getPort());
                                    } else {
                                        re.put("ezserver",ezserver);
                                    }
                                    Debug.printDebug('s', re.toString(), debug ,log);
                                    out.writeUTF(re.toString());
                                    resultSize ++;
                                    out.flush();
                                    iterator.remove();
                                }
                            }
                        }
                    }
                }catch(JsonSyntaxException j){
                    // Json parse error in resource template
                    reply.put("response", "error");
                    reply.put("errorMessage", "invalid resourceTemplate");
                    out.writeUTF(reply.toString());
                    Debug.printDebug('s', reply.toString(), debug ,log);
                }
            }
            else{
                // resourceTemplate missing
                reply.put("response", "error");
                reply.put("errorMessage", "missing resourceTemplate");
                out.writeUTF(reply.toString());
                Debug.printDebug('s',reply.toString(), debug, log );
            }

        }catch(IOException i){

        }catch(JsonSyntaxException j){
            reply.put("response", "error");
            reply.put("errorMessage", "missing resourceTemplate");
            try {
                out.writeUTF(reply.toString());
                Debug.printDebug('s',reply.toString(), debug, log );
            } catch (IOException e) {
            }
        }catch(Exception e){
        }
    }
}