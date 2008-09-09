package org.jboss.rails.protocol.rails;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.jboss.rails.RailsAppContextFactory;
import org.jboss.virtual.VFS;
import org.jboss.virtual.VirtualFile;
import org.jboss.virtual.plugins.vfs.VirtualFileURLConnection;

public class Handler extends URLStreamHandler {
	
	protected URLConnection openConnection(URL url) throws IOException {
		String host = url.getHost();
		VFS vfs = RailsAppContextFactory.find(host);
		if (vfs == null)
			throw new IOException("VFS does not exist: " + url);

		VirtualFile vf = vfs.getChild(url.getPath());
		if (vf == null)
			throw new IOException("VFS does not exist: " + url);

		return new VirtualFileURLConnection(url, vf);
	}

}
