package org.jboss.rails.core.deployers;

import java.io.IOException;
import java.net.URISyntaxException;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.rails.core.metadata.RailsApplicationMetaData;
import org.jboss.virtual.VirtualFile;

public class RailsRootRecognizingDeployer extends AbstractDeployer {
	
	public RailsRootRecognizingDeployer() {
		setAllInputs( true );
		addOutput( RailsApplicationMetaData.class );
		setStage(DeploymentStages.NOT_INSTALLED );
	}

	public void deploy(DeploymentUnit unit) throws DeploymentException {
		log.debug( "checking: " + unit );
		if ( unit.getAttachment( RailsApplicationMetaData.class ) != null ) {
			return;
		}
		
		log.debug( "deploying: " + unit );
		if ( unit instanceof VFSDeploymentUnit ) {
			deploy( (VFSDeploymentUnit) unit );
		}
	}
	
	public void deploy(VFSDeploymentUnit unit) throws DeploymentException {
		VirtualFile root = unit.getRoot();
		
		try {
			if ( root.getChild( "config/environment.rb" ) != null ) {
				log.debug( "attaching: " + unit );
				RailsApplicationMetaData railsAppMetaData = new RailsApplicationMetaData( root );
				unit.addAttachment( RailsApplicationMetaData.class, railsAppMetaData );
			}
		} catch (IOException e) {
			throw new DeploymentException( e );
		} catch (URISyntaxException e) {
			throw new DeploymentException( e );
		}
	}

}
