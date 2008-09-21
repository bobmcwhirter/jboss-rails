package org.jboss.rails.metadata;

public class RailsMetaData {
	
	private String environment;

	public RailsMetaData() {
		this.environment = "development";
	}
	
	public String getEnvironment() {
		return this.environment;
	}
	
	public void setEnvironment(String environment) {
		this.environment = environment;
	}

}
