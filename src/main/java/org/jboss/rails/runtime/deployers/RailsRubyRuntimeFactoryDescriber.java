package org.jboss.rails.runtime.deployers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.rails.core.metadata.RailsApplicationMetaData;
import org.jboss.ruby.runtime.metadata.RubyLoadPathMetaData;
import org.jboss.ruby.runtime.metadata.RubyRuntimeMetaData;
import org.jboss.virtual.VirtualFile;

public class RailsRubyRuntimeFactoryDescriber extends AbstractDeployer {

	public RailsRubyRuntimeFactoryDescriber() {
		setStage(DeploymentStages.PRE_DESCRIBE);
		addInput(RailsApplicationMetaData.class);
		addOutput(RubyRuntimeMetaData.class);
	}

	public void deploy(DeploymentUnit unit) throws DeploymentException {
		if (unit instanceof VFSDeploymentUnit) {
			deploy((VFSDeploymentUnit) unit);
		}
	}

	public void deploy(VFSDeploymentUnit unit) throws DeploymentException {
		log.debug("attempt deploy against: " + unit);
		RailsApplicationMetaData railsMetaData = unit.getAttachment(RailsApplicationMetaData.class);
		if (railsMetaData == null) {
			log.debug("no RailsApplicationMetaData attached");
			return;
		}
		log.debug("actually deploy against: " + unit);
		RailsRuntimeInitializer initializer = createRuntimeInitializer(railsMetaData.getRailsRootPath(), railsMetaData.getRailsEnv());

		RubyRuntimeMetaData runtimeMetaData = unit.getAttachment(RubyRuntimeMetaData.class);
		if (runtimeMetaData == null) {
			runtimeMetaData = new RubyRuntimeMetaData();
			unit.addAttachment(RubyRuntimeMetaData.class, runtimeMetaData);
		}

		runtimeMetaData.setRuntimeInitializer( initializer );

		try {
			RubyLoadPathMetaData railsRootPath = new RubyLoadPathMetaData();
			railsRootPath.setURL(unit.getRoot().toURL());
			unit.addAttachment( RubyLoadPathMetaData.class.getName() + "$RAILS_ROOT", railsRootPath, RubyLoadPathMetaData.class );
			
			RubyLoadPathMetaData railtiesPath = new RubyLoadPathMetaData();
			railtiesPath.setURL(unit.getRoot().getChild( "vendor/rails/railties/lib" ).toURL() );
			unit.addAttachment( RubyLoadPathMetaData.class.getName() + "$railties/lib", railtiesPath, RubyLoadPathMetaData.class );
			
			VirtualFile baseDir = unit.getRoot();
			unit.addAttachment( VirtualFile.class.getName() + "$ruby.baseDir", baseDir );
		} catch (MalformedURLException e) {
			throw new DeploymentException( e );
		} catch (URISyntaxException e) {
			throw new DeploymentException( e );
		} catch (IOException e) {
			throw new DeploymentException( e );
		}

	}

	public RailsRuntimeInitializer createRuntimeInitializer(String railsRoot, String railsEnv) {
		return new RailsRuntimeInitializer( railsRoot, railsEnv );
	}

	/*
	 * public static String createInitScript(String railsRoot, String railsEnv)
	 * { String initScript = "RAILS_ROOT='" + railsRoot + "'\n" + "RAILS_ENV='"
	 * + railsEnv + "'\n" + "require \"#{RAILS_ROOT}/config/boot.rb\"\n" +
	 * "require \"#{RAILS_ROOT}/config/environment.rb\"\n"; return initScript; }
	 */

}
