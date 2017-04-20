import java.io.File;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * Static methods for commands: PUBLISH, REMOVE, SHARE, QUERY, FETCH, EXCHANGE.
 * Resources are stored in Map<String, Resource>. The string is a composite key (channel, owner).
 * For example, if channel is "LPL S7", owner is "Shao Wang", the key is "LPL S7,Shao Wang". Refer Resource.getKey();
 * @author Shao Wang
 *
 */
public class Command {
	
	//Map<key(channel, URI), Resource>
	/**
	 * @param resource: A raw resource.
	 * @param resourceMap: Stored resources.
	 * @return Map<Boolean, String>: Boolean: success or failed. String: response the server should give.
	 * @throws URISyntaxException
	 */
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
							!resourceMap.get(resource.getKey()).getOwner().equals(resource.getOwner()))
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
	
	//TODO Upload
	public Map<Boolean, String> remove(Resource resource, Map<String, Resource> resourceMap) {
		Map<Boolean, String> toReturn = new HashMap<Boolean, String>();
		/**
		 * Invalid resource:
		 * 1. Missing URI;
		 * 2. Invalid URI;
		 * 3. Owner = "*";
		 */
		if (!resource.uriValid() || resource.getOwner().equals("*")) {
			toReturn.put(false, "invalid resource");
		} else {
			if (resourceMap.containsKey(resource.getKey()) && resourceMap.get(resource.getKey()).getOwner().equals(resource.getOwner())) {
				resourceMap.remove(resource.getKey());//The remove operation.
				toReturn.put(true, "success");
			} else {
				/**Cannot remove resource: the resource did not exist.*/
				toReturn.put(false, "cannot remove resource");
			}
		}
		return toReturn;
	}
	
	//Not tested
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
							!resourceMap.get(resource.getKey()).getOwner().equals(resource.getOwner()))
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
	
	//TODO Upload
	public Map<Boolean, Map<String, Resource>> query(Resource resource, Map<String, Resource> resourceMap)	{
		/**
		 * 1. Copy the resourceMap.
		 * 2. Remove resources that do not match.
		 * 3. Return the copied map.
		 */
		Map<String, Resource> resourceMapFiltered = resourceMap;
		Map<Boolean, Map<String, Resource>> toReturn = new HashMap<Boolean, Map<String, Resource>>();
		/**
		 * Remove conditions:
		 * 	  Channel not matching.
		 * || Owner is not empty && owner not matching.
		 * || Tags (in query) not contained (in resource).
		 * || URI is not empty && URI not matching.
		 * || (
		 * 		   Name is not empty && name not contained.
		 *    	&& Description is not empty && description not contained. 
		 *    	&& (!) Description is empty && name is empty.
		 *    )
		 */
		for (Resource r: resourceMapFiltered.values()) {
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
				resourceMapFiltered.remove(r.getKey());
			}
		}
		toReturn.put(true, resourceMapFiltered);
		
		return toReturn;
	}
	
	//Upload
	public Map<Boolean, Resource> fetch(Resource resource, Map<String, Resource> resourceMap) throws URISyntaxException {
		Map<Boolean, Resource> toReturn = new HashMap<Boolean, Resource>();
		/**
		 * Success:
		 * 1. Valid URI.
		 * 2. URI is file.
		 */
		if (resource.isFile() && resource.uriValid()) {
			toReturn.put(true, resourceMap.get(resource.getKey()));
		} else {
			toReturn.put(false, null);
		}
		return toReturn;
	}
	
	//Not tested
	//TODO What is "missing resourceTemplate"?
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
