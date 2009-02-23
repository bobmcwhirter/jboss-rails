package org.jboss.ruby.enterprise.web.rack;

import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;
import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;

public class RubyRackResponse implements RackResponse {
	
	private static final Logger log = Logger.getLogger( RubyRackResponse.class );
	private IRubyObject rackResponse;

	public RubyRackResponse(IRubyObject rackResponse) {
		this.rackResponse = rackResponse;
	}

	public void respond(HttpServletResponse response) {
		log.info( "handling response: " + rackResponse );
		Ruby ruby = rackResponse.getRuntime();
		ruby.evalScriptlet( "require %q(org/jboss/ruby/enterprise/web/rack/response_handler)");
		RubyClass responseHandler = (RubyClass) ruby.getClassFromPath( "JBoss::Rack::ResponseHandler" );
		log.info( "responseHandler: " + responseHandler );
		
		Object result = JavaEmbedUtils.invokeMethod( ruby, responseHandler, "handle", new Object[]{ rackResponse, response }, Object.class );
		log.info( "result: " + result );
	}

}
