package org.jboss.ruby.enterprise.web.metadata;


public class RackWebMetaData {
	
	private String context = "/";
	private String host = null;
	
	private String contextConfigClassName = "org.jboss.ruby.enterprise.web.tomcat.RackContextConfig";
	private String docBase;
	private Object frameworkMetaData;
	
	public RackWebMetaData() {
		
	}
	
	public RackWebMetaData(String context) {
		this.context = context;
	}
	
	public RackWebMetaData(String context, String host) {
		this.context = context;
		this.host = host;
	}
	
	public Object getFrameworkMetaData() {
		return this.frameworkMetaData;
	}
	
	public void setFrameworkMetaData(Object frameworkMetaData) {
		this.frameworkMetaData = frameworkMetaData;
	}
	
	public String getContext() {
		return this.context;
	}
	
	public void setContext(String context) {
		this.context = context;
	}
	
	public String getHost() {
		return this.host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public String getContextConfigClassName() {
		return this.contextConfigClassName;
	}
	
	public void setContextConfigClassName(String contextConfigClassName) {
		this.contextConfigClassName = contextConfigClassName;
	}
	
	public String getDocBase() {
		return this.docBase;
	}
	
	public void setDocBase(String docBase) {
		this.docBase = docBase;
	}

	
	public String toString() {
		return "[WebMetaData: context=" + this.context + "; host=" + this.host + "]";
	}


}
