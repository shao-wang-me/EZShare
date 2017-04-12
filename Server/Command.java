import java.io.File;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class Command {
	
	/**
	 * Publish
	 * 
	 * 
	 */
	
	

	//Map<key(channel, URI), Resource>
	public Map<Boolean, String> publish(Resource resource, Map<String, Resource> resourceMap) throws URISyntaxException {
		Map<Boolean, String> toReturn = new HashMap<Boolean, String>();
		if (!resource.isValid()) {
			toReturn.put(false, "invalid resource");
		} else {
			/**
			 * Cannot publish resource:
			 * 1. The resource is a file.
			 * 2. Same channel, same URI, different owner.
			 */
			if (resource.isFile() || 
					(resourceMap.containsKey(resource.getKey()) && 
							resourceMap.get(resource.getKey()).getOwner() != resource.getOwner())
					) {
				toReturn.put(false, "cannot publish resource");
			} else {
				/**Now we should be able to add or update the resource.*/
				resourceMap.put(resource.getKey(), resource);
				toReturn.put(true, "success");
			}
		}
		return toReturn;
	}
	
	public Map<Boolean, String> remove(Resource resource, Map<String, Resource> resourceMap) {
		Map<Boolean, String> toReturn = new HashMap<Boolean, String>();
		/**
		 * Invalid resource:
		 * 1. Missing URI;
		 * 2. Invalid URI;
		 * 3. Owner = "*";
		 */
		if (!resource.uriValid() || resource.getOwner() == "*") {
			toReturn.put(false, "invalid resource");
		} else {
			if (resourceMap.containsKey(resource.getKey()) && resourceMap.get(resource.getKey()).getOwner() == resource.getOwner()) {
				resourceMap.remove(resource.getKey());//The remove operation.
				toReturn.put(true, "success");
			} else {
				/**Cannot remove resource: the resource did not exist.*/
				toReturn.put(false, "cannot remove resource");
			}
		}
		return toReturn;
	}
	
	public Map<Boolean, String> share(Resource resource, Map<String, Resource> resourceMap) throws URISyntaxException {
		Map<Boolean, String> toReturn = new HashMap<Boolean, String>();
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
					(resourceMap.containsKey(resource.getKey()) && 
							resourceMap.get(resource.getKey()).getOwner() != resource.getOwner())
					) {
				toReturn.put(false, "cannot share resource");
			} else {
				File file = new File(resource.getURI());
				if (file.canRead()) {
					/**Now we should be able to add or update the resource.*/
					resourceMap.put(resource.getKey(), resource);
					toReturn.put(true, "success");
				} else {
					toReturn.put(false, "cannot share resource");
				}				
			}
		}
		return toReturn;
	}
	
	public Map<Boolean, Map<String, Resource>> query(Resource resource, Map<String, Resource> resourceMap)	{
		Map<String, Resource> resourceMapFiltered = resourceMap;
		Map<Boolean, Map<String, Resource>> toReturn = new HashMap<Boolean, Map<String, Resource>>();
		if (!resource.uriValid()) {
			toReturn.put(false, null);
		} else {
			for (Resource r: resourceMapFiltered.values()) {
				if (r.getChannel() != resource.getChannel() 
						|| (resource.getOwner() != "" && r.getOwner() != resource.getOwner()) 
						|| (!resource.getTags().isEmpty() && !r.getTags().containsAll(resource.getTags()))
						|| (resource.getUri() != "" && r.getUri() != resource.getUri())
						|| (
								(resource.getName() != "" && !r.getName().contains(resource.getName()))
								&& (resource.getDescription() != "" && !r.getDescription().contains(resource.getDescription()))
								&& (resource.getDescription() != "" || resource.getName() != "")
								)
						) {
					resourceMapFiltered.remove(r.getKey());
				}
			}
			toReturn.put(true, resourceMapFiltered);
		}
		return toReturn;
	}
	
	public Map<Boolean, Resource> fetch(Resource resource, Map<String, Resource> resourceMap) throws URISyntaxException {
		Map<Boolean, Resource> toReturn = new HashMap<Boolean, Resource>();
		if ((resource.isValid() || (!resource.isValid() && resource.cleanString(resource.getOwner()) == "*")) && resource.isFile()) {
			toReturn.put(true, resourceMap.get(resource.getKey()));
		} else {
			toReturn.put(false, null);
		}
		return toReturn;
	}
	
	//TODO "missing resourceTemplate"
	public Map<Boolean, String> exchange(Map<String, Integer> receivedList, Map<String, Integer> localList) {
		Map<Boolean, String> toReturn = new HashMap<Boolean, String>();
		for (Map.Entry<String, Integer> serverRecord : receivedList.entrySet()) {
			try {
				InetAddress.getByName(serverRecord.getKey());
				localList.put(serverRecord.getKey(), serverRecord.getValue());
			} catch (UnknownHostException e) {}
		}
		toReturn.put(true, "success");
		return toReturn;
	}
	
}
