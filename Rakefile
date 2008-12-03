
require 'fileutils'

def determine_version
  glob = "#{project_root}/target/jboss-rails-*-deployer.dir"
  matches = Dir[ glob ]
  if ( matches.length != 1 )
    fail "*** run 'mvn package' first"
  end

  deployer_dir = matches.first

  version = nil
  base = File.basename( deployer_dir, ".dir" )
  if ( base =~ /^jboss-rails-(.+)-deployer$/ )
    version = $1
  end
  if ( version.nil? )
    fail "*** unable to determine version"
  end
  return version
end

def version
  $VERSION ||= determine_version
end

def project_root
  File.dirname( __FILE__ )
end

def deployer_build_dir
  "#{project_root}/target/jboss-rails-#{version}-deployer.dir"
end

def dist_stage
  "#{project_root}/target/dist-stage"
end

def deployer_dist_dir
  "#{dist_stage}/deployer"
end

def simple_deployer_dir
  "#{deployer_dist_dir}/jboss-rails.deployer"
end

def as_bundle_dir
  "#{dist_stage}/as-bundle"
end

def as_zipfile
  Dir["#{project_root}/cache/jboss-*.zip"].first
end

def simple_bundle_name
  "jboss-5.0.x-rails"
end

def simple_bundle_dir
  "#{as_bundle_dir}/#{simple_bundle_name}"
end

task :dist_bundle=>[:dist_deployer] do
  puts "Making bundle from #{as_zipfile}"

  FileUtils.rm_rf( as_bundle_dir )
  FileUtils.mkdir_p( as_bundle_dir )

  created_dir = nil
  FileUtils.chdir( as_bundle_dir ) do
    puts `unzip #{as_zipfile}`
    created_dir = Dir['jboss-*'].first
    puts "unpacked to #{created_dir}"
    puts "moving to #{created_dir}"
    FileUtils.mv( created_dir, simple_bundle_name )
  end

  FileUtils.chdir( simple_bundle_dir ) do
    Dir['server/*/deploy/ROOT.war'].each do |root_war|
      puts "removing #{root_war}"
      FileUtils.rm_rf( root_war )
    end

    FileUtils.chdir( "server" ) do 
      (Dir['*']-['minimal']).each do |config|
        if ( File.exist?( "#{config}/deployers" ) )
          puts "installing jboss-rails into #{config}"
          FileUtils.cp_r( simple_deployer_dir, "#{config}/deployers" )
        end
      end
    end
  end

  FileUtils.chdir( "#{as_bundle_dir}" ) do 
    puts "packing to #{simple_bundle_name}-#{version}.zip"
    puts `zip -r ../#{simple_bundle_name}-#{version}.zip #{simple_bundle_name}`
  end


end

task :dist_deployer do

  puts "Making distribution of #{deployer_build_dir}"

  FileUtils.rm_rf( deployer_dist_dir )
  FileUtils.mkdir_p( deployer_dist_dir )

  FileUtils.cp_r( deployer_build_dir, simple_deployer_dir )
  FileUtils.cp( "#{project_root}/INSTALL.txt", deployer_dist_dir )
  FileUtils.cp( "#{project_root}/README.markdown", deployer_dist_dir )
  Dir[ "#{project_root}/LICENSE*.txt" ].each do |l|
    FileUtils.cp( l, deployer_dist_dir )
  end

  FileUtils.chdir( "#{deployer_dist_dir}" ) do 
    puts `zip -r ../jboss-rails-deployer-#{version}.zip jboss-rails.deployer *.txt *.markdown`
  end
end

task :dist=>[ :dist_deployer, :dist_bundle ]

task :release_dist do
  version = determine_version

  FileUtils.chdir( "#{dist_stage}" ) do 
    puts "uploading #{simple_bundle_name}-#{version}"
    puts `scp -q #{simple_bundle_name}-#{version}.zip oddthesis.org:/opt/oddthesis/repo/`
    puts "uploading jboss-rails-deployer-#{version}"
    puts `scp -q jboss-rails-deployer-#{version}.zip oddthesis.org:/opt/oddthesis/repo/`
  end
end
