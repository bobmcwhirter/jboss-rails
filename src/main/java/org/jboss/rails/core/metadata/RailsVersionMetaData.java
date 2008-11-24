package org.jboss.rails.core.metadata;

public class RailsVersionMetaData {

	private int major;
	private int minor;
	private int tiny;

	public RailsVersionMetaData(int major, int minor, int tiny) {
		this.major = major;
		this.minor = minor;
		this.tiny = tiny;
	}
	
	public boolean isThreadSafe() {
		return ( ( this.major == 2 && this.minor >= 2 ) || ( this.major > 2 ) );
	}

	public int getMajor() {
		return this.major;
	}

	public int getMinor() {
		return this.minor;
	}

	public int getTiny() {
		return this.tiny;
	}
	
	public String toString() {
		return "[RailsVersionMetaData: major=" + major + "; minor=" + minor + "; tiny=" + tiny + "]";
	}

}
