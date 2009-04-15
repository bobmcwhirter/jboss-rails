
require 'deployers/deployer_test_helper'

import org.jboss.rails.core.deployers.RailsStructure

describe RailsStructure do
  
  include DeployerTestHelper
  
  before( :each ) do 
    setup_microcontainer    
  end
  
end