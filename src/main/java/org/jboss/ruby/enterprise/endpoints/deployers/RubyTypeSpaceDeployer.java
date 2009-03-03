package org.jboss.ruby.enterprise.endpoints.deployers;

import java.util.Set;

import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.ValueMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.ruby.enterprise.endpoints.databinding.RubyTypeSpace;
import org.jboss.ruby.enterprise.endpoints.metadata.RubyEndpointMetaData;
import org.jboss.ruby.runtime.RubyDynamicClassLoader;

public class RubyTypeSpaceDeployer extends AbstractDeployer {

	private static final String PREFIX = "jboss.ruby.databinding.";

	public RubyTypeSpaceDeployer() {
		setStage(DeploymentStages.POST_CLASSLOADER);
		setAllInputs(true);
		addOutput(BeanMetaData.class);
	}

	public void deploy(DeploymentUnit unit) throws DeploymentException {
		Set<? extends RubyEndpointMetaData> allMetaData = unit.getAllMetaData(RubyEndpointMetaData.class);

		if (allMetaData.size() == 0) {
			return;
		}

		log.debug("deploying for: " + unit);

		BeanMetaData busMetaData = unit.getAttachment(BeanMetaData.class + "$cxf.bus", BeanMetaData.class);

		RubyDynamicClassLoader classLoader = unit.getAttachment(RubyDynamicClassLoader.class);

		for (RubyEndpointMetaData endpointMetaData : allMetaData) {
			String beanName = getBeanName(unit, endpointMetaData.getName());
			BeanMetaDataBuilder builder = BeanMetaDataBuilder.createBuilder(beanName, RubyTypeSpace.class.getName());

			builder.addPropertyMetaData("rubyPath", "jboss/databinding/" + endpointMetaData.getName() );
			builder.addPropertyMetaData("wsdlLocation", endpointMetaData.getWsdlLocation());
			builder.addPropertyMetaData("rubyDynamicClassLoader", classLoader);
			
			ValueMetaData busInjection = builder.createInject(busMetaData.getName());
			builder.addPropertyMetaData("bus", busInjection);

			BeanMetaData beanMetaData = builder.getBeanMetaData();
			unit.addAttachment(BeanMetaData.class.getName() + "$databinding." + endpointMetaData.getName(), beanMetaData, BeanMetaData.class);
		}
	}

	public static String getBeanName(DeploymentUnit unit, String serviceName) {
		return PREFIX + unit.getSimpleName() + "." + serviceName;
	}

}
