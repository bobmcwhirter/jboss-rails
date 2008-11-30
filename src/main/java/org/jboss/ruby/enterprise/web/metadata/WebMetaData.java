package org.jboss.ruby.enterprise.web.metadata;


public class WebMetaData {
	
	private String contextPath = "/";
	private String virtualHost = null;
	
	public WebMetaData() {
		
	}
	
	public WebMetaData(String contextPath) {
		this.contextPath = contextPath;
	}
	
	public WebMetaData(String contextPath, String virtualHost) {
		this.virtualHost = virtualHost;
	}
	
	public String getContextPath() {
		return this.contextPath;
	}
	
	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}
	
	public String getVirtualHost() {
		return this.virtualHost;
	}
	
	public void setVirtualHost(String virtualHost) {
		this.virtualHost = virtualHost;
	}

	

}
