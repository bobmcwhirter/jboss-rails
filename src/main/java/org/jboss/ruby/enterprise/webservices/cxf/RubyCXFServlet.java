package org.jboss.ruby.enterprise.webservices.cxf;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.cxf.Bus;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.jboss.kernel.Kernel;
import org.jboss.kernel.spi.registry.KernelRegistryEntry;

public class RubyCXFServlet extends CXFNonSpringServlet {
	
	private final String KERNEL_NAME = "jboss.kernel:service=Kernel";
	
	public RubyCXFServlet() {
		
	}

	@Override
	public void loadBus(ServletConfig servletConfig) throws ServletException {
		String busName = servletConfig.getInitParameter( "cxf.bus.name" );
		log( "busName is " + busName );
		Kernel kernel = (Kernel) servletConfig.getServletContext().getAttribute( KERNEL_NAME );
		log( "kernel is " + kernel );
		KernelRegistryEntry entry = kernel.getRegistry().getEntry( busName );
		log( "entry is " + entry );
		this.bus = (Bus) entry.getTarget();
		log( "bus is " + this.bus );
		super.loadBus(servletConfig);
	}
	
	

}
