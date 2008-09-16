package org.jboss.rails.deploy;

import org.jboss.deployers.vfs.plugins.client.AbstractVFSDeployment;
import org.jboss.virtual.VirtualFile;

public class RailsVFSDeployment extends AbstractVFSDeployment {
	
	private String name;

	public RailsVFSDeployment(String name, VirtualFile virtualFile) {
		super( virtualFile );
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

}
