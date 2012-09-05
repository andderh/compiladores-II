package Motor;

import Motor.lexic.Token;
import Motor.automatos.TypeClass;

/**
 *
 * @author Gustavo Liberatti
 */
public abstract class TSR {

    public static char sentenceDelim = ';';
    public static char tokenDelim = ' ';
    private static String[] reserved = new String[]{
        "program", "var", "real", "integer", "procedure", "begin",
        "read", "write",
        "while", "if", "then", "else", "do",
        "end" ,"end."
    };
    private static String[] doubleSimb = new String[]{
        ">=", "<=", "<>", ":=", "==", "/*", "*/"
    };

    public static boolean isTerminal(Token token) {
        TypeClass ty = token.getType();
        if (ty == TypeClass.PalavrasReservadas
                || ty == TypeClass.Identificadores
                || ty == TypeClass.InteirosSemSinal
                || ty == TypeClass.NumerosReais
                || ty == TypeClass.SimbolosDuplos
                || ty == TypeClass.SimbolosEspeciais) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isDS(String simb) {
        for (int i = 0; i < doubleSimb.length; i++) {
            if (doubleSimb[i].equals(simb)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isD(char c) {
        String letra = String.valueOf(c);
        if (letra.matches("[0-9]")) {
            return true;
        }
        return false;
    }

    public static boolean isS(char c) {
        String letra = String.valueOf(c);
        if (letra.matches("[-:()*/+\\<>,=;${}.@¬¢£³²¹§ªº°]")) {
            return true;
        }
        return false;

    }

    public static boolean isDelim(char ca) {
   
        if (ca == ' ' || ca == (char) 13 || ca == ';' || ca == '\n') {
            return true;
        }
        return false;


    }

    public static boolean isL(char c) {
        String letra = String.valueOf(c);
        if (letra.matches("[a-zA-ZáàãâäéèêëíìîïóòõôöúùûüçÁÀÃÂÄÉÈÊËÍÌÎÏÓÒÕÔÖÚÙÛÜÇ]")) {
            return true;
        }
        return false;
    }

    public static boolean isReserved(String w) {
        for (int i = 0; i < reserved.length; i++) {
            if (reserved[i].equals(w)) {
                return true;
            }
        }
        return false;
    }
}
