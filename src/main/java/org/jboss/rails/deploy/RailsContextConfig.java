package org.jboss.rails.deploy;

import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.deploy.FilterDef;
import org.apache.catalina.deploy.FilterMap;
import org.apache.catalina.startup.ContextConfig;
import org.jboss.logging.Logger;
import org.jboss.rails.metadata.RailsMetaData;

public class RailsContextConfig extends ContextConfig {

	// public static ThreadLocal<DeployerConfig> deployerConfig = new
	// ThreadLocal<DeployerConfig>();
	public static ThreadLocal<RailsMetaData> railsMetaData = new ThreadLocal<RailsMetaData>();

	private static Logger log = Logger.getLogger(RailsContextConfig.class);

	public RailsContextConfig() {
	}

	@Override
	protected void applicationWebConfig() {
		log.info("applicationWebConfig()");
		setUpApplicationParamValues();
	}

	private void setUpApplicationParamValues() {
		setUpRailsRoot();
		setUpRailsEnv();
	}

	private void setUpRailsEnv() {
		RailsMetaData metaData = RailsContextConfig.railsMetaData.get();
		String environment = metaData.getEnvironment();
		context.addParameter("rails.env", environment);
	}

	private void setUpRailsRoot() {
		context.addParameter("rails.root", "/");
		context.addParameter("public.root", "/public/");
	}

	@Override
	protected void defaultWebConfig() {
		log.info("defaultWebConfig()");

		setUpServletVersion();
		setUpParamValues();
		setUpDisplayName();
		setUpDistributable();
		setUpErrorPages();
		setUpFilters();
		setUpListeners();
		setUpLogin();
		setUpMimeMappings();
		setUpSecurity();
		setUpServlets();
		setUpJspMappings();
		setUpLocaleEncodings();
		setUpWelcomeFiles();
		setUpSessions();
	}

	private void setUpServletVersion() {
		log.debug("setUpServletVersion()");
		context.setPublicId("/javax/servlet/resources/web-app_2_4.dtd");
	}

	private void setUpParamValues() {
		log.debug("setUpParamValues()");
		// nothing
	}

	private void setUpDisplayName() {
		log.debug("setUpDisplayName()");
		// nothing
	}

	private void setUpDistributable() {
		log.debug("setUpDistributable()");
		context.setDistributable(false);
	}

	private void setUpErrorPages() {
		log.debug("setUpErrorPages()");
		// TODO Inject rails error page handling?
	}

	private void setUpFilters() {
		log.debug("setUpFilters()");
		setUpRackFilter();
	}

	private void setUpRackFilter() {
		log.debug("setUpRackFilter()");
		FilterDef filter = new FilterDef();
		filter.setFilterName("jruby-rack");
		filter.setFilterClass("org.jruby.rack.RackFilter");
		context.addFilterDef(filter);

		FilterMap filterMap = new FilterMap();
		filterMap.setFilterName("jruby-rack");
		filterMap.addURLPattern("/*");

		context.addFilterMap(filterMap);
	}

	private void setUpListeners() {
		log.debug("setUpListeners()");
		context.addApplicationListener("org.jruby.rack.rails.RailsServletContextListener");
	}

	private void setUpLogin() {
		log.debug("setUpLogin()");
		// TODO Auto-generated method stub
	}

	private void setUpMimeMappings() {
		log.debug("setUpMimeMappings()");
		// TODO Auto-generated method stub
	}

	private void setUpSecurity() {
		log.debug("setUpSecurity()");
		// TODO Auto-generated method stub
	}

	private void setUpServlets() {
		log.debug("setUpServlets()");
		// TODO Auto-generated method stub
		setUpDefaultServlet();
	}

	private void setUpDefaultServlet() {
		log.debug("setUpDefaultServlet()");
		Wrapper wrapper = context.createWrapper();
		wrapper.setName("jboss-rails-default");
		wrapper.setName("jboss-rails-default");
		wrapper.addInitParameter( "debug", "100" );
		wrapper.setServletClass("org.apache.catalina.servlets.DefaultServlet");
		context.addChild(wrapper);

		context.addServletMapping("/*", "jboss-rails-default");
	}

	private void setUpJspMappings() {
		log.debug("setUpJspMappings()");
		// TODO Auto-generated method stub
	}

	private void setUpLocaleEncodings() {
		log.debug("setUpLocaleEncodings()");
		// TODO Auto-generated method stub
	}

	private void setUpWelcomeFiles() {
		log.debug("setUpWelcomeFiles()");
		((StandardContext) context).setReplaceWelcomeFiles(true);
		context.addWelcomeFile("index.html");
	}

	private void setUpSessions() {
		log.debug("setUpSessions()");
		// TODO Auto-generated method stub
	}

}
