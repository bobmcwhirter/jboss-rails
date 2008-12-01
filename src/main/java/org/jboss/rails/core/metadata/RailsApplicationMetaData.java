package org.jboss.rails.core.metadata;

import org.jboss.virtual.VirtualFile;

public class RailsApplicationMetaData {

	private VirtualFile railsRoot;
	private String railsEnv = "development";

	public RailsApplicationMetaData() {
		
	}
	
	public RailsApplicationMetaData(VirtualFile railsRoot) {
		this( railsRoot, "development" );
	}
	
	public RailsApplicationMetaData(VirtualFile railsRoot, String railsEnv) {
		this.railsRoot = railsRoot;
		this.railsEnv = railsEnv;
	}
	
	public void setRailsRoot(VirtualFile railsRoot) {
		this.railsRoot = railsRoot;
	}
	
	public VirtualFile getRailsRoot() {
		return this.railsRoot;
	}
	
	public void setRailsEnv(String environment) {
		this.railsEnv = environment;
	}
	
	public String getRailsEnv() {
		return this.railsEnv;
	}
	
	public String toString() {
		return "[RailsApplicationMetaData: railsRoot=" + railsRoot + "; railsEnv=" + railsEnv + "]";
	}
	
}
