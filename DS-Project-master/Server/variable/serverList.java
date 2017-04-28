package variable;

import java.util.ArrayList;

public class serverList {
	
	private Host hostTemplate ;
	private ArrayList<Host> serverList;
	
	public void initialserverList(){
		ArrayList<Host> serverList = new ArrayList<Host>();
		setServerList(serverList);
	}
	
	public synchronized void add(Host h){
		if(!serverList.contains(h)){
			getServerList().add(h);
		}
	}
	
	public synchronized void delete(Host h){
		int index = 0 ;
		for(Host o : serverList){
			index++;
			if(o.equals(h)){
				getServerList().remove(index);
			}
		}
	}
	
	public Host getHostTemplate() {
		return hostTemplate;
	}
	public void setHostTemplate(Host hostTemplate) {
		this.hostTemplate = hostTemplate;
	}
	public ArrayList<Host> getServerList() {
		return serverList;
	}
	public void setServerList(ArrayList<Host> serverList) {
		this.serverList = serverList;
	}
	
	
}
