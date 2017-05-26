package client;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.util.logging.Logger;
import java.util.Scanner;

/**
 * Created by qiuyankun on 27/5/17.
 */
public class unsubscribeThread implements Runnable{
    private DataOutputStream output;
    private JSONObject subscribe;
    private boolean ifDebug;
    private Logger log;
    private Scanner sc;
    private boolean stop;

    public unsubscribeThread(DataOutputStream out, JSONObject subscribe, boolean ifDebug, Logger log, boolean stop){
        this.output = out;
        this.subscribe = subscribe;
        this.ifDebug = ifDebug;
        this.log = log;
        this.sc = new Scanner(System.in);
        this.stop = stop;
    }

    public void run(){
        try {
            Thread.sleep(100);
            String s = "";
            while(true) {
                s = sc.nextLine();
                if (s != null && !s.equals("\n")) {
                    JSONObject sentJSON = new JSONObject("{}");
                    String id = subscribe.getString("id");
                    sentJSON.put("command", "UNSUBSCRIBE");
                    sentJSON.put("id", id);
                    if (ifDebug) {
                        log.info("SENT:" + sentJSON.toString());
                    }
                    output.writeUTF(sentJSON.toString());
                    output.flush();
                    break;
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
