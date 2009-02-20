package org.jboss.ruby.enterprise.endpoints;

import java.security.Principal;

import javax.xml.namespace.QName;

import org.apache.cxf.interceptor.Fault;
import org.jboss.logging.Logger;
import org.jboss.ruby.enterprise.endpoints.databinding.RubyDataBinding;
import org.jboss.ruby.enterprise.endpoints.databinding.RubyType;
import org.jboss.ruby.runtime.RubyRuntimePool;
import org.jboss.ruby.util.StringUtils;
import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;

/**
 * Handler bean for dispatching to Ruby classes.
 * 
 * @author Bob McWhirter
 */
public class RubyEndpointHandler {

	private static final Logger log = Logger.getLogger(RubyEndpointHandler.class);

	private static final Object[] EMPTY_OBJECT_ARRAY = new Object[] {};

	private RubyRuntimePool runtimePool;

	private Logger endpointLogger;
	
	private String classLocation;
	private String endpointClassName;

	private RubyDataBinding dataBinding;

	public RubyEndpointHandler(RubyRuntimePool runtimePool, String classLocation, String endpointClassName, RubyDataBinding dataBinding) {
		this.endpointLogger = Logger.getLogger( "jboss.ruby.endpoints." + endpointClassName );
		this.runtimePool = runtimePool;
		this.classLocation = classLocation;
		this.endpointClassName = endpointClassName;
		this.dataBinding = dataBinding;
	}

	public Object invoke(Principal principal, String operationName, Object request, QName responseTypeName) {
		log.info("invoke(" + operationName + ", " + request + ", " + responseTypeName + ")");
		RubyType responseType = dataBinding.getTypeByQName(responseTypeName);
		String responseCreator = null;
		if (responseType != null) {
			responseCreator = responseType.getNewInstanceFragment();
		}

		Ruby ruby = null;

		Object response = null;

		try {
			ruby = runtimePool.borrowRuntime();

			loadSupport(ruby);
			loadEndpointClassLocation(ruby);

			RubyClass endpointClass = ruby.getClass(this.endpointClassName);

			BaseEndpointRb javaEndpoint = createEndpoint(endpointClass);
			inject(javaEndpoint, principal, request, responseCreator);

			IRubyObject rubyEndpoint = JavaEmbedUtils.javaToRuby(ruby, javaEndpoint);
			response = invoke(rubyEndpoint, operationName);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Fault(e);
		} finally {
			if (ruby != null) {
				runtimePool.returnRuntime(ruby);
			}
		}
		return response;
	}

	protected BaseEndpointRb createEndpoint(RubyClass endpointClass) {
		return (BaseEndpointRb) JavaEmbedUtils
				.invokeMethod(endpointClass.getRuntime(), endpointClass, "new", EMPTY_OBJECT_ARRAY, BaseEndpointRb.class);
	}

	private void inject(BaseEndpointRb endpoint, Principal principal, Object request, String responseCreator) {
		log.info("inject(" + endpoint + ", ...)");
		endpoint.setPrincipal(principal);
		endpoint.setRequest(request);
		endpoint.setResponseCreator(responseCreator);
		endpoint.setLogger( this.endpointLogger );
	}

	private Object invoke(IRubyObject endpoint, String operationName) {
		String methodName = StringUtils.underscore(operationName);
		log.info("invoke(" + endpoint + ", " + operationName + ") [" + methodName + "]");
		Object response = JavaEmbedUtils.invokeMethod(endpoint.getRuntime(), endpoint, methodName, EMPTY_OBJECT_ARRAY, Object.class);
		log.info("response is: " + response);
		return response;
	}

	protected void loadSupport(Ruby runtime) {
		String supportScript = "require %q(jboss/endpoints/base_endpoint)\n";
		log.info("eval: " + supportScript);
		runtime.evalScriptlet(supportScript);
	}

	private void loadEndpointClassLocation(Ruby ruby) {
		if (this.classLocation == null) {
			log.info("no classLocation, not loading");
			return;
		}
		String load = "load %q(" + this.classLocation + ".rb)\n";
		log.info("eval: " + load);
		ruby.evalScriptlet(load);
	}

}
