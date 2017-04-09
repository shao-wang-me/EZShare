import java.io.File;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class Command {

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
		if (!resource.isValid()) {
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
	
	//TODO "missing resourceTemplate"
	public Map<Boolean, String> exchange(Map<String, Integer> receivedList, Map<String, Integer> localList) {
		Map<Boolean, String> toReturn = new HashMap<Boolean, String>();
		for (Map.Entry<String, Integer> serverRecord : receivedList.entrySet()) {
			try {
				InetAddress ip = InetAddress.getByName(serverRecord.getKey());
				localList.put(serverRecord.getKey(), serverRecord.getValue());
			} catch (UnknownHostException e) {}
		}
		toReturn.put(true, "success");
		return toReturn;
	}
	
}
