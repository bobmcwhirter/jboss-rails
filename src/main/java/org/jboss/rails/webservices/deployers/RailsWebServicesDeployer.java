package org.jboss.rails.webservices.deployers;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.logging.Logger;
import org.jboss.rails.core.metadata.RailsApplicationMetaData;
import org.jboss.rails.runtime.deployers.RailsRubyRuntimeFactoryDescriber;
import org.jboss.ruby.enterprise.webservices.metadata.InboundSecurityMetaData;
import org.jboss.ruby.enterprise.webservices.metadata.RubyWebServiceMetaData;
import org.jboss.ruby.enterprise.webservices.metadata.RubyWebServicesMetaData;
import org.jboss.ruby.runtime.DefaultRubyRuntimeFactory;
import org.jboss.ruby.runtime.RubyRuntimeFactory;
import org.jboss.virtual.VirtualFile;
import org.jruby.Ruby;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;

public class RailsWebServicesDeployer extends AbstractDeployer {

	private static Logger log = Logger.getLogger(RailsWebServicesDeployer.class);

	public RailsWebServicesDeployer() {
		setStage(DeploymentStages.POST_CLASSLOADER);
		addInput(RailsApplicationMetaData.class);
		addOutput(RubyWebServicesMetaData.class);
	}

	public void deploy(DeploymentUnit unit) throws DeploymentException {

		VFSDeploymentUnit vfsUnit = (VFSDeploymentUnit) unit;

		RailsApplicationMetaData railsAppMetaData = unit.getAttachment(RailsApplicationMetaData.class);

		if (railsAppMetaData == null) {
			return;
		}

		RubyWebServicesMetaData servicesMetaData = null;

		try {
			VirtualFile apisDir = vfsUnit.getRoot().getChild("app/webservices");

			log.info("APIs: " + apisDir);

			if (apisDir == null) {
				return;
			}

			RubyRuntimeFactory factory = createRubyRuntimeFactory(railsAppMetaData, unit.getClassLoader());

			List<VirtualFile> serviceDirs = apisDir.getChildren();
			for (VirtualFile serviceDir : serviceDirs) {
				if (servicesMetaData == null) {
					servicesMetaData = new RubyWebServicesMetaData();
				}
				RubyWebServiceMetaData webService = new RubyWebServiceMetaData();
				introspectWsdlAnnotations(factory.createRubyRuntime(), webService, railsAppMetaData.getRailsRootPath() + "/"
						+ serviceDir.getPathName(), serviceDir.getName());
				webService.setDirectory(railsAppMetaData.getRailsRootPath() + "/" + serviceDir.getPathName());
				webService.setName(serviceDir.getName());
				servicesMetaData.addWebService(webService);
				log.info("deploying for: " + serviceDir);
			}
		} catch (IOException e) {
			log.error(e);
			e.printStackTrace();
			return;
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			return;
		}

		if (servicesMetaData != null) {
			log.info("attaching RubyWebServicesMetaData: " + servicesMetaData);
			vfsUnit.addAttachment(RubyWebServicesMetaData.class, servicesMetaData);
		}

	}

	private RubyRuntimeFactory createRubyRuntimeFactory(RailsApplicationMetaData railsAppMetaData, ClassLoader classLoader) {
		List<String> loadPaths = Collections.singletonList( "META-INF/jruby.home/lib/ruby/site_ruby/1.8" );
		String initScript =  RailsRubyRuntimeFactoryDescriber.createInitScript(railsAppMetaData
				.getRailsRootPath(), railsAppMetaData.getRailsEnv() );
		DefaultRubyRuntimeFactory factory = new DefaultRubyRuntimeFactory(loadPaths, initScript );
		factory.setClassLoader(classLoader);
		return factory;

	}

	private void introspectWsdlAnnotations(Ruby runtime, RubyWebServiceMetaData webService, String dir, String name) {
		log.info("introspect " + dir + " // " + name);
		IRubyObject serviceClass = runtime.evalScriptlet("require 'org/jboss/rails/webservices/deployers/web_service_introspector.rb'\n"
				+ "JBoss::WebServiceIntrospector.load_class('" + dir + "', '" + name + "')\n");

		log.info("serviceClass == " + serviceClass);

		// String targetNamespace = (String) JavaEmbedUtils.invokeMethod(
		// runtime, serviceClass, "targetNamespace", new Object[] { },
		// String.class );

		// log.info( "targetNamespace = " + targetNamespace );
		// String portName = (String) JavaEmbedUtils.invokeMethod( runtime,
		// serviceClass, "portName", new Object[] { }, String.class );
		// log.info( "portName = " + portName );

		// InboundSecurityMetaData inboundSecurity = new
		// InboundSecurityMetaData();
		// webService.setInboundSecurity(inboundSecurity);
		JavaEmbedUtils.invokeMethod(runtime, serviceClass, "setup_jboss_metadata", new Object[] { webService }, void.class);

		// webService.setTargetNamespace( targetNamespace );
		// webService.setPortName(portName);
	}

}
