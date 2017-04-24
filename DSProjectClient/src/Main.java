
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import org.apache.commons.cli.*;
import org.json.*;

public class Main {

	public static void main(String[] args) throws JSONException {
		int serverPort = 3000;
		String serverIP = "localhost";

		Options option = new Options();
		option.addOption("channel", true, "channel");
		option.addOption("debug", false, "print debug information");
		option.addOption("description", true, "resource description");
		option.addOption("exchange", false, "exchange server list with server");
		option.addOption("fetch", false, "fetch resources from server");
		option.addOption("host", true, "server host, a domain name or IP address");
		option.addOption("name", true, "resource name");
		option.addOption("owner", true, "owner");
		option.addOption("port", true, "server port, an integer");
		option.addOption("publish", false, "publish resource on server");
		option.addOption("query", false, "query for resources from server");
		option.addOption("remove", false, "remove resource from server");
		option.addOption("secret", true, "secret");
		option.addOption("servers", true, "server list, host1:port1,host2:port2,...");
		option.addOption("share", false, "share resource on server");
		option.addOption("tags", true, "resource tags, tag1,tag2,tag3,...");
		option.addOption("uri", true, "resource URI");

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;

		try {
			// get the host name and port number
			cmd = parser.parse(option, args);
		} catch (ParseException e) {
			// something needs to be done
		}

		if (cmd.hasOption("host")) {
			serverIP = cmd.getOptionValue("host");
		}

		if (cmd.hasOption("port")) {
			serverPort = Integer.parseInt(cmd.getOptionValue("port"));
		}
		Client client = new Client(serverIP, serverPort);
		
		
		if (cmd.hasOption("publish")) {
			// publish command
			try{
				Operations.Publish(cmd, client);
			} catch (JSONException e) {
				//Do something
			}
		}
		
		if (cmd.hasOption("query")) {
			// publish command
			try{
				Operations.Query(cmd, client);
			} catch (JSONException e){
				//Do something
			}
		}

		if (cmd.hasOption("remove")) {
			try{
				Operations.Remove(cmd, client);
			} catch (JSONException e) {
				//Do something
			}
		}
		
		if (cmd.hasOption("share")) {
			try{
				Operations.Share(cmd, client);
			} catch (JSONException e) {
				//Do something
			}
		}
		
		if (cmd.hasOption("fetch")) {
			try{
				Operations.Fetch(cmd, client);
			} catch (JSONException e) {
				//Do something
			}
		}
		
		if (cmd.hasOption("exchange")) {
			try{
				Operations.Exchange(cmd, client);
			} catch (JSONException e) {
				//Do something
			}
		}
		/*
		 * JSONObject j = new JSONObject("{}"); JSONObject resource = new
		 * JSONObject("{}"); ArrayList<String> l = new ArrayList<String>();
		 * ServerInfo s = null; resource.put("name",""); resource.put("tags",l);
		 * resource.put("description", ""); resource.put("uri", "");
		 * resource.put("channel",""); resource.put("owner", "");
		 * resource.put("ezserver",s); j.put("command","QUERY"); j.put("relay",
		 * true); j.put("resourceTemplate", resource);
		 * System.out.println(resource.toString());
		 * System.out.println(j.toString()); client.query(j);
		 */
	}

}
