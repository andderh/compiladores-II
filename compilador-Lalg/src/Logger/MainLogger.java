package Logger;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class MainLogger {

    public static Logger log = Logger.getLogger("Logger");
    private static boolean init = false;


    private MainLogger() {
    }

    public static void init(Appender appender, Level l) {
        if (init != true) {
            log.addAppender(appender);
            log.setLevel(l);
        }
        init = true;
    }

    public static void logWarn(final String s) {
        log.warn(s);
    }

    public static void logError(final String s) {
        log.error(s);
    }

    public static void logError(final Throwable ex) {
        log.fatal(new String(), ex);
    }

    public static void logInfo(final String s) {
        log.info(s);
    }
}
