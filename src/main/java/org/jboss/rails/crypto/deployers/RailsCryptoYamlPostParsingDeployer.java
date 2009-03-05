/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
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
