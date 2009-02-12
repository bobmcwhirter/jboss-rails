package org.jboss.ruby.enterprise.webservices.deployers;

import java.util.ArrayList;
import java.util.List;

import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.ValueMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.vfs.spi.deployer.AbstractSimpleVFSRealDeployer;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.logging.Logger;
import org.jboss.metadata.common.jboss.WebserviceDescriptionsMetaData;
import org.jboss.metadata.web.jboss.JBossServletsMetaData;
import org.jboss.metadata.web.jboss.JBossWebMetaData;
import org.jboss.metadata.web.spec.ServletMappingMetaData;
import org.jboss.ruby.enterprise.webservices.RubyWebService;
import org.jboss.ruby.enterprise.webservices.metadata.RubyWebServiceMetaData;
import org.jboss.ruby.enterprise.webservices.metadata.RubyWebServicesMetaData;
import org.jboss.system.metadata.ServiceMetaData;

public class RubyWebServicesDeployer extends AbstractSimpleVFSRealDeployer<RubyWebServicesMetaData> {

	private static final String BEAN_PREFIX = "jboss.ruby.enterprise.webservices";

	private static final Logger log = Logger.getLogger(RubyWebServicesDeployer.class);

	public RubyWebServicesDeployer() {
		super(RubyWebServicesMetaData.class);
		setOutput(BeanMetaData.class);
	}

	@Override
	public void deploy(VFSDeploymentUnit unit, RubyWebServicesMetaData metaData) throws DeploymentException {
		log.info("Deploying webservices for : " + metaData);

		for (RubyWebServiceMetaData serviceMetaData : metaData.getWebSerices()) {
			String beanName = BEAN_PREFIX + "." + unit.getSimpleName() + "." + serviceMetaData.getName();
			log.info("BeanMetaData for " + beanName);

			BeanMetaDataBuilder beanBuilder = BeanMetaDataBuilder.createBuilder(beanName, RubyWebService.class.getName());

			ValueMetaData poolInjection = beanBuilder.createInject("jboss.ruby.runtime.pool.ovirt-ec2");
			beanBuilder.addPropertyMetaData("rubyRuntimePool", poolInjection);
			beanBuilder.addPropertyMetaData("rubyClassName", serviceMetaData.getName());
			beanBuilder.addPropertyMetaData("wsdlLocation", getWSDLLocation(serviceMetaData));
			beanBuilder.addPropertyMetaData("targetNamespace", serviceMetaData.getTargetNamespace());
			beanBuilder.addPropertyMetaData("portName", serviceMetaData.getPortName());
			beanBuilder.addPropertyMetaData("address", serviceMetaData.getName());
			beanBuilder.addPropertyMetaData("verifySignature", serviceMetaData.getInboundSecurity().isVerifySignature());
			beanBuilder.addPropertyMetaData("verifyTimestamp", serviceMetaData.getInboundSecurity().isVerifyTimestamp());

			BeanMetaData busBean = unit.getAttachment(BeanMetaData.class + "$cxf.bus", BeanMetaData.class);
			ValueMetaData busInjection = beanBuilder.createInject(busBean.getName());
			beanBuilder.addPropertyMetaData("bus", busInjection);

			BeanMetaData beanMetaData = beanBuilder.getBeanMetaData();
			unit.addAttachment(BeanMetaData.class + "$webservice." + serviceMetaData.getName(), beanMetaData, BeanMetaData.class);
		}
	}

	private String getWSDLLocation(RubyWebServiceMetaData serviceMetaData) {
		return "file://" + serviceMetaData.getDirectory() + "/" + serviceMetaData.getName() + ".wsdl";
	}

}
