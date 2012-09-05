/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Motor;

import Motor.lexic.Token;
import Motor.syntax.Syntax;
import Logger.MainLogger;
import Motor.automatos.TypeClass;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 *
 * @author Gustavo Liberatti
 */
public abstract class TokensList {

    private static int ActualLine = 0;
    private static int ActualToken = -1;
    public static List<Token> tokens = new ArrayList<Token>();
    public static Logger log = Logger.getLogger(Syntax.class);

    public static Token getToken(int i) {
        return tokens.get(i);
    }

    public static void add(String content, TypeClass type) {
        // Valida a o conteudo do token antes de adicionar a lista de tokens
        if (!(" ".equals(content)) && !("".equals(content))) {
            Token t = new Token(getActualLine(), content, type);
            tokens.add(t);
        }
    }

    public static int size() {
        return tokens.size();
    }

    public static void insertion(Token t) {
        tokens.add(t);
    }

    public static void debugList() {
        MainLogger.logWarn("Inicio debug TokenList");
        setActualToken(-1);
        while (TokensList.hasNext()) {
            Token token = TokensList.next();
            MainLogger.logWarn("<" + token.getToken() + "," + token.getType() + ">");
        }
        MainLogger.logWarn("Fim debug TokenList");
    }

    public static void clear() {
        tokens = new ArrayList<Token>();
        ActualLine = 0;
        ActualToken = -1;

    }

    public static void reset() {

        ActualLine = 0;
        ActualToken = -1;

    }

    public static void newLine() {
        setActualLine(getActualLine() + 1);
    }

    public static void prevLine() {
        setActualLine(getActualLine() - 1);
    }

    public static Iterator<Token> iterator() {
        return tokens.iterator();
    }

    public static boolean hasNext() {
        if ((tokens.size() - 1) > getActualToken()) {
            return true;
        }
        return false;
    }

    public static Token next() {
        if (TokensList.hasNext()) {
            setActualToken(getActualToken() + 1);
            setActualLine(getActual().getLine());
            log.log(Priority.DEBUG, "TOKEN[" + tokens.get(getActualToken()).getToken() + "] (next)");
            return tokens.get(getActualToken());
        } else {
            return null;
        }

    }

    public static Token prev() {
        setActualToken(getActualToken() - 1);
        setActualLine(getActual().getLine());
        log.log(Priority.DEBUG, "TOKEN[" + tokens.get(getActualToken()).getToken() + "] (prev)");
        return tokens.get(getActualToken());
    }

    public static Token getActual() {
        return tokens.get(getActualToken());
    }

    public static void removeActual() {
        MainLogger.logInfo("Removing: " + getActual().getToken());
        tokens.remove(getActualToken());
        setActualToken(getActualToken() - 1);
    }

    /**
     * @return the ActualLine
     */
    public static int getActualLine() {
        return ActualLine;
    }

    /**
     * @param aActualLine the ActualLine to set
     */
    public static void setActualLine(int aActualLine) {
        ActualLine = aActualLine;
    }

    /**
     * @return the ActualToken
     */
    public static int getActualToken() {
        return ActualToken;
    }

    /**
     * @param aActualToken the ActualToken to set
     */
    public static void setActualToken(int aActualToken) {
        ActualToken = aActualToken;
    }
}
