package org.jboss.ruby.enterprise.queues.deployers;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.vfs.spi.deployer.AbstractSimpleVFSRealDeployer;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.jms.server.destination.QueueService;
import org.jboss.ruby.enterprise.queues.metadata.RubyTaskQueueMetaData;
import org.jboss.ruby.enterprise.queues.metadata.RubyTaskQueuesMetaData;
import org.jboss.system.metadata.ServiceAttributeMetaData;
import org.jboss.system.metadata.ServiceConstructorMetaData;
import org.jboss.system.metadata.ServiceMetaData;
import org.jboss.system.metadata.ServiceTextValueMetaData;
import org.jboss.system.metadata.ServiceValueMetaData;

public class RubyTaskQueuesDeployer extends AbstractSimpleVFSRealDeployer<RubyTaskQueuesMetaData> {

	private static final String SERVER_PEER_NAME = "jboss.messaging:service=ServerPeer";

	public RubyTaskQueuesDeployer() {
		super(RubyTaskQueuesMetaData.class);
		addOutput(ServiceMetaData.class);
	}

	public void deploy(VFSDeploymentUnit unit, RubyTaskQueuesMetaData queuesMetaData) throws DeploymentException {
		for ( RubyTaskQueueMetaData queueMetaData : queuesMetaData.getQueues() ) {
			deploy( unit, queueMetaData );
		}
	}
	
	public void deploy(VFSDeploymentUnit unit, RubyTaskQueueMetaData queueMetaData) throws DeploymentException {
	
		ServiceMetaData metaData = new ServiceMetaData();

		metaData.setCode(QueueService.class.getName());

		String simpleQueueName = queueMetaData.getQueueClassName();
		simpleQueueName = simpleQueueName.replaceAll( "::", "." );
		
		String queueName = "jboss.messaging.destination:service=Queue,name=" + unit.getSimpleName() + "." + simpleQueueName;

		try {
			metaData.setObjectName(new ObjectName(queueName));
		} catch (MalformedObjectNameException e) {
			throw new DeploymentException(e);
		}

		metaData.setXMBeanDD("xmdesc/Queue-xmbean.xml");

		ServiceConstructorMetaData constructorMetaData = new ServiceConstructorMetaData();
		constructorMetaData.setSignature(new String[] { Boolean.TYPE.getName() });
		constructorMetaData.setParameters(new Object[] { Boolean.TRUE });
		metaData.setConstructor(constructorMetaData);

		//ServiceValueMetaData serverPeerVal = new ServiceInjectionValueMetaData(SERVER_PEER_NAME);
		ServiceValueMetaData serverPeerVal = new ServiceTextValueMetaData( SERVER_PEER_NAME );
		

		ServiceAttributeMetaData serverPeerAttr = new ServiceAttributeMetaData();
		serverPeerAttr.setName("ServerPeer");
		serverPeerAttr.setValue(serverPeerVal);

		metaData.addAttribute(serverPeerAttr);

		unit.addAttachment(ServiceMetaData.class.getName() + "$queue." + queueName, metaData, ServiceMetaData.class);
	}

}
