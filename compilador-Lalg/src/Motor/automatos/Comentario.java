package Motor.automatos;

import Logger.MainLogger;
import Motor.lexic.Token;
import Motor.TokensList;

/**
 *
 * @author Gustavo Liberatti , Rodicrisller Rodrigues
 * @version 1.0
 *
 */
public abstract class Comentario {

    /* Metodo padrão nos automatos, implementado para iniciar o automato e fazer validações
     * se necessário
     * @param t da classe Token, representa o token a ser analisado
     * @return boolean retorna o resultado da analise efetuada pelo automato
     */
    public static boolean run(Token t) {
        return ec1(t);
    }

    /* Estado 1 do automato
     * Verifica se o token é o inicio de um comentario
     */
    private static boolean ec1(Token t) {
        String word = t.getToken();
        int st = 1;
        if ("/*".equals(word)) {
            t.setType(TypeClass.Comentarios);
            return ec2(t);
        } else if (word.charAt(0) == '{') {
            t.setType(TypeClass.Comentarios);
            return ec3(t);
        } else {
            return false;
        }
    }

    /* Estado 2 do automato
     * Consome todos os Tokens delimitados pelo caractere de comentário
     * correspondente ao de abertura
     */
    private static boolean ec2(Token t) {
        while (TokensList.hasNext()) {
           
            Token temp = TokensList.next();
            if ("*/".equals(temp.getToken())) {
               
                temp.setType(TypeClass.Comentarios);
                return true;
            }
            temp.setType(TypeClass.Comentarios);
        }
        MainLogger.logError("Erro Léxico - Encontrei inesperadamente fim de arquivo sem fim de comentário! 2");
        t.setType(TypeClass.Comentarios);
        return false;
    }

    /* Estado 3 do automato
     * Consome todos os Tokens delimitados pelo caractere de comentário
     * correspondente ao de abertura
     */
    private static boolean ec3(Token t) {
        while (TokensList.hasNext()) {
            Token temp = TokensList.next();
            if (temp.getToken().charAt(0) == '}') {
                temp.setType(TypeClass.Comentarios);
                return true;
            }
            temp.setType(TypeClass.Comentarios);
        }
        MainLogger.logError("Erro Léxico - Encontrei inesperadamente fim de arquivo sem fim de comentário! 2");
        t.setType(TypeClass.Comentarios);
        return false;
    }
}
