package org.jboss.rails.web.v2_3;

import org.jboss.rails.web.deployers.AbstractRailsRackUpScriptProvider;

public class Rails_v2_3_RackUpScriptProvider extends AbstractRailsRackUpScriptProvider {

	public Rails_v2_3_RackUpScriptProvider() {
		super(2, 3, 0);
	}

	public String getRackUpScript(String context) {
		if ( context.endsWith( "/" ) ) {
			context = context.substring( 0, context.length() - 1 );
		}
		
		String script = 
			"require %q(org/jboss/rails/web/v2_3/rails_rack_dispatcher)\n" +
			"::Rack::Builder.new {\n" + 
			"  run JBoss::Rails::Rack::Dispatcher.new(%q("+ context + "))\n" +
			"}.to_app\n";

		return script;
	}

}
