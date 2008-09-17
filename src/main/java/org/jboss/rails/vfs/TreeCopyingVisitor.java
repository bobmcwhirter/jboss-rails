package org.jboss.rails.vfs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jboss.virtual.VirtualFile;
import org.jboss.virtual.VisitorAttributes;
import org.jboss.virtual.plugins.vfs.helpers.AbstractVirtualFileVisitor;
import org.jboss.virtual.plugins.vfs.helpers.MatchAllVirtualFileFilter;

public class TreeCopyingVisitor extends AbstractVirtualFileVisitor {

	private File outRoot;

	public static void writeTree(VirtualFile root, File outRoot) throws IOException {
		TreeCopyingVisitor visitor = createVisitor(outRoot);
		root.visit(visitor);
	}

	public static TreeCopyingVisitor createVisitor(File outRoot) throws IOException {
		VisitorAttributes attributes = new VisitorAttributes();
		attributes.setIgnoreErrors(false);
		attributes.setIncludeRoot(false);
		attributes.setLeavesOnly(false);
		attributes.setRecurseFilter(MatchAllVirtualFileFilter.INSTANCE);
		return new TreeCopyingVisitor(outRoot, attributes);
	}

	public TreeCopyingVisitor(File outRoot, VisitorAttributes attributes) throws IOException {
		super(attributes);
		this.outRoot = outRoot;
	}

	public void visit(VirtualFile virtualFile) {
		try {
			copy(virtualFile);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	protected void copy(VirtualFile src) throws IOException {
		String path = src.getPathName();

		File out = new File(outRoot, path);

		if (src.isLeaf()) {
			copy(src, out);
		} else {
			out.mkdirs();
		}
	}

	protected void copy(VirtualFile src, File dest) throws IOException {
		dest.getParentFile().mkdirs();
		byte[] buffer = new byte[1024];
		int len = 0;

		InputStream in = src.openStream();

		OutputStream out = new FileOutputStream(dest);

		while ((len = in.read(buffer)) >= 0) {
			out.write(buffer, 0, len);
		}

		in.close();
		out.close();

	}

}
