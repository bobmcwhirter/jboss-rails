package org.jboss.rails.runtime;

import org.jboss.ruby.runtime.BasicRubyRuntimeFactory;
import org.jruby.Ruby;

public class RailsRuntimeFactory extends BasicRubyRuntimeFactory {
	
	String railsRoot;
	String environment;

	public RailsRuntimeFactory(String railsRoot, String environment) {
		this.railsRoot = railsRoot;
		this.environment = environment;
	}
	
	@Override
	public Ruby createRubyRuntime() {
		Ruby ruby = super.createRubyRuntime();
		String initScript = "$LOAD_PATH << 'META-INF/jruby.home/lib/ruby/site_ruby/1.8'\n" +
        	"RAILS_ROOT='" + railsRoot + "'\n" + 
        	"RAILS_ENV='" + environment + "'\n" + 
        	"require '" + railsRoot + "/config/boot.rb'\n" + 
        	"require '" + railsRoot + "/config/environment.rb'\n";
		ruby.evalScriptlet( initScript );
		return ruby;
	}
}
