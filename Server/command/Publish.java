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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Created by xutianyu on 4/26/17.
 * @ param JsonElement root, DataOutputStream out, resourceList resourceList,
     serverList serverList, Host h, boolean debug , Logger log
 * publish operation
 *
 */
public class Publish {

    public static void publish(JsonElement root, DataOutputStream out, resourceList resourceList,
                               serverList serverList, Host h, boolean debug , Logger log){

        ArrayList<String> list = new ArrayList<String>();
        Resource resource = new Resource("", "", list, "", "", "", "");
        JSONObject reply = new JSONObject();
        Gson gson = new Gson();
        HashMap<Boolean, String> response = new HashMap<Boolean, String>();

        try{

            if(root.getAsJsonObject().has("resource")){
                try{
                    JsonObject object = root.getAsJsonObject().get("resource").getAsJsonObject();
                    resource = gson.fromJson(object, Resource.class);
                    response = Function.publish(resource, resourceList);
                    if(response.containsKey(true)){
                        reply.put("response", "success");
                    }
                    else{
                        reply.put("response", "error");
                        reply.put("errorMessage", response.get(false));
                        //System.out.println(reply.toString());
                    }
                }catch(JsonSyntaxException j){
                    reply.put("response", "error");
                    reply.put("errorMessage", "missing resource");
                }
            }
            else{
                reply.put("response", "error");
                reply.put("errorMessage", "missing resource");
            }
            out.writeUTF(reply.toString());
            Debug.printDebug('s',reply.toString(), debug,log );


        }catch(IOException i){

        }catch(JsonSyntaxException j){
            reply.put("response", "error");
            reply.put("errorMessage", "missing resource");
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
