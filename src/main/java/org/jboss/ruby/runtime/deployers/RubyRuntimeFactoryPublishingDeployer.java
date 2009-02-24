package org.jboss.ruby.runtime.deployers;

import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.ruby.runtime.RubyRuntimeFactory;
import org.jboss.ruby.runtime.RubyRuntimeFactoryProxy;

public class RubyRuntimeFactoryPublishingDeployer extends AbstractDeployer {

	public RubyRuntimeFactoryPublishingDeployer() {
		addOutput(BeanMetaData.class);
		setStage(DeploymentStages.CLASSLOADER);
	}

	public void deploy(DeploymentUnit unit) throws DeploymentException {
		RubyRuntimeFactory factory = unit.getAttachment(RubyRuntimeFactory.class);

		if (factory != null) {
			String factoryName = "jboss.ruby.runtime.factory." + unit.getSimpleName();
			log.trace("publishing RubyRuntimeFactory: " + factoryName);
			BeanMetaDataBuilder builder = BeanMetaDataBuilder.createBuilder(factoryName, RubyRuntimeFactoryProxy.class.getName());
			builder.addConstructorParameter(RubyRuntimeFactory.class.getName(), factory);
			BeanMetaData factoryBean = builder.getBeanMetaData();
			unit.addAttachment(BeanMetaData.class.getName() + "$RubyRuntimeFactory", factoryBean, BeanMetaData.class);
		}

	}

}
