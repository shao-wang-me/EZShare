package server;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;


public class Server {
	private static final Logger LOGGER = Logger.getLogger( Server.class.getName() );
	
	private String hostname = "Justin_Sever";
	private String port  = " 20006";
	private String interval = "600";
	private int  intervalLimit = 30;
	private String secret = "123";//RandomStringUtils.randomAlphanumeric(20);
	private resourceList resourceList;
	private serverList serverList;
	
	public static void main(String[] args)throws Exception{
		Server server = new Server();
		server.initialize(args);
	}
	
	public  void initialize(String[] args)throws Exception{
		//set default parameters
		
		//HashMap<String, Resource> V = new HashMap<String, Resource>();
		//HashMap<String, Map> K = new HashMap<String, Map>();
		resourceList = new resourceList();
		resourceList.initialResourceList();
		serverList = new serverList();
		serverList.initialserverList();
		
		try{
			//test 
            ArrayList<String> list = new ArrayList<String>();
            list.add("web");
            list.add("html");
            Resource test = new Resource("justin","this is a description", 
            		list, "http://www.unimelb.edu.au", "cctv", "justin", "justin's server");
            //V.put(test.getOwner(), test);
            //K.put(test.getUri(), V);
            //String key = test.getChannel()+","+test.getUri();
            resourceList.add(test);
            
             test = new Resource("steven","this is a test2", 
            		list, "http://www.google.com", "", "", null);
             //V.put(test.getOwner(), test);
             //K.put(test.getUri(), V);
             //key = test.getChannel()+","+test.getUri();
             resourceList.add(test);;
             
             test = new Resource("steven","this is a test2", 
             		list, "file:/Users/xutianyu/Pictures/logo.jpg", "cctv", "justin", "justin's server");
              //V.put(test.getOwner(), test);
              //K.put(test.getUri(), V);
              //key = test.getChannel()+","+test.getUri();
              resourceList.add(test);
             
             Host h1 = new Host("10.13.111.7", 20006);
             Host h2 = new Host("192.168.1.2", 3002);
             serverList.add(h1);
             serverList.add(h2);           
            
            
			
			//build new command line options
			Options options = new Options();
			//options.addOption("t", false, "display current time");
			options.addOption("a", "advertisedhostname",true,"advertised hostname");
			options.addOption("c", "connectionintervallimit",true,"connection interval limit in seconds");
			options.addOption("e", "exchangeinterval",true,"exchange interval in seconds");
			options.addOption("p" ,"port",true,"server port, an integer");
			options.addOption("s", "secret",true,"secret");
			options.addOption("d", "debug",true,"print debug information");
			
			
			
			//parse Commands and arguments 
			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse(options,args);
			
			//test CommandLine
			if( cmd.hasOption("advertisedhostname")){
				setHostname(cmd.getOptionValue("advertisedhostname"));
			}
			if( cmd.hasOption("connectionintervallimit")){
				setIntervalLimit(Integer.parseInt( cmd.getOptionValue("connectionintervallimit")));
			}
			if(cmd.hasOption("port")){
				setPort(cmd.getOptionValue("port"));
			}
			if( cmd.hasOption("exchangeinterval")){
				setInterval(cmd.getOptionValue("exchangeinterval"));
			}
			if( cmd.hasOption("secret")){
				setSecret(cmd.getOptionValue("secret"));
			}

			 
			
			System.out.println("Port number:"+getPort()+"time: "+getInterval()+" secret: "+getSecret());
			Logger log = Logger.getLogger(Server.class.getName());
			log.setLevel(Level.INFO); 
			log.info("- Starting the EZShare Server");
			log.info("- using secret:"+secret);
			log.info("- using advertised hostname:"+hostname);
			log.info("- bound to port"+port);
			log.info("- started");
			
			System.out.println("Welcome to the server!");
			System.out.println(new Date());
			
			ServerSocket server = new ServerSocket(Integer.parseInt(getPort()));
			
			//time schedule thread pool
			ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
			
			exec.scheduleAtFixedRate(new Runnable() {
	            public void run() {            	
	                System.out.println("Exchange: ");
	                //random choose a server
	                int index = (int) (Math.random()*serverList.getServerList().size());
	                Host h = serverList.getServerList().get(index);
	                //send exchange command to it 
	                JSONObject update = new JSONObject();
	                serverUpdate s =  new serverUpdate(serverList);
	                update = s.update(h.getHostname(), h.getPort());
	                if(update.getString("response").equals("error")){
	                	serverList.delete(h);;
	                }
	                //return ressult
	                
	            }
	        }, getIntervalLimit() , getIntervalLimit() , TimeUnit.SECONDS);
			
			//thread pool to increase efficiency
			ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
			//Socket client = new Socket();
			Boolean f = true ;
			while(f){
				Socket client = server.accept();
				System.out.println("connection succeed !");
				ServerThread s = new ServerThread(client, getSecret(), resourceList, serverList);
				executor.execute(s);
			}
			executor.shutdown();
			server.close();
			
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}
	
	
	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getInterval() {
		return interval;
	}

	public void setInterval(String interval) {
		this.interval = interval;
	}

	public int getIntervalLimit() {
		return intervalLimit;
	}

	public void setIntervalLimit(int intervalLimit) {
		this.intervalLimit = intervalLimit;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

}
