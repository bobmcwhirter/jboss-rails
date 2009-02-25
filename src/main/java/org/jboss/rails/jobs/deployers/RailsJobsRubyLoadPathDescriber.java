package org.jboss.rails.jobs.deployers;

import java.io.IOException;
import java.net.URISyntaxException;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.vfs.spi.deployer.AbstractSimpleVFSRealDeployer;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.rails.core.metadata.RailsApplicationMetaData;
import org.jboss.ruby.runtime.metadata.RubyLoadPathMetaData;
import org.jboss.virtual.VirtualFile;

public class RailsJobsRubyLoadPathDescriber extends AbstractSimpleVFSRealDeployer<RailsApplicationMetaData> {

	public RailsJobsRubyLoadPathDescriber() {
		super(RailsApplicationMetaData.class);
		addOutput(RubyLoadPathMetaData.class);
		setStage(DeploymentStages.DESCRIBE);
	}

	@Override
	public void deploy(VFSDeploymentUnit unit, RailsApplicationMetaData root) throws DeploymentException {
		log.info( "DEPLOY [" + unit + "]" );
		VirtualFile jobsDir;
		try {
			jobsDir = unit.getRoot().getChild("app/jobs");
			if (jobsDir != null) {
				log.info( "adding jobsDir [" + jobsDir.toURL() + "]" );
				RubyLoadPathMetaData loadPathMetaData = new RubyLoadPathMetaData();
				loadPathMetaData.setURL( jobsDir.toURL() );
				unit.addAttachment( RubyLoadPathMetaData.class.getName() + "$jobs", loadPathMetaData, RubyLoadPathMetaData.class );
			}

		} catch (IOException e) {
			// ignore
			return;
		} catch (URISyntaxException e) {
			throw new DeploymentException( e );
		}

	}

}
