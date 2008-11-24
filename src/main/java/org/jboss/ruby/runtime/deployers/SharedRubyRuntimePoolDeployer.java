package org.jboss.ruby.runtime.deployers;

import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.helpers.AbstractSimpleRealDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.ruby.runtime.RubyRuntimeFactory;
import org.jboss.ruby.runtime.SharedRubyRuntimePool;
import org.jboss.ruby.runtime.metadata.SharedRubyRuntimePoolMetaData;

public class SharedRubyRuntimePoolDeployer extends AbstractSimpleRealDeployer<SharedRubyRuntimePoolMetaData> {

	
	public SharedRubyRuntimePoolDeployer() {
		super(SharedRubyRuntimePoolMetaData.class);
		addOutput(BeanMetaData.class);
	}

	@Override
	public void deploy(DeploymentUnit unit, SharedRubyRuntimePoolMetaData deployment) throws DeploymentException {
		BeanMetaDataBuilder builder = BeanMetaDataBuilder.createBuilder(deployment.getName(), SharedRubyRuntimePool.class.getName() );
		builder.addConstructorParameter( RubyRuntimeFactory.class.getName(), (Object) null );
		BeanMetaData poolMetaData = builder.getBeanMetaData();
		unit.addAttachment(BeanMetaData.class.getName() + "$SharedRubyRuntimePool_" + deployment.getName(), poolMetaData, BeanMetaData.class);
	}
	

}
