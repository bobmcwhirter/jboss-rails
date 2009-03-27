package org.jboss.ruby.enterprise.queues.deployers;

import java.io.IOException;
import java.util.Map;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.helpers.AbstractParsingDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.ruby.enterprise.queues.metadata.RubyTaskQueueMetaData;
import org.jboss.ruby.enterprise.queues.metadata.RubyTaskQueuesMetaData;
import org.jboss.virtual.VirtualFile;
import org.jruby.util.ByteList;
import org.jvyamlb.YAML;

public class QueuesYamlParsingDeployer extends AbstractParsingDeployer {

	public static final String FILENAME = "queues.yml";

	public QueuesYamlParsingDeployer() {
		addOutput(RubyTaskQueuesMetaData.class);
	}

	public void deploy(DeploymentUnit unit) throws DeploymentException {
		if (unit instanceof VFSDeploymentUnit) {
			deploy((VFSDeploymentUnit) unit);
		}
	}

	protected void deploy(VFSDeploymentUnit unit) throws DeploymentException {
		VirtualFile metaData = unit.getMetaDataFile(FILENAME);
		if (metaData != null) {
			parse(unit, metaData);
		}
	}

	@SuppressWarnings("unchecked")
	protected void parse(VFSDeploymentUnit unit, VirtualFile file) throws DeploymentException {
		log.info( "parsing " + file );
		RubyTaskQueuesMetaData queues = unit.getAttachment( RubyTaskQueuesMetaData.class );
		
		if ( queues == null ) {
			queues = new RubyTaskQueuesMetaData();
		}
		
		try {
			Map<ByteList, Map<ByteList,ByteList>> results = (Map<ByteList, Map<ByteList,ByteList>>) YAML.load(file.openStream());
			for (ByteList queueClassNameBytes : results.keySet()) {
				String queueClassName = queueClassNameBytes.toString();
				RubyTaskQueueMetaData queue = queues.getQueueByClassName(queueClassName);
				if ( queue == null ) {
					queue = new RubyTaskQueueMetaData();
					queue.setQueueClassName( queueClassName );
					queues.addQueue( queue );
				}
				
				log.info( "added queue: " + queue );
				
				Map<ByteList, ByteList> details = results.get( queueClassNameBytes );
				
				if ( details != null ) {
					// process details
				}
			}
		} catch (IOException e) {
			throw new DeploymentException(e);
		}
		
		if ( queues != null && ! queues.empty() ) {
			unit.addAttachment( RubyTaskQueuesMetaData.class, queues);
		}

	}

}
