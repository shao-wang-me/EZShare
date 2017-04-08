package client;

public class Command {
	
	private String command;
	private Resource resource;
	
	public Command(String command, Resource resource){
			this.command = command;
			this.resource = resource;
	}
	
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public Resource getResource() {
		return resource;
	}
	public void setResource(Resource resource) {
		this.resource = resource;
	}
	
}
