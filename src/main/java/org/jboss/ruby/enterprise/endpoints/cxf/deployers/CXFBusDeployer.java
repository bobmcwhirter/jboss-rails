package org.jboss.ruby.enterprise.endpoints.cxf.deployers;

import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.ruby.enterprise.endpoints.cxf.RubyCXFBus;

/** REAL deployer to provision a CXF bus if a RubyWebServiceMetaData is present.
 * 
 * @author Bob McWhirter
 */
public class CXFBusDeployer extends AbstractCXFDeployer {
	
	public static final String PREFIX = "jboss.jruby.webservices.cxf.bus";
	
	public CXFBusDeployer() {
		setStage( DeploymentStages.POST_CLASSLOADER );
		setAllInputs(true);
		addOutput( BeanMetaData.class );
	}

	public void deploy(DeploymentUnit unit) throws DeploymentException {
		if ( ! shouldDeploy( unit ) ) {
			return;
		}
		
		log.debug( "deploying CXF bus for: " + unit );
		
		unit.getAttachment( Object.class );
		
		String beanName = getBusName( unit.getSimpleName() );
		BeanMetaDataBuilder beanBuilder = BeanMetaDataBuilder.createBuilder( beanName, RubyCXFBus.class.getName() );
		
		BeanMetaData beanMetaData = beanBuilder.getBeanMetaData();
		
		unit.addAttachment( BeanMetaData.class + "$cxf.bus", beanMetaData );
	}
	
	public static String getBusName(String simpleName) {
		return PREFIX + "." + simpleName;
	}

}
