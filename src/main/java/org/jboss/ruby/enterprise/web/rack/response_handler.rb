
module JBoss
  module Rack
    class ResponseHandler
      def self.handle(rack_response, servlet_response)
        status  = rack_response[0]
        headers = rack_response[1]
        body    = rack_response[2]
        status_code, status_string = status.split
        status_code.strip!
        status_string.strip!
        servlet_response.setStatus( status_code.to_i )
        headers.each{|key,value|
          for v in value
            servlet_response.setHeader( key, v )
          end
        }
        out = servlet_response.getWriter()
        body.each{|str|
          out.write( str );
        }
      end
    end
  end
end
