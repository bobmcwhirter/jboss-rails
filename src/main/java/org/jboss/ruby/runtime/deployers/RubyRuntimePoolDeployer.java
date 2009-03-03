package org.jboss.ruby.runtime.deployers;

import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.ValueMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.deployers.vfs.spi.deployer.AbstractSimpleVFSRealDeployer;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.ruby.runtime.RubyRuntimeFactory;
import org.jboss.ruby.runtime.SharedRubyRuntimePool;
import org.jboss.ruby.runtime.metadata.RubyRuntimeMetaData;

public class RubyRuntimePoolDeployer extends AbstractSimpleVFSRealDeployer<RubyRuntimeMetaData> {
	
	public RubyRuntimePoolDeployer() {
		super( RubyRuntimeMetaData.class );
		addOutput(BeanMetaData.class);
		setStage(DeploymentStages.CLASSLOADER );
	}

	@Override
	public void deploy(VFSDeploymentUnit unit, RubyRuntimeMetaData metaData) throws DeploymentException {
		String beanName = getBeanName( unit );
		log.debug( "creating RubyRuntimePool: " + beanName );
		BeanMetaDataBuilder builder = BeanMetaDataBuilder.createBuilder( beanName, 
				SharedRubyRuntimePool.class.getName() );
		ValueMetaData factoryInjection = builder.createInject( "jboss.ruby.runtime.factory." + unit.getSimpleName() );
		builder.addConstructorParameter( RubyRuntimeFactory.class.getName(), factoryInjection );
		BeanMetaData poolBean = builder.getBeanMetaData();
		unit.addAttachment( BeanMetaData.class.getName() + "$RubyRuntimePool", poolBean, BeanMetaData.class );
		
	}
	
	public static String getBeanName(DeploymentUnit unit) {
		return "jboss.ruby.runtime.pool." + unit.getName();
	}

}
