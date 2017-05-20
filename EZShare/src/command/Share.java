package command;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.json.JSONObject;
import server.*;
import support.Debug;
import variable.Host;
import variable.Resource;
import variable.resourceList;
import variable.serverList;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Created by xutianyu on 4/26/17.
 * @ JsonElement root, DataOutputStream out, resourceList resourceList,
    serverList serverList, Host h, boolean debug , Logger log, String secret
 * share operation
 */
public class Share {

    public static void share(JsonElement root, DataOutputStream out, resourceList resourceList,
                             resourceList newResourceList, serverList serverList, Host h, boolean debug,
                             Logger log, String secret){
        Resource resource;
        JSONObject reply = new JSONObject();
        Gson gson = new Gson();
        HashMap<Boolean, String> response ;
        try{
            if(root.getAsJsonObject().has("resource") && root.getAsJsonObject().has("secret")){
                String str = root.getAsJsonObject().get("secret").getAsString();

                // check secret
                if(str.equals(secret)){
                    try{
                        JsonObject object = root.getAsJsonObject().get("resource").getAsJsonObject();
                        resource = gson.fromJson(object, Resource.class);
                        response = Function.share(resource, resourceList, newResourceList);

                        // share result check & print
                        if(response.containsKey(true)){
                            reply.put("response", "success");
                        }
                        else{
                            reply.put("response", "error");
                            reply.put("errorMessage", response.get(false));
                        }
                    }catch(JsonSyntaxException j){
                        // json parse error
                        reply.put("response", "error");
                        reply.put("errorMessage", "missing resource and/or secret");
                    }
                }
                else{
                    // secret error
                    reply.put("response", "error");
                    reply.put("errorMessage", "incorrect secret");
                }
            }
            else{
                reply.put("response", "error");
                reply.put("errorMessage", "missing resource and/or secret");
            }
            out.writeUTF(reply.toString());
            Debug.printDebug('s',reply.toString(), debug, log );
        }catch(IOException i){

        }catch(JsonSyntaxException j){
            // json parse error
            reply.put("response", "error");
            reply.put("errorMessage", "missing resource and\\/or secret");
            try {
                out.writeUTF(reply.toString());
            } catch (IOException e) {

            }
            finally{
                Debug.printDebug('s',reply.toString(), debug, log );

            }
        }catch(Exception e){
        }
    }
}
