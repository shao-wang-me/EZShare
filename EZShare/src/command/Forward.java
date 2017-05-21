package command;

import com.google.gson.JsonSyntaxException;

import security.*;

import org.json.JSONException;
import org.json.JSONObject;
import variable.resourceList;
import variable.Host;
import com.google.gson.Gson;
import variable.Resource;
import support.Debug;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.logging.Logger;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by xutianyu on 4/25/17.
 * build connection with host h
 * forward query to other server
 *
 */
public class Forward {

    public static resourceList forward(String str, Host h, boolean debug, Logger log, boolean secure){
        resourceList r = new resourceList();
        r.initialResourceList();

        // build connection with host h
        try{
            Socket agent ;
            SocketAddress socketaddr = new InetSocketAddress(h.getHostname(), h.getPort());
        	if (secure) {

                InputStream keystoreInput = Forward.class
                        .getResourceAsStream("/serverKeystore/server.jks");
                InputStream truststoreInput = Forward.class
                        .getResourceAsStream("/clientKeystore/client.jks");
                support.SetSecureSocket.setSSLFactories(keystoreInput, "comp90015", truststoreInput);
                keystoreInput.close();
                truststoreInput.close();
                //System.setProperty("javax.net.ssl.trustStore",
                        //Forward.class.getResource("/clientKeystore/client.jks").getFile());
                SSLSocketFactory sslsocketfactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
                agent = (SSLSocket)sslsocketfactory.createSocket();
                agent.connect(socketaddr, 5000);
        	} else {
                agent = new Socket();
                agent.connect(socketaddr, 5000);
        	}
            

            
            //Socket 输出流， 转发查询
            DataOutputStream forward = new DataOutputStream(agent.getOutputStream());
            //获取Socket的输入流，用来接收从服务端发送过来的数据
            DataInputStream in = new DataInputStream(agent.getInputStream());
            Boolean f = true;
            forward.writeUTF(str);
            Debug.printDebug('s',str, debug, log );
            try{
                boolean success = false;
                while(f){
                    String info = in.readUTF();
                    Debug.printDebug('r',info , debug, log );
                    //System.out.println(info);
                    JSONObject Info = new JSONObject(info);

                    if(Info.has("resultSize")){
                        f = false;
                    }
                    else{
                        if(success){
                            Gson gson = new Gson();
                            Resource resourceTemplate ;
                            resourceTemplate = gson.fromJson(info, Resource.class);
                            r.add(resourceTemplate);
                        }
                    }
                    if(Info.has("response")){
                        JSONObject j = new JSONObject(info);
                        if(j.get("response").equals("success")){
                            success = true;
                            //out.writeUTF(info);
                        }
                        if(j.get("response").equals("error")){
                            f = false;
                        }
                    }

                }

            }catch(JSONException j){
                return r;
            }catch(JsonSyntaxException s){
                return r;
            }catch(SocketTimeoutException t){
                return r;
            }
            catch(Exception t){
                return r;
            }
            finally{
                forward.close();
                in.close();
                agent.close();
            }

        }catch(Exception e){
            return r;
        }
        return r;

    }


}
