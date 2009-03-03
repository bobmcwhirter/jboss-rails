package org.jboss.ruby.enterprise.web.rack;

import org.jboss.logging.Logger;
import org.jboss.ruby.runtime.RubyRuntimeFactory;
import org.jruby.Ruby;

public class RubyRackApplicationFactory implements RackApplicationFactory {
	
	private static final Logger log = Logger.getLogger( RubyRackApplicationFactory.class );

	private RubyRuntimeFactory runtimeFactory;
	private String rackUpScript;

	public RubyRackApplicationFactory() {
		
	}
	
	public void setRubyRuntimeFactory(RubyRuntimeFactory runtimeFactory) {
		this.runtimeFactory = runtimeFactory;
	}
	
	public RubyRuntimeFactory getRubyRuntimeFactory() {
		return this.runtimeFactory;
	}
	
	public void setRackUpScript(String rackUpScript) {
		this.rackUpScript = rackUpScript;
	}
	
	public String getRackUpScript() {
		return this.rackUpScript;
	}
	
	public RackApplication createRackApplication() throws Exception {
		Ruby ruby = getRubyRuntimeFactory().createRubyRuntime();
		
		RubyRackApplication rackApp = new RubyRackApplication( ruby, rackUpScript );
		
		return rackApp;
	}

}
