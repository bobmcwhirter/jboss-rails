package org.jboss.ruby.enterprise.endpoints.metadata;

import java.net.URL;


public class RubyEndpointMetaData {
	
	//private String dir;
	
	private String name;
	
	private URL wsdlLocation;
	private String classLocation;
	private String endpointClassName;
	
	private String portName;
	private String targetNamespace;
	
	//private InboundSecurityMetaData inboundSecurity;

	public RubyEndpointMetaData() {
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setWsdlLocation(URL wsdlLocation) {
		this.wsdlLocation = wsdlLocation;
	}
	
	public URL getWsdlLocation() {
		return this.wsdlLocation;
	}
	
	public void setClassLocation(String classLocation) {
		this.classLocation = classLocation;
	}
	
	public String getClassLocation() {
		return this.classLocation;
	}
	
	public void setEndpointClassName(String endpointClassName) {
		this.endpointClassName = endpointClassName;
	}
	
	public String getEndpointClassName() {
		return this.endpointClassName;
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
		return "[RubyWebServiceMetaData: name=" + this.name + "; wsdlLocation=" + this.wsdlLocation + "; endpointClassName=" + this.endpointClassName + "]";
	}

}

