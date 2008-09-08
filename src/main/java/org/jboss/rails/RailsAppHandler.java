package org.jboss.rails;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.jboss.virtual.VirtualFile;
import org.jboss.virtual.plugins.context.DelegatingHandler;
import org.jboss.virtual.plugins.context.StructuredVirtualFileHandler;
import org.jboss.virtual.plugins.context.vfs.Assembled;
import org.jboss.virtual.plugins.vfs.helpers.PathTokenizer;
import org.jboss.virtual.spi.VFSContext;
import org.jboss.virtual.spi.VirtualFileHandler;

@Assembled
public class RailsAppHandler extends DelegatingHandler {

	Map<String, VirtualFile> overlayChildren;

	public RailsAppHandler(VFSContext context, VirtualFileHandler parent, String name, VirtualFileHandler delegate) {
		super(context, parent, name, delegate);
	}

	@Override
	public VirtualFileHandler getChild(String path) throws IOException {
		// Parse the path
		List<String> tokens = PathTokenizer.getTokens(path);
		if (tokens == null || tokens.size() == 0)
			return this;

		// Go through each context starting from ours
		// check the parents are not leaves.
		VirtualFileHandler current = this;
		for (int i = 0; i < tokens.size(); ++i) {
			if (current == null)
				return null;

			String token = tokens.get(i);
			if (PathTokenizer.isCurrentToken(token)) {
				continue;
			}

			if (PathTokenizer.isReverseToken(token)) {
				VirtualFileHandler parent = current.getParent();
				if (parent == null) {
					// TODO - still IOE or null?
					throw new IOException("Using reverse path on top file handler: " + current + ", " + path);
				} else {
					current = parent;
				}

				continue;
			}

			if (current.isLeaf()) {
				return null;
			} else if (current instanceof StructuredVirtualFileHandler) {
				StructuredVirtualFileHandler structured = (StructuredVirtualFileHandler) current;
				current = structured.createChildHandler(token);
			} else {
				String remainingPath = PathTokenizer.getRemainingPath(tokens, i);
				if (current instanceof DelegatingHandler) {
					return ((DelegatingHandler) current).getDelegate().getChild(remainingPath);
				}
				return current.getChild(remainingPath);
			}
		}

		// The last one is the result
		return current;
		// return super.getChild(path);
	}

	@Override
	public List<VirtualFileHandler> getChildren(boolean ignoreErrors) throws IOException {
		// TODO Auto-generated method stub
		return super.getChildren(ignoreErrors);
	}

	@Override
	public boolean isLeaf() throws IOException {
		// TODO Auto-generated method stub
		return super.isLeaf();
	}

	@Override
	public boolean removeChild(String path) throws IOException {
		// TODO Auto-generated method stub
		return super.removeChild(path);
	}

}
