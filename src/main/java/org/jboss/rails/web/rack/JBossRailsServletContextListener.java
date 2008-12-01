/*
 * Copyright 2007-2008 Sun Microsystems, Inc.
 * This source code is available under the MIT license.
 * See the file LICENSE.txt for details.
 */

package org.jboss.rails.web.rack;

import javax.servlet.ServletContext;

import org.jruby.rack.PoolingRackApplicationFactory;
import org.jruby.rack.RackApplicationFactory;
import org.jruby.rack.RackServletContextListener;
import org.jruby.rack.SharedRackApplicationFactory;

/**
 *
 * @author nicksieger
 * @author Bob McWhirter
 */
public class JBossRailsServletContextListener extends RackServletContextListener {
    @Override
    protected RackApplicationFactory newApplicationFactory(ServletContext context) {
        Integer maxRuntimes = null;
        try {
            maxRuntimes = Integer.parseInt(context.getInitParameter("jruby.max.runtimes").toString());
        } catch (Exception e) {
        }
        if (maxRuntimes != null && maxRuntimes == 1) {
            return new SharedRackApplicationFactory(new JBossRailsRackApplicationFactory());
        } else {
            return new PoolingRackApplicationFactory(new JBossRailsRackApplicationFactory());
        }
    }
}
