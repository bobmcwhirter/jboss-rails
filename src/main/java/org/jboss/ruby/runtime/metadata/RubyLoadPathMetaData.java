package org.jboss.ruby.runtime.metadata;

import java.net.URL;

public class RubyLoadPathMetaData {
	
	private URL url;

	public RubyLoadPathMetaData() {
		
	}
	
	public void setURL(URL url) {
		this.url = url;
	}
	
	public URL getURL() {
		return this.url;
	}

}
