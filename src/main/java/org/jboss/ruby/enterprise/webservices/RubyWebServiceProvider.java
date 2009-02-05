package org.jboss.ruby.enterprise.webservices;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.xml.transform.Source;
import javax.xml.ws.Provider;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.jboss.kernel.Kernel;
import org.jboss.logging.Logger;
import org.jboss.ruby.runtime.RubyRuntimeFactory;
import org.jboss.ws.core.CommonMessageContext;
import org.jboss.ws.core.soap.MessageContextAssociation;
import org.jruby.Ruby;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;

@WebService
public class RubyWebServiceProvider implements Provider<Source> {
	
    private static final String KERNEL_NAME = "jboss.kernel:service=Kernel";
	
	private static Logger log = Logger.getLogger( RubyWebServiceProvider.class );
	private String serviceName;
	
	private RubyRuntimeFactory factory;
	
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
		log.info( "USING FACTORY: " + factory );
		try {
			Ruby ruby = factory.createRubyRuntime();
			ruby.evalScriptlet( "require \"#{RAILS_ROOT}/config/environment\"\n" +
					"\nrequire 'org/jboss/ruby/enterprise/webservices/dispatcher.rb'\n" );
			IRubyObject rubyDispatcher = ruby.evalScriptlet( "JBoss::WebServiceDispatcher.dispatcher_for( '" + serviceName + "')\n" );
			Object result = JavaEmbedUtils.invokeMethod( ruby, rubyDispatcher, "dispatch", new Object[] { request }, Object.class );
		} catch (Exception e) {
			e.printStackTrace();
			log.error( e );
		}
		return request;
	}
	
	private void setupRuby() {
		log.info( "setupRuby()" );
		
		CommonMessageContext msgContext= MessageContextAssociation.peekMessageContext();
		
        ServletContext servletContext = (ServletContext) msgContext.get(MessageContext.SERVLET_CONTEXT);
        Kernel kernel = (Kernel) servletContext.getAttribute(KERNEL_NAME);
        
        this.factory =  (RubyRuntimeFactory) kernel.getRegistry().getEntry( "jboss.ruby.runtime.factory.ovirt-ec2" ).getTarget();
	}
	
	

}
