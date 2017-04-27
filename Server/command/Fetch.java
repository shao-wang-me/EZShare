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

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Created by xutianyu on 4/26/17.
 */
public class Fetch {

    public static void fetch(JsonElement root, DataOutputStream out, resourceList resourceList,
                             serverList serverList, Host h, boolean debug , Logger log){

        ArrayList<String> list = new ArrayList<String>();
        Resource resource = new Resource("", "", list, "", "", "", "");
        JSONObject reply = new JSONObject();
        Gson gson = new Gson();
        HashMap<Boolean, String> response ;

        try{
            JsonObject object = root.getAsJsonObject();

            if(object.has("resourceTemplate")){
                object = object.get("resourceTemplate").getAsJsonObject();
                try{

                    Resource template = gson.fromJson(object, Resource.class);
                    if(template != null){
                        resource.setChannel(template.getChannel());
                        resource.setUri(template.getUri());
                    }

                    long fileSize = 0;
                    response = Function.fetch(resource, resourceList);
                    if(response.containsKey(true) && response.get(true) != null ){
                        out.writeUTF(new JSONObject().put("response", "success").toString());
                        File f = new File(resource.getURI().getPath());
                        fileSize = f.length();

                        String str = gson.toJson(object);
                        JSONObject temp = new JSONObject(str);
                        reply = temp.put("resourceSize", fileSize);
                        out.writeUTF(reply.toString());
                        Debug.printDebug('s',reply.toString(), debug,log);

                        //read data from local disk
                        DataInputStream input = new DataInputStream
                                (new BufferedInputStream(new FileInputStream(f)));
                        int bufferSize = (int)fileSize;
                        byte[] buf = new byte[bufferSize];
                        int num =0;
                        //input.read(buf);
                        //out.write(buf);
                        //num=input.read(buf);
                        while((num=input.read(buf))!=-1){
                            out.write(buf, 0, num);
                        }
                        out.flush();
                        input.close();
                        Debug.printDebug('s',"File Transfer Success".toString(), debug, log );
                        out.writeUTF(new JSONObject().put("resultSize", 1).toString());
                        Debug.printDebug('s',new JSONObject().put("resultSize", 1).toString(), debug, log );

                    }
                    else{
                        //reply.put("response", "error");
                        //reply.put("errorMessage", "invalid resourceTemplate");
                        //out.writeUTF(reply.toString());
                        //Debug.printDebug('s',reply.toString(), debug, log );
                        reply.put("response", "success");
                        out.writeUTF(reply.toString());
                        Debug.printDebug('s',reply.toString(), debug, log );
                        out.writeUTF(new JSONObject().put("resultSize", 0).toString());
                        Debug.printDebug('s',new JSONObject().put("resultSize", 0).toString(), debug, log );

                    }
                }catch(JsonSyntaxException j){
                    reply.put("response", "error");
                    reply.put("errorMessage", "invalid resourceTemplate");
                }

            }
            else{
                reply.put("response", "error");
                reply.put("errorMessage", "missing resourceTemplate");
                out.writeUTF(reply.toString());
                Debug.printDebug('s',reply.toString(), debug, log );

            }
            // return file size

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
