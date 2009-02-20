package org.jboss.ruby.enterprise.endpoints.metadata;

public class SecurityMetaData {
	
	private InboundSecurityMetaData inboundSecurityMetaData;
	private OutboundSecurityMetaData outboundSecurityMetaData;

	public SecurityMetaData() {
		System.err.println( "no-arg ctor" );
	}
	
	public SecurityMetaData(Object o) {
		System.err.println( "1-arg ctor [" + o + "]" );
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
