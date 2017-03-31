/**This class simply represent resources. The constructor only takes 'correct//corrected' arguments as input.*/
public class Resource {
	
	private String name;
	private String description;
	private String[] tags;
	private String uri;
	private String channel;
	private String owner;
	private String ezServer;
	
	public Resource(String name, String description, String[] tags, String uri, String channel, String owner, String ezServer) {
		this.name = name;
		this.description = description;
		this.tags = tags;
		this.uri = uri;
		this.channel = channel;
		this.owner = owner;
		this.ezServer = ezServer;
	}
	
}
