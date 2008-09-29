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
package org.jboss.rails.deployers.app;

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
import org.jboss.mx.util.MBeanServerLocator;
import org.jboss.rails.metadata.RailsMetaData;
import org.jboss.rails.naming.JBossFileDirContext;
import org.jboss.web.tomcat.service.WebCtxLoader;
import org.jboss.web.tomcat.service.session.AbstractJBossManager;
import org.jboss.web.tomcat.service.session.JBossCacheManager;
import org.jboss.web.tomcat.service.session.distributedcache.spi.ClusteringNotSupportedException;

/**
 * Leaf managed web-deployment for Rails apps.
 * 
 * @author Bob McWhirter
 */
public class RailsDeployment implements RailsDeploymentMBean {

	/** The Catalina context class we work with. */
	public final static String DEFAULT_CONTEXT_CLASS_NAME = "org.apache.catalina.core.StandardContext";

	protected String managerClass = "org.jboss.web.tomcat.service.session.JBossCacheManager";

	/** Our logger. */
	private Logger log = Logger.getLogger(RailsDeployment.class);

	private MBeanServer server;

	@SuppressWarnings("unchecked")
	public synchronized void start(RailsMetaData metaData) throws Exception {
		if (log.isTraceEnabled()) {
			log.trace("start()");
		}
		log.debug("meta data: " + metaData);
		Class<StandardContext> contextClass = (Class<StandardContext>) Class.forName(DEFAULT_CONTEXT_CLASS_NAME);
		StandardContext context = contextClass.newInstance();

		setUpResources(context, metaData);
		setUpLoader(context);
		setUpJMX(context, metaData);
		setUpConfig(context, metaData);
		context.start();
		if (log.isTraceEnabled()) {
			log.debug("start() complete");
		}
		setUpClustering(context, metaData);

	}

	private void setUpClustering(StandardContext context, RailsMetaData metaData) {
		// Try to initate clustering, fallback to standard if no clustering is
		// available
		try {
			AbstractJBossManager manager = null;
			String managerClassName = this.managerClass;
			Class managerClass = Thread.currentThread().getContextClassLoader().loadClass(managerClassName);
			manager = (AbstractJBossManager) managerClass.newInstance();
			String hostName = null;
			String contextPath = getContextPath( metaData );
			String name = "//" + ((hostName == null) ? "localhost" : hostName) + contextPath;
			manager.init( name, createJBossWebMetaData(metaData) );

			ObjectName objectName = createObjectName( metaData );
			server.setAttribute(objectName, new Attribute("manager", manager));

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
		replicationConfig.setReplicationGranularity(ReplicationGranularity.SESSION );
		replicationConfig.setReplicationTrigger(ReplicationTrigger.SET);
		replicationConfig.setSnapshotMode(SnapshotMode.INTERVAL);
		replicationConfig.setUseJK(false );
		replicationConfig.setCacheName( "standard-session-cache" );
				
		jbossWebMetaData.setReplicationConfig(replicationConfig);
		return jbossWebMetaData;
	}

	private void setUpResources(StandardContext context, RailsMetaData metaData) throws Exception {
		context.setDocBase(metaData.getRailsRoot());
		FileDirContext resources = new JBossFileDirContext();
		resources.setDocBase(metaData.getRailsRoot() + "/public");
		context.setResources(resources);
	}

	private void setUpLoader(StandardContext context) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		Loader loader = new WebCtxLoader(classLoader);
		context.setLoader(loader);
	}

	private void setUpJMX(StandardContext context, RailsMetaData metaData) throws Exception {
		ObjectName objectName = createObjectName(metaData);
		context.setServer("jboss");
		registerContext(context, objectName);
	}

	protected void registerContext(StandardContext context, ObjectName objectName) throws Exception {
		log.debug("registerContext(..., " + objectName + ")");
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (Registry.getRegistry(cl, null).getMBeanServer().isRegistered(objectName)) {
			throw new DeploymentException("Web mapping already exists for deployment URL " + objectName);
		}
		Registry.getRegistry(cl, null).registerComponent(context, objectName, DEFAULT_CONTEXT_CLASS_NAME);
	}

	protected void setUpConfig(StandardContext context, RailsMetaData metaData) {
		log.debug("setUpConfig(...)");
		context.setConfigClass("org.jboss.rails.deployers.app.RailsContextConfig");
		RailsContextConfig.railsMetaData.set(metaData);
	}

	public synchronized void stop(RailsMetaData metaData) throws Exception {
		log.debug("stop()");

		// TODO: Need to remove the dependency on MBeanServer
		MBeanServer server = MBeanServerLocator.locateJBoss();
		// If the server is gone, all apps were stopped already
		if (server == null) {
			return;
		}

		ObjectName objectName = createObjectName(metaData);

		if (server.isRegistered(objectName)) {
			// Contexts should be stopped by the host already
			server.invoke(objectName, "destroy", new Object[] {}, new String[] {});
		}

	}

	private String getContextPath(RailsMetaData railsMetaData) {
		String context = railsMetaData.getContext();
		if (context != null) {
			return context;
		}
		String appName = railsMetaData.getApplicationName();
		context = "/" + appName;

		return context;

	}

	private ObjectName createObjectName(RailsMetaData metaData) throws MalformedObjectNameException, NullPointerException {
		String contextPath = getContextPath(metaData);
		String objectName = "jboss.web:j2eeType=WebModule,name=//localhost" + contextPath + ",J2EEApplication=none,J2EEServer=none";
		return new ObjectName(objectName);
	}

	public void setMBeanServer(MBeanServer server) {
		this.server = server;
		
	}

}
