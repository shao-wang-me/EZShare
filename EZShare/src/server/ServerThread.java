package server;

import java.net.*;
import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLSocket;

import EZShare.Server;
import command.*;
import org.json.JSONObject;

import java.io.*;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import support.Debug;
import support.LogFormatter;
import variable.*;


/**
 * Created by xutianyu on 4/25/17.
 * Main server thread  class implement runnable
 *
 */

public class ServerThread implements Runnable {


	private enum Operation {PUBLISH,REMOVE,SHARE,QUERY,FETCH,EXCHANGE,SUBSCRIBE,UNSUBSCRIBE}

	private Socket client ;
	
	private String secret;
	
	private resourceList resourceList;
	
	private serverList serverList;

	private Boolean debug = true;
	
	private Logger log ;
	
	private String hostname;
	
	private int port;

	private int intervalLimit;
	
	private boolean secure;

	private resourceList newResourceList;

	private resourceList newResourceList_copy;


	private subscribeList subscribeList;

	private subscribeList readyToSend;

	private serverList serverAddList;

	//private Resource resourceList;
	
	public ServerThread(Socket client, String secret, resourceList resourceList, resourceList newResourceList,
						resourceList newResourceList_copy ,serverList serverList, serverList serverAddList,
						Boolean debug, String hostname, int port, int intervalLimit,
						subscribeList subscribeList, subscribeList readyToSend){
		this.client = client ;
		this.secret = secret;
		this.resourceList = resourceList;
		this.serverList = serverList;
		this.debug = debug;
		this.setLog();
		this.hostname = hostname;
		this.port = port;
		this.intervalLimit = intervalLimit;
		this.secure = client instanceof SSLSocket;

		this.newResourceList = newResourceList;
		this.newResourceList_copy = newResourceList_copy;

		this.subscribeList = subscribeList;
		this.readyToSend = readyToSend;
		this.serverAddList = serverAddList;
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{
            Thread.sleep(getIntervalLimit());
            if(!client.isClosed()){

                //PrintStream out = new PrintStream(client.getOutputStream());
                DataOutputStream out = new DataOutputStream(client.getOutputStream());

                //BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));
                DataInputStream buf = new DataInputStream(client.getInputStream());

                //parse jaon messsage
                JsonReader reader = new JsonReader(new InputStreamReader(client.getInputStream()));
                //parseJson(reader);
                Boolean flag = true ;


                //client.setSoTimeout(100);
                try {
                    while(flag){
                        String str = buf.readUTF();
                        Debug.printDebug('r',str, debug, log);

                        if(str == null ){
                            flag = false ;
                        }
                        else{

							parseJson(str, out, buf, serverAddList);
                        }
                    }
                } catch (IOException i) {
                    // TODO: handle exception
                }
                finally{
                    out.close();
                    client.close();
                }

            }
            else{

            }
		}catch(Exception e){
            try {
                client.close();
            } catch (IOException e1) {
            }
        }
	}

	// parse json command & choose relevant method
	private void parseJson(String  message, DataOutputStream out, DataInputStream in, serverList serverAddList){
		String key = null;
		ArrayList<String> list = new ArrayList<String>();
		Resource resource = new Resource("", "", list, "", "", "", "");
		//String operation = null;
		JSONObject reply = new JSONObject();

		try {
			
			//initialize variables
			JsonElement root = new JsonParser().parse(message);
			Gson gson = new Gson();
			if(root.getAsJsonObject().has("command")){
				String commandName = root.getAsJsonObject().get("command").getAsString();
				Operation operation = Operation.valueOf(commandName);

				//check command and call corresponding method
				switch(operation){
					case PUBLISH:{
						//check resource field exists
						Host h = new Host(getHostname(), getPort());
						Publish.publish(root, out, resourceList, newResourceList, newResourceList_copy, serverList, h, getDebug(), getLog());
						break;
					}
					case REMOVE:{
						//check resource field exists
						Host h = new Host(getHostname(), getPort());
						Remove.remove(root, out, resourceList, serverList, h, getDebug(), getLog());
						break;
					}
					case SHARE:{
						Host h = new Host(getHostname(), getPort());
						Share.share(root, out, resourceList, newResourceList, newResourceList_copy, serverList, h, getDebug(), getLog(), this.secret);
						break;
					}
					case QUERY:{
						Host h = new Host(getHostname(), getPort());
						Query.query(root, out, resourceList, serverList, h, getDebug(), getLog(), secure);
						break;
					}
					case FETCH:{
						Host h = new Host(getHostname(), getPort());
						Fetch.fetch(root, out, resourceList, serverList, h, getDebug(), getLog());
						break;
					}
					case EXCHANGE:{
						Host h = new Host(getHostname(), getPort());

							Exchange.exchange(root, out, resourceList, serverList, serverAddList, h, getDebug(), getLog());
						break;
					}
					case SUBSCRIBE:{
						Host h = new Host(getHostname(), getPort());
						String clientID = this.client.getInetAddress().getHostAddress() + ":" + this.client.getPort();
						Subscribe.subscribe(root, out, in, clientID, newResourceList, subscribeList, readyToSend, serverList, h, getDebug(), getLog());
						client.close();
						break;
					}
					case UNSUBSCRIBE:{
						break;
					}
					default:{
						reply.put("response", "error");
						reply.put("errorMessage", "invalid Command");
						out.writeUTF(reply.toString());
						Debug.printDebug('s',reply.toString(), getDebug(), getLog() );

						break;
					}

				}
			}
			else{
				reply.put("response", "error");
				reply.put("errorMessage", "missing or incorrect type for command");
				try {
					out.writeUTF(reply.toString());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
				}finally {
					Debug.printDebug('s',reply.toString(), getDebug(), getLog() );
				}
			}
					
		} catch (JsonParseException e) {
			// TODO: handle exception
			reply.put("response", "error");
			reply.put("errorMessage", "missing or incorrect type for command");
			try {
				out.writeUTF(reply.toString());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
			}finally {
				Debug.printDebug('s',reply.toString(), getDebug(), getLog() );
			}
		}catch (Exception e){
			reply.put("response", "error");
			reply.put("errorMessage", "missing or incorrect type for command");
			try {
				out.writeUTF(reply.toString());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
			}finally {
				Debug.printDebug('s',reply.toString(), getDebug(), getLog() );

			}
		}
			
	}

	public Boolean getDebug() {
		return debug;
	}

	public void setDebug(Boolean debug) {
		this.debug = debug;
	}

	public Logger getLog() {
		return log;
	}

	public void setLog() {
    	LogFormatter formatter = new LogFormatter();
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(formatter);
        
		Logger log = Logger.getLogger(Server.class.getName());
		log.setUseParentHandlers(false);
		//log.addHandler(handler);
		log.setLevel(Level.FINE);
		this.log = log;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

    public int getIntervalLimit() {
        return intervalLimit;
    }

    public void setIntervalLimit(int intervalLimit) {
        this.intervalLimit = intervalLimit;
    }
		
	}


