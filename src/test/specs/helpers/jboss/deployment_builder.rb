#import org.jboss.virtual.plugins.context.memory.MemoryContextFactory

require 'helpers/jboss/vfs_builder'
require 'helpers/jboss/metadata_builder'
import org.jboss.deployers.vfs.spi.client .VFSDeploymentFactory

module JBoss
  
  class DeploymentBuilder
  
    def initialize(url=nil, &block)
      @vfs_builder = JBoss::VFSBuilder.new( url )
      instance_eval &block if block 
    end
    
    def attachments(&block)
      @metadata_builder = JBoss::MetadataBuilder.new( @vfs_builder.root_vfs, &block )
    end
    
    def root(opts={}, &block)
      @vfs_builder.root( &block )
    end
    
    def deployment
      root      = @vfs_builder.root_vfs 
      structure = @vfs_builder.structure
      
      dep = VFSDeploymentFactory.getInstance().createVFSDeployment( root )
        
      if ( structure )
        dep.getPredeterminedManagedObjects().addAttachment( StructureMetaData.java_class, structure )      
      end
      
      if ( @metadata_builder ) 
        @metadata_builder.attach_to( dep )        
      end
      
      return dep

    end
    
  end
end