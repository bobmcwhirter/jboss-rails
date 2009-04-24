
require 'helpers/jboss/deployer_test_helper'

describe "all deployers", :shared=>true do
  
  include DeployerTestHelper
  
  before( :all ) do 
    setup_microcontainer    
  end
  
  after( :all ) do
    destroy_microcontainer
  end
  
  after( :each ) do
    cleanup_vfs
  end
  
end