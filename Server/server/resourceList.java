package server;

import java.util.ArrayList;

public class resourceList {

	private Resource resourceTemplate ;
	private ArrayList<Resource> resourceList;
	
	
	public synchronized void add(Resource r){
		
	}
	
	public synchronized void delete(Resource r){
		
	}
	
	public Boolean equals(Resource r){
		return true;
	}
	
	public void initialResourceList(){
		ArrayList<Resource> resourceList = new ArrayList<Resource>();
		setResourceList(resourceList);
	}
	
	public Resource getResourceTemplate() {
		return resourceTemplate;
	}
	public void setResourceTemplate(Resource resourceTemplate) {
		this.resourceTemplate = resourceTemplate;
	}
	public ArrayList<Resource> getResourceList() {
		return resourceList;
	}
	public void setResourceList(ArrayList<Resource> resourceList) {
		this.resourceList = resourceList;
	}
	
}
