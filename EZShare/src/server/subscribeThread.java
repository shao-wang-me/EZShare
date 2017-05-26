package server;

import org.json.JSONObject;
import variable.*;
import variable.Resource;
import variable.resourceList;
import variable.serverList;

import java.util.concurrent.Executors;

/**
 * Created by qiuyankun on 13/5/17.
 */
public class subscribeThread implements Runnable {

    private resourceList newResourceList;
    private Boolean debug;
    private int intervalLimit;
    private subscribeList subscribeList;
    private subscribeList readyToSend;

    public subscribeThread(resourceList newResourceList, Boolean debug,
                           int intervalLimit , subscribeList subscribeList,
                           subscribeList readyToSend) {
        this.newResourceList = newResourceList;
        this.debug = debug;
        this.intervalLimit = intervalLimit;
        this.subscribeList = subscribeList;
        this.readyToSend = readyToSend;

    }
    public void run() {
        try {

            Thread.sleep(intervalLimit * 2);

            for (;;){

                Thread.sleep(1000);

                //synchronized(newResourceList){
                    if (newResourceList.getResourceList().size() > 0) {

                        Resource r = newResourceList.getFirstResource();
                        /**
                         * Comparing the resource in the newResourceList with
                         * the resourceTemplate in the subscribeList, if match,
                         * add the resource and id into the readyToSent list and
                         * remove it from the newResourceList
                         */
                        for (JSONObject temp : subscribeList.getSubList()) {
                            String actualID = temp.getString("actualID");
                            Resource tempRe = (Resource)temp.get("resourceTemplate");
                            boolean match = true;
                            if (!tempRe.getChannel().equals(r.getChannel())) {
                                match = false;
                            }
                            if (!tempRe.getOwner().isEmpty() && !r.getOwner().equals(tempRe.getOwner())) {
                                match = false;
                            }
                            if (!tempRe.getTags().isEmpty() && !r.getTags().containsAll(tempRe.getTags())) {
                                match = false;
                            }
                            if (!tempRe.getUri().isEmpty() && !r.getUri().equals(tempRe.getUri())) {
                                match = false;
                            }


                            if (!((tempRe.getName().isEmpty() || r.getName().contains(tempRe.getName())) &&
                                    (tempRe.getDescription().isEmpty() || r.getDescription().contains(tempRe.getDescription())))) {
                                match = false;
                            }

                            if(match) {
                                readyToSend.add(temp.getString("userID"), actualID, temp.getBoolean("relay"), r);
                            }
                        }
                        if(newResourceList.getResourceList().size() > 0 ){
                            newResourceList.delete(0);
                        }
                  // }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}