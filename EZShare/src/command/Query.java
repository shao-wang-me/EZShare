package command;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.json.JSONObject;
import server.Function;
import variable.Host;
import variable.Resource;
import variable.resourceList;
import variable.serverList;
import support.Debug;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Created by xutianyu on 4/25/17.
 * @ param JsonElement root, DataOutputStream out, resourceList resourceList,
        serverList serverList, Host h, boolean debug , Logger log
 * query operation
 *
 */
public class Query
{

    public static void query(JsonElement root, DataOutputStream out, resourceList resourceList,
                            serverList serverList, Host h, boolean debug , Logger log){
        ArrayList<String> list = new ArrayList<String>();
        Resource resource = new Resource("", "", list, "", "", "", "");
        JSONObject reply = new JSONObject();
        Gson gson = new Gson();
        int resultSize = 0;
        HashMap<Boolean, variable.resourceList> result ;
        Boolean relay = false;
        resourceList resultList = new resourceList();
        resultList.initialResourceList();


        try{

            if(root.getAsJsonObject().has("resourceTemplate")){
                //resource valid check
                try{
                    JsonObject object = root.getAsJsonObject().get("resourceTemplate").getAsJsonObject();
                    if(root.getAsJsonObject().has("relay")){
                        relay = root.getAsJsonObject().get("relay").getAsBoolean();
                    }
                    // parse query template
                    resource = gson.fromJson(object, Resource.class);

                    //return local server query result and print
                    result = Function.query(resource,resourceList, h.getHostname(), h.getPort());
                    reply.put("response", "success");
                    out.writeUTF(reply.toString());
                    Debug.printDebug('s',reply.toString() , debug, log);
                    if(result.get(true).getResourceList().size() != 0){
                        for(Resource entry : result.get(true).getResourceList()){
                            String temp = gson.toJson(entry);
                            out.writeUTF(gson.toJson(entry));
                            Debug.printDebug('s',temp , debug, log);
                            resultSize++;
                        }
                    }

                    // if relay = true forward query to other known servers
                    if(relay) {
                        //JSONObject trans = new JSONObject(message);
                        JsonObject trans = new JsonObject();
                        trans = root.getAsJsonObject();
                        trans.remove("relay");
                        trans.addProperty("relay", false);

                        for (Host host : serverList.getServerList()) {
                            resourceList tempList = new resourceList();
                            tempList.initialResourceList();
                            tempList = Forward.forward(trans.toString(), host, debug, log);
                            if (tempList.getResourceList().size() != 0) {
                               for (Resource r : tempList.getResourceList()) {
                                    resultList.add(r);
                                }
                            }
                        }

                    }
                    // print query results from other servers
                    resultSize += resultList.getResourceList().size();
                    if(resultList.getResourceList().size() != 0){
                        for(Resource entry : resultList.getResourceList()){
                            String temp = gson.toJson(entry);
                            out.writeUTF(gson.toJson(entry));
                            Debug.printDebug('s',temp , debug, log);
                        }
                    }

                    //print result size
                    out.writeUTF(new JSONObject().put("resultSize", resultSize).toString());
                    Debug.printDebug('s',new JSONObject().put("resultSize",resultSize).toString(), debug, log);

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
                reply.put("errorMessage", "missing ResourceTemplate");
                out.writeUTF(reply.toString());
                Debug.printDebug('s',reply.toString(), debug, log );
            }

        }catch(IOException i){

        }catch(JsonSyntaxException j){
            reply.put("response", "error");
            reply.put("errorMessage", "missing ResourceTemplate");
            try {
                out.writeUTF(reply.toString());
                Debug.printDebug('s',reply.toString(), debug, log );
            } catch (IOException e) {

            }
        }catch(Exception e){
        }


    }



}
