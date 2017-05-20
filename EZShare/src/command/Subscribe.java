package command;

import com.google.gson.*;
import org.json.JSONObject;
import server.Function;
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
                    Debug.printDebug('s',reply.toString() , debug, log);
                    int resultSize = 0;

                    while(true) {

                        Thread.sleep(1000);

                        if (in.available() > 0) {
                            JsonElement jsonEle = new JsonParser().parse(in.readUTF());
                            // Gson gs = new Gson();
                            if (jsonEle.getAsJsonObject().has("command")) {
                                String command = jsonEle.getAsJsonObject().get("command").getAsString();
                                if (command.equals("SUBSCRIBE")) {
                                    Resource r = new Resource("", "", tags, "", "", "", "");
                                    object = jsonEle.getAsJsonObject().get("resourceTemplate").getAsJsonObject();
                                    r = gson.fromJson(object, Resource.class);
                                    userID = jsonEle.getAsJsonObject().get("id").getAsString();
                                    actualID = userID + clientID;
                                    subscribeList.add(userID,actualID,false,r);
                                    JSONObject re = new JSONObject("{}");
                                    re.put("response","success");
                                    re.put("id",userID);
                                    out.writeUTF(re.toString());
                                    out.flush();



                                } else if (command.equals("UNSUBSCRIBE")) {
                                    userID = jsonEle.getAsJsonObject().get("id").getAsString();
                                    actualID = userID + clientID;
                                    JSONObject rep = new JSONObject("{}");
                                    rep.put("resultSize",resultSize);
                                    out.writeUTF(rep.toString());
                                    out.flush();
                                    subscribeList.remove(actualID);
                                    break;

                                }
                            }
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
                System.out.println("IOException");
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}