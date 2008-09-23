package org.jboss.rails.deploy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

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
import org.jboss.virtual.VFS;
import org.jboss.virtual.VirtualFile;

public class SoftReferenceDeployer extends AbstractParsingDeployer {

	private Logger log = Logger.getLogger(SoftReferenceDeployer.class);

	public SoftReferenceDeployer() {
		setAllInputs(true);
		setStage(DeploymentStages.REAL);
	}

	public void deploy(DeploymentUnit unit) throws DeploymentException {
		if (!(unit instanceof VFSDeploymentUnit)) {
			throw new DeploymentException("DeploymentUnit must be a VFSDeploymentUnit");
		}
		VFSDeploymentUnit vfsUnit = (VFSDeploymentUnit) unit;

		if (!vfsUnit.getRoot().getName().endsWith(".railsref")) {
			log.debug("skipping: " + vfsUnit.getRoot());
			return;
		}
		log.debug("deploying: " + vfsUnit.getRoot());
		try {
			String referencedPath = determineReferencedPath(vfsUnit);
			log.debug( "referenced: " + referencedPath );
			VirtualFile referencedFile = VFS.getRoot(new URL("file://" + referencedPath));
			Deployment referencedDeployment = new AbstractVFSDeployment(referencedFile);
			MutableAttachments attachments = ((MutableAttachments) referencedDeployment.getPredeterminedManagedObjects());

			StructureMetaData railsStructure = StructureMetaDataFactory.createStructureMetaData();
			ContextInfo contextInfo = StructureMetaDataFactory.createContextInfo("", "config", null );
			railsStructure.addContext(contextInfo);
			attachments.addAttachment(StructureMetaData.class, railsStructure);
			MainDeployer deployer = unit.getMainDeployer();
			deployer.addDeployment(referencedDeployment);
			deployer.process();
		} catch (IOException e) {
			throw new DeploymentException(e);
		}

	}

	private String determineReferencedPath(VFSDeploymentUnit unit) throws IOException {
		VirtualFile root = unit.getRoot();
		InputStream inStream = root.openStream();
		InputStreamReader inReader = new InputStreamReader(inStream);
		BufferedReader reader = new BufferedReader(inReader);
		String location = reader.readLine();
		return location.trim();
	}

}
