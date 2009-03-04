package org.jboss.ruby.enterprise.endpoints.cxf;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.cxf.Bus;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.jboss.kernel.Kernel;
import org.jboss.kernel.spi.registry.KernelRegistryEntry;

public class RubyCXFServlet extends CXFNonSpringServlet {
	
	private static final long serialVersionUID = -2809395081671794214L;
	
	private final String KERNEL_NAME = "jboss.kernel:service=Kernel";
	
	public RubyCXFServlet() {
		
	}

	@SuppressWarnings("deprecation")
	@Override
	public void loadBus(ServletConfig servletConfig) throws ServletException {
		String busName = servletConfig.getInitParameter( "cxf.bus.name" );
		Kernel kernel = (Kernel) servletConfig.getServletContext().getAttribute( KERNEL_NAME );
		KernelRegistryEntry entry = kernel.getRegistry().getEntry( busName );
		this.bus = (Bus) entry.getTarget();
		super.loadBus(servletConfig);
	}
	
	

}
