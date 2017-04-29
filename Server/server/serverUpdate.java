package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import support.Debug;
import variable.serverList;

public class serverUpdate {
	

	public static JSONObject update(serverList serverList, String Hostname, int port, boolean debug, Logger log) {
		// TODO Auto-generated method stub
		

        try {
            Socket client = new Socket(Hostname, port);  

			DataOutputStream out = new DataOutputStream(client.getOutputStream());
			
	        DataInputStream buf = new DataInputStream(client.getInputStream());
	        
			JSONObject result = new JSONObject();
	        
	        boolean flag = true;

	        // send exchange command to verify whether server works
	        while(flag){
	            JSONObject temp = new JSONObject();
	            temp.put("command", "EXCHANGE");
				JSONArray array = new JSONArray(serverList.getServerList());
				temp.put("serverList", array);
	            String str = temp.toString();
	                  
	            //send exchange command to selected server    
	            out.writeUTF(str);
				Debug.printDebug('s',str, debug, log);
	            try{
	            	String echo = buf.readUTF();
					Debug.printDebug('r',echo, debug, log);
					temp = new JSONObject(echo);
		            if(temp.has("response")){
		            	result = temp;
		            	flag = false;
		            }
	            	
	            }catch(SocketTimeoutException f){
	                JSONObject error = new JSONObject();
	                error.put("errorMessage", "Exchange Timeout");
	                error.put("response", "error");
	    			result = error;
	    			flag = false;

	            }
	            
	        }
	        
			client.close();
	        return result;
	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			// any exception return error message
            JSONObject error = new JSONObject();
            error.put("errorMessage", "Exchange Timeout");
            error.put("response", "error");
			return error;
		}

		
	}


	
	

}
