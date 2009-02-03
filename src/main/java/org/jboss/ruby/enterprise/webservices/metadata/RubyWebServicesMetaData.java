package org.jboss.ruby.enterprise.webservices.metadata;

import java.util.ArrayList;
import java.util.List;

public class RubyWebServicesMetaData {
	
	private List<RubyWebServiceMetaData> webServices = new ArrayList<RubyWebServiceMetaData>();
	
	public RubyWebServicesMetaData() {
		
	}
	
	public void addWebService(RubyWebServiceMetaData metaData) {
		this.webServices.add( metaData );
	}
	
	public String toString() {
		return "[RubyWebServicesMetaData: webServices=" + this.webServices + "]";
	}

	public List<RubyWebServiceMetaData> getWebSerices() {
		return this.webServices;
	}

}
