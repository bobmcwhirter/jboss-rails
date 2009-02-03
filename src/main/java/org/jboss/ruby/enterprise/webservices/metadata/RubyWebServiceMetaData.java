package org.jboss.ruby.enterprise.webservices.metadata;

import org.jboss.virtual.VirtualFile;

public class RubyWebServiceMetaData {
	
	private VirtualFile dir;
	private String name;

	public RubyWebServiceMetaData() {
		
	}
	
	public void setDirectory(VirtualFile dir) {
		this.dir = dir;
	}
	
	public VirtualFile getDirectory() {
		return this.dir;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String toString() {
		return "[RubyWebServiceMetaData: dir=" + this.dir + "; name=" + this.name + "]";
	}

}

