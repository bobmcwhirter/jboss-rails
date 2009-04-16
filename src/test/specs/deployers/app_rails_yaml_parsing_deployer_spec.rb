
require 'deployers/deployer_test_helper'

import org.jboss.rails.core.deployers.AppRailsYamlParsingDeployer
import org.jboss.rails.core.metadata.RailsApplicationMetaData

describe AppRailsYamlParsingDeployer do
  
  include DeployerTestHelper
  
  def create_deployers
    [ 
      AppRailsYamlParsingDeployer.new 
    ]
  end
  
  before( :each ) do 
    setup_microcontainer    
  end
  
  it "should create a sub-deployment with pre-attached RailsApplicationMetaData" do
    #deployment = deploy( "#{BASE_DIR}/src/test/resources/deployments/toplevel/simple-rails.yml" )
    deployment = deploy( 'toplevel/simple-rails.yml' )
    unit = deployment_unit_for( deployment )
    sub_deployment = unit.getAttachment( "jboss.rails.root.deployment" )
    sub_deployment.should_not be_nil
    sub_unit =  deployment_unit_for( sub_deployment )
    meta_data = sub_unit.getAttachment( RailsApplicationMetaData.java_class )
    meta_data.should_not be_nil
    meta_data.getRailsRootPath().should eql( '/Users/bob/oddthesis/oddthesis' )
    meta_data.getRailsEnv().should eql( 'development' )
  end
  
end