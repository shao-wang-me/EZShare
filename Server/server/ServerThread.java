package server;

import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import support.LogFormatter;

public class ServerThread implements Runnable {
	
	private enum Operation {PUBLISH,REMOVE,SHARE,QUERY,FETCH,EXCHANGE;} 

	private Socket client = null;
	
	private String secret;
	
	private resourceList resourceList;
	
	private serverList serverList;
	
	private Boolean debug = true;
	
	private Logger log ;

	//private Resource resourceList;
	
	public ServerThread(Socket client, String secret, 
			resourceList resourceList, serverList serverList, Boolean debug){
		this.client = client ;
		this.secret = secret;
		this.resourceList = resourceList;
		this.serverList = serverList;
		this.debug = debug;
		this.setLog();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{
		
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
					printDebug('r',str, debug );

					if(str == null || "".equals(str)){
						flag = false ;
					}
					else{
						//out.writeUTF(str);
						//System.out.println("from server :"+str);
						parseJson(str, out);
					}
				}
			} catch (EOFException e) {
				// TODO: handle exception

			}

			out.close();
			client.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private  void parseJson(String  message, DataOutputStream out){
		String key = null;
		ArrayList<String> list = new ArrayList<String>();
		Resource resource = new Resource("", "", list, "", "", "", "");
		//String operation = null;
		try {
			JsonElement root = new JsonParser().parse(message);
			JSONObject reply = new JSONObject();
			HashMap<Boolean, String> response = new HashMap<Boolean, String>();
			
			Gson gson = new Gson();
			String commandName = root.getAsJsonObject().get("command").getAsString();
			Operation operation = Operation.valueOf(commandName);
			switch(operation){
				case PUBLISH:{
					JsonObject object = root.getAsJsonObject().get("resource").getAsJsonObject();
					//Map map = gson.fromJson(message, Map.class);
					resource = gson.fromJson(object, Resource.class);
					System.out.println("publish: "+message);
					response = Function.publish(resource, resourceList);
					if(response.containsKey(true)){
						reply.put("response", "success");
					}
					else{
						reply.put("response", "error");
						reply.put("errorMessage", response.get(false));
						System.out.println(reply.toString());
					}
					out.writeUTF(reply.toString());
					printDebug('s',reply.toString(), debug );
					break;
				}
				case REMOVE:{
					JsonObject object = root.getAsJsonObject().get("resource").getAsJsonObject();
					resource = gson.fromJson(object, Resource.class);
					System.out.println("remove: "+message);
					response = Function.remove(resource, resourceList);
					//out.writeUTF(response.toString());
					if(response.containsKey(true)){
						reply.put("response", "success");
					}
					else{
						reply.put("response", "error");
						reply.put("errorMessage", response.get(false));
						System.out.println(reply.toString());
					}
					out.writeUTF(reply.toString());
					printDebug('s',reply.toString(), debug );
					break;
				} 
				case SHARE:{
					String secret = root.getAsJsonObject().get("secret").getAsString();
					if(secret.equals(this.secret)){
						JsonObject object = root.getAsJsonObject().get("resource").getAsJsonObject();
						resource = gson.fromJson(object, Resource.class);
						System.out.println("share: "+message);
						response = Function.share(resource, resourceList);
						out.writeUTF(response.toString());
						printDebug('s',reply.toString(), debug );

					}
					else{
						reply.put("response", "error");
						reply.put("errorMessage", "incorrect secret");
						out.writeUTF(reply.toString());
						printDebug('s',reply.toString(), debug );

					}
					
					break;
				}
				case QUERY:{
					Boolean relay = root.getAsJsonObject().get("relay").getAsBoolean();
					Boolean flag = false;
					HashMap<Boolean, resourceList> result = new HashMap<Boolean, resourceList>();
					
					
					if(root.getAsJsonObject().has("resourceTemplate")){
						JsonObject object = root.getAsJsonObject().get("resourceTemplate").getAsJsonObject();
						resource = gson.fromJson(object, Resource.class);
						System.out.println("query: "+message);
						result = Function.query(resource, resourceList);
						
						if(relay){
							if(result.containsKey(true) && result.get(true).getResourceList().size() != 0){
								reply.put("response", "sucess");
								out.writeUTF(reply.toString());
								int num = 0;
								for(Resource entry : result.get(true).getResourceList()){							
									out.writeUTF(gson.toJson(entry).toString());
									num++;
								}
								out.writeUTF(new JSONObject().put("resultSize", num).toString());
								
							}
							else{
								//relay = true & query failed => broadcast to server list
								JSONObject trans = new JSONObject(message);
								trans.put("relay", false);
								String str = trans.toString();
								// new query used to broadcast
								Boolean success = false;
								for(Host h: serverList.getServerList()){
							        Socket agent = new Socket(h.getHostname(), h.getPort());  
							        //Socket 输出流， 转发查询
							        DataOutputStream forward = new DataOutputStream(agent.getOutputStream());
							        //获取Socket的输入流，用来接收从服务端发送过来的数据    
							        DataInputStream in = new DataInputStream(agent.getInputStream());
							        Boolean f = true;
							        forward.writeUTF(str);
							        try{
								        while(f){
									        String info = in.readUTF();
									        System.out.println(info);
									        if(new JSONObject(info).get("response").equals("success")){
									        	success = true;
									        	//out.writeUTF(info);									        	
									        }
									        if(success){
										        if(info.contains("resultSize")){
										        	f = false;
										        	out.writeUTF(info);
										        	
										        }
										        else{
										        	out.writeUTF(info);
										        }
									        }

								        }
							        	
							        }catch(SocketTimeoutException t){
										reply.put("response", "success");
										//query failed => return error message
										out.writeUTF(reply.toString());
										printDebug('s',reply.toString(), debug );
										out.writeUTF(new JSONObject().put("resultSize",0).toString());
										printDebug('s',new JSONObject().put("resultSize",0).toString(), debug );

							        }

							        forward.close();
							        in.close();
							        agent.close();
								}		
							}
							
						}
						else{
							//relay = false 
							if(result.containsKey(true)){
								//query succeed => return result
								System.out.println("result,,,"+result.get(true).getResourceList().size());
								reply.put("response", "success");
								out.writeUTF(reply.toString());
								int num = 0;
								for(Resource entry : result.get(true).getResourceList()){
									out.writeUTF(gson.toJson(entry).toString());
									num++;
								}
								out.writeUTF(new JSONObject().put("resultSize", num).toString());
								printDebug('s',new JSONObject().put("resultSize",num).toString(), debug );

							}
							else{
								reply.put("response", "error");
								reply.put("errorMessage", "invalid ResourceTemplate");
								//query failed => return error message
								out.writeUTF(reply.toString());
								printDebug('s',reply.toString(), debug );

							}
						}
					}
					else{
						reply.put("response", "error");
						reply.put("errorMessage", "missing ResourceTemplate");
						out.writeUTF(reply.toString());
						printDebug('s',reply.toString(), debug );

					}
					
					break;
				}
				case FETCH:{
					JsonObject object = root.getAsJsonObject();

					if(object.has("resourceTemplate")){
						object = object.get("resourceTemplate").getAsJsonObject();
						resource = gson.fromJson(object, Resource.class);
						System.out.println("fetch: "+message);
						long fileSize = 0;
						response = Function.fetch(resource, resourceList);
						System.out.println(resource.getKey()+" "+response);
						if(response.containsKey(true)){
							out.writeUTF(new JSONObject().put("response", "success").toString());
							File f = new File(resource.getURI().getPath());
							fileSize = f.length();
							
							System.out.println(f);
							String str = gson.toJson(object);
							JSONObject temp = new JSONObject(str);
							reply = temp.put("resourceSize", fileSize);
							out.writeUTF(reply.toString());
							printDebug('s',reply.toString(), debug );
							
							System.out.println("after: "+reply.toString());
						
							//read data from local disk
							DataInputStream input = new DataInputStream
									(new BufferedInputStream(new FileInputStream(f)));
							int bufferSize = (int)fileSize;
							byte[] buf = new byte[bufferSize];
							int num =0;
							System.out.println(bufferSize);
							//input.read(buf);
							//out.write(buf);
							//num=input.read(buf);
				            while((num=input.read(buf))!=-1){
				            	System.out.println("num: "+num);
				                out.write(buf, 0, num);
				            }
				            out.flush();
				            System.out.println("文件发送成功！");
							input.close();
							out.writeUTF(new JSONObject().put("resultSize", 1).toString());
						}
						else{
							reply.put("response", "error");
							reply.put("errorMessage", "invalid resourceTemplate");
							out.writeUTF(reply.toString());
						}
					}
					else{
						reply.put("response", "error");
						reply.put("errorMessage", "missing resourceTemplate");
						out.writeUTF(reply.toString());
						printDebug('s',reply.toString(), debug );

					}				
					// return file size

					break;
				}
				case EXCHANGE:{
					JsonArray array = root.getAsJsonObject().get("serverList").getAsJsonArray();
					Host[] host = gson.fromJson(array, Host[].class);
					for(Host h : host){
						if( !serverList.getServerList().contains(h)){
							serverList.add(h);
							//System.out.println("add a new server");
						}
					}
					System.out.println("exchange: "+host[0].getHostname());
					reply.put("response", "success");
					out.writeUTF(reply.toString());
					printDebug('s',reply.toString(), debug );

					break;
				}
				default:{				
					reply.put("response", "error");
					reply.put("errorMessage", "invalid Command");
					out.writeUTF(reply.toString());
					printDebug('s',reply.toString(), debug );

					break;
				}
				
			}
					
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	
			
		}

	public Boolean getDebug() {
		return debug;
	}

	public void setDebug(Boolean debug) {
		this.debug = debug;
	}
	
	public void printDebug (char op, String str, Boolean debug){
        if(debug){

    		if(op=='s'){
    			
    			log.info("SENT:"+str);
    		}
    		else{
    			log.info("RECEIVED:"+str);
    		}
    		
        }
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
		
	}


