package org.jboss.rails.runtime.deployers;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.logging.Logger;
import org.jboss.ruby.runtime.metadata.LoadPathMetaData;
import org.jboss.ruby.runtime.metadata.RubyRuntimeMetaData;

public class RailsAutoLoadDeployer extends AbstractDeployer {
	private static final Logger log = Logger.getLogger( RailsAutoLoadDeployer.class);
	
	public RailsAutoLoadDeployer() {
		setStage(DeploymentStages.DESCRIBE );
		setRelativeOrder( 1000 );
		addInput(RubyRuntimeMetaData.class);
		addOutput(RubyRuntimeMetaData.class );
	}

	public void deploy(DeploymentUnit unit) throws DeploymentException {
		RubyRuntimeMetaData metaData = unit.getAttachment( RubyRuntimeMetaData.class + "$AutoLoad", RubyRuntimeMetaData.class );
		
		if ( metaData == null ) {
			return;
		}
		
		log.info( "deploy: " + unit );
		
		LoadPathMetaData loadPath = metaData.getLoadPath();
		
		StringBuilder clause = new StringBuilder();
		
		clause.append( "Rails.configuration.load_paths += %W(" );
		for ( String path : loadPath.getPaths() ) {
			clause.append( path + "\n" );
		}
		clause.append( ")" );
		
		String initScript = metaData.getInitScript();
		
		if ( initScript == null ) {
			initScript = clause.toString();
		} else {
			initScript = initScript + "\n" + clause.toString();
		}
		
		log.info( "initScript=" + initScript );
		metaData.setInitScript( initScript );
		
	}

}
