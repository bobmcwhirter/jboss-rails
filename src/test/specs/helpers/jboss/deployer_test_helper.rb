
import org.jboss.kernel.plugins.bootstrap.basic.BasicBootstrap
import org.jboss.dependency.plugins.AbstractController
import org.jboss.deployers.plugins.deployers.DeployersImpl
import org.jboss.deployers.plugins.main.MainDeployerImpl
import org.jboss.deployers.structure.spi.StructureBuilder
import org.jboss.deployers.structure.spi.helpers.AbstractStructuralDeployers
import org.jboss.deployers.structure.spi.helpers.AbstractStructureBuilder
import org.jboss.deployers.plugins.managed.DefaultManagedDeploymentCreator
import org.jboss.deployers.spi.deployer.managed.ManagedObjectCreator
import org.jboss.deployers.spi.deployer.helpers.DefaultManagedObjectCreator
import org.jboss.deployers.spi.structure.StructureMetaData
import org.jboss.deployers.vfs.plugins.structure.VFSStructureBuilder
import org.jboss.deployers.vfs.spi.client .VFSDeploymentFactory
import org.jboss.virtual.VFS
import java.net.URL

VFS.init()

require 'helpers/jboss/deployment_builder'

module DeployerTestHelper
  
  def setup_microcontainer
    @bootstrap = BasicBootstrap.new()
    @bootstrap.run()
    @kernel = @bootstrap.getKernel()
    @controller = @kernel.getController();

    @main_deployer = create_main_deployer();
    @cleanup = []
  end
  
  def destroy_microcontainer
    @cleanup.each do |clean_me|
      Java::OrgJbossVirtualPluginsContextMemory::MemoryContextFactory.getInstance().deleteRoot( clean_me.toURL() )
    end    
  end
  
  def deploy(path=nil,&block)
    vfs_file = nil
    structure = nil
    
    unless ( path.nil? )
      if ( path[0,1] != '/' ) 
        path = BASE_DIR + '/src/test/resources/deployments/' + path
      end
      url = "file:///#{path}"
      deployment   = JBoss::DeploymentBuilder.new( url ).deployment
    else
      unless block.nil?
        deployment   = JBoss::DeploymentBuilder.new( &block ).deployment
        @cleanup << deployment.getRoot()
      end
    end
    
    @main_deployer.addDeployment( deployment )    
    @main_deployer.process()
    deployment
  end
  
  def deployment_unit_for(deployment)
    @main_deployer.getDeploymentUnit( deployment.getName() )
  end
  
  def deployer_instances
    return []    
  end
  
  private
  
  def create_main_deployer()
    main_deployer = MainDeployerImpl.new
    structure = create_structural_deployers_holder
    main_deployer.setStructuralDeployers(structure);
    deployers = create_deployers_holder
    main_deployer.setDeployers(deployers);
    mdc = create_managed_deployment_creator();
    main_deployer.setMgtDeploymentCreator(mdc);
    create_deployers.each do |deployer|
      deployers.addDeployer( deployer )      
    end
    main_deployer 
  end
  
  def create_structural_deployers_holder
    builder = VFSStructureBuilder.new();
    structure = AbstractStructuralDeployers.new();
    structure.setStructureBuilder(builder);
    return structure;
  end
  
  def create_deployers_holder
    moc = DefaultManagedObjectCreator.new()
    di = DeployersImpl.new(@controller);
    di.setMgtObjectCreator(moc);
    di
  end
  
  def create_managed_deployment_creator()
    DefaultManagedDeploymentCreator.new();
  end
  
end
