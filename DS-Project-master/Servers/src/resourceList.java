package server;

import java.util.ArrayList;

public class resourceList {

	private Resource resourceTemplate ;
	private ArrayList<Resource> resourceList;
	
	
	public synchronized void add(Resource r){
		if (!contains(r)) {
			resourceList.add(r);
		} else {
			resourceList.remove(getSameResource(r));
			resourceList.add(r);
		}
	}
	
	public synchronized void delete(Resource r){
		resourceList.remove(getSameResource(r));
	}
	
	public Boolean contains(Resource r){
		String key = r.getKey();
		boolean contains = false;
		for (Resource c : resourceList) {
			if (c.getKey().equals(key)) {
				contains = true;
			}
		}
		return contains;
	}
	
	public Resource getSameResource(Resource r) {
		String key = r.getKey();
		for (Resource c : resourceList) {
			if (c.getKey().equals(key)) {
				return c;
			}
		}
		return null;
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
