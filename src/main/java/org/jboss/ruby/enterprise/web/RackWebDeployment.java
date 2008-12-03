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
package org.jboss.ruby.enterprise.web;

import java.io.File;

import javax.management.Attribute;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.catalina.Container;
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
import org.jboss.ruby.enterprise.web.metadata.RackWebMetaData;
import org.jboss.ruby.enterprise.web.naming.JBossFileDirContext;
import org.jboss.ruby.enterprise.web.tomcat.RackContextConfig;
import org.jboss.web.tomcat.service.WebCtxLoader;
import org.jboss.web.tomcat.service.session.AbstractJBossManager;
import org.jboss.web.tomcat.service.session.distributedcache.spi.ClusteringNotSupportedException;

/**
 * Leaf managed web-deployment for Rails apps.
 * 
 * @author Bob McWhirter
 */
//@JMX(registerDirectly=true, exposedInterface=void.class)
public class RackWebDeployment implements RackWebDeploymentMBean {

	/** The Catalina context class we work with. */
	public final static String DEFAULT_CONTEXT_CLASS_NAME = "org.apache.catalina.core.StandardContext";

	/** Cache manager class name. */
	protected String managerClass = "org.jboss.ruby.enterprise.web.tomcat.RubyCacheManager";

	/** Our logger. */
	private static Logger log = Logger.getLogger(RackWebDeployment.class);

	/** Meta-data. */
	private RackWebMetaData rackWebMetaData;

	/**
	 * Construct.
	 * 
	 */
	public RackWebDeployment() {

	}
	
	// ----------------------------------------
	 
	
	public void setRackWebMetaData(RackWebMetaData rackWebMetaData) {
		this.rackWebMetaData = rackWebMetaData;
	}
	
	public RackWebMetaData getRackWebMetaData() {
		return this.rackWebMetaData;
	}

	@SuppressWarnings("unchecked")
	public synchronized void start() throws Exception {
		if (log.isTraceEnabled()) {
			log.trace("start()");
		}
		Class<StandardContext> contextClass = (Class<StandardContext>) Class.forName(DEFAULT_CONTEXT_CLASS_NAME);
		StandardContext context = contextClass.newInstance();

		//context.setPath(rackWebMetaData.getContext());
		String contextPath = rackWebMetaData.getContext();
		
		if ( contextPath == null || contextPath == "/" ) {
			contextPath = "";
		}
	
		log.info( "Setting context: " + contextPath );
		context.setPath( contextPath );

		setUpLoader(context);
		setUpJMX(context);
		setUpConfig(context);
		setUpResources(context);

		context.start();

		log.debug("start() complete");
		setUpClustering(context);
		log.debug("clustering setup complete");

	}
	
	private void setUpResources(StandardContext context) {
		String docBase = rackWebMetaData.getDocBase();
		File docBaseFile = new File( docBase );
		context.setDocBase( docBaseFile.getAbsolutePath() );
	}

	private void setUpClustering(StandardContext context) {
		// Try to initiate clustering, fall back to standard if no clustering is
		// available
		try {
			AbstractJBossManager manager = null;
			String managerClassName = this.managerClass;
			Class<?> managerClass = Thread.currentThread().getContextClassLoader().loadClass(managerClassName);
			manager = (AbstractJBossManager) managerClass.newInstance();

			String contextPath = this.rackWebMetaData.getContext();
			String name = "//" + rackWebMetaData.getHost() + contextPath;
			manager.init(name, createJBossWebMetaData());

			ObjectName objectName = getObjectName();

			getMBeanServer().setAttribute(objectName, new Attribute("manager", manager));

			log.debug("Enabled clustering support for ctxPath=" + contextPath);
		} catch (ClusteringNotSupportedException e) {
			// JBAS-3513 Just log a WARN, not an ERROR
			log.warn("Failed to setup clustering, clustering disabled. ClusteringNotSupportedException: " + e.getMessage());
		} catch (NoClassDefFoundError ncdf) {
			// JBAS-3513 Just log a WARN, not an ERROR
			log.warn("Failed to setup clustering, clustering disabled. NoClassDefFoundError: " + ncdf.getMessage());
		} catch (Throwable t) {
			// TODO consider letting this through and fail the deployment
			log.error("Failed to setup clustering, clustering disabled. Exception: ", t);
		}
	}

	private JBossWebMetaData createJBossWebMetaData(){
		JBossWebMetaData jbossWebMetaData = new JBossWebMetaData();
		ReplicationConfig replicationConfig = new ReplicationConfig();
		replicationConfig.setReplicationFieldBatchMode(true);
		replicationConfig.setReplicationGranularity(ReplicationGranularity.SESSION);
		replicationConfig.setReplicationTrigger(ReplicationTrigger.SET);
		replicationConfig.setSnapshotMode(SnapshotMode.INSTANT);
		replicationConfig.setUseJK(false);
		replicationConfig.setCacheName("standard-session-cache");

		jbossWebMetaData.setReplicationConfig(replicationConfig);
		return jbossWebMetaData;
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
		
		context.setConfigClass( rackWebMetaData.getContextConfigClassName() );
		RackContextConfig.frameworkMetaData.set( rackWebMetaData.getFrameworkMetaData() );
	}

	public synchronized void stop() throws Exception {
		log.debug("stop()");

		ObjectName objectName = getObjectName();

		MBeanServer mbeanServer = getMBeanServer();
		
		log.info( "isRegistered for " + objectName + ": " + mbeanServer.isRegistered( objectName ) );
		if (mbeanServer.isRegistered(objectName)) {
			log.info( "destroying" + objectName );
			mbeanServer.invoke(objectName, "destroy", new Object[] {}, new String[] {});
		}
	}

	private ObjectName getObjectName() throws MalformedObjectNameException, NullPointerException {
		String contextPath = this.rackWebMetaData.getContext();
		if (contextPath == null || contextPath.equals("")) {
			contextPath = "/";
		}
		
		String objectName = "jboss.web:j2eeType=WebModule,name=//" + this.rackWebMetaData.getHost() + contextPath + ",J2EEApplication=none,J2EEServer=none";
		return new ObjectName(objectName);
	}

	private Registry getRegistry() {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		return Registry.getRegistry( cl, null );
	}
	
	private MBeanServer getMBeanServer() {
		return getRegistry().getMBeanServer();

	}

	public String getContext() {
		return rackWebMetaData.getContext();
	}

	public String getHost() {
		return rackWebMetaData.getHost();
	}

}