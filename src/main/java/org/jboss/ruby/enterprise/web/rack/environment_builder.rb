
require 'pp'

module JBoss
  module Rack
    class EnvironmentBuilder
      def self.build(servlet_request, input, errors)
        env = {}
        env['REQUEST_METHOD']    = servlet_request.getMethod()
        env['SCRIPT_NAME']       = servlet_request.getContextPath() || ''
        env['PATH_INFO']         = servlet_request.getPathInfo()
        env['QUERY_STRING']      = servlet_request.getQueryString()
        env['SERVER_NAME']       = servlet_request.getServerName()
        env['SERVER_PORT']       = servlet_request.getServerPort()
        env['CONTENT_LENGTH']    = servlet_request.getContentLength()
        env['REQUEST_URI']       = servlet_request.getRequestURI() 
        env['rack.version']      = [ 0, 1 ]
        env['rack.multithread']  = true
        env['rack.multiprocess'] = true
        env['rack.run_once']     = false
        env['rack.input']        = input
        env['rack.errors']       = errors
        servlet_request.getHeaderNames().each do |name|
          env["HTTP_#{name.upcase}"] = servlet_request.getHeader( name )
        end
        env['servlet_request'] = servlet_request
        env
      end
    end
  end
end
