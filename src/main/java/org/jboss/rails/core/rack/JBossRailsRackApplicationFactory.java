/*
 * Copyright 2007-2008 Sun Microsystems, Inc.
 * This source code is available under the MIT license.
 * See the file LICENSE.txt for details.
 */

package org.jboss.rails.core.rack;

import org.jruby.Ruby;
import org.jruby.rack.DefaultRackApplicationFactory;
import org.jruby.runtime.builtin.IRubyObject;

/**
 *
 * @author nicksieger
 * @author Bob McWhirter
 */
public class JBossRailsRackApplicationFactory extends DefaultRackApplicationFactory {
    @Override
    public IRubyObject createApplicationObject(Ruby runtime) {
        runtime.evalScriptlet("load 'jboss/rack/boot/rails.rb'");
        return createRackServletWrapper(runtime, "run JBoss::Rack::RailsFactory.new");
    }
}
