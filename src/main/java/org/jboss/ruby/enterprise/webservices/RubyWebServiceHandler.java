package org.jboss.ruby.enterprise.webservices;

import java.security.Principal;

import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.jboss.ruby.enterprise.webservices.databinding.RubyDataBinding;
import org.jboss.ruby.enterprise.webservices.databinding.RubyType;
import org.jboss.ruby.runtime.RubyRuntimePool;
import org.jruby.Ruby;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;

/** Handler bean for dispatching to Ruby classes.
 * 
 * @author Bob McWhirter
 */
public class RubyWebServiceHandler {
	
	private static final Logger log = Logger.getLogger( RubyWebServiceHandler.class );

	private RubyRuntimePool runtimePool;

	private RubyDataBinding dataBinding;

	private String dir;
	private String rubyClassName;

	public RubyWebServiceHandler(RubyRuntimePool runtimePool, RubyDataBinding dataBinding, String dir, String rubyClassName) {
		this.runtimePool = runtimePool;
		this.dataBinding = dataBinding;
		this.dir = dir;
		this.rubyClassName = rubyClassName;
	}
	
	public Object invoke(Principal principal, String operationName, Object request, QName responseTypeName) {
		log.info( "invoke(" + operationName + ", " + request + ", " + responseTypeName + ")" );
		RubyType responseType = dataBinding.getTypeByQName( responseTypeName );
		String responseCreator = null;
		if ( responseType != null ) {
			responseCreator = responseType.getNewInstanceFragment();
		}
		Ruby ruby = null;
		try {
			ruby = runtimePool.borrowRuntime();
			String dispatch = "require %q(org/jboss/ruby/enterprise/webservices/dispatcher)\n" +
				"JBoss::WebServiceDispatcher.dispatcher_for( %q(" + this.dir + "), %q(" + this.rubyClassName + ") )\n";
			IRubyObject bridge = ruby.evalScriptlet(dispatch);
			Object result = JavaEmbedUtils.invokeMethod( ruby, bridge, "dispatch", new Object[] { principal, operationName, request, responseCreator }, Object.class );
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			log.error( e );
		} finally {
			if ( ruby != null ) {
				runtimePool.returnRuntime( ruby );
				
			}
		}
		Object response = request;
		return response;
		//return request;
	}

}
