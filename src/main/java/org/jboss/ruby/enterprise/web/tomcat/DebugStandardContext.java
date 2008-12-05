package org.jboss.ruby.enterprise.web.tomcat;

import org.apache.catalina.core.StandardContext;
import org.jboss.logging.Logger;

public class DebugStandardContext extends StandardContext {
	
	@Override
	public boolean resourcesStart() {
		log.info( "resourcesStart() with basePath [" + getBasePath() + "]" );
		
		return super.resourcesStart();
	}

	private static final Logger log = Logger.getLogger( DebugStandardContext.class );

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected String getBasePath() {
		String basePath = super.getBasePath();
		
		log.info( "getBasePath() yields [" + basePath + "]" );
		
		return basePath;
	}
	
	

}
