package Motor.lexic;

import Logger.MainLogger;
import Motor.automatos.TypeClass;

/**
 *
 * @author Gustavo Liberatti
 */
public class Token {

    private int line;
    private String token;
    private TypeClass type;

    public Token(int line, String token, TypeClass type) {
        String cToken = new String();
        // Trata espaço fantasma [ :) ]
        for (int i = 0; i < token.length(); i++) {
            char c = token.charAt(i);
            if (c != ' ') {
                cToken += c;
            }
        }
        this.line = line;
        this.token = cToken;
        this.type = type;
    }

    /**
     * @return the line
     */
    public int getLine() {
        return line;
    }

    /**
     * @param line the line to set
     */
    public void setLine(int line) {
        this.line = line;
    }

    /**
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token the token to set
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * @return the type
     */
    public TypeClass getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(TypeClass type) {
        // MainLogger.logInfo("Token <" + this.getToken() + "> encontrado do tipo <" + type + ">.");
        this.type = type;
    }
}
