/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Motor.automatos;

import Motor.TSR;
import Motor.lexic.Token;

/**
 *
 * @author tux
 */
public abstract class Simbolo {

    public static boolean run(Token t) {
        if (e1(t)) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean e1(Token t) {
        String word = t.getToken();
        if (TSR.isS(word.charAt(0))) {
            if (TSR.isDS(word)) {
                t.setType(TypeClass.SimbolosDuplos);
                return true;
            } else {
                if (word.length() == 1) {
                    t.setType(TypeClass.SimbolosEspeciais);
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }
}
