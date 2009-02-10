package org.jboss.ruby.enterprise.webservices.cxf;

import java.net.URL;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.factory.WSDLFactory;

import org.apache.cxf.service.model.ServiceSchemaInfo;
import org.apache.cxf.wsdl.WSDLManager;
import org.jboss.logging.Logger;
import org.w3c.dom.Element;

public class DebugWSDLManager implements WSDLManager {
	private static final Logger log = Logger.getLogger( DebugWSDLManager.class );
	private WSDLManager orig;

	public DebugWSDLManager(WSDLManager orig) {
		this.orig = orig;
	}

	public void addDefinition(Object arg0, Definition arg1) {
		log.info( "1 add definition: " + arg0 );
		log.info( arg1.getServices() );
		log.info( "--------" );
		orig.addDefinition(arg0, arg1);
	}

	public Definition getDefinition(Element arg0) throws WSDLException {
		log.info( "2 get definition: " + arg0 );
		log.info( orig.getDefinition(arg0) );
		log.info( orig.getDefinition(arg0).getServices() );
		log.info( "--------" );
		return orig.getDefinition(arg0);
	}

	public Definition getDefinition(String arg0) throws WSDLException {
		log.info( "3 get definition: " + arg0 );
		log.info( orig.getDefinition(arg0) );
		log.info( orig.getDefinition(arg0).getServices() );
		log.info( "--------" );
		return orig.getDefinition(arg0);
	}

	public Definition getDefinition(URL arg0) throws WSDLException {
		log.info( "4 get definition: " + arg0 );
		log.info( orig.getDefinition(arg0) );
		log.info( orig.getDefinition(arg0).getServices() );
		log.info( "--------" );
		return orig.getDefinition(arg0);
	}

	public Map<Object, Definition> getDefinitions() {
		log.info( "get definitions" );
		return orig.getDefinitions();
	}

	public ExtensionRegistry getExtensionRegistry() {
		log.info( "get extension registry" );
		return orig.getExtensionRegistry();
	}

	public ServiceSchemaInfo getSchemasForDefinition(Definition arg0) {
		log.info( "get schemas for definition" );
		return orig.getSchemasForDefinition(arg0);
	}

	public WSDLFactory getWSDLFactory() {
		log.info( "get wsdl factory" );
		return orig.getWSDLFactory();
	}

	public void putSchemasForDefinition(Definition arg0, ServiceSchemaInfo arg1) {
		log.info( "put schemas for definition: " + arg0 );
		orig.putSchemasForDefinition(arg0, arg1);
	}

	public void removeDefinition(Definition arg0) {
		log.info( "remove definition: " + arg0 );
		orig.removeDefinition(arg0);
	}
	
	
	
}