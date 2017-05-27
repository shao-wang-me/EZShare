package server;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.json.JSONObject;
import support.Debug;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.logging.Logger;
import variable.subscribeList;

/**
 * Created by qiuyankun on 27/5/17.
 */
public class unsubThread implements Runnable {
    private boolean debug;
    private Logger log;
    private DataOutputStream out;
    private DataInputStream in;
    private String clientID;
    private String actualID;
    private subscribeList subscribeList;

    public unsubThread(boolean debug, Logger log, DataOutputStream out, DataInputStream in, String clientID, String actualID, subscribeList subscribeList){

        this.debug = debug;
        this.log = log;
        this.out = out;
        this.in = in;
        this.clientID = clientID;
        this.actualID = actualID;
        this.subscribeList = subscribeList;
    }


    public void run() {
        try {
            Thread.sleep(1000);

            while(true) {


                    String input = null;

                    if ((input = in.readUTF()) != null) {

                       //Thread.sleep(1000);
                        //nput = in.readUTF();
                        JsonElement jsonEle = new JsonParser().parse(input);
                        Debug.printDebug('r', input, debug, log);
                        // Gson gs = new Gson();
                        if (jsonEle.getAsJsonObject().has("command")) {
                            String command = jsonEle.getAsJsonObject().get("command").getAsString();
                            if (command.equals("UNSUBSCRIBE")) {

                                String userID = jsonEle.getAsJsonObject().get("id").getAsString();
                                actualID = userID + clientID;
                                subscribeList.remove(actualID);
                                break;

                            }
                        }
                    }

            }
            Thread.currentThread().interrupt();
            return;
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            return;
        }
    }
}
