package org.jboss.ruby.runtime.metadata;

public class RubyRuntimeMetaData {
	
	private LoadPathMetaData loadPath;
	private String initScript;

	public RubyRuntimeMetaData() {
		
	}
	
	public void setLoadPath(LoadPathMetaData loadPath) {
		this.loadPath = loadPath;
	}
	
	public LoadPathMetaData getLoadPath() {
		return this.loadPath;
	}
	
	public void setInitScript(String initScript) {
		this.initScript = initScript;
	}
	
	public String getInitScript() {
		return this.initScript;
	}

}
