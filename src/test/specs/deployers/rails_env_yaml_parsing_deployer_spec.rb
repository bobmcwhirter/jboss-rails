
require 'deployers/deployer_test_helper'

import org.jboss.rails.core.deployers.RailsEnvYamlParsingDeployer

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
  
  it "should do something" do
    deploy( "path/to/foo" ).should be_true
  end
  
end