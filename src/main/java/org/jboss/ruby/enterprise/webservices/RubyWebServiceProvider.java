package org.jboss.ruby.enterprise.webservices;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Provider;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.jboss.kernel.Kernel;
import org.jboss.logging.Logger;
import org.jboss.ruby.runtime.RubyRuntimePool;
import org.jboss.ws.core.CommonMessageContext;
import org.jboss.ws.core.soap.MessageContextAssociation;
import org.jruby.Ruby;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;

@WebService
public class RubyWebServiceProvider implements Provider<DOMSource> {

	private static final String KERNEL_NAME = "jboss.kernel:service=Kernel";

	private static Logger log = Logger.getLogger(RubyWebServiceProvider.class);
	private String directory;
	private String serviceName;
	private String runtimePoolName;

	private RubyRuntimePool pool;

	public RubyWebServiceProvider(String directory, String serviceName, String runtimePoolName) {
		this.directory = directory;
		this.serviceName = serviceName;
		this.runtimePoolName = runtimePoolName;
	}

	@Resource
	public WebServiceContext context;

	@PostConstruct
	public void postConstruct() {
		log.info("postConstruct() " + this.serviceName);
		setupRuby();
	}

	@PreDestroy
	public void preDestroy() {
		log.info("preDestroy() " + this.serviceName);
		destroyRuby();
	}

	public DOMSource invoke(DOMSource request) {
		log.info("invoke(...) [" + serviceName + "] :: " + request);
		log.info("USING FACTORY: " + pool);
		Ruby ruby = null;
		try {
			ruby = pool.borrowRuntime();
			ruby.evalScriptlet( "\nrequire 'org/jboss/ruby/enterprise/webservices/dispatcher.rb'\n");
			IRubyObject rubyDispatcher = ruby.evalScriptlet("JBoss::WebServiceDispatcher.dispatcher_for( '" + directory + "', '"
					+ serviceName + "')\n");
			Object result = JavaEmbedUtils.invokeMethod(ruby, rubyDispatcher, "dispatch", new Object[] { request }, Object.class);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		} finally {
			if (ruby != null) {
				pool.returnRuntime(ruby);
			}
		}
		return request;
	}

	@SuppressWarnings("deprecation")
	private void setupRuby() {
		log.info("setupRuby()");

		CommonMessageContext msgContext = MessageContextAssociation.peekMessageContext();

		ServletContext servletContext = (ServletContext) msgContext.get(MessageContext.SERVLET_CONTEXT);
		Kernel kernel = (Kernel) servletContext.getAttribute(KERNEL_NAME);

		this.pool = (RubyRuntimePool) kernel.getRegistry().getEntry(this.runtimePoolName).getTarget();
	}

	private void destroyRuby() {
		this.pool = null;
	}

}
