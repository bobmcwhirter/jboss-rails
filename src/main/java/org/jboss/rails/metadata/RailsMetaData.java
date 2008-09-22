package org.jboss.rails.metadata;

public class RailsMetaData {
	
	private String environment;
	private String railsRoot;

	public RailsMetaData() {
		this.environment = "development";
	}
	
	public String getEnvironment() {
		return this.environment;
	}
	
	public void setEnvironment(String environment) {
		this.environment = environment;
	}
	
	public String getRailsRoot() {
		return this.railsRoot;
	}
	
	public void setRailsRoot(String railsRoot) {
		this.railsRoot = railsRoot;
	}

}
