package server;

public class Command_template {
	
	private String command;
	private Resource resourceTemplate;
	
	public Command_template(String command, Resource resourceTemplate){
			this.setCommand(command);
			this.setResourceTemplate(resourceTemplate);
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public Resource getResourceTemplate() {
		return resourceTemplate;
	}

	public void setResourceTemplate(Resource resourceTemplate) {
		this.resourceTemplate = resourceTemplate;
	}
}
