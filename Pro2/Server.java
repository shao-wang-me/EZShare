package EZShare;

import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import netscape.javascript.JSObject;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.RandomStringUtils;

import org.json.JSONObject;
import server.ServerThread;
import server.TimerTask;
import server.subscribeThread;
import server.relayThread;
import support.LogFormatter;
import variable.*;

/**
 * Created by xutianyu on 4/25/17.
 * Main server class
 *
 */

public class Server {
	// initial parameters
	private String hostname = "Team_Awesome_Sever";
	private int port  = 10000;
	private String interval = "600";
	private int  intervalLimit = 1000;
	private String secret = RandomStringUtils.randomAlphanumeric(20);
	private variable.resourceList resourceList;
	private variable.serverList serverList;
	private Boolean debug = false;
	/**
	 * New resource list is for storing new added resources
	 * when executing SHARE and PUBLISH commands
	 * Subscribe list is for storing the subscriptions sent
	 * by clients
	 */
	private volatile variable.resourceList newResourceList;
	private volatile variable.subscribeList subscribeList;
	private volatile variable.subscribeList readyToSend;
	private volatile variable.serverList serverDeleteList;
	private volatile variable.serverList serverAddList;
	
	public static void main(String[] args)throws Exception{
		Server server = new Server();
		server.initialize(args);
	}
	
	public  void initialize(String[] args)throws Exception{

		//set default parameters
		resourceList = new resourceList();
		resourceList.initialResourceList();
		serverList = new serverList();
		serverList.initialserverList();
		//added by yankun
		newResourceList  = new resourceList();
		newResourceList.initialResourceList();
		subscribeList = new subscribeList();
		readyToSend = new subscribeList();
		serverDeleteList = new serverList();
		serverDeleteList.initialserverList();
		serverAddList = new serverList();
		serverAddList.initialserverList();
		
		try{
			
			//build new command line options
			Options options = new Options();
			//options.addOption("t", false, "display current time");
			options.addOption("advertisedhostname",true,"advertised hostname");
			options.addOption("connectionintervallimit",true,"connection interval limit in seconds");
			options.addOption("exchangeinterval",true,"exchange interval in seconds");
			options.addOption("port",true,"server port, an integer");
			options.addOption("secret",true,"secret");
			options.addOption("debug",false,"print debug information");
			
			
			
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
			if(debug){
				log.info("- setting debug on");
			}
			
			ServerSocket server = new ServerSocket(getPort());
			
			//Time schedule thread pool
			ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
			Host local = new Host(getHostname(),getPort());
			TimerTask timerTask = new TimerTask(serverList, serverDeleteList, resourceList,local, debug, log);
			exec.scheduleAtFixedRate(timerTask,Integer.parseInt(getInterval()) , Integer.parseInt(getInterval()) , TimeUnit.SECONDS);

			//Single thread executor for the subscription thread, added by yankun
			ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
			subscribeThread subscriptionThread = new subscribeThread(newResourceList, debug, intervalLimit , subscribeList, readyToSend);
			singleThreadExecutor.execute(subscriptionThread);

			//Single thread executor for the relay thread, added by yankun
			ExecutorService relayThreadExecutor = Executors.newSingleThreadExecutor();
			relayThread relayThread = new relayThread(newResourceList, debug , subscribeList, serverList, serverDeleteList, serverAddList, readyToSend);
			relayThreadExecutor.execute(relayThread);

			//Thread pool to increase efficiency
			ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

			//create multiple threads to build connections
			Boolean f = true ;
			while(f){
				 Socket client = server.accept();
				if(client.isConnected()){
					ServerThread s = new ServerThread(client, getSecret(),
							resourceList, newResourceList, serverList, serverAddList, getDebug(), getHostname(), getPort(), getIntervalLimit(), subscribeList, readyToSend);

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
			System.out.println("exception");
			System.out.println(e.getMessage());
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
