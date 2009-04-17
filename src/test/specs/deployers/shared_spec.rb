
require 'helpers/jboss/deployer_test_helper'

describe "all deployers", :shared=>true do
  
  include DeployerTestHelper
  
  before( :each ) do 
    setup_microcontainer    
  end
  
  after( :each ) do
    destroy_microcontainer
  end
  
end