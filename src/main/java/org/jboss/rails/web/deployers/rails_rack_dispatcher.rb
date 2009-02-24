
require 'org/jboss/rails/web/deployers/servlet_rack_request'

module JBoss
  module Rails
    module Rack
      class Dispatcher < ActionController::Dispatcher
        def call(env)
          @request  = JBoss::Rails::Rack::ServletRackRequest.new(env)
          @response = ActionController::RackResponse.new(@request)
          dispatch
        end
      end
    end
  end
end