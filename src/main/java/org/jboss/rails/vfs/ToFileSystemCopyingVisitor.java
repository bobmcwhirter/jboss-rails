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

public class ToFileSystemCopyingVisitor extends AbstractVirtualFileVisitor {

	private File outputDirectory;

	public static ToFileSystemCopyingVisitor createVisitor(String outputDirectoryPath) throws IOException {
		return createVisitor(new File(outputDirectoryPath));
	}

	public static ToFileSystemCopyingVisitor createVisitor(File outputDirectory, VisitorAttributes attributes) throws IOException {
		return new ToFileSystemCopyingVisitor(outputDirectory, attributes);
	}

	public static ToFileSystemCopyingVisitor createVisitor(File outputDirectory) throws IOException {
		VisitorAttributes attributes = new VisitorAttributes();
		attributes.setIgnoreErrors(false);
		attributes.setIncludeRoot(true);
		attributes.setLeavesOnly(false);
		attributes.setRecurseFilter(MatchAllVirtualFileFilter.INSTANCE);
		return new ToFileSystemCopyingVisitor(outputDirectory, attributes);
	}

	public ToFileSystemCopyingVisitor(File outputDirectory, VisitorAttributes attributes) throws IOException {
		super(attributes);
		this.outputDirectory = outputDirectory;
		setUpDirectory();
	}

	private void setUpDirectory() throws IOException {
		if (!outputDirectory.exists()) {
			if (!outputDirectory.mkdirs()) {
				throw new IOException("unable to create output directory: " + outputDirectory);
			}
		}

		if (!outputDirectory.canWrite()) {
			throw new IOException("unable to write to output directory: " + outputDirectory);
		}

	}

	public void visit(VirtualFile virtualFile) {
		System.err.println("Visit: " + virtualFile.getPathName());
		try {
			copyToFileSystem(virtualFile);
		} catch (IOException e) {
			System.err.println( e.getMessage() );
		}
	}

	protected void copyToFileSystem(VirtualFile src) throws IOException {
		String path = src.getPathName();
		File dest = new File(outputDirectory, path);

		if (src.isLeaf()) {
			if (dest.exists()) {
				if (src.getLastModified() > dest.lastModified()) {
					dest.delete();
				}
			} else {
				File parent = dest.getParentFile();
				if ( ! parent.exists() ) {
					if (!dest.getParentFile().mkdirs()) {
						throw new IOException("unable to create directory: " + dest.getParentFile());
					}
				}
			}
			InputStream in = src.openStream();
			OutputStream out = new FileOutputStream(dest);

			try {
				byte[] buffer = new byte[10240];
				int len = 0;

				while ((len = in.read(buffer)) >= 0) {
					out.write(buffer, 0, len);
				}
			} finally {
				in.close();
				out.close();
			}

		} else {
			if (!dest.exists()) {
				if (!dest.mkdirs()) {
					throw new IOException("unable to create directory: " + dest);
				}

			}
		}
	}

}
