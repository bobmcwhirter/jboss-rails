package org.jboss.ruby.enterprise.web.rack.deployers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.vfs.spi.deployer.AbstractSimpleVFSRealDeployer;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.logging.Logger;
import org.jboss.metadata.javaee.spec.ParamValueMetaData;
import org.jboss.metadata.web.jboss.JBossServletMetaData;
import org.jboss.metadata.web.jboss.JBossServletsMetaData;
import org.jboss.metadata.web.jboss.JBossWebMetaData;
import org.jboss.metadata.web.spec.FilterMappingMetaData;
import org.jboss.metadata.web.spec.FilterMetaData;
import org.jboss.metadata.web.spec.FiltersMetaData;
import org.jboss.metadata.web.spec.ServletMappingMetaData;
import org.jboss.ruby.enterprise.web.StaticResourceServlet;
import org.jboss.ruby.enterprise.web.rack.RackFilter;
import org.jboss.ruby.enterprise.web.rack.metadata.RackWebApplicationMetaData;

public class RackWebApplicationDeployer extends AbstractSimpleVFSRealDeployer<RackWebApplicationMetaData> {

	private static final Logger log = Logger.getLogger(RackWebApplicationDeployer.class);

	public RackWebApplicationDeployer() {
		super(RackWebApplicationMetaData.class);
		setStage(DeploymentStages.PRE_REAL);
		addOutput(JBossWebMetaData.class);
	}

	@Override
	public void deploy(VFSDeploymentUnit unit, RackWebApplicationMetaData metaData) throws DeploymentException {

		log.info("deploying " + unit);

		JBossWebMetaData webMetaData = unit.getAttachment(JBossWebMetaData.class);

		if (webMetaData == null) {
			webMetaData = new JBossWebMetaData();
			unit.addAttachment(JBossWebMetaData.class, webMetaData);
		}

		FilterMetaData rackFilter = new FilterMetaData();
		rackFilter.setFilterClass(RackFilter.class.getName());
		rackFilter.setFilterName("jboss.rack");

		List<ParamValueMetaData> initParams = new ArrayList<ParamValueMetaData>();
		ParamValueMetaData rackAppFactory = new ParamValueMetaData();
		rackAppFactory.setParamName(RackFilter.RACK_APP_POOL_INIT_PARAM);
		rackAppFactory.setParamValue(metaData.getRackApplicationFactoryName() + ".pool");
		initParams.add(rackAppFactory);

		rackFilter.setInitParam(initParams);

		FiltersMetaData filters = new FiltersMetaData();
		filters.add(rackFilter);

		webMetaData.setFilters(filters);

		FilterMappingMetaData filterMapping = new FilterMappingMetaData();
		filterMapping.setFilterName("jboss.rack");
		filterMapping.setUrlPatterns(Collections.singletonList("/*"));

		webMetaData.setFilterMappings(Collections.singletonList(filterMapping));

		webMetaData.setContextRoot(metaData.getContext());
		if (metaData.getHost() != null) {
			webMetaData.setVirtualHosts(Collections.singletonList(metaData.getHost()));
		}

		if (metaData.getStaticPathPrefix() != null) {
			JBossServletsMetaData servlets = new JBossServletsMetaData();
			JBossServletMetaData staticServlet = new JBossServletMetaData();
			staticServlet.setServletClass(StaticResourceServlet.class.getName());
			staticServlet.setServletName("jboss.static");

			ParamValueMetaData resourceRootParam = new ParamValueMetaData();
			resourceRootParam.setParamName("resource.root");
			resourceRootParam.setParamValue( metaData.getStaticPathPrefix() );
			staticServlet.setInitParam(Collections.singletonList(resourceRootParam));
			servlets.add(staticServlet);
			webMetaData.setServlets(servlets);

			ServletMappingMetaData staticMapping = new ServletMappingMetaData();
			staticMapping.setServletName("jboss.static");
			staticMapping.setUrlPatterns(Collections.singletonList("/*"));
			webMetaData.setServletMappings(Collections.singletonList(staticMapping));
		}

		unit.addAttachment(JBossWebMetaData.class, webMetaData);
	}
}
