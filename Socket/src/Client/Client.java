package Client;
import java.net.*;
import java.io.*;

public class Client {
	public static void main(String args[]){
		Socket s = null;
		try{
			
			
			int serverPort = 3780;
			s = new Socket(args[0], serverPort);
			System.out.println("Connection Established");
			DataInputStream in = new DataInputStream(s.getInputStream());
			DataOutputStream out = new DataOutputStream(s.getOutputStream());
			System.out.print("Sending data");
			out.writeUTF("connecting test");
			String data = in.readUTF();
			System.out.print(data);
			
			
			
			
		}catch(UnknownHostException e){
			System.out.println("Socket:" + e.getMessage());
		}catch(EOFException e){
			System.out.println("EOF:" + e.getMessage());
		}catch(IOException e){
			System.out.println("readline:" + e.getMessage());
		}
		finally{
			if(s != null) 
				try{
					s.close();
				}catch(IOException e){
					System.out.println("close:" + e.getMessage());
				}
		}
	}
}
