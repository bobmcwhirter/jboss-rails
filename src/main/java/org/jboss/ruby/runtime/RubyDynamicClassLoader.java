package org.jboss.ruby.runtime;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.jboss.virtual.MemoryFileFactory;

public class RubyDynamicClassLoader extends URLClassLoader {
	
	private URL baseUrl;

	public RubyDynamicClassLoader(String name, ClassLoader parent) throws MalformedURLException {
		this( new URL( "vfsmemory://" + name + ".ruby.jboss/" ), parent );
	}
	
	RubyDynamicClassLoader(URL baseUrl, ClassLoader parent) {
		super( new URL[]{ baseUrl  }, parent );
		
		this.baseUrl = baseUrl;
	}
	
	public void putFile(String path, String contents) throws MalformedURLException {
		URL fileUrl = new URL( this.baseUrl, path );
		MemoryFileFactory.putFile(fileUrl, contents.getBytes());
	}

}
