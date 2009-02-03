package org.jboss.ruby.enterprise.webservices;

import javax.xml.transform.Source;
import javax.xml.ws.Provider;
import javax.xml.ws.WebServiceProvider;

import org.jboss.logging.Logger;

@WebServiceProvider
public class RubyWebServiceProvider implements Provider<Source> {
	
	private static Logger log = Logger.getLogger( RubyWebServiceProvider.class );
	private String serviceName;

	protected void initialize(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public Source invoke(Source request) {
		log.info( "handle upon " + request );
		return request;
	}
	
	

}
