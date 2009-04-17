
require 'deployers/deployer_test_helper'

import org.jboss.ruby.enterprise.crypto.deployers.CryptoYamlParsingDeployer
import org.jboss.ruby.enterprise.crypto.metadata.CryptoMetaData

describe CryptoYamlParsingDeployer do
  
  include DeployerTestHelper
  
  def create_deployers
    @deployer =  CryptoYamlParsingDeployer.new()
    @deployer.setStoreBasePath( 'path/to/crypto/' )
    [ 
      @deployer
    ]
  end
  
  before( :each ) do 
    setup_microcontainer    
  end
  
  after( :each ) do
    unless ( @cleanup.nil? )
      Java::OrgJbossVirtualPluginsContextMemory::MemoryContextFactory.getInstance().deleteRoot( @cleanup.toURL() )
    end
  end
  
  it "should use adjust based upon base-path" do
    deployment = deploy {
      root {
        dir( 'config', :metadata=>true ) {
          file 'crypto.yml', :read=>"crypto/multi-crypto.yml"
        }
      }
    }
    unit       = deployment_unit_for( deployment )
    meta_data  = unit.getAttachment( CryptoMetaData.java_class )
    
    meta_data.should_not be_nil
  end
  
end