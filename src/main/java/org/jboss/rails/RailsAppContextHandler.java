package org.jboss.rails;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.jboss.virtual.VirtualFile;
import org.jboss.virtual.spi.VFSContext;
import org.jboss.virtual.spi.VirtualFileHandler;

public class RailsAppContextHandler implements VirtualFileHandler {

	private RailsAppContext context;
	private String name;

	public RailsAppContextHandler(RailsAppContext context, String name) {
		this.context = context;
		this.name    = name;
	}
	public void close() {
	}

	public boolean delete(int gracePeriod) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean exists() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	public VirtualFileHandler getChild(String path) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<VirtualFileHandler> getChildren(boolean ignoreErrors) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public long getLastModified() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getLocalPathName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		return this.name;
	}

	public VirtualFileHandler getParent() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPathName() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getSize() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	public VFSContext getVFSContext() {
		// TODO Auto-generated method stub
		return null;
	}

	public VirtualFile getVirtualFile() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasBeenModified() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isHidden() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isLeaf() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isNested() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	public InputStream openStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean removeChild(String name) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	public void replaceChild(VirtualFileHandler original, VirtualFileHandler replacement) {
		// TODO Auto-generated method stub
		
	}

	public URI toURI() throws URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

	public URL toURL() throws MalformedURLException, URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

	public URL toVfsUrl() throws MalformedURLException, URISyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

}
