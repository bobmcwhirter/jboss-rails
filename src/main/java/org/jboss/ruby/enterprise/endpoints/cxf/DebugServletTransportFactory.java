package org.jboss.ruby.enterprise.endpoints.cxf;

import java.io.IOException;

import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.transport.Destination;
import org.apache.cxf.transport.servlet.ServletDestination;
import org.apache.cxf.transport.servlet.ServletTransportFactory;
import org.jboss.logging.Logger;

public class DebugServletTransportFactory extends ServletTransportFactory {
	private static final Logger log = Logger.getLogger( DebugServletTransportFactory.class );

	public DebugServletTransportFactory(RubyCXFBus bus) {
		super( bus );
	}

	@Override
	public Destination getDestination(EndpointInfo arg0) throws IOException {
		log.info( "getDestination(" + arg0 + ")" );
		log.info( " ----->" + super.getDestination( arg0 ) );
		log.info( "ALL " + getDestinations() );
		ServletDestination dest = (ServletDestination) super.getDestination( arg0 );
		log.info( "ENDPOINT: " + dest.getEndpointInfo() );
		log.info( "MATCH: " + dest.getContextMatchStrategy() );
		log.info( "SERVER: " + dest.getServer() );
		// TODO Auto-generated method stub
		return super.getDestination(arg0);
	}

	@Override
	public ServletDestination getDestinationForPath(String path) {
		log.info( "getDestinationForPath(" + path + ")" );
		log.info( " ----->" + super.getDestinationForPath( path ) );
		log.info( "ALL " + getDestinations() );
		// TODO Auto-generated method stub
		return super.getDestinationForPath(path);
	}
	

}
