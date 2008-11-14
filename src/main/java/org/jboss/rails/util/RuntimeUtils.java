package org.jboss.rails.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;
import org.jruby.Ruby;
import org.jruby.RubyInstanceConfig;
import org.jruby.javasupport.JavaEmbedUtils;

public class RuntimeUtils {

	private static Logger log = Logger.getLogger(RuntimeUtils.class);

	public static Ruby createRuntime(String railsRoot, String railsEnv) {
		RubyInstanceConfig config = new RubyInstanceConfig();

		List<String> loadPaths = new ArrayList<String>();

		Map<Object, Object> environment = new HashMap<Object, Object>();

		environment.put("RAILS_ROOT", railsRoot);
		environment.put("RAILS_ENV", railsEnv);

		try {
			String binjruby = RubyInstanceConfig.class.getResource("/META-INF/jruby.home/bin/jruby").toURI().getSchemeSpecificPart();
			config.setJRubyHome(binjruby.substring(0, binjruby.length() - 10));
		} catch (Exception e) {	
			// ignore
		}

		config.setEnvironment(environment);
		config.setOutput(System.out);
		config.setError(System.err);

		Ruby runtime = JavaEmbedUtils.initialize(loadPaths, config);

		String initScript = "$LOAD_PATH << 'META-INF/jruby.home/lib/ruby/site_ruby/1.8'\n" +
                            "RAILS_ROOT='" + railsRoot + "'\n" + 
		                    "RAILS_ENV='" + railsEnv + "'\n" + 
		                    "begin\n" + 
		                    "  require '" + railsRoot + "/config/boot.rb'\n" + 
		                    "  require '" + railsRoot + "/config/environment.rb'\n" +
		                    "rescue Exception=>e\n" +
		                    "  puts e.message\n" +
		                    "  puts e.backtrace\n" +
		                    "  raise e\n" +
		                    "end";

		runtime.evalScriptlet(initScript);

		return runtime;
	}
}
