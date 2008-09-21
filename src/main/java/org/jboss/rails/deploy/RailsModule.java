package org.jboss.rails.deploy;

import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.logging.Logger;
import org.jboss.rails.metadata.RailsMetaData;

public class RailsModule implements RailsModuleMBean {
	
	private Logger log = Logger.getLogger( RailsModule.class );
	private VFSDeploymentUnit unit;
	private RailsAppDeployer deployer;
	private RailsDeployment deployment;
	
	public RailsModule(VFSDeploymentUnit unit, RailsAppDeployer deployer, RailsDeployment deployment) {
		this.unit = unit;
		this.deployer = deployer;
		this.deployment = deployment;
	}

	public void create() throws Exception {
		log.debug( "create()" );
	}

	public void destroy() throws Exception {
		log.debug( "destroy()" );
	}

	public void start() throws Exception {
		log.debug( "start() ish" );
		log.debug( "starting deployment" );
		deployment.start( unit.getAttachment( RailsMetaData.class ));
	}

	public void stop() throws Exception {
		log.debug( "stop()" );
		deployment.stop();
	}

}
