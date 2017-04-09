import java.net.URISyntaxException;
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
							resourceMap.get(resource.getKey()).getOwner() == resource.getOwner())
					) {
				toReturn.put(false, "cannot publish resource");
			}
			/**Now we should be able to add or update the resource.*/
			resourceMap.put(resource.getKey(), resource);
			toReturn.put(true, "success");
		}
		return toReturn;
	}
	
}
