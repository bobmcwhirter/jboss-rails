package org.jboss.rails.web.deployers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractSimpleRealDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.logging.Logger;
import org.jboss.rails.core.metadata.RailsApplicationMetaData;
import org.jboss.ruby.enterprise.web.metadata.RackWebMetaData;

public class RailsRackWebPostParseDeployer extends AbstractSimpleRealDeployer<RackWebMetaData> {
	
	private static final String CONTEXT_CONFIG_CLASS_NAME = "org.jboss.rails.web.tomcat.RailsContextConfig";
	
	private static Logger log = Logger.getLogger( RailsRackWebPostParseDeployer.class );
	
	public RailsRackWebPostParseDeployer() {
		super( RackWebMetaData.class );
		addInput( RailsApplicationMetaData.class );
		addOutput( RackWebMetaData.class );
		setStage( DeploymentStages.POST_PARSE );
	}

	@Override
	public void deploy(DeploymentUnit unit, RackWebMetaData rackMetaData) throws DeploymentException {
		rackMetaData.setContextConfigClassName( CONTEXT_CONFIG_CLASS_NAME );
		RailsApplicationMetaData railsAppMetaData = unit.getAttachment( RailsApplicationMetaData.class );
		try {
			String docBase = railsAppMetaData.getRailsRoot().toURL().getFile();
			rackMetaData.setDocBase( docBase );
		} catch (URISyntaxException e) {
			throw new DeploymentException( e );
		} catch (MalformedURLException e) {
			throw new DeploymentException( e );
		}
	}


}
