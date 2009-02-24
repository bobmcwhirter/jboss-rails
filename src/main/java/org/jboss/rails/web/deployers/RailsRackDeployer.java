package org.jboss.rails.web.deployers;

import org.apache.log4j.Logger;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.vfs.spi.deployer.AbstractSimpleVFSRealDeployer;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.rails.core.metadata.RailsApplicationMetaData;
import org.jboss.ruby.enterprise.web.rack.metadata.RackWebApplicationMetaData;
import org.jboss.ruby.enterprise.web.rack.metadata.RubyRackApplicationMetaData;

public class RailsRackDeployer extends AbstractSimpleVFSRealDeployer<RailsApplicationMetaData> {

	private static final Logger log = Logger.getLogger(RailsRackDeployer.class);

	public RailsRackDeployer() {
		super(RailsApplicationMetaData.class);
		addInput(RackWebApplicationMetaData.class);
		addOutput(RackWebApplicationMetaData.class);
		addOutput(RubyRackApplicationMetaData.class);
		setStage(DeploymentStages.POST_PARSE);
	}

	@Override
	public void deploy(VFSDeploymentUnit unit, RailsApplicationMetaData railsAppMetaData) throws DeploymentException {

		log.info("deploying rails rack app");
		RackWebApplicationMetaData rackWebAppMetaData = unit.getAttachment(RackWebApplicationMetaData.class);

		if (rackWebAppMetaData == null) {
			rackWebAppMetaData = new RackWebApplicationMetaData();
			rackWebAppMetaData.setContext("/");
			unit.addAttachment(RackWebApplicationMetaData.class, rackWebAppMetaData);
		}

		String appFactoryName = "jboss.rack.app." + unit.getName();
		rackWebAppMetaData.setRackApplicationFactoryName(appFactoryName);
		rackWebAppMetaData.setStaticPathPrefix( "/public" );

		RubyRackApplicationMetaData rubyRackAppMetaData = new RubyRackApplicationMetaData();
		rubyRackAppMetaData.setRackUpScript(getRackUpScript());

		unit.addAttachment(RubyRackApplicationMetaData.class, rubyRackAppMetaData);
	}

	protected String getRackUpScript() {
		// "  use RailsSetup, helper\n" +
		String script = 
			"require %q(org/jboss/rails/web/deployers/rails_rack_dispatcher)\n" +
			"::Rack::Builder.new {\n" + 
			"  run JBoss::Rails::Rack::Dispatcher.new()\n" +
			"}.to_app\n";

		return script;

	}

}
