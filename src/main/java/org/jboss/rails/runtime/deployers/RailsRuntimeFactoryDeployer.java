package org.jboss.rails.runtime.deployers;

import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.helpers.AbstractSimpleRealDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.logging.Logger;
import org.jboss.rails.core.metadata.RailsMetaData;
import org.jboss.rails.runtime.RailsRuntimeFactory;

public class RailsRuntimeFactoryDeployer extends AbstractSimpleRealDeployer<RailsMetaData> {

	private static final Logger log = Logger.getLogger( RailsRuntimeFactoryDeployer.class );
	
	public RailsRuntimeFactoryDeployer() {
		super( RailsMetaData.class );
		addOutput(BeanMetaData.class);
	}
	
	@Override
	public void deploy(DeploymentUnit unit, RailsMetaData deployment) throws DeploymentException {
		BeanMetaDataBuilder builder = BeanMetaDataBuilder.createBuilder( "jboss.ruby.runtime.factory." + unit.getSimpleName(), RailsRuntimeFactory.class.getName() );
		builder.addConstructorParameter( String.class.getName(), deployment.getRailsRoot() );
		builder.addConstructorParameter( String.class.getName(), deployment.getEnvironment() );
		BeanMetaData factoryBean = builder.getBeanMetaData();
		unit.addAttachment( BeanMetaData.class.getName() + "$RailsRuntimeFactory", factoryBean, BeanMetaData.class );
	}

}
