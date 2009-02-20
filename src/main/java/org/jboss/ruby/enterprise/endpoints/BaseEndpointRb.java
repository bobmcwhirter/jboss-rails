package org.jboss.ruby.enterprise.endpoints;

import java.security.Principal;

public class BaseEndpointRb {
	
	private Principal principal;
	private Object request;
	private String responseCreator;

	public BaseEndpointRb() {
	}

	public void setPrincipal(Principal principal) {
		this.principal = principal;
	}

	public void setRequest(Object request) {
		this.request = request;
	}
	
	public Object getRequest() {
		return this.request;
	}

	public void setResponseCreator(String responseCreator) {
		this.responseCreator = responseCreator;
	}
	
	public String getResponseCreator() {
		return this.responseCreator;
	}

	public String toString() {
		return "[BaseEndpointRb: principal=" + principal + "; request=" + request + "; responseCreator=" + responseCreator + "]";
	}

}
