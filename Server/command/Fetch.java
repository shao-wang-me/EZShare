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
                    resource = gson.fromJson(object, Resource.class);
                }catch(JsonSyntaxException j){
                    reply.put("response", "error");
                    reply.put("errorMessage", "invalid resourceTemplate");
                    out.writeUTF(reply.toString());
                    Debug.printDebug('s',reply.toString(),debug, log );
                }						//System.out.println("fetch: "+message);
                long fileSize = 0;
                response = Function.fetch(resource, resourceList);
                System.out.println(resource.getKey()+" "+response);
                if(response.containsKey(true)){
                    out.writeUTF(new JSONObject().put("response", "success").toString());
                    File f = new File(resource.getURI().getPath());
                    fileSize = f.length();

                    System.out.println(f);
                    String str = gson.toJson(object);
                    JSONObject temp = new JSONObject(str);
                    reply = temp.put("resourceSize", fileSize);
                    out.writeUTF(reply.toString());
                    Debug.printDebug('s',reply.toString(), debug,log);

                    System.out.println("after: "+reply.toString());

                    //read data from local disk
                    DataInputStream input = new DataInputStream
                            (new BufferedInputStream(new FileInputStream(f)));
                    int bufferSize = (int)fileSize;
                    byte[] buf = new byte[bufferSize];
                    int num =0;
                    System.out.println(bufferSize);
                    //input.read(buf);
                    //out.write(buf);
                    //num=input.read(buf);
                    while((num=input.read(buf))!=-1){
                        System.out.println("num: "+num);
                        out.write(buf, 0, num);
                    }
                    out.flush();
                    System.out.println("文件发送成功！");
                    input.close();
                    out.writeUTF(new JSONObject().put("resultSize", 1).toString());
                }
                else{
                    reply.put("response", "error");
                    reply.put("errorMessage", "invalid resourceTemplate");
                    out.writeUTF(reply.toString());
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
            reply.put("errorMessage", "missing resourcetemplate");
            try {
                out.writeUTF(reply.toString());
            } catch (IOException e) {

            }finally{
                Debug.printDebug('s',reply.toString(), debug, log );

            }

        }catch(Exception e){

        }
    }
}
