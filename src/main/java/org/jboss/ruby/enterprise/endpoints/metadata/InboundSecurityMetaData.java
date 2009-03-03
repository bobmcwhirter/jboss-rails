package org.jboss.ruby.enterprise.endpoints.metadata;

public class InboundSecurityMetaData {
	
	private String trustStore;
	
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
	
	public void setTrustStore(String trustStore) {
		this.trustStore = trustStore;
	}
	
	public String getTrustStore() {
		return this.trustStore;
	}
	
	public void setVerifyTimestamp(boolean verifyTimestamp) {
		this.verifyTimestamp = verifyTimestamp;
	}
	
	public boolean isVerifyTimestamp() {
		return this.verifyTimestamp;
	}
	
	public String toString() {
		return "[InboundSecurityMetaData: verifySignature=" + this.verifySignature + "; verifyTimestamp=" + this.verifyTimestamp + "; trustStore=" + trustStore + "]";
	}
	

}
