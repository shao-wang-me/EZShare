package command;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import org.json.JSONObject;
import server.*;
import support.Debug;
import variable.Host;
import variable.resourceList;
import variable.serverList;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Created by xutianyu on 4/26/17.
 */
public class Exchange {

    public static void exchange(JsonElement root, DataOutputStream out, resourceList resourceList,
                                serverList serverList, Host h, boolean debug , Logger log){

        ArrayList<String> list = new ArrayList<String>();
        JSONObject reply = new JSONObject();
        Gson gson = new Gson();
        HashMap<Boolean, String> response ;

        try{
            if(root.getAsJsonObject().has("serverList")){
                JsonArray array = root.getAsJsonObject().get("serverList").getAsJsonArray();
                Host[] host = gson.fromJson(array, Host[].class);
                response = Function.exchange(serverList, host);
                //System.out.println("exchange: "+host[0].getHostname());
                if(response.containsKey(true)){
                    reply.put("response", "success");
                }
                else{
                    reply.put("response", "error");
                    reply.put("errorMessage", response.get(false));

                }

            }
            else{
                reply.put("response", "error");
                reply.put("errorMessage", "missing or invalid serverList");
            }
            out.writeUTF(reply.toString());
            Debug.printDebug('s',reply.toString(), debug, log );

        }catch(IOException i){

        }catch(JsonSyntaxException j){
            reply.put("response", "error");
            reply.put("errorMessage", "missing or invalid serverList");
            try {
                out.writeUTF(reply.toString());
                Debug.printDebug('s',reply.toString(), debug, log );
            } catch (IOException e) {

            }
        }catch(Exception e){

        }
    }
}
