package variable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

/**This class simply represent resources. The constructor only takes 'correct//corrected' arguments as input.*/
public class Resource {
	
	private String name;
	private String description;
	private List<String> tags ;
	private String uri;
	private String channel;
	private String owner;
	private String ezServer;
	
	public Resource(String name, String description, List<String> tags, String uri, String channel, String owner, String ezServer) {
		this.name = cleanString(name);
		this.description = cleanString(description);
		for (int i = 0; i < tags.size(); i++) {
			tags.set(i, cleanString(tags.get(i)));
		}
		this.tags = tags;
		this.uri = cleanString(uri);//TODO URIs may not need to be cleaned.
		this.channel = cleanString(channel);
		owner = owner.replace("*", "");
		this.owner = cleanString(owner);
		this.ezServer = cleanString(ezServer);
	}	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getEzServer() {
		return ezServer;
	}

	public void setEzServer(String ezServer) {
		this.ezServer = ezServer;
	}
	
	public HashMap<String, String > toJSON(){
		HashMap<String , String> map = new HashMap<String, String>();
        map.put("name",this.getName());
        map.put("tags",this.getTags().toString());
        map.put("description",this.getDescription());
        map.put("uri", this.getUri());
        map.put("channel", this.getChannel());
        map.put("owner", this.getOwner());
        map.put("ezserver", this.getEzServer());
        return map;
	}
	
	//add method
	/**
	 * Invalid resource:
	 * 1. URI is empty.
	 * 2. Owner is "*".
	 * 3. URI not official.
	 * 4. URI not absolute.
	 */
	public boolean isValid() {
		if (uri.isEmpty()|| owner.equals("*")) {
			return false;
		}
		try {
			URI dummyUri = new URI(uri);
			if (!dummyUri.isAbsolute()) {
				return false;
			}
			uri = dummyUri.toString();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			return false;
		}
		return true;
	}
	
	public String cleanString(String string) {
		if (string == null) {
			return string;
		}
		string = string.replace("\0", "");
		if( string.length() >= 1){
			if (string.charAt(0) == ' ' ) {
				string = string.substring(1);
			}
			if (string.charAt(string.length() - 1) == ' ' ) {
				string = string.substring(0, string.length() - 1);
			}
		}
		
		return string;
	}
	
	public boolean isFile() throws URISyntaxException {
		URI dummyUri = new URI(uri);
		//System.out.println(dummyUri.getScheme());
		if (dummyUri.getScheme().equals("file") ){
			return true;
		} else {
			return false;
		}
	}
	
	public boolean uriValid() {
		try {
			URI dummyURI = new URI(uri);
			return true;
		} catch (URISyntaxException e) {
			return false;
		}		
	}
	
	public URI getURI() throws URISyntaxException {
		URI dummyUri = new URI(uri);
		return dummyUri;
	}

	public String getKey() {
		return channel + "," + uri;
	}
	
	
	
}