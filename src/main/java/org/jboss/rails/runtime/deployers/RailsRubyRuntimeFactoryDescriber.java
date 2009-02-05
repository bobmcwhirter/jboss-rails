package org.jboss.rails.runtime.deployers;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.rails.core.metadata.RailsApplicationMetaData;
import org.jboss.ruby.runtime.metadata.LoadPathMetaData;
import org.jboss.ruby.runtime.metadata.RubyRuntimeMetaData;

public class RailsRubyRuntimeFactoryDescriber extends AbstractDeployer {
	
	public RailsRubyRuntimeFactoryDescriber() {
		setStage( DeploymentStages.PRE_DESCRIBE );
		addInput( RailsApplicationMetaData.class );
		addOutput( RubyRuntimeMetaData.class );
	}

	public void deploy(DeploymentUnit unit) throws DeploymentException {
		RailsApplicationMetaData railsMetaData = unit.getAttachment( RailsApplicationMetaData.class );
		if ( railsMetaData == null ) {
			return;
		}
		String initScript = 
			"RAILS_ROOT='" + railsMetaData.getRailsRootPath()+ "'\n" + 
			"RAILS_ENV='" + railsMetaData.getRailsEnv() + "'\n" + 
			"require \"#{RAILS_ROOT}/config/boot.rb\"\n";
		
		RubyRuntimeMetaData runtimeMetaData = unit.getAttachment( RubyRuntimeMetaData.class );
		if ( runtimeMetaData == null ) {
			runtimeMetaData = new RubyRuntimeMetaData();
			unit.addAttachment( RubyRuntimeMetaData.class, runtimeMetaData );
		}
		
		LoadPathMetaData loadPath = runtimeMetaData.getLoadPath();
		
		if ( loadPath == null ) {
			loadPath = new LoadPathMetaData();
			runtimeMetaData.setLoadPath( loadPath );
			
		}
		
		loadPath.addPath( "META-INF/jruby.home/lib/ruby/site_ruby/1.8" );
		runtimeMetaData.setInitScript( initScript );
		
	}
	

}
