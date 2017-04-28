package  client;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.cli.*;
import org.json.*;

public class Operations {
	
	private static String getName(CommandLine cmd) {
		String name = "";
		if (cmd.hasOption("name")) {
			name = cmd.getOptionValue("name");
		}
		return name;
	}
	
	private static ArrayList<String> getTags(CommandLine cmd) {
		ArrayList<String> tags = new ArrayList<String>();
		if (cmd.hasOption("tags")) {
			String tagString = cmd.getOptionValue("tags");
			String[] allTag = tagString.split(",");
			for (String s : allTag) {
				tags.add(s);
			}
		}
		return tags;
	}
	
	private static String getDescription(CommandLine cmd) {
		String description = "";
		if (cmd.hasOption("description")) {
			description = cmd.getOptionValue("description");
		}
		return description;
	}
	
	private static String getUri(CommandLine cmd) {
		String uri = "";
		if (cmd.hasOption("uri")) {
			uri = cmd.getOptionValue("uri");
		}
		return uri;
	}
	
	private static String getChannel(CommandLine cmd) {
		String channel = "";
		if (cmd.hasOption("channel")) {
			channel = cmd.getOptionValue("channel");
		}
		return channel;
	}
	
	private static String getOwner(CommandLine cmd) {
		String owner = "";
		if (cmd.hasOption("owner")) {
			owner = cmd.getOptionValue("owner");
		}
		return owner;
	}
	
	private static String getSecret(CommandLine cmd) {
		String secret = "";
		if (cmd.hasOption("secret")) {
			secret = cmd.getOptionValue("secret");
		}
		return secret;
	}
	
	private static String getServers(CommandLine cmd) {
		String servers = "";
		if (cmd.hasOption("servers")) {
			servers = cmd.getOptionValue("servers");
		}
		return servers;
	}

	private static JSONObject getResource(CommandLine cmd) throws JSONException {
		JSONObject resource = new JSONObject("{}");
		String name = getName(cmd);
		ArrayList<String> tags = getTags(cmd);
		String description = getDescription(cmd);
		String uri = getUri(cmd);
		String channel = getChannel(cmd);
		String owner = getOwner(cmd);
		String ezserver = null;
		resource.put("name", name);
		resource.put("tags", tags);
		resource.put("description", description);
		resource.put("uri", uri);
		resource.put("channel", channel);
		resource.put("owner", owner);
		resource.put("ezserver", ezserver);
		return resource;
	}
	
	//The first version, all the msgs received from servers will be 
	//printed out on the console
	public static void Publish(CommandLine cmd,Client c) throws JSONException {
		
		
		JSONObject sentJSON = new JSONObject("{}");
		JSONObject resource = getResource(cmd);
		
		sentJSON.put("command", "PUBLISH");
		sentJSON.put("resource", resource);
		c.sendJSON(sentJSON,cmd.hasOption("debug"),cmd.getOptionValue("host"),cmd.getOptionValue("port"));
		
		
	}
	
	public static void Query(CommandLine cmd,Client c) throws JSONException {
		JSONObject sentJSON = new JSONObject("{}");
		JSONObject resource = getResource(cmd);
		
		sentJSON.put("resourceTemplate", resource);
		sentJSON.put("relay", true);
		sentJSON.put("command", "QUERY");
		c.sendJSON(sentJSON,cmd.hasOption("debug"),cmd.getOptionValue("host"),cmd.getOptionValue("port"));
	}
	
	public static void Remove(CommandLine cmd,Client c) throws JSONException {
		JSONObject sentJSON = new JSONObject("{}");
		JSONObject resource = getResource(cmd);
		
		sentJSON.put("command", "REMOVE");
		sentJSON.put("resource", resource);
		c.sendJSON(sentJSON,cmd.hasOption("debug"),cmd.getOptionValue("host"),cmd.getOptionValue("port"));
	}
	
	public static void Share(CommandLine cmd,Client c) throws JSONException {
		JSONObject sentJSON = new JSONObject("{}");
		JSONObject resource = getResource(cmd);
		sentJSON.put("command", "SHARE");
		sentJSON.put("secret", getSecret(cmd));
		sentJSON.put("resource", resource);
		c.sendJSON(sentJSON,cmd.hasOption("debug"),cmd.getOptionValue("host"),cmd.getOptionValue("port"));
	}
	
	public static void Fetch(CommandLine cmd,Client c) throws JSONException {
		JSONObject sentJSON = new JSONObject("{}");
		JSONObject resource = getResource(cmd);
		sentJSON.put("command", "FETCH");
		sentJSON.put("resourceTemplate", resource);
		c.fetch(sentJSON,cmd.hasOption("debug"),cmd.getOptionValue("host"),cmd.getOptionValue("port"));
	}
	
	public static void Exchange(CommandLine cmd,Client c) throws JSONException,ArrayIndexOutOfBoundsException {
		JSONObject sentJSON = new JSONObject("{}");
		List<JSONObject> servers = new ArrayList<JSONObject>();
		String serverInfo = getServers(cmd);
		String[] serverList = serverInfo.split(",");
		for (String server : serverList) {
			String host = server.split(":")[0];
			int port = Integer.parseInt(server.split(":")[1]);
			JSONObject temp = new JSONObject("{}");
			temp.put("hostname", host);
			temp.put("port", port);
			servers.add(temp);
		}
		sentJSON.put("command", "EXCHANGE");
		sentJSON.put("serverList", servers);
		c.sendJSON(sentJSON,cmd.hasOption("debug"),cmd.getOptionValue("host"),cmd.getOptionValue("port"));
	}
	public static String getCurrentTime() {
		long millis = System.currentTimeMillis();
		Date now = new Date(millis); 
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String time = dateFormat.format(now);
		return time + "." + millis % 1000;
	}
}
