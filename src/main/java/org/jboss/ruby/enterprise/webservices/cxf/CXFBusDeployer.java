package org.jboss.ruby.enterprise.webservices.cxf;

import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.ruby.enterprise.webservices.metadata.RubyWebServicesMetaData;

public class CXFBusDeployer extends AbstractDeployer {
	
	public static final String PREFIX = "jboss.jruby.webservices.cxf.bus";
	
	public CXFBusDeployer() {
		setStage( DeploymentStages.DESCRIBE );
		setInput( RubyWebServicesMetaData.class );
		addOutput( BeanMetaData.class );
	}

	public void deploy(DeploymentUnit unit) throws DeploymentException {
		RubyWebServicesMetaData metaData = unit.getAttachment( RubyWebServicesMetaData.class );
		
		if ( metaData == null || metaData.getWebSerices().size() == 0 ) {
			return;
		}
		
		log.info( "deploying CXF bus for: " + unit );
		
		String beanName = getBusName( unit.getSimpleName() );
		BeanMetaDataBuilder beanBuilder = BeanMetaDataBuilder.createBuilder( beanName, RubyCXFBus.class.getName() );
		
		BeanMetaData beanMetaData = beanBuilder.getBeanMetaData();
		
		unit.addAttachment( BeanMetaData.class + "$cxf.bus", beanMetaData );
	}
	
	public static String getBusName(String simpleName) {
		return PREFIX + "." + simpleName;
	}

}
