package support;

import java.util.logging.Logger;

/**
 * Created by xutianyu on 4/26/17.
 * debug class
 * print debug info
 *
 */
public class Debug {

    public static void printDebug (char op, String str, Boolean debug, Logger log){
        if(debug){

            if(op=='s'){

                log.info("SENT:"+str);
            }
            else{
                log.info("RECEIVED:"+str);
            }

        }
    }}
