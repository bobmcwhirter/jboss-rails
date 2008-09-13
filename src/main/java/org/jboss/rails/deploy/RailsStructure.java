package org.jboss.rails.deploy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.vfs.plugins.structure.AbstractVFSStructureDeployer;
import org.jboss.deployers.vfs.spi.structure.StructureContext;
import org.jboss.rails.vfs.RailsAppContext;
import org.jboss.virtual.VFS;
import org.jboss.virtual.VirtualFile;

public class RailsStructure extends AbstractVFSStructureDeployer {

	public RailsStructure() {
		setRelativeOrder(900);
	}

	public boolean determineStructure(StructureContext structureContext) throws DeploymentException {
		log.info( "Determining structure for " + structureContext.getFile() );
		VirtualFile file = structureContext.getFile();
		if ( ! file.getName().endsWith( ".rails" ) ) {
			return false;
		}
		try {
			log.info( "Determine Structure name: " + structureContext.getName() );
			log.info( "Determine Structure root: " + structureContext.getRoot() );
			log.info( "Determine Structure file: " + structureContext.getFile() );
			String location = determineLocation(structureContext.getFile());
			VirtualFile railsRoot = VFS.getRoot(new URL(location));
			VirtualFile eor = new RailsAppContext(structureContext.getName() + ".eor", railsRoot).getRoot().getVirtualFile();
			return structureContext.determineChildStructure(eor);
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
