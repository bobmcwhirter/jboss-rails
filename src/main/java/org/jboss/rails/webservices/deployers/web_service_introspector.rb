module JBoss
  class WebServiceIntrospector
    def self.load_class( dir, name ) 
      puts "ruby load_class(#{dir},#{name})"
      load "#{dir}/#{name}_web_service.rb"
      service_class = eval "#{name}_web_service".camelize
      puts "service class is #{service_class}"
      return service_class
    end
  end
end