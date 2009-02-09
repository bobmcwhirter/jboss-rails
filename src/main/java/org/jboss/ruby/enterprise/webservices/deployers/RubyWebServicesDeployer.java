package org.jboss.ruby.enterprise.webservices.deployers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javassist.CannotCompileException;
import javassist.NotFoundException;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.vfs.spi.deployer.AbstractSimpleVFSRealDeployer;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.logging.Logger;
import org.jboss.metadata.common.jboss.WebserviceDescriptionMetaData;
import org.jboss.metadata.common.jboss.WebserviceDescriptionsMetaData;
import org.jboss.metadata.web.jboss.JBossServletMetaData;
import org.jboss.metadata.web.jboss.JBossServletsMetaData;
import org.jboss.metadata.web.jboss.JBossWebMetaData;
import org.jboss.metadata.web.spec.ServletMappingMetaData;
import org.jboss.ruby.enterprise.webservices.metadata.RubyWebServiceMetaData;
import org.jboss.ruby.enterprise.webservices.metadata.RubyWebServicesMetaData;
import org.jboss.wsf.spi.metadata.j2ee.JSEArchiveMetaData;

public class RubyWebServicesDeployer extends AbstractSimpleVFSRealDeployer<RubyWebServicesMetaData> {

	private static final Logger log = Logger.getLogger(RubyWebServicesDeployer.class);

	public RubyWebServicesDeployer() {
		super(RubyWebServicesMetaData.class);
		//setStage(DeploymentStages.POST_PARSE);
		setStage(DeploymentStages.POST_CLASSLOADER);
		setOutput(JSEArchiveMetaData.class);
	}

	@Override
	public void deploy(VFSDeploymentUnit unit, RubyWebServicesMetaData metaData) throws DeploymentException {
		log.info("Deploying webservices for : " + metaData);

		JBossWebMetaData jbossWebMetaData = new JBossWebMetaData();
		JBossServletsMetaData servlets = new JBossServletsMetaData();

		List<ServletMappingMetaData> servletMappings = new ArrayList<ServletMappingMetaData>();

		WebserviceDescriptionsMetaData descriptions = new WebserviceDescriptionsMetaData();

		ProviderCompiler compiler = new ProviderCompiler( unit.getClassLoader(), "jboss.ruby.runtime.pool." + unit.getSimpleName() );
		for (RubyWebServiceMetaData serviceMetaData : metaData.getWebSerices()) {

			try {
				Class serviceClass = compiler.compile(serviceMetaData);
				log.info( "serviceClass: " + serviceClass  );
				JBossServletMetaData servlet = new JBossServletMetaData();
				servlet.setServletName(serviceMetaData.getName());
				servlet.setServletClass(serviceClass.getName() );
				servlet.setLoadOnStartup(1);
				servlets.add(servlet);

				ServletMappingMetaData servletMapping = new ServletMappingMetaData();
				servletMapping.setServletName(serviceMetaData.getName());
				servletMapping.setUrlPatterns(Collections.singletonList("/" + serviceMetaData.getName() + "/*"));
				servletMappings.add(servletMapping);

				WebserviceDescriptionMetaData description = new WebserviceDescriptionMetaData();
				description.setName(serviceMetaData.getName());
				//description.setWsdlPublishLocation("/foo");
				// description.setConfigFile( "/" + serviceMetaData.getName() +
				// "-webservice.xml" );
				description.setConfigName("Standard WSSecurity Endpoint");
				description.setWebserviceDescriptionName(serviceMetaData.getName());
				descriptions.add(description);
			} catch (NotFoundException e) {
				log.error( e );
			} catch (CannotCompileException e) {
				log.error( e );
			}
		}

		jbossWebMetaData.setContextRoot("/soap");
		jbossWebMetaData.setServlets(servlets);
		jbossWebMetaData.setServletMappings(servletMappings);
		jbossWebMetaData.setWebserviceDescriptions(descriptions);
		jbossWebMetaData.setContextLoader( unit.getClassLoader() );
		jbossWebMetaData.setENCLoader( unit.getClassLoader() );

		unit.addAttachment(JBossWebMetaData.class, jbossWebMetaData);
	}

}
