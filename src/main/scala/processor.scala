import java.util.logging.Logger

class processor {
  System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n")
  val logger = Logger.getLogger("assignment-processor")
}
