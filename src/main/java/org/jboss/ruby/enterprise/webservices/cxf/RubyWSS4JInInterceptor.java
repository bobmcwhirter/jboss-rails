package org.jboss.ruby.enterprise.webservices.cxf;

import java.util.Map;
import java.util.Vector;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.jboss.logging.Logger;

public class RubyWSS4JInInterceptor extends WSS4JInInterceptor {
	
	private static final Logger log = Logger.getLogger( RubyWSS4JInInterceptor.class );
	
	public RubyWSS4JInInterceptor(Map<String, Object> props) {
		super( props );
	}

	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		log.info( "start handleMessage(" + message + ")" );
		super.handleMessage(message);
		log.info( "completed handleMessage(" + message + ")" );
	}

	@Override
	protected boolean checkReceiverResults(Vector wsResult, Vector actions) {
		log.info( "checkReceiverResults(");
		log.info( "   " + wsResult );
		log.info( "   " + actions );
		log.info( ")" );
		return super.checkReceiverResults(wsResult, actions);
	}
	
	
	

}
