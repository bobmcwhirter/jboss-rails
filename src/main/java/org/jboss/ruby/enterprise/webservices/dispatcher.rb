
puts "loading dispatcher.rb"

module JBoss
  module WebServiceDispatcher
    def self.dispatcher_for(name) 
      puts "create dispatcher for [#{name}]"
      DispatcherBridge.new( name )
    end
  end
  
  class DispatcherBridge
    def initialize(name)
      @name = name
      load "#{RAILS_ROOT}/app/webservices/#{name}/#{name}_web_service.rb"
      @service_class = eval "#{name}_web_service".camelize
      puts "service class is #{@service_class.inspect}"
    end
    
    def dispatch(request)
      service = @service_class.new
      puts "dispatching #{request} to #{service}"
      service.dispatch( request )
    end
  end
end