/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
public abstract class Identificador {

    private static Logger logger = Logger.getLogger(UInteiro.class);

    /* Metodo padrão nos automatos, implementado para iniciar o automato e fazer validações
     * se necessário
     * @param t da classe Token, representa o token a ser analisado
     * @return boolean retorna o resultado da analise efetuada pelo automato
     */
    public static boolean run(Token t) {
        return e1(t);
    }

    /* Estado 1 do automato
     * Verifica se a palavra informada é do classe reservada
     * caso não seja chama o metodo e2
     */
    private static boolean e1(Token t) {
        String word = t.getToken();
        if (TSR.isReserved(word)) {
            t.setType(TypeClass.PalavrasReservadas);
            return true;
        } else {
            return e2(t);
        }

    }
/*
 * Verifica se o token t é um identificado
 * return boolean retorna false se não for um identificador
 */
    private static boolean e2(Token t) {
        String word = t.getToken();
        for (int i = 0; i < word.length(); i++) {
            if (!TSR.isL(word.charAt(i)) && !TSR.isD(word.charAt(i))) {
                return false;
            }
        }
        t.setType(TypeClass.Identificadores);
        return true;
    }
}
