Overview
========

JBoss Rails is a deployer for JBoss AS 5.x to enable deployment
of Ruby-on-Rails (RoR) projects.  Applications run on the JVM
by way of JRuby and JRuby-Rack.

Deployment
==========

Live application directories can be deployed, allowing editing
of views and controllers without having to redeploy the application.

A JBoss Rails deployment descriptor placed in the server's `deploy/`
directory will cause immediate deployment of the application.  Editing,
touching, or removing the descriptor will cause either a redeployment
or an undeployment.

Deployment Descriptor
---------------------

Once installed, the deployer recognizes deployment descriptors
matching the pattern of `*-rails.yml`.  This YAML file contains
a configuration tree specifying the deployment information.

An example for `groundhog-rails.yml`

	application: 
	  RAILS_ROOT: /home/bob/checkouts/groundhog
	  RAILS_ENV: development
	web: 
	  context: /

* The `application.RAILS_ROOT` entry points to `RAILS_ROOT` of the application to be deployed.
* The `application.RAILS_ENV` entry sets the RAILS_ENV for the deployment.
* The `web.context` entry describes the URL to which the application should be bound.

Other Projects
==============

To facilitate usage of JBoss Rails from a traditional Rails application,
the [JBoss Rails Plugin](http://github.com/bobmcwhirter/jboss-rails-plugin/tree/master)
installs easily under your application's `vendor/plugins/` directory and provides
a set of Rake tasks to make deploying and undeploying applications super-simple.

