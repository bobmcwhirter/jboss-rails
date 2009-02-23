
module JBoss
  module Rack
    class ResponseHandler
      def self.handle(rack_response, servlet_response)
        status  = rack_response[0]
        headers = rack_response[1]
        body    = rack_response[2]
        headers.each{|key,value|
          puts "#{key} = #{value}"
          for v in value
            servlet_response.setHeader( key, v )
          end
        }
        out = servlet_response.getWriter()
        body.each{|str|
          puts str
          out.write( str );
        }
      end
    end
  end
end
