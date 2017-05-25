package  client;

import java.io.*;
import java.net.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.*;

import EZShare.Client;
import support.LogFormatter;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


public class clientObject {

	private Socket s = null;
	private Logger log = null;
	private SSLSocket ss = null;
	private  boolean secureFlag;

	public clientObject(String serverIP, int serverPort, boolean secureFlag) {
		this.setLog();
		this.secureFlag = secureFlag;
		LogFormatter formatter = new LogFormatter();
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(formatter);
		Logger logIni = Logger.getLogger(Client.class.getName());
		logIni.setUseParentHandlers(false);
		logIni.setLevel(Level.INFO);
		logIni.info("setting debug on");
		try {
			if(secureFlag){

				InputStream keystoreInput = getClass()
						.getResourceAsStream("/serverKeystore/server.jks");
				InputStream truststoreInput = getClass()
						.getResourceAsStream("/clientKeystore/client.jks");
				support.SetSecureSocket.setSSLFactories(keystoreInput, "comp90015", truststoreInput);
				keystoreInput.close();
				truststoreInput.close();

				//System.setProperty("javax.net.ssl.trustStore", getClass().getResource("/clientKeystore/client.jks").getFile());
                SSLSocketFactory sslsocketfactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
				s = (SSLSocket)sslsocketfactory.createSocket(serverIP,serverPort);
			}
			else{
				s = new Socket(serverIP, serverPort);
			}
		} catch (UnknownHostException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setLog() {
    	LogFormatter formatter = new LogFormatter();
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(formatter);
        
		Logger log = Logger.getLogger(Client.class.getName());
		log.setUseParentHandlers(false);
		log.addHandler(handler);
		log.setLevel(Level.FINE);
		this.log = log;
	}
	
	//Given the situation that in client side, all the operations except fetch are almost the same. So, 
	//we can just implement these functions with one method
	public void sendJSON(JSONObject j,boolean ifDebug,String host,String port) throws JSONException {
		try {
			DataInputStream input;
			DataOutputStream output;

				 input = new DataInputStream(s.getInputStream());
				 output = new DataOutputStream(s.getOutputStream());


			if (ifDebug) {
				log.info("SENT:" + j.toString());
			}
			output.writeUTF(j.toString());
			output.flush();

			String message = "";
			long startTime = System.currentTimeMillis();
			long currentTime = 0;
			
			//Setting a time limit of 5 mins, and keep receiving msgs from the server 
			while ((currentTime = System.currentTimeMillis()) - startTime <= 5*60*1000) {
				JSONObject msgGet = null;
				if ((message = input.readUTF())!= null) {
					//message = input.readUTF();
					msgGet = new JSONObject(message);
					if(ifDebug) {
						log.info("RECEIVED:" + msgGet.toString());
					} else {
						System.out.println(msgGet.toString());
					}
					if (j.getString("command").equalsIgnoreCase("QUERY") && msgGet.has("resultSize")) {
						break;
					} else if (!j.getString("command").equalsIgnoreCase("QUERY") && msgGet.has("response")) {
						break;
					}
				}
			}
			if (currentTime - startTime > 5*60*1000) {
				System.out.println("Time out");
				System.exit(0);
			}
		} catch (UnknownHostException e) {
			System.exit(-1);
		} catch (SocketException e) {
			System.exit(-1);
		} catch (Exception e) {
			System.exit(-1);
		} 
	}

	public void fetch(JSONObject j,boolean ifDebug,String host,String port) throws JSONException {
		int resourceSize = 0;
		String uri = "";
		try {

			DataInputStream input = new DataInputStream(s.getInputStream());
			DataOutputStream output = new DataOutputStream(s.getOutputStream());
			
			if (ifDebug) {
				log.info("fetching from " + host + ":" + port);
				log.info("SENT:" + j.toString());
			}
			output.writeUTF(j.toString());
			output.flush();

			String message = "";
			JSONObject msgGet = null;
			while (true) {
				if (input.available() > 0) {
					message = input.readUTF();
					msgGet = new JSONObject(message);
					if(ifDebug) {
						log.info("RECEIVED:" + msgGet.toString());
					} else {
						System.out.println(msgGet.toString());
					}
				}

				// fetch the file using the uri and resource size
				if (msgGet != null && msgGet.has("resourceSize")) {
					resourceSize = msgGet.getInt("resourceSize");
					if (Runtime.getRuntime().maxMemory() < resourceSize) {
						System.out.println("Out of memory");
						System.exit(-1);
					}
					uri = msgGet.getString("uri");
					
					String filename = uri.split("/")[uri.split("/").length - 1];
					
					int bytesRead = 0;
				        int current = 0;
					
					byte [] mybytearray  = new byte [resourceSize];
					File file = new File(filename);
					if(!file.exists())
						file.createNewFile();
				        InputStream is = s.getInputStream();
				        FileOutputStream fos = new FileOutputStream(file);
				        BufferedOutputStream bos = new BufferedOutputStream(fos);
				        bytesRead = is.read(mybytearray,0,mybytearray.length);
				        current = bytesRead;

				        do {
				            bytesRead =
				            is.read(mybytearray, current, (mybytearray.length-current));
				            if(bytesRead >= 0) current += bytesRead;
				        } while(bytesRead > 0);

				        bos.write(mybytearray, 0 , current);
				        bos.flush();
		
				        if (fos != null) fos.close();
				        if (bos != null) bos.close();
				    
				} else if (msgGet != null && msgGet.has("resultSize")) {
					break;
				}

			}
		} catch (UnknownHostException e) {
			System.exit(-1);
		} catch (SocketException e) {
			System.exit(-1);
		} catch (Exception e) {
			System.exit(-1);
		}
	}

	public void subscribe(JSONObject j,boolean ifDebug,String host,String port) throws JSONException {
		try {
			DataInputStream input = new DataInputStream(s.getInputStream());
			DataOutputStream output = new DataOutputStream(s.getOutputStream());

			if (ifDebug) {
				log.info("SENT:" + j.toString());
			}
			output.writeUTF(j.toString());
			output.flush();


			while (true) {
				String message = "";
				JSONObject msgGet = null;
				if (input.available() > 0) {
					message = input.readUTF();
					msgGet = new JSONObject(message);
					if(ifDebug) {
						log.info("RECEIVED:" + msgGet.toString());
					} else {
						System.out.println(msgGet.toString());
					}
					if(msgGet.has("resultSize")) {
						break;
					}
				}

				long start = System.currentTimeMillis();

				while(System.currentTimeMillis() - start < 1000) {


					String str = null;
					BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
					if (bf.ready()) {
						str = bf.readLine();
					}
					if (str != null && str.length() == 0) {
						JSONObject sentJSON = new JSONObject("{}");
						String id = Operations.getId(port);
						sentJSON.put("command", "UNSUBSCRIBE");
						sentJSON.put("id", id);
						if (ifDebug) {
							log.info("SENT:" + sentJSON.toString());
						}
						output.writeUTF(sentJSON.toString());
						output.flush();

					}
				}
			}
		} catch (UnknownHostException e) {
			System.out.println(e.getMessage());
			System.exit(-1);
		} catch (SocketException e) {
			System.out.println(e.getMessage());
			System.exit(-1);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.exit(-1);
		}
	}

}
