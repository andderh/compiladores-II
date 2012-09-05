package Motor.automatos;

import Motor.TSR;
import Motor.lexic.Token;
import org.apache.log4j.Logger;

/**
 *
 * @author Gustavo Liberatti , Rodicrisller Rodrigues
 * @version 1.0
 *
 */
public abstract class UInteiro {

    private static String word;
    private static Logger logger = Logger.getLogger(UInteiro.class);
    private static int cp = 0;

    /* Metodo padrão nos automatos, implementado para iniciar o automato e fazer validações
     * se necessário
     * @param t da classe Token, representa o token a ser analisado
     * @return boolean retorna o resultado da analise efetuada pelo automato
     */
    public static boolean run(Token t) {
        logger.debug("Init " + UInteiro.class);
        word = t.getToken();
        if (e1()) {
            t.setType(TypeClass.InteirosSemSinal);
            return true;
        }
        return false;
    }

    private static boolean e1() {
        if (TSR.isD(word.charAt(0))) {
            return e2();
        }
        return false;
    }

    private static boolean e2() {
        for (int i = 0; i < word.length(); i++) {
            if (!TSR.isD(word.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
