/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.rails.core.deployers;

import javax.management.Attribute;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.catalina.Loader;
import org.apache.catalina.core.StandardContext;
import org.apache.naming.resources.FileDirContext;
import org.apache.tomcat.util.modeler.Registry;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.logging.Logger;
import org.jboss.metadata.web.jboss.JBossWebMetaData;
import org.jboss.metadata.web.jboss.ReplicationConfig;
import org.jboss.metadata.web.jboss.ReplicationGranularity;
import org.jboss.metadata.web.jboss.ReplicationTrigger;
import org.jboss.metadata.web.jboss.SnapshotMode;
import org.jboss.rails.core.metadata.RailsMetaData;
import org.jboss.rails.core.tomcat.RailsContextConfig;
import org.jboss.rails.naming.JBossFileDirContext;
import org.jboss.web.tomcat.service.WebCtxLoader;
import org.jboss.web.tomcat.service.session.AbstractJBossManager;
import org.jboss.web.tomcat.service.session.distributedcache.spi.ClusteringNotSupportedException;

/**
 * Leaf managed web-deployment for Rails apps.
 * 
 * @author Bob McWhirter
 */
public class RailsWebDeployment implements RailsDeploymentMBean {

	/** The Catalina context class we work with. */
	public final static String DEFAULT_CONTEXT_CLASS_NAME = "org.apache.catalina.core.StandardContext";

	/** Catalina context configuration class name. */
	protected String contextConfigClass = "org.jboss.rails.core.tomcat.RailsContextConfig";

	/** Cache manager class name. */
	protected String managerClass = "org.jboss.rails.core.tomcat.RailsCacheManager";

	/** Our logger. */
	private static Logger log = Logger.getLogger(RailsWebDeployment.class);

	/** Meta-data. */
	private RailsMetaData railsMetaData;

	/**
	 * Construct.
	 * 
	 */
	public RailsWebDeployment() {

	}

	public void setRailsMetaData(RailsMetaData metaData) {
		this.railsMetaData = metaData;
	}

	public RailsMetaData getRailsMetaData() {
		return this.railsMetaData;
	}

	@SuppressWarnings("unchecked")
	public synchronized void start() throws Exception {
		if (log.isTraceEnabled()) {
			log.trace("start()");
		}
		log.debug("meta data: " + railsMetaData);
		Class<StandardContext> contextClass = (Class<StandardContext>) Class.forName(DEFAULT_CONTEXT_CLASS_NAME);
		StandardContext context = contextClass.newInstance();

		context.setPath(railsMetaData.getContext());

		setUpResources(context);
		setUpLoader(context);
		setUpJMX(context);
		setUpConfig(context);

		context.start();

		if (log.isTraceEnabled()) {
			log.debug("start() complete");
		}
		setUpClustering(context, railsMetaData);

	}

	private void setUpClustering(StandardContext context, RailsMetaData metaData) {
		// Try to initiate clustering, fall back to standard if no clustering is
		// available
		try {
			AbstractJBossManager manager = null;
			String managerClassName = this.managerClass;
			Class<?> managerClass = Thread.currentThread().getContextClassLoader().loadClass(managerClassName);
			manager = (AbstractJBossManager) managerClass.newInstance();

			String hostName = null;
			String contextPath = metaData.getContext();
			String name = "//" + ((hostName == null) ? "localhost" : hostName) + contextPath;
			manager.init(name, createJBossWebMetaData(metaData));

			ObjectName objectName = getObjectName();

			getMBeanServer().setAttribute(objectName, new Attribute("manager", manager));

			log.debug("Enabled clustering support for ctxPath=" + contextPath);
		} catch (ClusteringNotSupportedException e) {
			// JBAS-3513 Just log a WARN, not an ERROR
			log.warn("Failed to setup clustering, clustering disabled. ClusteringNotSupportedException: " + e.getMessage());
		} catch (NoClassDefFoundError ncdf) {
			// JBAS-3513 Just log a WARN, not an ERROR
			log.debug("Classes needed for clustered webapp unavailable", ncdf);
			log.warn("Failed to setup clustering, clustering disabled. NoClassDefFoundError: " + ncdf.getMessage());
		} catch (Throwable t) {
			// TODO consider letting this through and fail the deployment
			log.error("Failed to setup clustering, clustering disabled. Exception: ", t);
		}
	}

	private JBossWebMetaData createJBossWebMetaData(RailsMetaData railsMetaData) {
		JBossWebMetaData jbossWebMetaData = new JBossWebMetaData();
		ReplicationConfig replicationConfig = new ReplicationConfig();
		replicationConfig.setReplicationFieldBatchMode(true);
		replicationConfig.setReplicationGranularity(ReplicationGranularity.SESSION);
		replicationConfig.setReplicationTrigger(ReplicationTrigger.SET);
		replicationConfig.setSnapshotMode(SnapshotMode.INTERVAL);
		replicationConfig.setUseJK(false);
		replicationConfig.setCacheName("standard-session-cache");

		jbossWebMetaData.setReplicationConfig(replicationConfig);
		return jbossWebMetaData;
	}

	private void setUpResources(StandardContext context) throws Exception {
		context.setDocBase(railsMetaData.getRailsRoot());
		FileDirContext resources = new JBossFileDirContext();
		resources.setDocBase(railsMetaData.getRailsRoot() + "/public");
		context.setResources(resources);
	}

	private void setUpLoader(StandardContext context) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		Loader loader = new WebCtxLoader(classLoader);
		context.setLoader(loader);
	}

	private void setUpJMX(StandardContext context) throws Exception {
		context.setServer( getMBeanServer().getDefaultDomain() );
		registerContext(context);
	}

	protected void registerContext(StandardContext context) throws Exception {
		
		ObjectName objectName = getObjectName();
		log.debug("registerContext(..., " + objectName + ")");
		Registry registry = getRegistry();
		
		if (registry.getMBeanServer().isRegistered(objectName)) {
			throw new DeploymentException("Web mapping already exists for deployment URL " + objectName);
		}
		
		registry.registerComponent(context, objectName, DEFAULT_CONTEXT_CLASS_NAME);
	}

	protected void setUpConfig(StandardContext context) {
		log.debug("setUpConfig(...)");
		context.setConfigClass(contextConfigClass);
		RailsContextConfig.railsMetaData.set(railsMetaData);
	}

	public synchronized void stop() throws Exception {
		log.debug("stop()");

		ObjectName objectName = getObjectName();

		MBeanServer mbeanServer = getMBeanServer();
		
		if (mbeanServer.isRegistered(objectName)) {
			mbeanServer.invoke(objectName, "destroy", new Object[] {}, new String[] {});
		}
	}

	private ObjectName getObjectName() throws MalformedObjectNameException, NullPointerException {
		String contextPath = railsMetaData.getContext();
		if (contextPath == null || contextPath.equals("")) {
			contextPath = "/";
		}
		String objectName = "jboss.web:j2eeType=WebModule,name=//localhost" + contextPath + ",J2EEApplication=none,J2EEServer=none";
		return new ObjectName(objectName);
	}

	private Registry getRegistry() {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		return Registry.getRegistry( cl, null );
	}
	
	private MBeanServer getMBeanServer() {
		return getRegistry().getMBeanServer();

	}

}
