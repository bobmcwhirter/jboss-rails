package org.jboss.ruby.enterprise.webservices;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.apache.cxf.Bus;
import org.apache.cxf.binding.soap.SoapBindingFactory;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.frontend.ServerFactoryBean;
import org.apache.cxf.service.factory.AbstractServiceConfiguration;
import org.apache.cxf.service.factory.ReflectionServiceFactoryBean;
import org.apache.cxf.service.invoker.Invoker;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.jboss.logging.Logger;
import org.jboss.ruby.enterprise.webservices.cxf.RubyInvoker;
import org.jboss.ruby.enterprise.webservices.cxf.RubyReflectionServiceFactoryBean;
import org.jboss.ruby.enterprise.webservices.cxf.RubyServiceConfiguration;
import org.jboss.ruby.enterprise.webservices.cxf.RubyWSS4JInInterceptor;
import org.jboss.ruby.enterprise.webservices.databinding.RubyDataBinding;
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
	
	
	private String address;
	
	private String targetNamespace;
	private String portName;

	private boolean verifySignature;

	private boolean verifyTimestamp;

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
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getAddress() {
		return this.address;
	}
	
	public void setVerifySignature(boolean verifySignature) {
		this.verifySignature = verifySignature;
	}
	
	public boolean isVerifySignature() {
		return this.verifySignature;
	}
	
	public void setVerifyTimestamp(boolean verifyTimestamp) {
		this.verifyTimestamp = verifyTimestamp;
	}
	
	public boolean isVerifyTimestamp() {
		return this.verifyTimestamp;
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
		AbstractServiceConfiguration serviceConfig = new RubyServiceConfiguration( getPortName() );
		ReflectionServiceFactoryBean serviceFactory = new RubyReflectionServiceFactoryBean();
		serviceFactory.setServiceConfigurations( Collections.singletonList( serviceConfig ) );
		
		ServerFactoryBean serverFactory = new ServerFactoryBean();
		serverFactory.setStart( false );
        serverFactory.setBus( bus );
		serverFactory.setServiceFactory( serviceFactory );
		
		RubyWebServiceHandler serviceBean = createServiceBean();
		serverFactory.setServiceName( new QName( getTargetNamespace(), getPortName() ) );
		serverFactory.setEndpointName( new QName( getTargetNamespace(), getPortName() ) );
		serverFactory.setServiceClass( RubyWebServiceHandler.class );
		serverFactory.setInvoker( createInvoker( serviceBean ) );
		
		serverFactory.setAddress( getAddress() );
		serverFactory.setWsdlURL( getWsdlLocation() );
		
		RubyDataBinding dataBinding = new RubyDataBinding( this.runtimePool );
		serviceFactory.setDataBinding( dataBinding );
		
		SoapBindingFactory bindingFactory = new SoapBindingFactory();
		serverFactory.setBindingFactory( bindingFactory );
		
		this.server = serverFactory.create();
		
		if ( isVerifySignature() || isVerifyTimestamp() ) {
			setupSecurity();
		}
		
		log.info( "RUBY CLASS DEFS: " + dataBinding.getRubyClassDefinitions() );
		
		this.server.start();
	}
	
	private void setupSecurity() {
		WSS4JInInterceptor securityInterceptor = new RubyWSS4JInInterceptor( createSecurityProps() );
		this.server.getEndpoint().getInInterceptors().add( securityInterceptor );
	}

	private Map<String, Object> createSecurityProps() {
		Map<String, Object> props = new HashMap<String,Object>();
		String actions = "";
		if ( isVerifySignature() ) {
			actions += "Signature ";
		}
		if ( isVerifyTimestamp() ) {
			actions += "Timestamp ";
		}
		props.put(WSHandlerConstants.ACTION, actions.trim() );
		props.put(WSHandlerConstants.SIG_PROP_REF_ID, "jboss.ruby.webservices.crypto.config" );
		props.put("jboss.ruby.webservices.crypto.config", createCryptoProps() );
		return props;
	}
	
	private Properties createCryptoProps() {
		Properties props = new Properties();
		props.setProperty( "org.apache.ws.security.crypto.provider",                 "org.apache.ws.security.components.crypto.Merlin" );
		props.setProperty( "org.apache.ws.security.crypto.merlin.keystore.type",     "jks" );
		//props.setProperty( "org.apache.ws.security.crypto.merlin.keystore.type",     "pkcs12" );
		props.setProperty( "org.apache.ws.security.crypto.merlin.keystore.password", "foobar" );
		props.setProperty( "org.apache.ws.security.crypto.merlin.file",              "/Users/bob/oddthesis/ovirt-ec2/auth/truststore.jks" );
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
