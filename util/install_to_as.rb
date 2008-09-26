#!/usr/bin/env ruby

require 'FileUtils'

jboss_home = ENV['JBOSS_HOME']

deployer_src = File.dirname( __FILE__ ) + '/../target/jboss-rails-deployer.dir'

if ( ! File.exist?( deployer_src ) )
  puts "Deployer not built; not installing."
  puts "Try 'maven package' first."
  exit 1
end

if ( jboss_home.nil? && File.exist?( File.dirname( __FILE__ ) + '/../../jboss-as-rails' ) )
  jboss_home = File.dirname( __FILE__ ) + '/../../jboss-as-rails' 
end

if ( jboss_home.nil? )
  puts "No JBOSS_HOME or jboss-as-rails/ peer; not installing."
  exit 1
end

deployers_dir = "#{jboss_home}/server/default/deployers/"

puts "Installing into #{deployers_dir}"

if ( File.exist?( "#{deployers_dir}/jboss-rails.deployer" ) )
  puts "Existing deployer in the way; removing."
  FileUtils.rm_rf( "#{deployers_dir}/jboss-rails.deployer" )
end

FileUtils.cp_r( deployer_src, "#{deployers_dir}/jboss-rails.deployer" )


