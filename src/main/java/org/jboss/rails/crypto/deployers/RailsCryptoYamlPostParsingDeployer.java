package org.jboss.rails.crypto.deployers;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.rails.core.metadata.RailsApplicationMetaData;
import org.jboss.ruby.enterprise.crypto.metadata.CryptoMetaData;
import org.jboss.ruby.enterprise.crypto.metadata.CryptoStoreMetaData;

public class RailsCryptoYamlPostParsingDeployer extends AbstractDeployer {

	public RailsCryptoYamlPostParsingDeployer() {
		setInput(CryptoMetaData.class);
		addInput(RailsApplicationMetaData.class);
		setStage(DeploymentStages.POST_PARSE);
	}

	public void deploy(DeploymentUnit unit) throws DeploymentException {
		CryptoMetaData metaData = unit.getAttachment(CryptoMetaData.class);

		if (metaData == null) {
			return;
		}

		RailsApplicationMetaData railsAppMetaData = unit.getAttachment(RailsApplicationMetaData.class);

		if (railsAppMetaData == null) {
			return;
		}

		for (CryptoStoreMetaData store : metaData.getCryptoStoreMetaDatas()) {
			String path = store.getStore();
			if (!path.startsWith("/")) {
				path = railsAppMetaData.getRailsRootPath() + "/" + path;
				store.setStore(path);
			}
		}

	}

}
