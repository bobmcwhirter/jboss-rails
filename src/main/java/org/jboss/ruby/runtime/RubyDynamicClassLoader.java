package org.jboss.ruby.runtime;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

import org.jboss.virtual.MemoryFileFactory;
import org.jboss.virtual.plugins.context.memory.MemoryContextFactory;

public class RubyDynamicClassLoader extends URLClassLoader {
	
	private URL baseUrl;

	private RubyDynamicClassLoader(URL baseUrl, URL[] urls, ClassLoader parent) {
		super( urls, parent );
		MemoryContextFactory.getInstance().createRoot( baseUrl );
		this.baseUrl = baseUrl;
	}
	
	public void putFile(String path, String contents) throws MalformedURLException {
		URL fileUrl = new URL( this.baseUrl, path );
		MemoryFileFactory.putFile(fileUrl, contents.getBytes());
	}
	
	public static RubyDynamicClassLoader create(String name, Collection<URL> urls, ClassLoader parent) throws MalformedURLException {
		URL baseUrl = new URL( "vfsmemory://" + name + ".ruby.jboss/" );
		
		URL[] urlArray = new URL[urls.size() + 1 ];
		
		int i = 0;
		
		for ( URL url : urls ) {
			urlArray[i] = url;
			++i;
		}
		
		urlArray[i] = baseUrl;
		
		return new RubyDynamicClassLoader( baseUrl, urlArray, parent );
		
	}

}
