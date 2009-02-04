package org.jboss.ruby.enterprise.webservices;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.naming.NameNotFoundException;
import javax.servlet.ServletContext;
import javax.xml.transform.Source;
import javax.xml.ws.Provider;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.MessageContext;

import org.jboss.kernel.Kernel;
import org.jboss.kernel.spi.registry.KernelRegistryEntry;
import org.jboss.logging.Logger;
import org.jboss.ws.core.CommonMessageContext;
import org.jboss.ws.core.soap.MessageContextAssociation;

@WebService
public class RubyWebServiceProvider implements Provider<Source> {
	
    private static final String KERNEL_NAME = "jboss.kernel:service=Kernel";
	
	private static Logger log = Logger.getLogger( RubyWebServiceProvider.class );
	private String serviceName;
	
	@Resource
	public WebServiceContext context;
	
	@PostConstruct
	public void postConstruct() {
		log.info( "postConstruct() " + this.serviceName );
	}
	
	@PreDestroy
	public void preDestroy() {
		log.info( "preDestroy() " + this.serviceName );
	}

	protected void initialize(String serviceName) {
		log.info( "initialize(" + serviceName + ")" );
		this.serviceName = serviceName;
		setupRuby();
	}
	
	public Source invoke(Source request) {
		log.info( "invoke(...) [" + serviceName + "] :: " + request );
		return request;
	}
	
	private void setupRuby() {
		log.info( "setupRuby()" );
		
		CommonMessageContext msgContext= MessageContextAssociation.peekMessageContext();
		
        ServletContext servletContext = (ServletContext) msgContext.get(MessageContext.SERVLET_CONTEXT);
        log.info( "servlet context is " + servletContext );
        Kernel kernel = (Kernel) servletContext.getAttribute(KERNEL_NAME);
        
        log.info( "kernel is " + kernel );
        
        log.info( "factory is " + kernel.getRegistry().getEntry( "jboss.ruby.runtime.factory.ovirt-ec2" ).getTarget() );
		
	}
	
	

}
