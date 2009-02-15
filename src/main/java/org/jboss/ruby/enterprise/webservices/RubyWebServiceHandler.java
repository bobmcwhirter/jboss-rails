package org.jboss.ruby.enterprise.webservices;

import java.security.Principal;

import org.jboss.logging.Logger;
import org.jboss.ruby.runtime.RubyRuntimePool;

/** Handler bean for dispatching to Ruby classes.
 * 
 * @author Bob McWhirter
 */
public class RubyWebServiceHandler {
	
	private static final Logger log = Logger.getLogger( RubyWebServiceHandler.class );

	private RubyRuntimePool runtimePool;
	private String rubyClassName;

	public RubyWebServiceHandler(RubyRuntimePool runtimePool, String rubyClassName) {
		this.runtimePool = runtimePool;
		this.rubyClassName = rubyClassName;
	}
	
	public Object invoke(Principal principal, String operationName, Object request) {
		log.info( "invoke(" + operationName + ", " + request + ")" );
		Object response = request;
		return response;
		//return request;
	}

}
