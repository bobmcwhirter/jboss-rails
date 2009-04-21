
require 'deployers/shared_spec'

import org.jboss.rails.core.deployers.RailsVersionDeployer
import org.jboss.rails.core.metadata.RailsVersionMetaData
import org.jboss.rails.core.metadata.RailsApplicationMetaData

describe RailsVersionDeployer do
  
  it_should_behave_like "all deployers"
  
  def create_deployers
    [ 
      RailsVersionDeployer.new 
    ]
  end
  
  it "should parse version.rb" do
    deployment = deploy {
      root {
        dir( "vendor/rails/railties/lib/rails" ) { 
          file( 'version.rb', :read=>'rails-version.rb' ) 
        }
      }
      attachments {
        attach( RailsApplicationMetaData ) do |md, root|
          md.setRailsRoot( root )
          puts "metadata is #{md.inspect}"
        end 
      }
    }
    unit       = deployment_unit_for( deployment )
    meta_data  = unit.getAttachment( RailsVersionMetaData.java_class )
    
    meta_data.should_not be_nil
  end
  
end