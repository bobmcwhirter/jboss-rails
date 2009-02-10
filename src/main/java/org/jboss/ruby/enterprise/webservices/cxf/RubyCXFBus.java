package org.jboss.ruby.enterprise.webservices.cxf;

import org.apache.cxf.BusException;
import org.apache.cxf.bus.extension.ExtensionManagerBus;
import org.apache.cxf.transport.DestinationFactory;
import org.apache.cxf.transport.DestinationFactoryManager;
import org.apache.cxf.transport.servlet.ServletTransportFactory;
import org.apache.cxf.wsdl.WSDLManager;
import org.jboss.logging.Logger;

public class RubyCXFBus extends ExtensionManagerBus {
	
	private static Logger log = Logger.getLogger( RubyCXFBus.class );
	
	public RubyCXFBus() {
		
	}
	
	public void start() throws BusException {
		log.info( "START" );
        WSDLManager origManager = getExtension( WSDLManager.class );
        WSDLManager wrapper = new DebugWSDLManager( origManager );
        setExtension( wrapper, WSDLManager.class );
        
        DestinationFactoryManager dfm = getExtension(DestinationFactoryManager.class );
        log.info( "DFM: " + dfm );
        DestinationFactory destinationFactory = new DebugServletTransportFactory( this );
        log.info( "DEST FACTORY: " + destinationFactory );
        
        for ( String transportId : destinationFactory.getTransportIds() ) {
        	dfm.registerDestinationFactory( transportId,  destinationFactory );
        }
        
	}

}
