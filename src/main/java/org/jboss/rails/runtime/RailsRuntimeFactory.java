package org.jboss.rails.runtime;

import org.jboss.logging.Logger;
import org.jboss.ruby.runtime.BasicRubyRuntimeFactory;
import org.jboss.virtual.VirtualFile;
import org.jruby.Ruby;

public class RailsRuntimeFactory extends BasicRubyRuntimeFactory implements RailsRuntimeFactoryMBean {
	
	private static Logger log = Logger.getLogger( RailsRuntimeFactory.class );
	
	private VirtualFile railsRoot;
	private String environment;

	public RailsRuntimeFactory(VirtualFile railsRoot, String environment) {
		this.railsRoot = railsRoot;
		this.environment = environment;
	}
	
	public Ruby createRubyRuntime() throws Exception {
		Ruby ruby = super.createRubyRuntime();
		
		String railsRootPath = this.railsRoot.toURL().getFile();
		
		String initScript = "$LOAD_PATH << 'META-INF/jruby.home/lib/ruby/site_ruby/1.8'\n" +
        	"RAILS_ROOT='" + railsRootPath + "'\n" + 
        	"RAILS_ENV='" + this.environment + "'\n" + 
        	"require \"#{RAILS_ROOT}/config/boot.rb\"\n";
		log.info( "initScript" + initScript );
		ruby.evalScriptlet( initScript );
		return ruby;
	}
}
