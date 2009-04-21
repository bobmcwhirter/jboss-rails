#import org.jboss.virtual.plugins.context.memory.MemoryContextFactory

require 'helpers/jboss/vfs_builder'
require 'helpers/jboss/metadata_builder'
import org.jboss.deployers.vfs.spi.client .VFSDeploymentFactory

module JBoss
  
  class DeploymentBuilder
  
    def initialize(url=nil, &block)
      @url = url
      instance_eval &block if block 
    end
    
    def apply_structure
      @structure = StructureMetaDataFactory.createStructureMetaData()
      context = StructureMetaDataFactory.createContextInfo()
      @metadata_paths.each do |path|
        context.addMetaDataPath( path ) 
      end
      @structure.addContext( context )
    end
    
    def attachments(&block)
      @metadata_builder = JBoss::MetadataBuilder.new( @vfs_builder.root_vfs, &block )
    end
    
    def root(opts={}, &block)
      raise "deployment created by URL may not be created using a block" if @url
      @vfs_builder = JBoss::VFSBuilder.new( opts, &block )
    end
    
    def deployment
      if ( @url )
        vfs_file = VFS.getRoot( java.net.URL.new( @url ) )
        dep = VFSDeploymentFactory.getInstance().createVFSDeployment( vfs_file )
      else
        root = nil
        structure = nil
        if ( @vfs_builder )
          root      = @vfs_builder.root_vfs 
          structure = @vfs_builder.structure
        end
      
        dep = VFSDeploymentFactory.getInstance().createVFSDeployment( root )
        
        if ( structure )
          dep.getPredeterminedManagedObjects().addAttachment( StructureMetaData.java_class, structure )      
        end
      end
      
      if ( @metadata_builder ) 
        @metadata_builder.attach_to( dep )        
      end
      
      return dep

    end
    
  end
end