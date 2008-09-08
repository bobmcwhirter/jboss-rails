
require 'java'

import org.jboss.rails.RailsAppHandler

describe RailsAppHandler do
  it "should provide the contents of public/ at the root of the .war" 
  it "should provide WEB-INF/web.xml synthetically"
  it "should provide WEB-INF/** from the rails app directory"
end
