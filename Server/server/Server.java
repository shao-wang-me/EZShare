package server;

import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;

import support.LogFormatter;


public class Server {
	// initial parameters
	private String hostname = "Awesome_Sever";
	private int port  = 20006;
	private String interval = "600";
	private int  intervalLimit = 1000;
	private String secret = RandomStringUtils.randomAlphanumeric(20);
	private resourceList resourceList;
	private serverList serverList;
	private Boolean debug = true;
	
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
             serverList.add(h1);
           
            
            
			
			//build new command line options
			Options options = new Options();
			//options.addOption("t", false, "display current time");
			options.addOption("a", "advertisedhostname",true,"advertised hostname");
			options.addOption("c", "connectionintervallimit",true,"connection interval limit in seconds");
			options.addOption("e", "exchangeinterval",true,"exchange interval in seconds");
			options.addOption("p" ,"port",true,"server port, an integer");
			options.addOption("s", "secret",true,"secret");
			options.addOption("d", "debug",false,"print debug information");
			
			
			
			//parse Commands and arguments 
			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse(options,args);
			
			//test CommandLine
			if( cmd.hasOption("advertisedhostname")){
				setHostname(cmd.getOptionValue("advertisedhostname"));
			}
			if( cmd.hasOption("connectionintervallimit")){
				setIntervalLimit(Integer.parseInt( cmd.getOptionValue("connectionintervallimit"))*1000);
			}
			if(cmd.hasOption("port")){
				setPort(Integer.parseInt(cmd.getOptionValue("port")));
			}
			if( cmd.hasOption("exchangeinterval")){
				setInterval(cmd.getOptionValue("exchangeinterval"));
			}
			if( cmd.hasOption("secret")){
				setSecret(cmd.getOptionValue("secret"));
			}
			if( cmd.hasOption("debug")){
				setDebug(true);
			}

			 
			//Print log Info
	        LogFormatter formatter = new LogFormatter();
	        ConsoleHandler handler = new ConsoleHandler();
	        handler.setFormatter(formatter);
	        
			Logger log = Logger.getLogger(Server.class.getName());
			log.setUseParentHandlers(false);
			log.addHandler(handler);
			log.setLevel(Level.INFO); 
			log.info("- Starting the EZShare Server");
			log.info("- using secret:"+secret);
			log.info("- using advertised hostname:"+hostname);
			log.info("- bound to port"+port);
			log.info("- started");
			
			ServerSocket server = new ServerSocket(getPort());
			
			//time schedule thread pool
			ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
			
			TimerTask timerTask = new TimerTask(serverList, resourceList);
			
			exec.scheduleAtFixedRate(timerTask,Integer.parseInt(getInterval()) , Integer.parseInt(getInterval()) , TimeUnit.SECONDS);
			
			//thread pool to increase efficiency
			ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
			//Socket client = new Socket();
			
			//create multiple threads to build connections
			Boolean f = true ;
			while(f){
				Socket client = server.accept();
				//System.out.println("connection succeed !");
				Thread.sleep(getIntervalLimit());
				if(client.isConnected()){
					ServerThread s = new ServerThread(client, getSecret(), 
							resourceList, serverList, getDebug(), getHostname(), getPort());

					executor.execute(s);
				}
				else{
					client.close();
				}
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

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
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

	public Boolean getDebug() {
		return debug;
	}

	public void setDebug(Boolean debug) {
		this.debug = debug;
	}

}
