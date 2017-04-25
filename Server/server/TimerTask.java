package server;

import org.json.JSONObject;
import variable.Host;
import variable.resourceList;
import variable.serverList;

public class TimerTask implements Runnable{
	
	private variable.serverList serverList;
	private variable.resourceList resourceList;
	
	public TimerTask(serverList serverList, resourceList resourceList){
		this.resourceList = resourceList;
		this.serverList = serverList;
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
	        serverUpdate s =  new serverUpdate(serverList);
	        update = s.update(h.getHostname(), h.getPort());
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
