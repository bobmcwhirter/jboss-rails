package org.jboss.rails.deploy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.deployers.vfs.spi.client.VFSDeployment;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.rails.vfs.RailsAppContext;
import org.jboss.rails.vfs.RailsAppContextFactory;
import org.jboss.virtual.VFS;
import org.jboss.virtual.VirtualFile;

public class RailsRootDeployer extends AbstractDeployer {
	
	static {
		RailsAppContextFactory.initializeRailsUrlHandling();
	}

	public RailsRootDeployer() {
		// addOutput(ClassLoadingMetaData.class);
		setStage( DeploymentStages.REAL );
		setTopLevelOnly(true);
		setAllInputs(true);
	}

	public void deploy(DeploymentUnit unit) throws DeploymentException {
		if (!(unit instanceof VFSDeploymentUnit)) {
			return;
		}
		VFSDeploymentUnit vfsUnit = (VFSDeploymentUnit) unit;
		VirtualFile file = vfsUnit.getRoot();
		log.debug("Deploying " + file);
		try {
			if (!file.isLeaf()) {
				log.debug("not a leaf");
				return;
			}
			if (!file.getName().endsWith(".rails")) {
				log.debug("does not end with .rails");
				return;
			}
			String location = determineLocation(file);
			if (location == null) {
				log.warn("no location within the reference file");
				return;
			}

			log.debug("deploying from " + location );
			VirtualFile railsRoot = VFS.getRoot(new URL(location));

			RailsAppContext appContext = new RailsAppContext(unit.getSimpleName() + ".eor", railsRoot);

			VFSDeployment railsDeployment = new RailsVFSDeployment("rails://" + unit.getSimpleName() + ".eor/", appContext.getRoot().getVirtualFile());
			log.debug( "deployment name is " + railsDeployment.getName() );
			unit.getMainDeployer().addDeployment(railsDeployment);
			unit.getMainDeployer().process();
		} catch (IOException e) {
			throw new DeploymentException(e);
		} catch (URISyntaxException e) {
			throw new DeploymentException(e);
		}
	}

	private String determineLocation(VirtualFile file) throws DeploymentException, IOException {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(file.openStream()));
			String location = in.readLine();
			if (location != null) {
				log.info("deploy from referenced directory: " + location);
				return location;
			}
			throw new DeploymentException("no location specified in .rails pointer");
		} finally {
			file.closeStreams();
		}
	}

}