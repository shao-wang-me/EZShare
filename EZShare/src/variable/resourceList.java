package variable;

import java.util.ArrayList;
import java.util.Iterator;

public class resourceList {

	private Resource resourceTemplate ;
	private volatile ArrayList<Resource> resourceList;


	public synchronized void add(Resource r){
		String key = r.getKey();
		Resource temp = null ;
		boolean flag = false;
		Iterator<Resource> iter = this.resourceList.iterator();
		while(iter.hasNext()){
			Resource c = iter.next();
			if(c.getKey().equals(r.getKey())
					&& c.getOwner().equals(r.getOwner())){
				iter.remove();
			}
		}
		/*for (Resource c : resourceList) {
			if (c.getKey().equals(key)) {
				temp = c;
				flag = true;
			}
		}
		if( flag){
			resourceList.remove(temp);
		}*/
		resourceList.add(r);
	}

	public synchronized void delete(Resource r){
		Resource temp = null ;
		boolean flag = true;
		Iterator<Resource> iter = this.resourceList.iterator();
		while(iter.hasNext()){
			Resource c = iter.next();
			if(c.getKey().equals(r.getKey())
					&& c.getOwner().equals(r.getOwner())){
				iter.remove();
			}
		}
		/*for (Resource c : resourceList) {
			if (c.getKey().equals(r.getKey())
					&& c.getOwner().equals(r.getOwner())) {
				temp = c;
				flag = true;
			}
		}
		if( flag){
			resourceList.remove(temp);
		}*/
	}

	public synchronized void delete(int index) {
		if(resourceList.size() > index +1){
			resourceList.remove(index);

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

	public int size(){ return this.resourceList.size(); }

	public void initialResourceList(){
		ArrayList<Resource> resourceList = new ArrayList<Resource>();
		setResourceList(resourceList);
	}

	public Resource getFirstResource(){
		return resourceList.get(0);
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