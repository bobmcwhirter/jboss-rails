package org.jboss.rails.deploy;

import javax.management.ObjectName;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.rails.metadata.RailsMetaData;
import org.jboss.system.metadata.ServiceConstructorMetaData;
import org.jboss.system.metadata.ServiceMetaData;
import org.jboss.web.deployers.AbstractWarDeployer;
import org.jboss.web.deployers.AbstractWarDeployment;
import org.jboss.web.deployers.WebModule;

public class RailsAppDeployer extends AbstractDeployer {

	public RailsAppDeployer() {
		setStage(DeploymentStages.REAL);
		setTopLevelOnly(true);
		setInput(RailsMetaData.class);
	}

	public void deploy(DeploymentUnit unit) throws DeploymentException {
		if (!(unit instanceof VFSDeploymentUnit)) {
			throw new DeploymentException("deployment unit must be a VFSDeploymentUnit");
		}

		VFSDeploymentUnit vfsUnit = (VFSDeploymentUnit) unit;
		RailsMetaData railsMetaData = vfsUnit.getAttachment(RailsMetaData.class);
		if (railsMetaData == null) {
			return;
		}

		doDeploy(vfsUnit, railsMetaData);
	}

	protected void doDeploy(VFSDeploymentUnit unit, RailsMetaData railsMetaData) throws DeploymentException {
		try {
			RailsDeployment deployment = new RailsDeployment();
			ServiceMetaData railsModule = new ServiceMetaData();
			String name = getObjectName(railsMetaData);
			ObjectName objectName = new ObjectName(name);
			railsModule.setObjectName(objectName);
			railsModule.setCode(RailsModule.class.getName());
			ServiceConstructorMetaData constructor = new ServiceConstructorMetaData();
			constructor.setSignature(new String[] { VFSDeploymentUnit.class.getName(), RailsAppDeployer.class.getName(), RailsDeployment.class.getName() });
			constructor.setParameters(new Object[] { unit, this, deployment });
			railsModule.setConstructor(constructor);
			unit.addAttachment("RailsServiceMetaData", railsModule, ServiceMetaData.class);
		} catch (Throwable e) {
			throw new DeploymentException(e);
		}
	}

	private String getObjectName(RailsMetaData railsMetaData) {
		String ctxPath = "/ballast";
		String objectName = "jboss.rails.deployment:root=" + ctxPath;
		return objectName;
	}

}