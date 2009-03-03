package org.jboss.ruby.enterprise.endpoints.cxf.deployers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.cxf.common.logging.LogUtils;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.metadata.javaee.spec.ParamValueMetaData;
import org.jboss.metadata.web.jboss.JBossServletMetaData;
import org.jboss.metadata.web.jboss.JBossServletsMetaData;
import org.jboss.metadata.web.jboss.JBossWebMetaData;
import org.jboss.metadata.web.spec.ServletMappingMetaData;
import org.jboss.ruby.enterprise.endpoints.cxf.RubyCXFServlet;

public class CXFServletDeployer extends AbstractCXFDeployer {
	static {
		LogUtils.setLoggerClass( JBossLoggerBridge.class );
	}
	
	public CXFServletDeployer() {
		addInput( JBossWebMetaData.class );
		addOutput( JBossWebMetaData.class );
		setAllInputs(true);
		setStage( DeploymentStages.PRE_REAL );
		setRelativeOrder( 2000 );
	}
	
	public void deploy(DeploymentUnit unit) throws DeploymentException {
		if ( ! shouldDeploy( unit ) ) {
			return;
		}
		
		log.debug( "Deploy CXF servlet for " + unit );
		JBossWebMetaData webMetaData = unit.getAttachment(JBossWebMetaData.class );
		
		
		if ( webMetaData == null ) {
			webMetaData = new JBossWebMetaData();
			unit.addAttachment( JBossWebMetaData.class, webMetaData );
		}
		
		JBossServletMetaData servletMetaData = new JBossServletMetaData();
		servletMetaData.setServletName( "cxf-servlet" );
		servletMetaData.setServletClass( RubyCXFServlet.class.getName() );
		servletMetaData.setLoadOnStartup( 1 );
		
		List<ParamValueMetaData> initParams = new ArrayList<ParamValueMetaData>();
		ParamValueMetaData cxfBusNameParam = new ParamValueMetaData();
		cxfBusNameParam.setParamName( "cxf.bus.name" );
		String busName =  CXFBusDeployer.getBusName( unit.getSimpleName() );
		cxfBusNameParam.setParamValue( busName );
		initParams.add( cxfBusNameParam );
		servletMetaData.setInitParam( initParams );
		
		JBossServletsMetaData servlets = webMetaData.getServlets();
		if ( servlets == null ) {
			servlets = new JBossServletsMetaData();
			webMetaData.setServlets( servlets );
		}
		
		servlets.add( servletMetaData );
		
		ServletMappingMetaData servletMapping = new ServletMappingMetaData();
		servletMapping.setServletName( "cxf-servlet" );
		servletMapping.setUrlPatterns( Collections.singletonList( "/endpoints/*" ) );
		
		List<ServletMappingMetaData> servletMappings = webMetaData.getServletMappings();
		
		if ( servletMappings == null ) {
			servletMappings = new ArrayList<ServletMappingMetaData>();
			webMetaData.setServletMappings(servletMappings);
		}
		
		servletMappings.add( servletMapping );
		
	}

}
