package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;

import org.json.JSONObject;

import com.google.gson.Gson;

public class serverUpdate {
	
	private serverList serverList; 
	serverUpdate(serverList serverList){
		this.setServerList(serverList);
	}

	public JSONObject update(String Hostname, int port) {
		// TODO Auto-generated method stub
		

        try {
            Socket client = new Socket(Hostname, port);  

			DataOutputStream out = new DataOutputStream(client.getOutputStream());
			
	        DataInputStream buf = new DataInputStream(client.getInputStream());
	        
			JSONObject result = new JSONObject();
	        
	        boolean flag = true;  
	        while(flag){  
	            JSONObject temp = new JSONObject();
	            temp.put("serverList", serverList);
	            temp.put("command", "EXCHANGE");
	            String str = temp.toString();
	                  
	            //send exchange command to selected server    
	            out.writeUTF(str); 
	            try{
	            	String echo = buf.readUTF(); 
		            temp = new JSONObject(echo);
		            if(temp.has("response")){
		            	result = temp;
		            	flag = false;
		            }
	            	
	            }catch(SocketTimeoutException f){
	                JSONObject error = new JSONObject();
	                error.put("errorMessage", "Timeout");
	                error.put("response", "error");
	    			result = error;
	            }
	            
	        }
	        
			client.close();
	        return result;
	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("errorMessage", "Timeout");
            error.put("response", "error");
			return error;
		}

		
	}

	public serverList getServerList() {
		return serverList;
	}

	public void setServerList(serverList serverList) {
		this.serverList = serverList;
	}
	
	

}
