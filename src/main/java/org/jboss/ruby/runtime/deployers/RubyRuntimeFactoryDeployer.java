package org.jboss.ruby.runtime.deployers;

import java.util.List;

import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.vfs.spi.deployer.AbstractSimpleVFSRealDeployer;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.ruby.runtime.DefaultRubyRuntimeFactory;
import org.jboss.ruby.runtime.RubyRuntimeFactory;
import org.jboss.ruby.runtime.metadata.RubyRuntimeMetaData;

public class RubyRuntimeFactoryDeployer extends AbstractSimpleVFSRealDeployer<RubyRuntimeMetaData> {
	
	public RubyRuntimeFactoryDeployer() {
		super( RubyRuntimeMetaData.class );
		//addOutput(BeanMetaData.class);
		setStage(DeploymentStages.CLASSLOADER );
	}

	@Override
	public void deploy(VFSDeploymentUnit unit, RubyRuntimeMetaData metaData) throws DeploymentException {
		String factoryName = "jboss.ruby.runtime.factory." + unit.getSimpleName();
		log.info( "creating RubyRuntimeFactory: " + factoryName );
		
		RubyRuntimeFactory factory = new DefaultRubyRuntimeFactory( metaData.getLoadPath().getPaths(), metaData.getInitScript() );
		
		unit.addAttachment(RubyRuntimeFactory.class, factory);
		
	}

}
