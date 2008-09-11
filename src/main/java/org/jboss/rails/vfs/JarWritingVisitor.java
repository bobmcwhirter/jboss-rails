package org.jboss.rails.vfs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.jboss.virtual.VirtualFile;
import org.jboss.virtual.VisitorAttributes;
import org.jboss.virtual.plugins.vfs.helpers.AbstractVirtualFileVisitor;
import org.jboss.virtual.plugins.vfs.helpers.MatchAllVirtualFileFilter;

public class JarWritingVisitor extends AbstractVirtualFileVisitor {

	private JarOutputStream out;
	
	public static void writeJar(VirtualFile root, String path) throws IOException {
		JarWritingVisitor visitor = createVisitor(path);
		try {
			root.visit( visitor );
		} finally {
			visitor.close();
		}
	}
	
	public static JarWritingVisitor createVisitor(String path) throws IOException {
		return createVisitor( new File( path ) );
	}
	
	public static JarWritingVisitor createVisitor(File file) throws IOException {
		return createVisitor( new FileOutputStream( file ) );
	}

	public static JarWritingVisitor createVisitor(OutputStream out) throws IOException {
		VisitorAttributes attributes = new VisitorAttributes();
		attributes.setIgnoreErrors(false);
		attributes.setIncludeRoot(false);
		attributes.setLeavesOnly(false);
		attributes.setRecurseFilter(MatchAllVirtualFileFilter.INSTANCE);
		return new JarWritingVisitor(out, attributes);
	}

	public JarWritingVisitor(OutputStream out, VisitorAttributes attributes) throws IOException {
		super(attributes);
		this.out = new JarOutputStream(out);
	}

	public void visit(VirtualFile virtualFile) {
		try {
			addToJar(virtualFile);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	
	public void close() throws IOException {
		out.close();
	}

	protected void addToJar(VirtualFile src) throws IOException {
		String path = src.getPathName();
		
		if ( src.isLeaf() ) {
			JarEntry entry = new JarEntry( path );
			out.putNextEntry( entry );
		
			InputStream in = src.openStream();
		
			byte[] buffer = new byte[10240];
			int len = 0;
		
			while ( (len = in.read( buffer ) ) >= 0 ) {
				out.write( buffer, 0, len );
			}
		}
	}
}
