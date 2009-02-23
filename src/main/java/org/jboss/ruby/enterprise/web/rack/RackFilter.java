package org.jboss.ruby.enterprise.web.rack;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.kernel.Kernel;
import org.jboss.kernel.spi.registry.KernelRegistryEntry;
import org.jboss.logging.Logger;
import org.jruby.Ruby;

public class RackFilter implements Filter {
	
	private static final Logger log = Logger.getLogger( RackFilter.class );
	
	private static final String KERNEL_NAME = "jboss.kernel:service=Kernel";

	//public static final String RACK_APP_FACTORY_INIT_PARAM = "jboss.rack.app.factory.name";
	public static final String RACK_APP_POOL_INIT_PARAM = "jboss.rack.app.pool.name";

	private RackApplicationPool rackAppFactory;

	@SuppressWarnings("deprecation")
	public void init(FilterConfig filterConfig) throws ServletException {
		Kernel kernel = (Kernel) filterConfig.getServletContext().getAttribute(KERNEL_NAME);
		log.info( "kernel is " + kernel );
		
		String rackAppFactoryName = filterConfig.getInitParameter( RACK_APP_POOL_INIT_PARAM );
		
		log.info( "rack app factory name is " + rackAppFactoryName );
		
		KernelRegistryEntry entry = kernel.getRegistry().findEntry( rackAppFactoryName );
		if ( entry != null ) {
			this.rackAppFactory = (RackApplicationPool) entry.getTarget();
		}
		log.info( "rack app factory is " + this.rackAppFactory );
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		log.info( "doFilter(" + request + ", ..." );
		if ( request instanceof HttpServletRequest && response instanceof HttpServletResponse ) {
			doFilter( (HttpServletRequest) request, (HttpServletResponse) response, chain );
		}
	}
	
	public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException {
		RackApplication rackApp = null;
		
		try {
			rackApp = borrowRackApplication();
			log.info( "using rack application: " + rackApp );
			Object rackEnv = rackApp.createEnvironment( request );
			rackApp.call(rackEnv).respond(response);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if ( rackApp != null ) {
				releaseRackApplication( rackApp );
				rackApp = null;
			}
		}
	}

	private RackApplication borrowRackApplication() throws Exception {
		return this.rackAppFactory.borrowApplication();
	}
	
	private void releaseRackApplication(RackApplication rackApp) {
		this.rackAppFactory.releaseApplication( rackApp );
	}

	
	

}
