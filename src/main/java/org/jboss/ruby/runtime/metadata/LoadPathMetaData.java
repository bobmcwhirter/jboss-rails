package org.jboss.ruby.runtime.metadata;

import java.util.ArrayList;
import java.util.List;

public class LoadPathMetaData {
	
	private List<String> loadPaths = new ArrayList<String>();
	
	public LoadPathMetaData() {
		
	}
	
	public LoadPathMetaData(String loadPath) {
		this.loadPaths.add( loadPath );
	}
	
	public LoadPathMetaData(List<String> loadPaths) {
		this.loadPaths.addAll( loadPaths );
	}
	
	public void addLoadPath(String loadPath) {
		this.loadPaths.add( loadPath );
	}
	
	public List<String> getLoadPaths() {
		return this.loadPaths;
	}

}
