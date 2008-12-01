package org.jboss.rails.runtime.deployers;

import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.helpers.AbstractSimpleRealDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.logging.Logger;
import org.jboss.rails.core.metadata.RailsApplicationMetaData;
import org.jboss.rails.core.metadata.RailsMetaData;
import org.jboss.rails.runtime.RailsRuntimeFactory;
import org.jboss.virtual.VirtualFile;

public class RailsRuntimeFactoryDeployer extends AbstractSimpleRealDeployer<RailsApplicationMetaData> {

	private static final Logger log = Logger.getLogger( RailsRuntimeFactoryDeployer.class );
	
	public RailsRuntimeFactoryDeployer() {
		super( RailsApplicationMetaData.class );
		addOutput(BeanMetaData.class);
	}
	
	@Override
	public void deploy(DeploymentUnit unit, RailsApplicationMetaData deployment) throws DeploymentException {
		BeanMetaDataBuilder builder = BeanMetaDataBuilder.createBuilder( "jboss.ruby.runtime.factory." + unit.getSimpleName(), RailsRuntimeFactory.class.getName() );
		builder.addConstructorParameter( VirtualFile.class.getName(), deployment.getRailsRoot() );
		builder.addConstructorParameter( String.class.getName(), deployment.getRailsEnv() );
		builder.addAnnotation("@org.jboss.aop.microcontainer.aspects.jmx.JMX(registerDirectly=true, exposedInterface=void.class, name=\"jboss.ruby.runtime.factory:app=" + unit.getSimpleName() + "\")");
		BeanMetaData factoryBean = builder.getBeanMetaData();
		unit.addAttachment( BeanMetaData.class.getName() + "$RailsRuntimeFactory", factoryBean, BeanMetaData.class );
	}

}
