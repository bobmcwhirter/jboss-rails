package org.jboss.ruby.runtime;

import java.net.URL;
import java.net.URLClassLoader;

import org.jboss.logging.Logger;

public class LoadPathClassLoader extends URLClassLoader {
	
	private static final Logger log = Logger.getLogger(LoadPathClassLoader.class);
	
	private URL[] urls;
	
	public LoadPathClassLoader(URL[] urls, ClassLoader parent) {
		super( urls, parent );
		this.urls = urls;
	}

	@Override
	public URL findResource(String name) {
		URL result = super.findResource(name);
		return result;
		
	}
	
	

}
