
require 'deployers/deployer_test_helper'

import org.jboss.rails.core.deployers.RailsEnvYamlParsingDeployer
import org.jboss.rails.core.metadata.RailsApplicationMetaData

describe RailsEnvYamlParsingDeployer do
  
  include DeployerTestHelper
  
  def create_deployers
    [ 
      RailsEnvYamlParsingDeployer.new 
    ]
  end
  
  before( :each ) do 
    setup_microcontainer    
  end
  
  after( :each ) do
    destroy_microcontainer
  end
  
  it "should use the unit's root as RAILS_ROOT" do
    deployment = deploy {
      root {
        dir( 'config', :metadata=>true ) {
          file 'rails-env.yml', :read=>"rails-env/simple-rails-env.yml"
        }
      }
    }
    unit       = deployment_unit_for( deployment )
    meta_data  = unit.getAttachment( RailsApplicationMetaData.java_class )
    
    meta_data.should_not be_nil
    meta_data.getRailsRoot().should eql( unit.getRoot() )
    meta_data.getRailsEnv().should eql( 'simply-an-env' )
  end
  
end