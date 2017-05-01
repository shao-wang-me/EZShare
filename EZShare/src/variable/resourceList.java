package variable;

import java.util.ArrayList;

public class resourceList {

	private Resource resourceTemplate ;
	private volatile ArrayList<Resource> resourceList;
	
	
	public synchronized void add(Resource r){
		String key = r.getKey();
		Resource temp = null ;
		boolean flag = false;
		for (Resource c : resourceList) {
			if (c.getKey().equals(key)) {
				temp = c;
				flag = true;
			}
		}
		if( flag){
			resourceList.remove(temp);
		}
		resourceList.add(r);
	}
	
	public synchronized void delete(Resource r){
		Resource temp = null ;
		boolean flag = true;
		for (Resource c : resourceList) {
			if (c.getKey().equals(r.getKey())
					&& c.getOwner().equals(r.getOwner())) {
				temp = c;
				flag = true;
			}
		}
		if( flag){
			resourceList.remove(temp);
		}
	}
	
	public boolean contains(Resource r){
		String key = r.getKey();
		boolean contains = false;
		for (Resource c : resourceList) {
			if (c.getKey().equals(key)) {
				contains = true;
			}
		}
		return contains;
	}
	
	public synchronized void replace(Resource r) {
		String key = r.getKey();
		for (Resource c : resourceList) {
			if (c.getKey().equals(key)) {
				resourceList.remove(c);
			}
		}
		resourceList.add(r);
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
