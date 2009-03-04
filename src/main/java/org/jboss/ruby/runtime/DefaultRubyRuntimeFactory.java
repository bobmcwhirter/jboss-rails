package org.jboss.ruby.runtime;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jboss.kernel.Kernel;
import org.jboss.logging.Logger;
import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyInstanceConfig;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.util.ClassCache;

public class DefaultRubyRuntimeFactory implements RubyRuntimeFactory {

	private static final Logger log = Logger.getLogger(DefaultRubyRuntimeFactory.class);

	private Kernel kernel;
	private RuntimeInitializer initializer;

	private RubyDynamicClassLoader classLoader;
	private ClassCache classCache;

	public DefaultRubyRuntimeFactory() {
		this(null);
	}

	public DefaultRubyRuntimeFactory(RuntimeInitializer initializer) {
		this.initializer = initializer;
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

	public synchronized Ruby createRubyRuntime() throws Exception {
		RubyInstanceConfig config = new RubyInstanceConfig();

		RubyDynamicClassLoader childLoader = this.classLoader.createChild();
		config.setLoader(childLoader);
		
		if ( this.classCache == null ) {
			this.classCache = new ClassCache( this.classLoader );
		}
		config.setClassCache( classCache );

		try {
			String binjruby = RubyInstanceConfig.class.getResource("/META-INF/jruby.home/bin/jruby").toURI().getSchemeSpecificPart();
			config.setJRubyHome(binjruby.substring(0, binjruby.length() - 10));
		} catch (Exception e) {
			// ignore
		}

		config.setEnvironment(getEnvironment());
		config.setOutput(getOutput());
		config.setError(getError());

		List<String> loadPath = new ArrayList<String>();
		loadPath.add("META-INF/jruby.home/lib/ruby/site_ruby/1.8");

		Ruby runtime = JavaEmbedUtils.initialize(loadPath, config);

		if (this.initializer != null) {
			this.initializer.initialize(childLoader, runtime);
		}
		injectKernel(runtime);
		return runtime;
	}

	private void injectKernel(Ruby runtime) {
		runtime.evalScriptlet("require %q(jboss/kernel)");
		RubyClass jbossKernel = (RubyClass) runtime.getClassFromPath("JBoss::Kernel");
		JavaEmbedUtils.invokeMethod(runtime, jbossKernel, "kernel=", new Object[] { this.kernel }, void.class);
	}

	public Map<Object, Object> getEnvironment() {
		return Collections.emptyMap();
	}

	public PrintStream getOutput() {
		return System.out;
	}

	public PrintStream getError() {
		return System.err;
	}
}
