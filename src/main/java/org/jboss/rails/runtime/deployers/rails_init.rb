require %q(vendor/rails/railties/lib/initializer)

module Rails
  
  def self.vendor_rails?
    true
  end
  
  class Configuration
  	def set_root_path!
      @root_path = RAILS_ROOT
    end
    
    def framework_paths
      paths = %w(railties railties/lib activesupport/lib)
      paths << 'actionpack/lib' if frameworks.include? :action_controller or frameworks.include? :action_view

      [:active_record, :action_mailer, :active_resource, :action_web_service].each do |framework|
        paths << "#{framework.to_s.gsub('_', '')}/lib" if frameworks.include? framework
      end

      paths.map { |dir| "#{framework_root_path}/#{dir}" }
    end
	end

	class Initializer
  	def set_load_path
      load_paths = configuration.load_paths + configuration.framework_paths
    	load_paths.reverse_each do |dir| 
        $LOAD_PATH.unshift(dir)
    	end
    	$LOAD_PATH.uniq!
  	end
	end
  
end

Rails::Initializer.run(:install_gem_spec_stubs)
Rails::Initializer.run(:set_load_path)