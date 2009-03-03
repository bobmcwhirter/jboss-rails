package org.jboss.ruby.enterprise.endpoints.metadata;

public class SecurityMetaData {
	
	private InboundSecurityMetaData inboundSecurityMetaData;
	private OutboundSecurityMetaData outboundSecurityMetaData;

	public SecurityMetaData() {
	}
	
	public void setInboundSecurityMetaData(InboundSecurityMetaData inboundSecurityMetaData) {
		this.inboundSecurityMetaData = inboundSecurityMetaData;
	}
	
	public InboundSecurityMetaData getInboundSecurityMetaData() {
		return this.inboundSecurityMetaData;
	}
	
	public void setOutboundSecurityMetaData(OutboundSecurityMetaData outboundSecurityMetaData) {
		this.outboundSecurityMetaData = outboundSecurityMetaData;
	}
	
	public OutboundSecurityMetaData getOutboundSecurityMetaData() {
		return this.outboundSecurityMetaData;
	}
}
