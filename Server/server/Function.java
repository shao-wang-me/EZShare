package server;

import java.io.File;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Function {

	//Map<key(channel, URI), Resource>

	public static HashMap<Boolean, String> publish(Resource resource, resourceList resourceList) throws URISyntaxException {
		HashMap<Boolean, String> toReturn = new HashMap<Boolean, String>();
		if (!resource.isValid()) {
			toReturn.put(false, "invalid resource");
		} else {
			/**
			 * Cannot publish resource:
			 * 1. The resource is a file.
			 * 2. Same channel, same URI, different owner.
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
		 * 3. Owner = "*";
		 */
		if (!resource.uriValid() || resource.getOwner().equals("*")) {
			toReturn.put(false, "invalid resource");
		} else {
			if (resourceList.contains(resource) && resourceList.getSameResource(resource).getOwner().equals(resource.getOwner())) {
				resourceList.delete(resource);//The remove operation.
				toReturn.put(true, "success");
			} else {
				/**Cannot remove resource: the resource did not exist.*/
				toReturn.put(false, "cannot remove resource");
			}
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
				toReturn.put(false, "Not a file");
			} else {
				File file = new File(resource.getURI().getPath());
				if (file.canRead()) {
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
	

	public static HashMap<Boolean, resourceList> query(Resource resource, resourceList resourceList)	{
		resourceList resourceListFiltered = new resourceList();
		resourceList.initialResourceList();
		resourceListFiltered = resourceList;
		HashMap<Boolean, resourceList> toReturn = new HashMap<Boolean, resourceList>();

		for (Resource r: resourceListFiltered.getResourceList()) {
			if (	   (!r.getChannel().equals(resource.getChannel()))
					|| (!resource.getOwner().isEmpty() && !r.getOwner().equals(resource.getOwner()))
					|| (!resource.getTags().isEmpty() && !r.getTags().containsAll(resource.getTags()))
					|| (!resource.getUri().isEmpty() && !r.getUri().equals(resource.getUri()))

					|| (
							   (!resource.getName().isEmpty() && !r.getName().contains(resource.getName()))
							&& (!resource.getDescription().isEmpty() && !r.getDescription().contains(resource.getDescription()))
							&& !(resource.getDescription().isEmpty() && resource.getName().isEmpty())
							)
					) {
				resourceListFiltered.delete(r);
			}
		}
		toReturn.put(true, resourceListFiltered);
		
		return toReturn;
	}
	
	public static HashMap<Boolean, String> fetch(Resource resource, resourceList resourceList) throws URISyntaxException {
		HashMap<Boolean, String> toReturn = new HashMap<Boolean, String>();

		if (resource.isFile() && resource.uriValid()) {
			toReturn.put(true, resource.getKey());
		} else {
			toReturn.put(false, null);
		}
		return toReturn;
	}
	
	//TODO what is "missing resourceTemplate"
	public static HashMap<Boolean, String> exchange(serverList receivedList, serverList localList) {
		HashMap<Boolean, String> toReturn = new HashMap<Boolean, String>();
		for (Host h : receivedList.getServerList()) {
			try {
				InetAddress ip = InetAddress.getByName(h.getHostname());
				localList.add(h);
			} catch (UnknownHostException e) {}
		}
		toReturn.put(true, "success");
		return toReturn;
	}
	
}

