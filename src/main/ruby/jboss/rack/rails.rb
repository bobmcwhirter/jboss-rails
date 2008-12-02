# JBoss, Home of Professional Open Source
# Copyright 2008, Red Hat Middleware LLC, and individual contributors
# by the @authors tag. See the copyright.txt in the distribution for a
# full listing of individual contributors.
#
# This is free software; you can redistribute it and/or modify it
# under the terms of the GNU Lesser General Public License as
# published by the Free Software Foundation; either version 2.1 of
# the License, or (at your option) any later version.
#
# This software is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with this software; if not, write to the Free
# Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
# 02110-1301 USA, or see the FSF site: http://www.fsf.org.

#--
# Copyright 2007-2008 Sun Microsystems, Inc.
# This source code is available under the MIT license.
# See the file LICENSE.txt for details.
#++

require 'jruby/rack'
require 'jruby/rack/rails'

import org.jboss.logging.Logger

module JBoss
  module Rack
    class JBossRailsServletHelper < JRuby::Rack::RailsServletHelper
      
      def initialize(servlet_context = nil)
        puts "servlet_context 1 #{servlet_context}"
        servlet_context ||= $servlet_context
        puts "servlet_context 2 #{servlet_context}"
        @root_path = servlet_context.getInitParameter( "root.path" )
        super(servlet_context)
      end
      
      def logdev
        @logdev ||= JBossServletLog.new @servlet_context
      end
      
      def real_path(path)
        puts "real_path (#{path})"
        return path
      end
      
    end
    
    class JBossServletLog
      
      def initialize(context = $servlet_context)
        @context = context
      end
      def puts(msg)
        write msg.to_s
      end
      def write(msg)
        @context.log(msg.strip)
      end
      def flush; end
      def close; end
    end
    
    Bootstrap = JBossRailsServletHelper
    
    class RailsFactory
      def self.new
        puts $:.inspect
        helper = JRuby::Rack::ServletHelper.instance
        helper.load_environment
        ::Rack::Builder.new {
          use ::JRuby::Rack::RailsSetup, helper
          run ::Rack::Adapter::Rails.new(helper.options)
        }.to_app
      end
    end
  end
end