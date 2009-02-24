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

public class RackFilter implements Filter {

	private static final Logger log = Logger.getLogger(RackFilter.class);

	private static final String KERNEL_NAME = "jboss.kernel:service=Kernel";

	// public static final String RACK_APP_FACTORY_INIT_PARAM =
	// "jboss.rack.app.factory.name";
	public static final String RACK_APP_POOL_INIT_PARAM = "jboss.rack.app.pool.name";

	private RackApplicationPool rackAppFactory;

	@SuppressWarnings("deprecation")
	public void init(FilterConfig filterConfig) throws ServletException {
		Kernel kernel = (Kernel) filterConfig.getServletContext().getAttribute(KERNEL_NAME);
		String rackAppFactoryName = filterConfig.getInitParameter(RACK_APP_POOL_INIT_PARAM);
		KernelRegistryEntry entry = kernel.getRegistry().findEntry(rackAppFactoryName);
		if (entry != null) {
			this.rackAppFactory = (RackApplicationPool) entry.getTarget();
		}
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
			doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
		}
	}

	protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException {
		HttpServletResponseCapture responseCapture = new HttpServletResponseCapture(response);
		try {
			chain.doFilter(request, responseCapture);
			if ( responseCapture.isError() ) {
				response.reset();
			} else {
				return;
			}
		} catch (ServletException e) {
			log.error( e );
		}
		doRack( request, response );
	}

	protected void doRack(HttpServletRequest request, HttpServletResponse response) throws IOException {
		RackApplication rackApp = null;

		try {
			rackApp = borrowRackApplication();
			Object rackEnv = rackApp.createEnvironment(request);
			rackApp.call(rackEnv).respond(response);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rackApp != null) {
				releaseRackApplication(rackApp);
				rackApp = null;
			}
		}
	}

	private RackApplication borrowRackApplication() throws Exception {
		return this.rackAppFactory.borrowApplication();
	}

	private void releaseRackApplication(RackApplication rackApp) {
		this.rackAppFactory.releaseApplication(rackApp);
	}

}
