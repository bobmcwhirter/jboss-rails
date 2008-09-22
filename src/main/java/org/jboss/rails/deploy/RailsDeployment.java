package org.jboss.rails.deploy;

import javax.management.ObjectName;

import org.apache.catalina.Loader;
import org.apache.catalina.core.StandardContext;
import org.apache.naming.resources.FileDirContext;
import org.apache.tomcat.util.modeler.Registry;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.logging.Logger;
import org.jboss.rails.catalina.JBossFileDirContext;
import org.jboss.rails.metadata.RailsMetaData;
import org.jboss.web.tomcat.service.WebCtxLoader;

public class RailsDeployment implements RailsDeploymentMBean {

	public final static String DEFAULT_CONTEXT_CLASS_NAME = "org.apache.catalina.core.StandardContext";

	private Logger log = Logger.getLogger(RailsDeployment.class);

	@SuppressWarnings("unchecked")
	public synchronized void start(RailsMetaData metaData) throws Exception {
		log.debug("start()");
		Class<StandardContext> contextClass = (Class<StandardContext>) Class.forName(DEFAULT_CONTEXT_CLASS_NAME);
		StandardContext context = contextClass.newInstance();

		setUpResources(context, metaData);
		setUpLoader(context);
		setUpJMX(context);
		setUpConfig(context, metaData);

		context.start();
		log.debug("start() complete");
	}

	private void setUpResources(StandardContext context, RailsMetaData metaData) throws Exception {
		log.debug("setting context docBase to [" + metaData.getRailsRoot() + "]");
		context.setDocBase( metaData.getRailsRoot() );
		FileDirContext resources = new JBossFileDirContext();
		log.debug("setting resources docBase to [" + metaData.getRailsRoot() + "/public]");
		resources.setDocBase( metaData.getRailsRoot() + "/public" );
		context.setResources(resources);
	}

	private void setUpLoader(StandardContext context) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		Loader loader = new WebCtxLoader(classLoader);
		context.setLoader(loader);
	}

	private void setUpJMX(StandardContext context) throws Exception {
		String objectNameS = "jboss.web:j2eeType=WebModule,name=//localhost/ballast,J2EEApplication=none,J2EEServer=none";
		ObjectName objectName = new ObjectName(objectNameS);
		context.setServer("jboss");
		registerContext(context, objectName);
	}

	protected void registerContext(StandardContext context, ObjectName objectName) throws Exception {
		log.debug("registerContext(..., " + objectName + ")");
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (Registry.getRegistry(cl, null).getMBeanServer().isRegistered(objectName)) {
			throw new DeploymentException("Web mapping already exists for deployment URL " + "ballast");
		}
		Registry.getRegistry(cl, null).registerComponent(context, objectName, DEFAULT_CONTEXT_CLASS_NAME);
	}

	protected void setUpConfig(StandardContext context, RailsMetaData metaData) {
		log.debug("setUpConfig(...)");
		context.setConfigClass("org.jboss.rails.deploy.RailsContextConfig");
		RailsContextConfig.railsMetaData.set(metaData);
	}

	public synchronized void stop() throws Exception {
		log.debug("stop()");
	}

}
