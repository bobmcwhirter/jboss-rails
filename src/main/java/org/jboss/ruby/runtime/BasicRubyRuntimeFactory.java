package org.jboss.ruby.runtime;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jruby.Ruby;
import org.jruby.RubyInstanceConfig;
import org.jruby.javasupport.JavaEmbedUtils;

public class BasicRubyRuntimeFactory implements RubyRuntimeFactory {
	
	public BasicRubyRuntimeFactory() {
		
	}

	public Ruby createRubyRuntime() throws Exception {
		RubyInstanceConfig config = new RubyInstanceConfig();

		List<String> loadPaths = new ArrayList<String>();

		try {
			String binjruby = RubyInstanceConfig.class.getResource("/META-INF/jruby.home/bin/jruby").toURI().getSchemeSpecificPart();
			config.setJRubyHome(binjruby.substring(0, binjruby.length() - 10));
		} catch (Exception e) {	
			// ignore
		}

		config.setEnvironment( getEnvironment() );
		config.setOutput( getOutput() );
		config.setError( getError() );

		Ruby runtime = JavaEmbedUtils.initialize(loadPaths, config);
		return runtime;
	}
	
	public Map<Object,Object> getEnvironment() {
		return Collections.emptyMap();
	}
	
	public PrintStream getOutput() {
		return System.out;
	}
	
	public PrintStream getError() {
		return System.err;
	}

}
