package org.jboss.rails.runtime.deployers;

import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;
import org.jboss.ruby.runtime.RubyDynamicClassLoader;
import org.jboss.ruby.runtime.RuntimeInitializer;
import org.jruby.Ruby;
import org.jruby.RubyArray;

public class RailsRuntimeInitializer implements RuntimeInitializer {
	
	private static final Logger log = Logger.getLogger(RailsRuntimeInitializer.class );
	
	private String railsRoot;
	private String railsEnv;

	public RailsRuntimeInitializer(String railsRoot, String railsEnv) {
		this.railsRoot = railsRoot;
		this.railsEnv  = railsEnv;
	}

	public void initialize(RubyDynamicClassLoader cl, Ruby ruby) throws Exception {
		ruby.evalScriptlet( createProlog() );
		
		RubyArray rubyLoadPath = (RubyArray) ruby.getGlobalVariables().get( "$LOAD_PATH" );
		
		List<String> loadPaths = new ArrayList<String>();
		int len = rubyLoadPath.size();
		for ( int i = 0 ; i < len ; ++i ) {
			String path = (String) rubyLoadPath.get( i );
			loadPaths.add( path );
		}
		
		cl.addLoadPaths( loadPaths );
		
		ruby.evalScriptlet( createEpilog() );
	}
	
	public String createProlog() {
		return
			"RAILS_ROOT='" + railsRoot + "'\n" + 
			"RAILS_ENV='" + railsEnv + "'\n" + 
		    "require %q(org/jboss/rails/runtime/deployers/rails_init.rb)\n";
	}
	
	public String createEpilog() {
		return  "load %q(config/environment.rb)\n";
	}

}
