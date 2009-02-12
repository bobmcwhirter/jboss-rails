package org.jboss.ruby.enterprise.webservices.metadata;

public class InboundSecurityMetaData {
	
	private boolean verifySignature;
	private boolean verifyTimestamp;

	public InboundSecurityMetaData() {
		
	}
	
	public void setVerifySignature(boolean verifySignature) {
		this.verifySignature = verifySignature;
	}
	
	public boolean isVerifySignature() {
		return this.verifySignature;
	}
	
	public void setVerifyTimestamp(boolean verifyTimestamp) {
		this.verifyTimestamp = verifyTimestamp;
	}
	
	public boolean isVerifyTimestamp() {
		return this.verifyTimestamp;
	}
	
	public String toString() {
		return "[InboundSecurityMetaData: verifySignature=" + this.verifySignature + "; verifyTimestamp=" + this.verifyTimestamp + "]";
	}
	

}
