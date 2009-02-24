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