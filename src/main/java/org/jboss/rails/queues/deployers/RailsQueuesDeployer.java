package org.jboss.rails.queues.deployers;

import java.io.IOException;
import java.util.List;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.ruby.enterprise.queues.metadata.RubyTaskQueueMetaData;
import org.jboss.ruby.enterprise.queues.metadata.RubyTaskQueuesMetaData;
import org.jboss.ruby.util.StringUtils;
import org.jboss.virtual.VirtualFile;
import org.jboss.virtual.VirtualFileFilter;
import org.jboss.virtual.VisitorAttributes;
import org.jboss.virtual.plugins.vfs.helpers.SuffixMatchFilter;

public class RailsQueuesDeployer extends AbstractDeployer {

	private static final String APP_QUEUES = "app/queues/";
	private static final VirtualFileFilter QUEUE_FILTER = new SuffixMatchFilter("_queue.rb", VisitorAttributes.DEFAULT);

	public void deploy(DeploymentUnit unit) throws DeploymentException {
		if (unit instanceof VFSDeploymentUnit) {
			deploy((VFSDeploymentUnit) unit);
		}
	}

	public void deploy(VFSDeploymentUnit unit) throws DeploymentException {
		VirtualFile queuesDir = null;
		try {
			queuesDir = unit.getRoot().getChild(APP_QUEUES);

			if (queuesDir != null) {
				log.info("scanning: " + queuesDir);

				List<VirtualFile> queueClassFiles = queuesDir.getChildrenRecursively(QUEUE_FILTER);

				if (!queueClassFiles.isEmpty()) {
					RubyTaskQueuesMetaData metaData = unit.getAttachment(RubyTaskQueuesMetaData.class);
					if (metaData == null) {
						metaData = new RubyTaskQueuesMetaData();
						unit.addAttachment( RubyTaskQueuesMetaData.class, metaData );
					}

					for (VirtualFile queueClassFile : queueClassFiles) {
						String pathName = queueClassFile.getPathName();
						String simplePath = pathName.substring(APP_QUEUES.length());
						simplePath = simplePath.substring(0, simplePath.length() - 3);
						log.info("simple path [" + simplePath + "]");
						String rubyClassName = StringUtils.camelize(simplePath, false);
						rubyClassName = rubyClassName.replaceAll("\\.", "::");
						log.info("ruby className [" + rubyClassName + "]");
						
						RubyTaskQueueMetaData queueMetaData = metaData.getQueueByClassName( rubyClassName );
						
						if ( queueMetaData == null ) {
							queueMetaData = new RubyTaskQueueMetaData();
							queueMetaData.setQueueClassName( rubyClassName );
							metaData.addQueue( queueMetaData );
						}
					}
				}
			}
		} catch (IOException e) {
			throw new DeploymentException(e);
		}

	}

}
