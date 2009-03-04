package org.jboss.ruby.runtime.metadata;

import org.jboss.ruby.runtime.RuntimeInitializer;

public class RubyRuntimeMetaData {
	
	private LoadPathMetaData loadPath;
	private RuntimeInitializer initializer;

	public RubyRuntimeMetaData() {
		
	}
	
	public void setRuntimeInitializer(RuntimeInitializer initializer) {
		this.initializer = initializer;
	}
	
	public RuntimeInitializer getRuntimeInitializer() {
		return this.initializer;
	}

}
