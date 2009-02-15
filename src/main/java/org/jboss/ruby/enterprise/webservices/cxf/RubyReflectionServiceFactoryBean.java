package org.jboss.ruby.enterprise.webservices.cxf;

import java.lang.reflect.Method;
import java.security.Principal;
import java.util.List;

import javax.xml.transform.dom.DOMSource;

import org.apache.cxf.service.factory.ReflectionServiceFactoryBean;
import org.apache.cxf.service.model.InterfaceInfo;
import org.apache.cxf.service.model.MessagePartInfo;
import org.apache.cxf.service.model.OperationInfo;
import org.jboss.logging.Logger;

public class RubyReflectionServiceFactoryBean extends ReflectionServiceFactoryBean {

	private static final Logger log = Logger.getLogger(RubyReflectionServiceFactoryBean.class);

	protected void initializeWSDLOperations()  {
		InterfaceInfo intf = getInterfaceInfo();

		try {
			Method method = serviceClass.getMethod("invoke", Principal.class, String.class, Object.class);
			for (OperationInfo o : intf.getOperations()) {
				initializeWSDLOperation(intf, o, method);
			}
		} catch (SecurityException e) {
			e.printStackTrace();
			log.error(e);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			log.error(e);
		}
	}

	protected boolean isWrapped(Method m) {
		//log.info( "isWrapped(" + m + ") " + super.isWrapped( m ) );
		//return super.isWrapped(m );
		return false;
	}
	protected boolean initializeClassInfo(OperationInfo o, Method method, List<String> paramOrder) {
		log.info( "initializeClassInfo(" + o + "...)" );
		log.info( "op: " + o );
		log.info( "op unwrapped: " + o.getUnwrappedOperation() );
		boolean result = super.initializeClassInfo(o , method, paramOrder);
		log.info( "SUPER: " + result );
		
		for ( MessagePartInfo p : o.getInput().getMessageParts() ) {
			p.setTypeClass( DOMSource.class );
			log.info( "part: " + p );
			//log.info( "part.type: " + p.getTypeClass() );
		}
		return result;

	}

}
