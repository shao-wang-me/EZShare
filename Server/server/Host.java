package server;

public class Host {
	private String hostname;
	private int port;
	
	public Host(String hostname, int port){
		this.hostname = hostname;
		this.port = port;
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
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Host) {
			if (this.getHostname().equals(((Host) obj).getHostname()) && this.getPort() == ((Host) obj).getPort()) {
				return true;
			} else {
				return false;
			}
		}
	        return (this == obj);
	}
}
