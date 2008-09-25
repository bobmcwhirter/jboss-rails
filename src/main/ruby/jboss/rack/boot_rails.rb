#--
# Copyright 2007-2008 Sun Microsystems, Inc.
# This source code is available under the MIT license.
# See the file LICENSE.txt for details.
#++

require 'jboss/rack/rails'
require 'jboss/rack/servlet_helper'
require 'rack/adapter/rails'

module JRuby
  module Rack
    silence_warnings do
      const_set('Bootstrap', JBoss::Rack::RailsServletHelper)
    end
  end
end
