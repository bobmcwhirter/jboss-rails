package org.jboss.ruby.enterprise.webservices.deployers;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.ws.metadata.umdm.ServiceMetaData;
import org.jboss.ws.metadata.umdm.UnifiedMetaData;

public class RubyWSSecurityDeployer extends AbstractDeployer {
	
	public RubyWSSecurityDeployer() {
		setStage(DeploymentStages.PRE_REAL );
		addInput( UnifiedMetaData.class );
	}

	public void deploy(DeploymentUnit unit) throws DeploymentException {
		UnifiedMetaData md = unit.getAttachment( UnifiedMetaData.class );
		if ( md == null ) {
			return;
		}
		log.info( "deploying : " + unit );
		log.info( "  MD: " + md );
		
	}

}
