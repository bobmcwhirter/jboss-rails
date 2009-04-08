package org.jboss.ruby.enterprise.queues.deployers;

import java.io.IOException;
import java.util.List;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.ruby.core.deployers.AbstractRubyScanningDeployer;
import org.jboss.ruby.core.util.StringUtils;
import org.jboss.ruby.enterprise.queues.metadata.RubyTaskQueueMetaData;
import org.jboss.ruby.enterprise.queues.metadata.RubyTaskQueuesMetaData;
import org.jboss.virtual.VirtualFile;
import org.jboss.virtual.VirtualFileFilter;
import org.jboss.virtual.VisitorAttributes;
import org.jboss.virtual.plugins.vfs.helpers.SuffixMatchFilter;

public class AbstractRubyQueuesScanningDeployer extends AbstractRubyScanningDeployer {

	private static final VirtualFileFilter QUEUE_FILTER = new SuffixMatchFilter("_queue.rb", VisitorAttributes.DEFAULT);

	public AbstractRubyQueuesScanningDeployer() {
		addInput(RubyTaskQueuesMetaData.class);
		addOutput(RubyTaskQueuesMetaData.class);
		setStage( DeploymentStages.POST_PARSE );
	}

	public void deploy(VFSDeploymentUnit unit, VirtualFile queueClassFile, String relativePath) throws DeploymentException {

		RubyTaskQueuesMetaData metaData = unit.getAttachment(RubyTaskQueuesMetaData.class);
		if (metaData == null) {
			metaData = new RubyTaskQueuesMetaData();
			unit.addAttachment(RubyTaskQueuesMetaData.class, metaData);
		}

		String simplePath = relativePath.substring(0, relativePath.length() - 3);
		String rubyClassName = StringUtils.camelize(simplePath, false);
		rubyClassName = rubyClassName.replaceAll("\\.", "::");

		RubyTaskQueueMetaData queueMetaData = metaData.getQueueByClassName(rubyClassName);

		if (queueMetaData == null) {
			queueMetaData = new RubyTaskQueueMetaData();
			queueMetaData.setQueueClassName(rubyClassName);
			metaData.addQueue(queueMetaData);
		}

		queueMetaData.setQueueClassLocation(simplePath);
	}

}