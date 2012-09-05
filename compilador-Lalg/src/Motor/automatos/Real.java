package Motor.automatos;

import Motor.TSR;
import Motor.lexic.Token;

/**
 *
 * @author Gustavo Liberatti , Rodicrisller Rodrigues
 * @version 1.0
 *
 */
public abstract class Real {

    /* Metodo padrão nos automatos, implementado para iniciar o automato e fazer validações
     * se necessário
     * @param t da classe Token, representa o token a ser analisado
     * @return boolean retorna o resultado da analise efetuada pelo automato
     */
    private static String word;
    private static char RealDelim = '.';
    private static boolean delimFound = false;

    public static boolean run(Token t) {
        word = t.getToken();
        if (e1() && word.length() >= 2) {
            t.setType(TypeClass.NumerosReais);
            return true;
        } else {
            return false;
        }
    }

    private static boolean e1() {
        if (TSR.isD(word.charAt(0)) || word.charAt(0) == RealDelim) {
            return e2();
        } else {
            return false;
        }
    }

    private static boolean e2() {
        delimFound = false;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (!(TSR.isD(c) || c == '.')) {
                return false;
            }
            if (c == '.') {
                if (delimFound) {
                    return false;
                } else {
                    delimFound = true; // Encontrei o primeiro '.'
                    System.out.println("Encontrei: [" + c + "] mudando delim para : " + delimFound);
                }
            }
        }
        return delimFound;
    }
}
