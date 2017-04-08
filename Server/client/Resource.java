package client;

import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;

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
		this.setName(name);
		this.setDescription(description);
		this.setTags(tags);
		this.setUri(uri);
		this.setChannel(channel);
		this.setOwner(owner);
		this.setEzServer(ezServer);
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
		Gson gson = new Gson();
		gson.toJson(this.tags);
		System.out.println(gson.toString());
        map.put("name",this.getName());
        map.put("tags",this.getTags().toString());
        map.put("description",this.getDescription());
        map.put("uri", this.getUri());
        map.put("channel", this.getChannel());
        map.put("owner", this.getOwner());
        map.put("ezserver", this.getEzServer());
        return map;
	}
	
}