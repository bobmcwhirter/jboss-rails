package org.jboss.ruby.enterprise.web.rack.metadata;

public class RackWebApplicationMetaData {
	
	private String host;
	private String context;
	
	private String rackApplicationFactoryName;
	
	private String staticPathPrefix;

	public RackWebApplicationMetaData() {
		
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public String getHost() {
		return this.host;
	}
	
	public void setContext(String context) {
		this.context = context;
	}
	
	public String getContext() {
		return this.context;
	}
	
	public void setRackApplicationFactoryName(String rackApplicationFactoryName) {
		this.rackApplicationFactoryName = rackApplicationFactoryName;
	}
	
	public String getRackApplicationFactoryName() {
		return this.rackApplicationFactoryName;
	}
	
	public void setStaticPathPrefix(String staticPathPrefix) {
		this.staticPathPrefix = staticPathPrefix;
	}
	
	public String getStaticPathPrefix() {
		return this.staticPathPrefix;
	}

}
