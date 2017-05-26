package variable;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by xutianyu on 4/25/17.
 * server list object
 * used for store servers & keep concurrency
 */


public class serverList {

	private Host hostTemplate ;
	private volatile ArrayList<Host> serverList;

	public void initialserverList(){
		ArrayList<Host> serverList = new ArrayList<Host>();
		setServerList(serverList);
	}

	public synchronized void add(Host h){
		if(!this.contains(h)){
			getServerList().add(h);
		}
	}

	public synchronized void delete(Host h){
		int index = 0 ;
		Iterator<Host> iter = this.serverList.iterator();
		while(iter.hasNext()){
			Host o = iter.next();
			if(h.getHostname().equals(o.getHostname()) &&
					h.getPort()==o.getPort()){
				iter.remove();
			}
		}
		/*for(Host o : serverList){
			if(o.equals(h)){
				getServerList().remove(index);
			}
			index++;
		}*/
	}

	public boolean contains(Host h){
		boolean flag = false;
		for(Host host : serverList){
			if(host.getHostname().equals(h.getHostname())
					&& host.getPort() == h.getPort()){
				flag = true;
			}
		}
		return flag;
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