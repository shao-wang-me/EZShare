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






	public static HashMap<Boolean, String> publish(Resource resource, Map<String, Resource> resourceMap) throws URISyntaxException {
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
	

	public static HashMap<Boolean, String> remove(Resource resource, Map<String, Resource> resourceMap) {
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
	

	public static HashMap<Boolean, String> share(Resource resource, Map<String, Resource> resourceMap) throws URISyntaxException {
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
					(resourceMap.containsKey(resource.getKey()) && 
							!resourceMap.get(resource.getKey()).getOwner().equals(resource.getOwner()))
					) {
				toReturn.put(false, "Not a file");
			} else {
				File file = new File(resource.getURI().getPath());
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
	

	public static Map<Boolean, Map<String, Resource>> query(Resource resource, Map<String, Resource> resourceMap)	{
		Map<String, Resource> resourceMapFiltered = new HashMap<String, Resource>();





		resourceMapFiltered = resourceMap;
		HashMap<Boolean, Map<String, Resource>> toReturn = new HashMap<Boolean, Map<String, Resource>>();













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
	

	public static HashMap<Boolean, String> fetch(Resource resource, Map<String, Resource> resourceMap) throws URISyntaxException {
		HashMap<Boolean, String> toReturn = new HashMap<Boolean, String>();





		if (resource.isFile() && resource.uriValid()) {
			toReturn.put(true, resource.getKey());
		} else {
			toReturn.put(false, null);
		}
		return toReturn;
	}
	

	//TODO "missing resourceTemplate"
	public static HashMap<Boolean, String> exchange(Map<String, Integer> receivedList, Map<String, Integer> localList) {
		HashMap<Boolean, String> toReturn = new HashMap<Boolean, String>();
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

