package org.jboss.ruby.enterprise.webservices.metadata;


public class RubyWebServiceMetaData {
	
	private String dir;
	private String name;
	
	private String portName;
	private String targetNamespace;

	public RubyWebServiceMetaData() {
		
	}
	
	public void setDirectory(String dir) {
		this.dir = dir;
	}
	
	public String getDirectory() {
		return this.dir;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setTargetNamespace(String targetNamespace) {
		this.targetNamespace = targetNamespace;
	}
	
	public String getTargetNamespace() {
		return this.targetNamespace;
	}
	
	public void setPortName(String portName) {
		this.portName = portName;
	}
	
	public String getPortName() {
		return this.portName;
	}
	
	public String toString() {
		return "[RubyWebServiceMetaData: dir=" + this.dir + "; name=" + this.name + "]";
	}

}

