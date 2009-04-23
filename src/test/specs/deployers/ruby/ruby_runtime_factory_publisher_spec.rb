
require 'deployers/shared_spec'

import org.jboss.ruby.core.runtime.deployers.RubyRuntimeFactoryPublisher
import org.jboss.ruby.core.runtime.metadata.RubyRuntimeMetaData
import org.jboss.ruby.core.runtime.spi.RubyRuntimeFactory
import org.jboss.ruby.core.DefaultRubyRuntimeFactory
import org.jboss.beans.metadata.spi.BeanMetaData

describe RubyRuntimeFactoryPublisher do
  
  it_should_behave_like "all deployers"
  
  def create_deployers
    [
      RubyRuntimeFactoryPublisher.new()
    ]
  end
  
  it "should proxy the attached factory as a named MCBean" do
    deployment = deploy {
      attachments {
        attach_object( RubyRuntimeFactory, DefaultRubyRuntimeFactory.new )
      }
    }
    unit = deployment_unit_for( deployment )
    bmd = unit.getAttachment( BeanMetaData.java_class.to_s + "$RubyRuntimeFactory", BeanMetaData.java_class )
    bmd.should_not be_nil
    bmd.getName().should eql( RubyRuntimeFactoryPublisher.getBeanName( unit ) )
  end
  
end