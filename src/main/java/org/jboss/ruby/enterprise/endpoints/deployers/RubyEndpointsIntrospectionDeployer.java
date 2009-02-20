package org.jboss.ruby.enterprise.endpoints.deployers;

import java.util.Set;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.ruby.enterprise.endpoints.metadata.RubyEndpointMetaData;
import org.jboss.ruby.runtime.RubyRuntimeFactory;
import org.jruby.Ruby;
import org.jruby.RubyClass;

public class RubyEndpointsIntrospectionDeployer extends AbstractDeployer {

	public RubyEndpointsIntrospectionDeployer() {
		setStage(DeploymentStages.POST_CLASSLOADER);
		setAllInputs(true);
		addOutput(RubyEndpointMetaData.class);
	}

	public void deploy(DeploymentUnit unit) throws DeploymentException {
		Set<? extends RubyEndpointMetaData> allMetaData = unit.getAllMetaData(RubyEndpointMetaData.class);

		if (allMetaData.size() == 0) {
			return;
		}

		RubyRuntimeFactory factory = unit.getAttachment(RubyRuntimeFactory.class);

		if (factory == null) {
			throw new DeploymentException("RubyRuntimeFactory is not present.");
		}

		try {
			Ruby runtime = factory.createRubyRuntime();

			for (RubyEndpointMetaData each : allMetaData) {
				if (each.getTargetNamespace() == null || each.getPortName() == null) {
					introspect(runtime, each);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DeploymentException(e);
		}
	}

	protected void introspect(Ruby runtime, RubyEndpointMetaData metaData) {
		log.info("introspecting: " + metaData);
		String classLocation = metaData.getClassLocation();

		if (classLocation != null) {
			requireOrLoad(runtime, classLocation);
		}
		RubyClass rubyClass = runtime.getClass(metaData.getEndpointClassName());
		log.info("ruby class is " + rubyClass);
		log.info("ruby class is 22: " + runtime.evalScriptlet(metaData.getEndpointClassName()));
	}

	private void requireOrLoad(Ruby runtime, String classLocation) {
		if ( ! classLocation.endsWith( ".rb" ) ) {
			classLocation = classLocation + ".rb";
		}
		String require = "load %q(" + classLocation + ")\n";
		log.info("eval: " + require);
		runtime.evalScriptlet(require);
	}

}
