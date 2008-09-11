package org.jboss.rails.vfs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.virtual.plugins.context.DelegatingHandler;
import org.jboss.virtual.spi.VFSContext;
import org.jboss.virtual.spi.VirtualFileHandler;

public class RedelegatingHandler extends DelegatingHandler {

	public RedelegatingHandler(VFSContext context, VirtualFileHandler parent, String name, VirtualFileHandler delegate) {
		super(context, parent, name, delegate);
	}

	@Override
	public VirtualFileHandler getChild(String path) throws IOException {
		VirtualFileHandler child = super.getChild(path);
		RedelegatingHandler childDelegate = new RedelegatingHandler( getVFSContext(), this, child.getName(), child );
		childDelegate.setPathName( getPathName() + "/" + child.getName() );
		return childDelegate;
	}

	@Override
	public List<VirtualFileHandler> getChildren(boolean ignoreErrors) throws IOException {
		List<VirtualFileHandler> children = super.getChildren(ignoreErrors);
		List<VirtualFileHandler> wrappedChildren = new ArrayList<VirtualFileHandler>();

		for (VirtualFileHandler child : children) {
			RedelegatingHandler childDelegate = new RedelegatingHandler(getVFSContext(), this, child.getName(), child);
			childDelegate.setPathName(getPathName() + "/" + child.getName());
			wrappedChildren.add(childDelegate);
		}

		return wrappedChildren;
	}

}
