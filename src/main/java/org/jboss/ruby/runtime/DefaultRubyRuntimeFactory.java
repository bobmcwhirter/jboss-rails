package org.jboss.ruby.runtime;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;
import org.jruby.Ruby;
import org.jruby.RubyInstanceConfig;
import org.jruby.javasupport.JavaEmbedUtils;

public class DefaultRubyRuntimeFactory implements RubyRuntimeFactory {
	
	private static final Logger log = Logger.getLogger( DefaultRubyRuntimeFactory.class );

	private List<String> loadPaths = null;
	private String initScript = null;

	private ClassLoader classLoader;

	public DefaultRubyRuntimeFactory() {
		this( null, null );
	}
	
	public DefaultRubyRuntimeFactory(List<String> loadPaths) {
		this( loadPaths, null );
	}
	
	public DefaultRubyRuntimeFactory(List<String> loadPaths, String initScript) {
		this.loadPaths = loadPaths;
		if ( this.loadPaths == null ) {
			this.loadPaths = Collections.emptyList();
		}
		this.initScript = initScript;
		log.info( "constructor" );
		
	}
	
	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	
	public ClassLoader getClassLoader() {
		return this.classLoader;
	}
	
	public Ruby createRubyRuntime() throws Exception {
		log.info( "createRubyRuntime()" );
		RubyInstanceConfig config = new RubyInstanceConfig();

		try {
			String binjruby = RubyInstanceConfig.class.getResource("/META-INF/jruby.home/bin/jruby").toURI().getSchemeSpecificPart();
			config.setJRubyHome(binjruby.substring(0, binjruby.length() - 10));
		} catch (Exception e) {	
			// ignore
		}

		config.setEnvironment( getEnvironment() );
		config.setOutput( getOutput() );
		config.setError( getError() );

		log.info( "LOAD_PATHS: " + loadPaths );
		Ruby runtime = JavaEmbedUtils.initialize(loadPaths, config);
		
		log.info( "INIT_SCRIPT: " + this.initScript );
		if ( this.initScript != null ) {
			runtime.evalScriptlet( this.initScript );
		}
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
