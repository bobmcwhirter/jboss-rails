package org.jboss.ruby.enterprise.endpoints.cxf;

import javax.wsdl.Definition;

import org.apache.cxf.BusException;
import org.apache.cxf.bus.extension.ExtensionManagerBus;
import org.apache.cxf.transport.DestinationFactory;
import org.apache.cxf.transport.DestinationFactoryManager;
import org.apache.cxf.wsdl.WSDLManager;
import org.jboss.logging.Logger;

public class RubyCXFBus extends ExtensionManagerBus {
	
	private static Logger log = Logger.getLogger( RubyCXFBus.class );
	
	public RubyCXFBus() {
		
	}
	
	public void start() throws BusException {
        DestinationFactoryManager dfm = getExtension(DestinationFactoryManager.class );
        DestinationFactory destinationFactory = new DebugServletTransportFactory( this );
        for ( String transportId : destinationFactory.getTransportIds() ) {
        	dfm.registerDestinationFactory( transportId,  destinationFactory );
        }
        
        WSDLManager manager = getExtension(WSDLManager.class);
        //Definition def = manager.getDefinition( "" );
        //def.get
        
	}

}
