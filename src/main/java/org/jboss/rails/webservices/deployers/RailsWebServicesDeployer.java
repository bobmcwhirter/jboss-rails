package org.jboss.rails.webservices.deployers;

import java.io.IOException;
import java.util.List;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.logging.Logger;
import org.jboss.rails.core.metadata.RailsApplicationMetaData;
import org.jboss.ruby.enterprise.webservices.metadata.RubyWebServiceMetaData;
import org.jboss.ruby.enterprise.webservices.metadata.RubyWebServicesMetaData;
import org.jboss.virtual.VirtualFile;

public class RailsWebServicesDeployer extends AbstractDeployer {

	private static Logger log = Logger.getLogger(RailsWebServicesDeployer.class);

	public RailsWebServicesDeployer() {
		setStage(DeploymentStages.PARSE );
		addInput(RailsApplicationMetaData.class);
	}

	public void deploy(DeploymentUnit unit) throws DeploymentException {
		VFSDeploymentUnit vfsUnit = (VFSDeploymentUnit) unit;
		
		RailsApplicationMetaData railsAppMetaData = unit.getAttachment( RailsApplicationMetaData.class );
		
		if ( railsAppMetaData == null ) {
			return;
		}
		
		RubyWebServicesMetaData servicesMetaData = null;
		
		try {
			VirtualFile apisDir = vfsUnit.getRoot().getChild("app/webservices");

			log.info("APIs: " + apisDir);

			if (apisDir == null) {
				return;
			}

			List<VirtualFile> apiDirs = apisDir.getChildren();
			for (VirtualFile apiDir : apiDirs) {
				if ( servicesMetaData == null ) {
					servicesMetaData = new RubyWebServicesMetaData();
				}
				RubyWebServiceMetaData webService = new RubyWebServiceMetaData();
				webService.setDirectory( apiDir );
				webService.setName( apiDir.getName() );
				servicesMetaData.addWebService( webService );
				log.info("deploying for: " + apiDir);
			}
		} catch (IOException e) {
			log.error( e );
			return;
		}
		
		if ( servicesMetaData != null ) {
			log.info( "attaching RubyWebServicesMetaData: " + servicesMetaData );
			vfsUnit.addAttachment( RubyWebServicesMetaData.class, servicesMetaData );
		}

	}

}
