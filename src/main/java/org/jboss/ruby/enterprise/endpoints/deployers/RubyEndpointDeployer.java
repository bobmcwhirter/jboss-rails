/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.ruby.enterprise.endpoints.deployers;

import java.util.Set;

import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.ValueMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.logging.Logger;
import org.jboss.ruby.core.runtime.deployers.RubyRuntimePoolDeployer;
import org.jboss.ruby.enterprise.crypto.metadata.CryptoMetaData;
import org.jboss.ruby.enterprise.crypto.metadata.CryptoStoreMetaData;
import org.jboss.ruby.enterprise.endpoints.RubyEndpoint;
import org.jboss.ruby.enterprise.endpoints.metadata.InboundSecurityMetaData;
import org.jboss.ruby.enterprise.endpoints.metadata.RubyEndpointMetaData;
import org.jboss.ruby.enterprise.endpoints.metadata.SecurityMetaData;

/**
 * REAL stage deployer to deploy all <code>RubyEndpointMetaData</code> in a
 * unit.
 * 
 * This deployer will seek out all instances of RubyWebServiceMetaData attached
 * to the unit, regardless of name, and deploy them.
 * 
 * Output is zero-or-more <code>BeanMetaData</code> describing
 * <code>RubyWebService</code> instances.
 * 
 * @author Bob McWhirter
 */
public class RubyEndpointDeployer extends AbstractDeployer {

	private static final String BEAN_PREFIX = "jboss.ruby.enterprise.webservices";

	private static final Logger log = Logger.getLogger(RubyEndpointDeployer.class);

	public RubyEndpointDeployer() {
		super();
		setStage(DeploymentStages.REAL);
		setAllInputs(true);
		addInput(CryptoMetaData.class);
		setOutput(BeanMetaData.class);
	}

	public void deploy(DeploymentUnit unit) throws DeploymentException {
		Set<? extends RubyEndpointMetaData> allMetaData = unit.getAllMetaData(RubyEndpointMetaData.class);

		if (allMetaData.size() == 0) {
			return;
		}

		BeanMetaData busBean = unit.getAttachment(BeanMetaData.class + "$cxf.bus", BeanMetaData.class);
		if (busBean == null) {
			throw new DeploymentException("No CXF Bus available");
		}

		for (RubyEndpointMetaData each : allMetaData) {
			deployWebService(unit, busBean, each);
		}
	}

	public void deployWebService(DeploymentUnit unit, BeanMetaData busBean, RubyEndpointMetaData metaData) throws DeploymentException {
		log.debug("Deploying webservices for : " + metaData);

		String beanName = BEAN_PREFIX + "." + unit.getSimpleName() + "." + metaData.getName();

		BeanMetaDataBuilder beanBuilder = BeanMetaDataBuilder.createBuilder(beanName, RubyEndpoint.class.getName());

		ValueMetaData poolInjection = beanBuilder.createInject(RubyRuntimePoolDeployer.getBeanName(unit));
		beanBuilder.addPropertyMetaData("rubyRuntimePool", poolInjection);
		beanBuilder.addPropertyMetaData("name", metaData.getName());
		beanBuilder.addPropertyMetaData("classLocation", metaData.getClassLocation());
		beanBuilder.addPropertyMetaData("endpointClassName", metaData.getEndpointClassName());
		beanBuilder.addPropertyMetaData("wsdlLocation", metaData.getWsdlLocation());
		beanBuilder.addPropertyMetaData("targetNamespace", metaData.getTargetNamespace());
		beanBuilder.addPropertyMetaData("portName", metaData.getPortName());
		beanBuilder.addPropertyMetaData("address", "/" + metaData.getName());

		ValueMetaData typeSpaceInjection = beanBuilder.createInject(RubyTypeSpaceDeployer.getBeanName(unit, metaData.getName()));
		beanBuilder.addPropertyMetaData("rubyTypeSpace", typeSpaceInjection);

		SecurityMetaData securityMetaData = metaData.getSecurityMetaData();

		if (securityMetaData != null) {

			InboundSecurityMetaData inboundSecurity = securityMetaData.getInboundSecurityMetaData();

			if (inboundSecurity != null) {
				beanBuilder.addPropertyMetaData("verifyTimestamp", inboundSecurity.isVerifyTimestamp());
				beanBuilder.addPropertyMetaData("verifySignature", inboundSecurity.isVerifySignature());

				if (inboundSecurity.isVerifySignature()) {
					CryptoMetaData crypto = unit.getAttachment(CryptoMetaData.class);

					if (crypto != null) {
						String storeName = metaData.getSecurityMetaData().getInboundSecurityMetaData().getTrustStore();
						if (storeName == null) {
							storeName = "truststore";
						}

						CryptoStoreMetaData storeMetaData = crypto.getCryptoStoreMetaData(storeName);

						if (storeMetaData != null) {
							beanBuilder.addPropertyMetaData("trustStoreFile", storeMetaData.getStore());
							beanBuilder.addPropertyMetaData("trustStorePassword", storeMetaData.getPassword());
						} else {
							throw new DeploymentException( "no such crypto store: " + storeName );
						}
					} else {
						throw new DeploymentException( "unable to setup crypto" );
					}
				}
			}
		}

		ValueMetaData busInjection = beanBuilder.createInject(busBean.getName());
		beanBuilder.addPropertyMetaData("bus", busInjection);

		BeanMetaData beanMetaData = beanBuilder.getBeanMetaData();
		unit.addAttachment(BeanMetaData.class + "$endpoint." + metaData.getName(), beanMetaData, BeanMetaData.class);
	}
}
