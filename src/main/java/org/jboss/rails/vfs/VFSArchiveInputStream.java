package org.jboss.rails.vfs;

import java.io.IOException;
import java.io.InputStream;

public class VFSArchiveInputStream extends InputStream {

	private RailsAppContext context;

	public VFSArchiveInputStream(RailsAppContext context) {
		this.context = context;
	}
	
	public int read() throws IOException {
		return 0;
	}
	
	

}
