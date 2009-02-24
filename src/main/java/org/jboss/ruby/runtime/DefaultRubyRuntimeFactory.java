package org.jboss.ruby.runtime;

import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jboss.kernel.Kernel;
import org.jboss.logging.Logger;
import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyInstanceConfig;
import org.jruby.javasupport.JavaEmbedUtils;

public class DefaultRubyRuntimeFactory implements RubyRuntimeFactory {
	
	private static final Logger log = Logger.getLogger( DefaultRubyRuntimeFactory.class );

	private Kernel kernel;
	private List<String> loadPaths = null;
	private String initScript = null;

	private RubyDynamicClassLoader classLoader;

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
		
	}
	
	public void setKernel(Kernel kernel) {
		this.kernel = kernel;
	}
	
	public Kernel getKernel() {
		return this.kernel;
	}
	
	public void setClassLoader(RubyDynamicClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	
	public RubyDynamicClassLoader getClassLoader() {
		return this.classLoader;
	}
	
	public Ruby createRubyRuntime() throws Exception {
		RubyInstanceConfig config = new RubyInstanceConfig();
		
		if ( this.classLoader != null ) {
			config.setLoader( this.classLoader );
		}

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
		
		if ( this.initScript != null ) {
			runtime.evalScriptlet( this.initScript );
		}
		injectKernel(runtime);
		return runtime;
	}
	
	private void injectKernel(Ruby runtime) {
		runtime.evalScriptlet( "require %q(jboss/kernel)" );
		RubyClass jbossKernel = (RubyClass) runtime.getClassFromPath( "JBoss::Kernel" );
		JavaEmbedUtils.invokeMethod(runtime, jbossKernel, "kernel=", new Object[] { this.kernel }, void.class );
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
