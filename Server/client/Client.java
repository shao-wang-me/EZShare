package client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;  
import java.io.InputStreamReader;  
import java.io.PrintStream;  
import java.net.Socket;  
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.json.JSONObject;

import com.google.gson.Gson;


  
public class Client {  
    public static void main(String[] args) throws IOException {  
        //客户端请求与本机在20006端口建立TCP连接   
        Socket client = new Socket("127.0.0.1", 20006);  
        client.setSoTimeout(100);  
        //获取键盘输入   
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));  
        //获取Socket的输出流，用来发送数据到服务端    
        //PrintStream out = new PrintStream(client.getOutputStream());  
        DataOutputStream out = new DataOutputStream(client.getOutputStream());

        //获取Socket的输入流，用来接收从服务端发送过来的数据    
        //BufferedReader buf =  new BufferedReader(new InputStreamReader(client.getInputStream()));
        DataInputStream buf = new DataInputStream(client.getInputStream());
        
        

        boolean flag = true;  
        while(flag){  
            System.out.print("输入信息：");  
            ArrayList<String> list = new ArrayList<String>();
            list.add("web");
            list.add("html");
            Resource resource = new Resource("justin","this is a description", 
            		list, "http://www.unimelb.edu.au", "", "", null);
            Command command = new Command("PUBLISH", resource);
            Command_template fetch = new Command_template("FETCH", resource);
            
            ArrayList<Host> serverList = new ArrayList<Host>();
            Host h1 = new Host("192.168.1.1", 3001);
            Host h2 = new Host("192.168.1.2", 3002);
            serverList.add(h1);
            serverList.add(h2); 
            
            Gson gson = new Gson();
            //gson.toJson(command);
            //JSONObject json = new JSONObject();
            //json.put("command", "PUBLISH");
            //json.put("resource", resource.toJSON());
            String str =  gson.toJson(serverList);
            JSONObject temp = new JSONObject();
            temp.put("serverList", serverList);
            temp.put("command", "EXCHANGE");
            str = temp.toString();
            System.out.println("from client: "+str);

            //发送数据到服务端    
            out.writeUTF(str); 
            if("bye".equals(str)){  
                flag = false;  
            }else{  
                try{  
                    //从服务器端接收数据有个时间限制（系统自设，也可以自己设置），超过了这个时间，便会抛出该异常  
                    String echo = buf.readUTF();  
                    //System.out.println(echo);  
                }catch(SocketTimeoutException e){  
                    System.out.println("Time out, No response");  
                }  
            }  
        }  
        input.close();  
        if(client != null){  
            //如果构造函数建立起了连接，则关闭套接字，如果没有建立起连接，自然不用关闭  
            client.close(); //只关闭socket，其关联的输入输出流也会被关闭  
        }  
    }  
}