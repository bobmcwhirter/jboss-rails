
puts "loading dispatcher.rb"

module JBoss
  module WebServiceDispatcher
    def self.dispatcher_for(dir, name) 
      puts "create dispatcher for [#{dir}][#{name}]"
      DispatcherBridge.new( dir, name )
    end
  end
  
  class DispatcherBridge
    def initialize(dir, name)
      @dir = dir
      @name = name
      load "#{dir}/#{name}_web_service.rb"
      @service_class = eval "#{name}_web_service".camelize
      puts "service class is #{@service_class.inspect}"
    end
    
    def dispatch(principal, operation, request)
      service = @service_class.new
      puts "dispatching [#{operation}] #{request} to #{service}"
      service.dispatch( principal, operation, request )
    end
  end
end