package org.jboss.ruby.enterprise.webservices.deployers;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.logging.Logger;
import org.jboss.ws.metadata.umdm.ServiceMetaData;

public class ServiceFixupDeployer extends AbstractDeployer {
	
	private Logger log = Logger.getLogger( ServiceFixupDeployer.class );
	
	public ServiceFixupDeployer() {
		setStage(DeploymentStages.PRE_REAL );
		setInput( ServiceMetaData.class );
		setOutput( ServiceMetaData.class );
	}

	public void deploy(DeploymentUnit unit) throws DeploymentException {
		log.info( "**** " + unit );
		log.info( "HAS SERVICEMETADATA: " + unit.getAttachment(ServiceMetaData.class));
	}

}
