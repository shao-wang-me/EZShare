package server;

import org.json.JSONObject;
import variable.Host;
import variable.resourceList;
import variable.serverList;

import java.util.logging.Logger;

/**
 * Created by xutianyu on 4/25/17.
 * Server list  update  class
 * test a random server from server list every interval
 *
 */

public class TimerTask implements Runnable{
	
	private serverList serverList;
	private resourceList resourceList;
	private boolean debug ;
	private Logger log;
	
	public TimerTask(serverList serverList, resourceList resourceList, boolean debug, Logger log){
		this.resourceList = resourceList;
		this.serverList = serverList;
		this.debug = debug;
		this.log = log;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
        //random choose a server
		try{
	        int index = (int) (Math.random()*serverList.getServerList().size());
	        Host h = serverList.getServerList().get(index);
	        //send exchange command to it 
	        JSONObject update = new JSONObject();
	        update = serverUpdate.update(serverList, h.getHostname(), h.getPort(), debug, log);
	        if(update.getString("response").equals("error")){
	        	serverList.delete(h);
	        }
		}catch(Exception e){
			
		}
        //return ressult
	}

	public resourceList getResourceList() {
		return resourceList;
	}

	public void setResourceList(resourceList resourceList) {
		this.resourceList = resourceList;
	}
	

}
