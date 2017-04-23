import java.io.*;
import java.net.*;

import org.json.*;

public class Client {

	private Socket s = null;

	Client(String serverIP, int serverPort) {
		try {
			s = new Socket(serverIP, serverPort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendJSON(JSONObject j,boolean ifDebug) throws JSONException {
		try {
			DataInputStream input = new DataInputStream(s.getInputStream());
			DataOutputStream output = new DataOutputStream(s.getOutputStream());

			if (ifDebug) {
				System.out.println("SENT:" + j.toString());
			}
			
			output.writeUTF(j.toString());
			output.flush();

			String message = "";
			while (true) {
				JSONObject msgGet = null;
				if (input.available() > 0) {
					message = input.readUTF();
					msgGet = new JSONObject(message);
					System.out.println(ifDebug?"RECEIVED:" + msgGet.toString():msgGet.toString());
					if (j.getString("command").equalsIgnoreCase("QUERY") && msgGet.has("resultSize")) {
						break;
					} else if (!j.getString("command").equalsIgnoreCase("QUERY") && msgGet.has("response")) {
						break;
					}
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			//Do something
		}
	}

	public void fetch(JSONObject j) throws JSONException {
		int resourceSize = 0;
		String uri = "";
		try {
			DataInputStream input = new DataInputStream(s.getInputStream());
			DataOutputStream output = new DataOutputStream(s.getOutputStream());

			output.writeUTF(j.toString());
			output.flush();

			String message = "";
			JSONObject msgGet = null;
			while (true) {
				if (input.available() > 0) {
					message = input.readUTF();
					System.out.println(message);
					msgGet = new JSONObject(message);
				}

				// fetch the file using the uri and resource size

				if (msgGet != null && msgGet.has("resourceSize")) {
					resourceSize = msgGet.getInt("resourceSize");
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
			e.printStackTrace();
		} catch (IOException e) {

		}
	}

}
