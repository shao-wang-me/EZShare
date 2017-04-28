
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.commons.cli.*;
import org.json.*;

public class Main {

	public static void main(String[] args) throws JSONException {
		Integer serverPort = 10000;
		String serverIP = "localhost";

		// String serverIP = "sunrise.cis.unimelb.edu.au";
		// int serverPort = 3781;

		/*
		 * Test module
		 */

		/*
		 * 
		 * Socket s = null; try { s = new Socket(serverIP, serverPort); } catch
		 * (UnknownHostException e) { e.printStackTrace(); } catch (IOException
		 * e) { e.printStackTrace(); } JSONObject j = new JSONObject("{}");
		 * JSONObject re = new JSONObject("{}"); ArrayList<String> tag = new
		 * ArrayList<String>(); ArrayList<JSONObject> servers = new
		 * ArrayList<JSONObject>(); JSONObject serverOne = new JSONObject("{}");
		 * JSONObject serverTwo = new JSONObject("{}");
		 * serverOne.put("host","10.13.62.119"); serverOne.put("port",10000);
		 * serverTwo.put("host", "gg"); serverTwo.put("port","1234");
		 * servers.add(serverTwo); //tag.add("QUERY"); //tag.add("PUBLISH");
		 * j.put("command", "QUERY"); j.put("relay", true); re.put("name", "");
		 * re.put("tags", tag); re.put("description",""); re.put("uri", "");
		 * re.put("channel", 11); //re.put("owner","");
		 * j.put("resourceTemplate",re); System.out.println(j.toString()); try {
		 * DataInputStream input = new DataInputStream(s.getInputStream());
		 * DataOutputStream output = new DataOutputStream(s.getOutputStream());
		 * output.writeUTF(j.toString()); output.flush(); String message = "";
		 * long startTime = System.currentTimeMillis(); long currentTime = 0;
		 * while ((currentTime = System.currentTimeMillis()) - startTime <=
		 * 50000000) { JSONObject msgGet = null; if (input.available() > 0) {
		 * message = input.readUTF(); msgGet = new JSONObject(message);
		 * System.out.println(msgGet.toString()); } } } catch (IOException e) {
		 * // TODO Auto-generated catch block e.printStackTrace(); }
		 */

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
			System.out.println(e.getMessage());
			System.exit(-1);
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
			try {
				Operations.Publish(cmd, client);
			} catch (JSONException e) {
				// Do something
				System.out.println(e.getMessage());
				System.exit(-1);
			}
		}

		if (cmd.hasOption("query")) {
			// publish command
			try {
				Operations.Query(cmd, client);
			} catch (JSONException e) {
				// Do something
				System.out.println(e.getMessage());
				System.exit(-1);
			}
		}

		if (cmd.hasOption("remove")) {
			try {
				Operations.Remove(cmd, client);
			} catch (JSONException e) {
				// Do something
				System.out.println(e.getMessage());
				System.exit(-1);
			}
		}

		if (cmd.hasOption("share")) {
			try {
				Operations.Share(cmd, client);
			} catch (JSONException e) {
				// Do something
				System.out.println(e.getMessage());
				System.exit(-1);
			}
		}

		if (cmd.hasOption("fetch")) {
			try {
				Operations.Fetch(cmd, client);
			} catch (JSONException e) {
				// Do something
				System.out.println(e.getMessage());
				System.exit(-1);
			}
		}

		if (cmd.hasOption("exchange")) {
			try {
				Operations.Exchange(cmd, client);
			} catch (JSONException e) {
				// Do something
				System.out.println(e.getMessage());
				System.exit(-1);
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("Missing \"-servers\"");
				System.exit(-1);
			}
		}

	}

}
