package server;

import variable.Host;
import variable.Resource;
import variable.resourceList;
import variable.serverList;

import java.io.File;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.HashMap;

public class Function {

	public static HashMap<Boolean, String> publish(Resource resource, resourceList resourceList) throws URISyntaxException {
		HashMap<Boolean, String> toReturn = new HashMap<Boolean, String>();
		if (!resource.isValid()) {
			toReturn.put(false, "invalid resource");
		} else {
			/**
			 * Cannot publish resource:
			 * 1. The resource is a file.
			 * 2. Same channel, same URI, different owner.
			 * 3. URI missing 
			 */
			if (resource.isFile() || 
					(resourceList.contains(resource) && 
							!resourceList.getSameResource(resource).getOwner().equals(resource.getOwner())
					)
				) {
				toReturn.put(false, "cannot publish resource");
			} else {
				/**Now we should be able to add or update the resource.*/
				resourceList.add(resource);
				toReturn.put(true, "success");
			}
		}
		return toReturn;
	}


	public static HashMap<Boolean, String> remove(Resource resource, resourceList resourceList) {
		HashMap<Boolean, String> toReturn = new HashMap<Boolean, String>();
		/**
		 * Invalid resource:
		 * 1. Missing URI;
		 * 2. Invalid URI;
		 */
		if (resource.isValid() || (!resource.isValid() && resource.getOwner().equals("*"))) {
			if (resourceList.contains(resource) && resourceList.getSameResource(resource).getOwner().equals(resource.getOwner())) {
				resourceList.delete(resource);//The remove operation.
				toReturn.put(true, "success");
			} else {
				/**Cannot remove resource: the resource did not exist.*/
				toReturn.put(false, "cannot remove resource");
			}

		} else {
			toReturn.put(false, "invalid resource");
		}
		return toReturn;
	}


	public static HashMap<Boolean, String> share(Resource resource, resourceList resourceList) throws URISyntaxException {
		HashMap<Boolean, String> toReturn = new HashMap<Boolean, String>();
		if (!resource.isValid()) {
			toReturn.put(false, "invalid resource");
		} else {
			/**
			 * Cannot publish resource:
			 * 1. The resource is not a file.
			 * 2. Same channel, same URI, different owner.
			 * 3. Not pointing to a file on the local file system.
			 * 4. Cannot be read as a file.
			 */
			if (!resource.isFile() || 
					(resourceList.contains(resource) && 
							!resourceList.getSameResource(resource).getOwner().equals(resource.getOwner()))
					) {
				toReturn.put(false, "cannot share resource");
			} else {
				File file = new File(resource.getURI().getPath());
				if (file.canRead() && !file.isDirectory()) {
					/**Now we should be able to add or update the resource.*/
					resourceList.add(resource);
					toReturn.put(true, "success");
				} else {
					toReturn.put(false, "cannot share resource");
				}				
			}
		}
		return toReturn;
	}
	

	public static HashMap<Boolean, resourceList> query(Resource resource, resourceList resourceList,
			String hostname, int port)	{
		resourceList resourceListFiltered = new resourceList();
		resourceListFiltered.initialResourceList();
		HashMap<Boolean, resourceList> toReturn = new HashMap<Boolean, resourceList>();
		//System.out.println(resourceList.getResourceList().size());
		//boolean match = true;
		for (Resource r: resourceList.getResourceList()) {
			//System.out.println("r: " + r.getUri());
			//System.out.println("resource: " + resource.getUri());
			boolean match = true;
			if (!resource.getChannel().equals(r.getChannel())) {
				match = false;
			}
			if (!resource.getOwner().isEmpty() && !r.getOwner().equals(resource.getOwner())) {
				match = false;
			}
			if (!resource.getTags().isEmpty() && !r.getTags().containsAll(resource.getTags())) {
				match = false;
			}
			if (!resource.getUri().isEmpty() && !r.getUri().equals(resource.getUri())) {
				match = false;
			}
			if (!resource.getDescription().isEmpty() && !resource.getName().isEmpty() && !r.getName().contains(resource.getName()) && !r.getDescription().contains(resource.getDescription())) {
				match = false;
			}
			if (match) {
				Resource temp = new Resource(
						r.getName(), r.getDescription(), r.getTags(),r.getUri(),
						r.getChannel(), "*", hostname+":"+port);
				resourceListFiltered.add(temp);
			}
		}
		toReturn.put(true, resourceListFiltered);
		
		return toReturn;
	}
	
	public static HashMap<Boolean, String> fetch(Resource resource, resourceList resourceList) throws URISyntaxException {
		HashMap<Boolean, String> toReturn = new HashMap<Boolean, String>();
		toReturn.put(true, null);

		for(Resource r: resourceList.getResourceList()){
			if (r.getChannel().equals(resource.getChannel())&& r.getUri().equals(resource.getUri())){
				if (resource.isFile() && resource.uriValid()) {
					toReturn.put(true, resource.getKey());
				} else {
					toReturn.put(true, null);
				}
			}
		}

		return toReturn;
	}
	
	//TODO what is "missing resourceTemplate"
	public static HashMap<Boolean, String> exchange(serverList serverList, Host[] host) {
		HashMap<Boolean, String> toReturn = new HashMap<Boolean, String>();
		for (Host h : host) {
				try {
					boolean flag = true;
					for(Host s : serverList.getServerList()){
						InetAddress ip = InetAddress.getByName(h.getHostname());
						if(s.getHostname().equals(h.getHostname()) && s.getPort()==h.getPort()){
							flag = false;
						}
					}
					if(flag == true){
						serverList.add(h);
					}
				} catch (UnknownHostException e) {
					toReturn.put(false, "invalid server");
					return toReturn;
				}
		}
		toReturn.put(true, "success");
		return toReturn;
	}
	
}

