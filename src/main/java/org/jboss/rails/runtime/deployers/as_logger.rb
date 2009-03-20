module JBoss
  module Rails
  
    class ASLogger
      def initialize(logger)
        @logger = logger
      end
      def puts(msg)
        write msg.to_s
      end
      def write(msg)
        @logger.info(msg.to_s.chomp)
      end
      def flush; end
      def close; end
    end
  end  
end
