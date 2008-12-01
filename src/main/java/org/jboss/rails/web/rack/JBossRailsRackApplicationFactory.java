/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
/*
 * Copyright 2007-2008 Sun Microsystems, Inc.
 * This source code is available under the MIT license.
 * See the file LICENSE.txt for details.
 */

package org.jboss.rails.web.rack;

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
