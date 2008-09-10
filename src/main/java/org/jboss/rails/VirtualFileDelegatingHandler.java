package org.jboss.rails;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jboss.virtual.VirtualFile;
import org.jboss.virtual.spi.VFSContext;
import org.jboss.virtual.spi.VirtualFileHandler;

public class VirtualFileDelegatingHandler implements VirtualFileHandler {

	private VirtualFile delegate;
	private VirtualFileHandler parent;

	public VirtualFileDelegatingHandler(VirtualFile delegate) {
		this.delegate = delegate;
	}
	
	public void close() {
		// TODO Auto-generated method stub
	}

	public boolean delete(int gracePeriod) throws IOException {
		return delegate.delete();
	}

	public boolean exists() throws IOException {
		return delegate.exists();
	}

	public VirtualFileHandler getChild(String path) throws IOException {
		VirtualFile child = delegate.getChild(path);
		if ( child == null ) {
			return null;
		}
		
		return new VirtualFileDelegatingHandler( child );
	}

	public List<VirtualFileHandler> getChildren(boolean ignoreErrors) throws IOException {
		List<VirtualFile> children = delegate.getChildren();
		List<VirtualFileHandler> wrappedChildren = new ArrayList<VirtualFileHandler>();
		
		for ( VirtualFile child : children ) {
			wrappedChildren.add( new VirtualFileDelegatingHandler( child ) );
		}
		
		return wrappedChildren;
	}

	public long getLastModified() throws IOException {
		return delegate.getLastModified();
	}

	public String getLocalPathName() {
		return delegate.getPathName();
	}

	public String getName() {
		return delegate.getName();
	}

	public VirtualFileHandler getParent() throws IOException {
		if ( this.parent == null ) {
			VirtualFile parent = delegate.getParent();
			if ( parent != null ) {
				return new VirtualFileDelegatingHandler( parent );
			}
		}
		
		return this.parent;
	}

	public String getPathName() {
		return delegate.getPathName();
	}

	public long getSize() throws IOException {
		return delegate.getSize();
	}

	public VFSContext getVFSContext() {
		return null;
	}

	public VirtualFile getVirtualFile() {
		return delegate;
	}

	public boolean hasBeenModified() throws IOException {
		return delegate.hasBeenModified();
	}

	public boolean isHidden() throws IOException {
		return delegate.isHidden();
	}

	public boolean isLeaf() throws IOException {
		return delegate.isLeaf();
	}

	public boolean isNested() throws IOException {
		return true;
	}

	public InputStream openStream() throws IOException {
		return delegate.openStream();
	}

	public boolean removeChild(String name) throws IOException {
		return false;
	}

	public void replaceChild(VirtualFileHandler original, VirtualFileHandler replacement) {
	}

	public URI toURI() throws URISyntaxException {
		try {
			return delegate.toURI();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public URL toURL() throws MalformedURLException, URISyntaxException {
		return delegate.toURL();
	}

	public URL toVfsUrl() throws MalformedURLException, URISyntaxException {
		return delegate.toURL();
	}

}
