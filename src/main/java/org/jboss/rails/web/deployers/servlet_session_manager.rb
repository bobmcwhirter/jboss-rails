
module JBoss
  module Rails
    class ServletSessionManager
      RAILS_SESSION_KEY = "__current_rails_session"
      
      def initialize(session, option) 
        @servlet_request = option['servlet_request']
        @session_data = {}
        @digest = 'SHA1'
      end
      
      def restore
        @session_data = {}
        java_session = @servlet_request.getSession(false)
        if java_session
          java_session.getAttributeNames.each do |k|
            if k == RAILS_SESSION_KEY
              marshalled_bytes = java_session.getAttribute(RAILS_SESSION_KEY)
              if marshalled_bytes
                data = Marshal.load(String.from_java_bytes(marshalled_bytes))
                @session_data.update data if Hash === data
              end
            else
              @session_data[k] = java_session.getAttribute(k)
            end
          end
        end
        @session_data
      end
      
      def update
        java_session = @servlet_request.getSession(true)
        hash = @session_data.dup
        hash.delete_if do |k,v|
          if String === k
            case v
              when String, Numeric, true, false, nil
              java_session.setAttribute k, v
              true
            else
              if v.respond_to?(:java_object)
                java_session.setAttribute k, v
                true
              else
                false
              end
            end
          end
        end
        unless hash.empty?
          marshalled_string = Marshal.dump(hash)
          marshalled_bytes = marshalled_string.to_java_bytes
          java_session.setAttribute(RAILS_SESSION_KEY, marshalled_bytes)
        end
      end
      
      # Update and close the Java session entry
      def close
        update
      end
      
      # Delete the Java session entry
      def delete
        java_session = @servlet_request.getSession(false)
        java_session.invalidate if java_session
      end
      
      def generate_digest(data)
        java_session = @servlet_request.getSession(true)
        @secret ||= java_session.getAttribute("__rails_secret")
        unless @secret
          @secret = java_session.getId + java_session.getLastAccessedTime.to_s
          java_session.setAttribute("__rails_secret", @secret)
        end
        OpenSSL::HMAC.hexdigest(OpenSSL::Digest::Digest.new(@digest), @secret, data)
      end
      
      # The session state
      def data
        @session_data
      end
      
      def []=(k, v)
        @session_data[k] = v
      end
      
      def [](k)
        @session_data[k]
      end
      
      def each(&b)
        @session_data.each(&b)
      end
      
      private
      # Attempts to redirect any messages to the data object.
      def method_missing(name, *args, &block)
        @session_data.send(name, *args, &block)
      end
      
    end
  end
end