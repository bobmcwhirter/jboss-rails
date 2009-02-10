package org.jboss.ruby.enterprise.webservices.cxf;

import java.lang.reflect.Method;
import java.util.List;

import javax.xml.transform.dom.DOMSource;

import org.apache.cxf.service.factory.ReflectionServiceFactoryBean;
import org.apache.cxf.service.model.InterfaceInfo;
import org.apache.cxf.service.model.MessagePartInfo;
import org.apache.cxf.service.model.OperationInfo;
import org.jboss.logging.Logger;

public class RubyReflectionServiceFactoryBean extends ReflectionServiceFactoryBean {

	private static final Logger log = Logger.getLogger(RubyReflectionServiceFactoryBean.class);

	protected void initializeWSDLOperations() {
		log.info("initializeWSDLOperations()");
		InterfaceInfo intf = getInterfaceInfo();

		try {
			log.info("service class: " + serviceClass);
			Method method = serviceClass.getMethod("invoke", String.class, DOMSource.class);
			log.info("inovke method: " + method);
			for (OperationInfo o : intf.getOperations()) {
				log.info("calling initializeWSDLOperation(" + intf + ", " + o + ", " + method + ")");
				initializeWSDLOperation(intf, o, method);
				log.info("success");
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
