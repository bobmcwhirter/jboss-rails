package org.jboss.ruby.enterprise.webservices.cxf;

import java.lang.reflect.Method;

import javax.xml.namespace.QName;
import javax.xml.transform.stax.StAXSource;

import org.apache.cxf.service.factory.DefaultServiceConfiguration;
import org.apache.cxf.service.model.OperationInfo;

public class RubyServiceConfiguration extends DefaultServiceConfiguration {
	
	private String portName;

	public RubyServiceConfiguration(String portName) {
		this.portName = portName;
	}

	@Override
	public String getServiceName() {
		return this.portName;
	}

	@Override
	public Class getRequestWrapper(Method selected) {
		return StAXSource.class;
	}

	@Override
	public String getRequestWrapperClassName(Method selected) {
		return StAXSource.class.getName();
	}

	@Override
	public Class getResponseWrapper(Method selected) {
		return StAXSource.class;
	}

	@Override
	public String getResponseWrapperClassName(Method selected) {
		return StAXSource.class.getName();
	}

}
