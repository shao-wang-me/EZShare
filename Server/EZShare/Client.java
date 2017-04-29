package EZShare;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.commons.cli.*;
import org.json.*;

public class Client {

	public static void main(String[] args) throws JSONException {
		
		//This is the default host host and port number
		Integer serverPort = 20006;
		String serverIP = "localhost";
		
		HelpFormatter formatter = new HelpFormatter();

		Options option = new Options();
		option.addOption("h","help",false,"help documentation");
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
			cmd = parser.parse(option, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			System.exit(-1);
		}
		
		if (cmd.hasOption("help") || cmd.hasOption("h")) { 
			    formatter.printHelp("Help Documentation",option);
			    System.exit(0);
		}
		
		if (cmd.hasOption("host")) {
			serverIP = cmd.getOptionValue("host");
		}
		if (cmd.hasOption("port")) {
			serverPort = Integer.parseInt(cmd.getOptionValue("port"));
		}

		clientObject c = new clientObject(serverIP, serverPort);

		if (cmd.hasOption("publish")) {
			try {
				Operations.Publish(cmd, c);
			} catch (JSONException e) {
				System.out.println(e.getMessage());
				System.exit(-1);
			}
		}

		if (cmd.hasOption("query")) {
			try {
				Operations.Query(cmd, c);
			} catch (JSONException e) {
				System.out.println(e.getMessage());
				System.exit(-1);
			}
		}

		if (cmd.hasOption("remove")) {
			try {
				Operations.Remove(cmd, c);
			} catch (JSONException e) {
				System.out.println(e.getMessage());
				System.exit(-1);
			}
		}

		if (cmd.hasOption("share")) {
			try {
				Operations.Share(cmd, c);
			} catch (JSONException e) {
				System.out.println(e.getMessage());
				System.exit(-1);
			}
		}

		if (cmd.hasOption("fetch")) {
			try {
				Operations.Fetch(cmd, c);
			} catch (JSONException e) {
				System.out.println(e.getMessage());
				System.exit(-1);
			}
		}

		if (cmd.hasOption("exchange")) {
			try {
				Operations.Exchange(cmd, c);
			} catch (JSONException e) {
				System.out.println(e.getMessage());
				System.exit(-1);
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("Missing \"-servers\"");
				System.exit(-1);
			}
		}

	}

}
