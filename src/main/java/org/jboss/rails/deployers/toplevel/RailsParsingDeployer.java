package org.jboss.rails.deployers.toplevel;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.ho.yaml.Yaml;
import org.jboss.deployers.client.spi.Deployment;
import org.jboss.deployers.client.spi.main.MainDeployer;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.attachments.MutableAttachments;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractParsingDeployer;
import org.jboss.deployers.spi.structure.ContextInfo;
import org.jboss.deployers.spi.structure.StructureMetaData;
import org.jboss.deployers.spi.structure.StructureMetaDataFactory;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.deployers.vfs.plugins.client.AbstractVFSDeployment;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.logging.Logger;
import org.jboss.rails.metadata.RailsMetaData;
import org.jboss.virtual.VFS;
import org.jboss.virtual.VirtualFile;

public class RailsParsingDeployer extends AbstractParsingDeployer {

	private Logger log = Logger.getLogger(RailsParsingDeployer.class);

	public RailsParsingDeployer() {
		setAllInputs(true);
		setStage(DeploymentStages.REAL);
	}

	public void deploy(DeploymentUnit unit) throws DeploymentException {
		if (!(unit instanceof VFSDeploymentUnit)) {
			log.info("DeploymentUnit must be a VFSDeploymentUnit");
			return;
		}
		VFSDeploymentUnit vfsUnit = (VFSDeploymentUnit) unit;

		if (!vfsUnit.getRoot().getName().endsWith("-rails.yml")) {
			log.debug("skipping: " + vfsUnit.getRoot());
			return;
		}
		if (!vfsUnit.isTopLevel()) {
			log.debug("not top-level; skipping: " + vfsUnit.getRoot());
			return;
		}
		log.debug("deploying: " + vfsUnit.getRoot());

		RailsMetaData metaData = parseDescriptor(vfsUnit.getRoot());

		try {
			Deployment deployment = createDeployment(metaData);
			addStructure(deployment);

			performDeploy(vfsUnit, deployment);

		} catch (IOException e) {
			throw new DeploymentException(e);
		}

	}

	@Override
	public void undeploy(DeploymentUnit unit) {
		if (!(unit instanceof VFSDeploymentUnit)) {
			log.info("DeploymentUnit must be a VFSDeploymentUnit");
			return;
		}

		Deployment deployment = unit.getAttachment("jboss.rails.root.deployment", Deployment.class);
		if (deployment != null) {
			log.info( "undeploying: " + deployment.getName() );
			MainDeployer deployer = unit.getMainDeployer();
			try {
				deployer.removeDeployment(deployment);
			} catch (DeploymentException e) {
				log.error( e );
			}
		}
	}

	private void performDeploy(DeploymentUnit unit, Deployment deployment) throws DeploymentException {
		MainDeployer deployer = unit.getMainDeployer();
		deployer.addDeployment(deployment);
		deployer.process();
		deployer.checkComplete(deployment);
		unit.addAttachment("jboss.rails.root.deployment", deployment);
	}

	private void addStructure(Deployment deployment) {
		StructureMetaData structure = StructureMetaDataFactory.createStructureMetaData();
		ContextInfo contextInfo = StructureMetaDataFactory.createContextInfo("", "config", null);
		structure.addContext(contextInfo);

		MutableAttachments attachments = ((MutableAttachments) deployment.getPredeterminedManagedObjects());
		attachments.addAttachment(StructureMetaData.class, structure);
	}

	private Deployment createDeployment(RailsMetaData metaData) throws MalformedURLException, IOException {
		VirtualFile railsRoot = VFS.getRoot(new URL("file://" + metaData.getRailsRoot()));
		Deployment deployment = new AbstractVFSDeployment(railsRoot);

		MutableAttachments attachments = ((MutableAttachments) deployment.getPredeterminedManagedObjects());
		attachments.addAttachment(RailsMetaData.class, metaData);

		return deployment;
	}

	@SuppressWarnings("unchecked")
	private RailsMetaData parseDescriptor(VirtualFile file) throws DeploymentException {
		try {
			Map<String, Object> results = (Map<String, Object>) Yaml.load(file.openStream());

			Map<String, Object> application = (Map<String, Object>) results.get("application");
			Map<String, Object> web = (Map<String, Object>) results.get("web");
			Map<String, Object> jruby = (Map<String, Object>) results.get("jruby");

			RailsMetaData metaData = new RailsMetaData();

			if (application != null) {
				String railsRoot = (String) application.get("RAILS_ROOT");
				String railsEnv = (String) application.get("RAILS_ENV");
				metaData.setRailsRoot(railsRoot);
				metaData.setEnvironment(railsEnv);
			}

			if (web != null) {
				String context = (String) web.get("context");
				metaData.setContext(context);
			}

			return metaData;
		} catch (IOException e) {
			file.closeStreams();
			throw new DeploymentException(e);
		}
	}

}
