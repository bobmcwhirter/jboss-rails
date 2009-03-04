package org.jboss.ruby.runtime;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.logging.Logger;
import org.jboss.virtual.MemoryFileFactory;
import org.jboss.virtual.VirtualFile;
import org.jboss.virtual.plugins.context.memory.MemoryContextFactory;

public class RubyDynamicClassLoader extends URLClassLoader {

	private static final Logger log = Logger.getLogger(RubyDynamicClassLoader.class);

	private static final URL[] EMPTY_URL_ARRAY = new URL[]{};

	private URL baseUrl;
	private VirtualFile baseDir;

	private URLClassLoader extension;
	
	private RubyDynamicClassLoader(URL baseUrl, URL[] urls, ClassLoader parent, VirtualFile baseDir) {
		super(urls, parent);
		MemoryContextFactory.getInstance().createRoot(baseUrl);
		this.baseUrl = baseUrl;
		this.baseDir = baseDir;
	}
	
	private RubyDynamicClassLoader(RubyDynamicClassLoader parent) {
		super( EMPTY_URL_ARRAY, parent );
		this.baseUrl = parent.baseUrl;
		this.baseDir = parent.baseDir;
	}
	
	public RubyDynamicClassLoader createChild() {
		return new RubyDynamicClassLoader( this );
	}

	public void destroy() {
		MemoryContextFactory.getInstance().deleteRoot(baseUrl);
	}

	public void putFile(String path, String contents) throws MalformedURLException {
		URL fileUrl = new URL(this.baseUrl, path);
		MemoryFileFactory.putFile(fileUrl, contents.getBytes());
	}

	public void addLoadPaths(List<String> paths) throws URISyntaxException, IOException {
		List<URL> urls = new ArrayList<URL>();

		String prefix = baseDir.toURL().getPath();

		for (String path : paths) {
			URL url = null;
			if (path.startsWith(prefix)) {
				path = path.substring( prefix.length() );
				VirtualFile file = this.baseDir.getChild( path );
				if ( file != null ) {
					url = file.toURL();
				}
			}
			if ( url != null ) {
				urls.add( url );
			}
		}
		
		URL[] urlArray = urls.toArray( new URL[ urls.size() ] );
		
		this.extension = new LoadPathClassLoader( urlArray, extension );
	}

	@Override
	public URL findResource(String name) {

		if (name.startsWith("./")) {
			name = name.substring(2);
		}

		URL result = super.findResource(name);

		if (result == null) {
			try {
				String prefix = baseDir.toURL().getPath();
				if (name.startsWith(prefix)) {
					name = name.substring(prefix.length());
					result = super.findResource(name);
				}
			} catch (MalformedURLException e) {
				return null;
			} catch (URISyntaxException e) {
				return null;
			}
		}
		
		if ( result == null && this.extension != null ) {
			result = extension.findResource(name);
		}
		return result;
	}

	public static RubyDynamicClassLoader create(String name, Collection<URL> urls, ClassLoader parent, VirtualFile baseDir)
			throws MalformedURLException {
		URL baseUrl = new URL("vfsmemory://" + name + ".ruby.jboss/");

		URL[] urlArray = new URL[urls.size() + 1];

		int i = 0;

		for (URL url : urls) {
			urlArray[i] = url;
			++i;
		}

		urlArray[i] = baseUrl;

		return new RubyDynamicClassLoader(baseUrl, urlArray, parent, baseDir);

	}

}
