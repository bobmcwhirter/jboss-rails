package org.jboss.ruby.runtime.deployers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.vfs.spi.deployer.AbstractSimpleVFSRealDeployer;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.kernel.Kernel;
import org.jboss.ruby.runtime.DefaultRubyRuntimeFactory;
import org.jboss.ruby.runtime.RubyDynamicClassLoader;
import org.jboss.ruby.runtime.RubyRuntimeFactory;
import org.jboss.ruby.runtime.metadata.RubyLoadPathMetaData;
import org.jboss.ruby.runtime.metadata.RubyRuntimeMetaData;

public class RubyRuntimeFactoryDeployer extends AbstractSimpleVFSRealDeployer<RubyRuntimeMetaData> {

	private Kernel kernel;

	public RubyRuntimeFactoryDeployer() {
		super(RubyRuntimeMetaData.class);
		setStage(DeploymentStages.CLASSLOADER);
	}
	
	public void setKernel(Kernel kernel) {
		this.kernel = kernel;
	}
	
	public Kernel getKernel() {
		return this.kernel;
	}

	@Override
	public void deploy(VFSDeploymentUnit unit, RubyRuntimeMetaData metaData) throws DeploymentException {
		String factoryName = "jboss.ruby.runtime.factory." + unit.getSimpleName();
		log.trace("creating RubyRuntimeFactory: " + factoryName);

		DefaultRubyRuntimeFactory factory = new DefaultRubyRuntimeFactory(metaData.getInitScript());
		factory.setKernel( this.kernel );

		try {
			RubyDynamicClassLoader classLoader = createClassLoader(unit);
			factory.setClassLoader(classLoader);
			unit.addAttachment( RubyDynamicClassLoader.class, classLoader );
		} catch (MalformedURLException e) {
			throw new DeploymentException(e);
		}

		unit.addAttachment(RubyRuntimeFactory.class, factory);
	}

	private RubyDynamicClassLoader createClassLoader(VFSDeploymentUnit unit) throws MalformedURLException {
		
		Set<? extends RubyLoadPathMetaData> allMetaData = unit.getAllMetaData( RubyLoadPathMetaData.class );
		
		Set<URL> urls = new HashSet<URL>();
		
		for ( RubyLoadPathMetaData each : allMetaData ) {
			urls.add( each.getURL() );
		}
		
		RubyDynamicClassLoader classLoader = RubyDynamicClassLoader.create(unit.getSimpleName(), urls, unit.getClassLoader() );
		return classLoader;
	}

}
