import java.io.*;
import java.net.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.*;

public class Client {

	private Socket s = null;
	private Logger log = null;

	Client(String serverIP, int serverPort) {
		this.setLog();
		LogFormatter formatter = new LogFormatter();
       ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(formatter);
		Logger logIni = Logger.getLogger(Main.class.getName());
		logIni.setUseParentHandlers(false);
		logIni.setLevel(Level.INFO);
		logIni.info("setting debug on");
		try {
			s = new Socket(serverIP, serverPort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setLog() {
    	LogFormatter formatter = new LogFormatter();
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(formatter);
        
		Logger log = Logger.getLogger(Main.class.getName());
		log.setUseParentHandlers(false);
		log.addHandler(handler);
		log.setLevel(Level.FINE);
		this.log = log;
	}
	
	public void sendJSON(JSONObject j,boolean ifDebug,String host,String port) throws JSONException {
		try {
			
			DataInputStream input = new DataInputStream(s.getInputStream());
			DataOutputStream output = new DataOutputStream(s.getOutputStream());

			if (ifDebug) {
				//log.info(j.getString("command").toLowerCase() + "ing to " + host + ":" + port);
				log.info("SENT:" + j.toString());

			}
			
			output.writeUTF(j.toString());
			output.flush();
			
			

			String message = "";
			long startTime = System.currentTimeMillis();
			long currentTime = 0;
			while ((currentTime = System.currentTimeMillis()) - startTime <= 5*60*1000) {
				JSONObject msgGet = null;
				if (input.available() > 0) {
					message = input.readUTF();
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
			System.out.println(e.getMessage());
			System.exit(-1);
		} catch (SocketException e) {
			System.out.println(e.getMessage());
			System.exit(-1);
		} catch (IOException e) {
			//Do something
			System.out.println(e.getMessage());
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
			System.out.println(e.getMessage());
			System.exit(-1);
		} catch (SocketException e) {
			System.out.println(e.getMessage());
			System.exit(-1);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(-1);
		}
	}

}
