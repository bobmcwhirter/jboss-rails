# JBoss, Home of Professional Open Source
# Copyright 2009, Red Hat Middleware LLC, and individual contributors
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

require 'org/jboss/rails/web/deployers/servlet_session_manager'

module JBoss
  module Rails
    module Rack
      class ServletRackRequest < ActionController::RackRequest
        def initialize(env)
          super( env, session_options)
          @servlet_request = env['servlet_request']
        end
        
        def session_options_with_string_keys
          opts = super
          opts['database_manager'] = JBoss::Rails::ServletSessionManager
          opts['servlet_request']  = @servlet_request
          opts['no_cookies'] = true
          opts
        end
      end
    end
  end
end