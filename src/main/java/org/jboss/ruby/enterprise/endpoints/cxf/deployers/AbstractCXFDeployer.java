package org.jboss.ruby.enterprise.endpoints.cxf.deployers;

import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.ruby.enterprise.endpoints.metadata.RubyEndpointMetaData;

public abstract class AbstractCXFDeployer extends AbstractDeployer {
	
	
	public boolean shouldDeploy(DeploymentUnit unit) {
		return ( unit.getAllMetaData( RubyEndpointMetaData.class ).size() > 0 );
	}

}
