package org.jboss.rails.deploy;

import java.io.IOException;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.vfs.plugins.structure.AbstractVFSStructureDeployer;
import org.jboss.deployers.vfs.spi.structure.StructureContext;
import org.jboss.virtual.VirtualFile;

public class RailsStructure extends AbstractVFSStructureDeployer {

	public RailsStructure() {
		setRelativeOrder( 1000 );
	}

	@Override
	public boolean determineStructure(StructureContext context) throws DeploymentException {
		boolean recognized = false;
		VirtualFile root = context.getRoot();

		try {
			if ( root.isLeaf() ) {
				return false;
			}
			VirtualFile config = root.getChild("config");
			if (config != null) {
				if (config.getChild("environment.rb") != null) {
					createContext(context, "config");
					recognized = true;
				}
			}
		} catch (IOException e) {
			throw new DeploymentException(e);
		}

		return recognized;
	}
}
