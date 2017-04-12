import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**This class simply represent resources.*/
public class Resource {
	
	private String name;
	private String description;
	private List<String> tags;
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
	
	/**
	 * Invalid resource:
	 * 1. URI is empty.
	 * 2. Owner is "*".
	 * 3. URI not official.
	 * 4. URI not absolute.
	 */
	public boolean isValid() {
		if (uri == "" || owner == "*") {
			return false;
		}
		try {
			URI dummyUri = new URI(uri);
			if (!dummyUri.isAbsolute()) {
				return false;
			}
			uri = dummyUri.toString();
		} catch (URISyntaxException e) {
			return false;
		}
		return true;
	}
	
	public String cleanString(String string) {
		if (string == null) {
			return string;
		}
		string = string.replace("\0", "");
		if (string.charAt(0) == ' ') {
			string = string.substring(1);
		}
		if (string.charAt(string.length() - 1) == ' ') {
			string = string.substring(0, string.length() - 1);
		}
		return string;
	}
	
	public boolean isFile() throws URISyntaxException {
		URI dummyUri = new URI(uri);
		if (dummyUri.getScheme() == "file") {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean uriValid() {
		try {
			new URI(uri);
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
	
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public List<String> getTags() {
		return tags;
	}

	public String getUri() {
		return uri;
	}

	public String getChannel() {
		return channel;
	}

	public String getOwner() {
		return owner;
	}

	public String getEzServer() {
		return ezServer;
	}
	
}
