package org.jboss.ruby.enterprise.web.rack;

import java.io.ByteArrayOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.jboss.logging.Logger;
import org.jruby.Ruby;
import org.jruby.RubyArray;
import org.jruby.RubyHash;
import org.jruby.RubyIO;
import org.jruby.RubyModule;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;

public class RubyRackApplication implements RackApplication {
	private static final Logger log = Logger.getLogger(RubyRackApplication.class);

	private Ruby ruby;
	private IRubyObject rubyApp;

	public RubyRackApplication(Ruby ruby, String rackUpScript) {
		this.ruby = ruby;
		rackUp( rackUpScript );
	}
	
	private void rackUp(String script) {
		String fullScript = "require %q(rack/builder)\n" + script;
		log.info( "rackUp(" + fullScript + ")" );
		rubyApp = this.ruby.evalScriptlet( fullScript );
	}
	
	public Object createEnvironment(HttpServletRequest request) throws Exception {
		Ruby ruby = rubyApp.getRuntime();
		
		RubyIO input = new RubyIO( ruby, request.getInputStream() );
		RubyIO errors = new RubyIO( ruby, System.err );
		
		ruby.evalScriptlet( "require %q(org/jboss/ruby/enterprise/web/rack/environment_builder)" );
		
		RubyModule envBuilder = ruby.getClassFromPath( "JBoss::Rack::EnvironmentBuilder" );
		
		log.info( "envBuilder: " + envBuilder );
		
		Object env = JavaEmbedUtils.invokeMethod( ruby, envBuilder, "build", new Object[] { request, input, errors }, Object.class );
		log.info( "built: " + env );
		return env;
	}

	public RackResponse call(Object env) {
		IRubyObject response = (RubyArray) JavaEmbedUtils.invokeMethod( this.ruby, this.rubyApp, "call", new Object[]{env}, RubyArray.class);
		log.info( "response [" + response + "]" );
		return new RubyRackResponse( response );
	}

}
