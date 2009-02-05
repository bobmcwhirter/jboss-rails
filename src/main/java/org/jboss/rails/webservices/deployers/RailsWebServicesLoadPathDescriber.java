package org.jboss.rails.webservices.deployers;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.logging.Logger;
import org.jboss.ruby.runtime.metadata.LoadPathMetaData;
import org.jboss.ruby.runtime.metadata.RubyRuntimeMetaData;

public class RailsWebServicesLoadPathDescriber extends AbstractDeployer {
	
	private static final Logger log = Logger.getLogger( RailsWebServicesLoadPathDescriber.class );
	
	public RailsWebServicesLoadPathDescriber() {
		setStage(DeploymentStages.DESCRIBE );
		addInput( RubyRuntimeMetaData.class );
	}

	public void deploy(DeploymentUnit unit) throws DeploymentException {
		RubyRuntimeMetaData runtimeMetaData = unit.getAttachment( RubyRuntimeMetaData.class );
		
		if ( runtimeMetaData == null ) {
			return;
		}
		
		LoadPathMetaData loadPaths = runtimeMetaData.getLoadPath();
		
		log.info( "loadPaths = " + loadPaths.getPaths() );
	}

}
