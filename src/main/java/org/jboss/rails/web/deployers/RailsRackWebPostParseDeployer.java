package org.jboss.rails.web.deployers;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractSimpleRealDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.logging.Logger;
import org.jboss.rails.core.tomcat.RailsContextConfig;
import org.jboss.ruby.enterprise.web.metadata.RackWebMetaData;

public class RailsRackWebPostParseDeployer extends AbstractSimpleRealDeployer<RackWebMetaData> {
	
	private static Logger log = Logger.getLogger( RailsRackWebPostParseDeployer.class );
	
	public RailsRackWebPostParseDeployer() {
		super( RackWebMetaData.class );
		addOutput( RackWebMetaData.class );
		setStage( DeploymentStages.POST_PARSE );
	}

	@Override
	public void deploy(DeploymentUnit unit, RackWebMetaData rackMetaData) throws DeploymentException {
		rackMetaData.setContext( RailsContextConfig.class.getName() );
	}


}
