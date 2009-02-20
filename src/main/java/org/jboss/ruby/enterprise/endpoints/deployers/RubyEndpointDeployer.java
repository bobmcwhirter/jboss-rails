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
import org.jboss.ruby.enterprise.endpoints.RubyEndpoint;
import org.jboss.ruby.enterprise.endpoints.metadata.RubyEndpointMetaData;

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
		setOutput(BeanMetaData.class);
	}

	public void deploy(DeploymentUnit unit) throws DeploymentException {
		log.info("attempt ruby ws deploy against: " + unit);
		Set<? extends RubyEndpointMetaData> allMetaData = unit.getAllMetaData(RubyEndpointMetaData.class);
		
		if ( allMetaData.size() == 0 ) {
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
		log.info("Deploying webservices for : " + metaData);

		String beanName = BEAN_PREFIX + "." + unit.getSimpleName() + "." + metaData.getName();
		log.info("BeanMetaData for " + beanName);

		BeanMetaDataBuilder beanBuilder = BeanMetaDataBuilder.createBuilder(beanName, RubyEndpoint.class.getName());

		ValueMetaData poolInjection = beanBuilder.createInject("jboss.ruby.runtime.pool." + unit.getSimpleName());
		beanBuilder.addPropertyMetaData("rubyRuntimePool", poolInjection);
		beanBuilder.addPropertyMetaData("endpointClassName", metaData.getEndpointClassName());
		beanBuilder.addPropertyMetaData("wsdlLocation", metaData.getWsdlLocation() );
		beanBuilder.addPropertyMetaData("targetNamespace", metaData.getTargetNamespace());
		beanBuilder.addPropertyMetaData("portName", metaData.getPortName());
		//beanBuilder.addPropertyMetaData("address", metaData.getName());
		//beanBuilder.addPropertyMetaData("verifySignature", metaData.getInboundSecurity().isVerifySignature());
		//beanBuilder.addPropertyMetaData("verifyTimestamp", metaData.getInboundSecurity().isVerifyTimestamp());
		//beanBuilder.addPropertyMetaData("trustStore", metaData.getInboundSecurity().getTrustStore());

		ValueMetaData busInjection = beanBuilder.createInject(busBean.getName());
		beanBuilder.addPropertyMetaData("bus", busInjection);

		BeanMetaData beanMetaData = beanBuilder.getBeanMetaData();
		unit.addAttachment(BeanMetaData.class + "$endpoint." + metaData.getName(), beanMetaData, BeanMetaData.class);
	}

}
