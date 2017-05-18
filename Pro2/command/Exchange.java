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
 * @ param JsonElement root, DataOutputStream out, resourceList resourceList,
 *         serverList serverList, Host h, boolean debug , Logger log
 * @ exchange operation
 *
 */
public class Exchange {

    public static void exchange(JsonElement root, DataOutputStream out, resourceList resourceList,
                                serverList serverList, serverList serverAddList, Host h, boolean debug , Logger log){

        ArrayList<String> list = new ArrayList<String>();
        JSONObject reply = new JSONObject();
        Gson gson = new Gson();
        HashMap<Boolean, String> response = new HashMap<Boolean, String>();

        // exchange received server list with own server list
        try{
            if(root.getAsJsonObject().has("serverList")){
                JsonArray array = root.getAsJsonObject().get("serverList").getAsJsonArray();
                Host[] host = gson.fromJson(array, Host[].class);
                if(host.length != 0){
                    response = Function.exchange(serverList, serverAddList, host, h);
                }
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
            //parse json error
            reply.put("response", "error");
            reply.put("errorMessage", "missing or invalid serverList");
            try {
                out.writeUTF(reply.toString());
                Debug.printDebug('s',reply.toString(), debug, log );
            } catch (IOException e) {

            }
        }catch(IllegalStateException i){

            // json array parse error
            reply.put("response", "error");
            reply.put("errorMessage", "missing or invalid serverList");
            try {
                out.writeUTF(reply.toString());
                Debug.printDebug('s',reply.toString(), debug, log );
            } catch (IOException e) {

            }
        }
        catch(Exception e){
            System.out.println("Exception");
            //System.out.println(e.getMessage());
        }
    }
}
