package compilador;

import java.util.logging.Logger;
import Interface.TelaPrincipal;
import Logger.JTextPaneAppender;
import Logger.MainLogger;

/**
 *
 * @author Gustavo Liberatti , Rodicrisller Rodrigues
 * @version 3.3
 *
 */
public class Main {

    static final Logger logger = Logger.getLogger("Logger");

    public static void main(String[] args) {
        TelaPrincipal principal = new TelaPrincipal();
        principal.setVisible(true);
        MainLogger.init(new JTextPaneAppender(principal.getjTextPane1()), org.apache.log4j.Level.TRACE);
    }
}
