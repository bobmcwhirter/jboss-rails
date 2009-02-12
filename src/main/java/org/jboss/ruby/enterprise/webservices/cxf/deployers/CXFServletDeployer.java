package org.jboss.ruby.enterprise.webservices.cxf.deployers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.metadata.javaee.spec.ParamValueMetaData;
import org.jboss.metadata.web.jboss.JBossServletMetaData;
import org.jboss.metadata.web.jboss.JBossServletsMetaData;
import org.jboss.metadata.web.jboss.JBossWebMetaData;
import org.jboss.metadata.web.spec.ServletMappingMetaData;
import org.jboss.ruby.enterprise.webservices.cxf.RubyCXFServlet;
import org.jboss.ruby.enterprise.webservices.metadata.RubyWebServicesMetaData;

public class CXFServletDeployer extends AbstractDeployer {
	
	public CXFServletDeployer() {
		setStage( DeploymentStages.POST_PARSE );
		setInput( RubyWebServicesMetaData.class );
		addInput( JBossWebMetaData.class );
		addOutput( JBossWebMetaData.class );
	}

	public void deploy(DeploymentUnit unit) throws DeploymentException {
		
		log.info( "Deploy CXF servlet for " + unit );
		JBossWebMetaData webMetaData = unit.getAttachment(JBossWebMetaData.class );
		
		if ( webMetaData == null ) {
			webMetaData = new JBossWebMetaData();
			webMetaData.setContextRoot( "/soap" );
			unit.addAttachment( JBossWebMetaData.class, webMetaData );
		}
		
		JBossServletsMetaData servlets = webMetaData.getServlets();
		if ( servlets == null ) {
			servlets = new JBossServletsMetaData();
			webMetaData.setServlets( servlets );
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
		servlets.add( servletMetaData );
		
		List<ServletMappingMetaData> servletMappings = webMetaData.getServletMappings();
		
		if ( servletMappings == null ) {
			servletMappings = new ArrayList<ServletMappingMetaData>();
			webMetaData.setServletMappings(servletMappings);
		}
		
		ServletMappingMetaData servletMapping = new ServletMappingMetaData();
		servletMapping.setServletName( "cxf-servlet" );
		servletMapping.setUrlPatterns( Collections.singletonList( "/*" ) );
		servletMappings.add( servletMapping );
		
	}

}
