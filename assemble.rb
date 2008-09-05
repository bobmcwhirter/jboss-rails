#!/usr/bin/env ruby

require 'fileutils'

FileUtils.rm_rf './output/rails.deployer'
FileUtils.mkdir_p './output/rails.deployer/META-INF'
puts `jar cvf ./output/rails.deployer/rails-deployer.jar -C ./output/eclipse-classes .`
FileUtils.cp './src/resources/rails-deployer-jboss-beans.xml', './output/rails.deployer/META-INF/rails-deployer-jboss-beans.xml'



