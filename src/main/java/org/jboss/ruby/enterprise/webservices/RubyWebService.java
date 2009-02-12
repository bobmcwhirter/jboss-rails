package org.jboss.ruby.enterprise.webservices;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.apache.cxf.Bus;
import org.apache.cxf.binding.soap.SoapBindingFactory;
import org.apache.cxf.binding.soap.saaj.SAAJInInterceptor;
import org.apache.cxf.databinding.source.SourceDataBinding;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.frontend.ServerFactoryBean;
import org.apache.cxf.service.factory.AbstractServiceConfiguration;
import org.apache.cxf.service.factory.ReflectionServiceFactoryBean;
import org.apache.cxf.service.invoker.Invoker;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.jboss.logging.Logger;
import org.jboss.ruby.enterprise.webservices.cxf.DebugSAAJInInterceptor;
import org.jboss.ruby.enterprise.webservices.cxf.DebugWSS4JInInterceptor;
import org.jboss.ruby.enterprise.webservices.cxf.RubyInvoker;
import org.jboss.ruby.enterprise.webservices.cxf.RubyReflectionServiceFactoryBean;
import org.jboss.ruby.runtime.RubyRuntimePool;

/** The bean within MC representing a deployed Ruby WebService.
 * 
 * @author Bob McWhirter 
 */
public class RubyWebService {
	
	private static final Logger log = Logger.getLogger( RubyWebService.class );

	private RubyRuntimePool runtimePool;
	private Bus bus;
	private Server server;

	private String rubyClassName;
	private String wsdlLocation;
	private String targetNamespace;
	private String portName;

	public RubyWebService() {
		
	}
	
	public void setBus(Bus bus) {
		this.bus = bus;
	}
	
	public Bus getBus() {
		return this.bus;
	}
	
	public void setRubyRuntimePool(RubyRuntimePool runtimePool) {
		this.runtimePool = runtimePool;
	}
	
	public RubyRuntimePool getRubyRuntimePool() {
		return this.runtimePool;
	}
	
	public void setRubyClassName(String rubyClassName) {
		this.rubyClassName = rubyClassName;
	}
	
	public String getRubyClassName() {
		return this.rubyClassName;
	}
	
	public void setWsdlLocation(String wsdlLocation) {
		this.wsdlLocation = wsdlLocation;
	}
	
	public String getWsdlLocation() {
		return this.wsdlLocation;
	}
	
	public void setTargetNamespace(String targetNamespace) {
		this.targetNamespace = targetNamespace;
	}
	
	public String getTargetNamespace() {
		return this.targetNamespace;
	}
	
	public void setPortName(String portName) {
		this.portName = portName;
	}
	
	public String getPortName() {
		return this.portName;
	}
	
	public void start() {
		log.info( "start()" );
		ServerFactoryBean serverFactory = new ServerFactoryBean();
		serverFactory.setStart( false );
        serverFactory.setBus( bus );
        
		
		RubyWebServiceHandler serviceBean = createServiceBean();
		serverFactory.setServiceName( new QName( getTargetNamespace(), getPortName() ) );
		serverFactory.setEndpointName( new QName( getTargetNamespace(), getPortName() ) );
		serverFactory.setServiceClass( RubyWebServiceHandler.class );
		serverFactory.setInvoker( createInvoker( serviceBean ) );
		//serverFactory.setoA
		
		AbstractServiceConfiguration serviceConfig = new RubyServiceConfiguration( getPortName() );
		
		ReflectionServiceFactoryBean serviceFactory = new RubyReflectionServiceFactoryBean();
		serviceFactory.setServiceConfigurations( Collections.singletonList( serviceConfig ) );
		SourceDataBinding dataBinding = new SourceDataBinding();
		serviceFactory.setDataBinding( dataBinding );
		serverFactory.setServiceFactory( serviceFactory );
		//serverFactory.setPublishedEndpointUrl( "/ec2" );
		serverFactory.setAddress( "/ec2" );
		serverFactory.setWsdlURL( "http://s3.amazonaws.com/ec2-downloads/2008-12-01.ec2.wsdl" );
		
		//DataBinding dataBinding = new SourceDataBinding();
		//serverFactory.setDataBinding(dataBinding);
		
		
		SoapBindingFactory bindingFactory = new SoapBindingFactory();
		serverFactory.setBindingFactory( bindingFactory );
		
		log.info( "bindingFactory: " + serverFactory.getBindingFactory() );
		
		this.server = serverFactory.create();
		log.info( "server is: " + server );
		
		SAAJInInterceptor saajInterceptor = new DebugSAAJInInterceptor();
		this.server.getEndpoint().getInInterceptors().add( saajInterceptor );
		
		WSS4JInInterceptor securityInterceptor = new DebugWSS4JInInterceptor( createSecurityProps() );
		this.server.getEndpoint().getInInterceptors().add( securityInterceptor );
		
		log.info( "interceptors: " + this.server.getEndpoint().getInInterceptors());
		this.server.start();
	}
	
	private Map<String, Object> createSecurityProps() {
		Map<String, Object> props = new HashMap<String,Object>();
		props.put(WSHandlerConstants.ACTION, "Signature");
		//props.put(WSHandlerConstants.SIG_PROP_REF_ID, createCryptoProps() );
		return props;
	}
	
	private Properties createCryptoProps() {
		Properties props = new Properties();
		props.setProperty( "org.apache.ws.security.crypto.provider",                 "org.apache.ws.security.components.crypto.Merlin" );
		props.setProperty( "org.apache.ws.security.crypto.merlin.keystore.type",     "jks" );
		props.setProperty( "org.apache.ws.security.crypto.merlin.keystore.password", "amex123" );
		props.setProperty( "org.apache.ws.security.crypto.merlin.file",              "server_keystore.jks" );
		return props;
	}

	private RubyWebServiceHandler createServiceBean() {
		return new RubyWebServiceHandler( this.runtimePool, this.rubyClassName );
	}
	
	private Invoker createInvoker(RubyWebServiceHandler handler) {
		return new RubyInvoker( handler );
	}

	public void stop() {
		log.info( "stop()" );
		this.server.stop();
	}
}
