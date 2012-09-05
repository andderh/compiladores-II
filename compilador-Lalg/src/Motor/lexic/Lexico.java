package Motor.lexic;

import Logger.MainLogger;
import Motor.TSR;
import Motor.TokensList;
import Motor.automatos.Comentario;
import Motor.automatos.Identificador;
import Motor.automatos.Real;
import Motor.automatos.Simbolo;
import Motor.automatos.UInteiro;
import Motor.automatos.TypeClass;
import compilador.Helper;
import java.util.List;

/**
 *
 * @author Gustavo Liberatti , Rodicrisller Rodrigues
 * @version 1.0
 *
 */
public abstract class Lexico {

    static TypeClass type;
    static int sentenceId = 0;
    static int tokenId = 0;
    static List<String> tokens;
    static List<String> sentences;

    public static void run(List<String> sentences) {
        MainLogger.logInfo("[" + Helper.getTimeStamp() + "] Iniciando análise lexica");
        int ActLine = 1;
        while (!sentences.isEmpty()) {
            Helper.stractTokens(ActLine, sentences.get(0));
            ActLine++;
            sentences.remove(0);
        }
        while (TokensList.hasNext()) {
            Token t = TokensList.next();

            boolean undefined = true;
            // Caso o token inicie com Digito, só poderá corresponder a iteiro ou real
            if (TSR.isD(t.getToken().charAt(0))) {
                undefined = !UInteiro.run(t);
                if (undefined) {
                    undefined = !Real.run(t);
                }
            } else if (TSR.isL(t.getToken().charAt(0))) {
                // Se iniciar com letra poderá ser um identificador ou palavra reservada
                undefined = !Identificador.run(t);
            } else if (TSR.isS(t.getToken().charAt(0))) {
                // se começar com simbolo só poderá ser simbolos especiais ou comentário
                if (undefined) {
                    // um  comentário é iniciado por um simbolo duplo ou por { } portanto,
                    // devem ser checados de forma sobreposta
                    int cont = 0;
                    undefined = !Simbolo.run(t);
                    if (!undefined) {
                        cont++; // É simbolo
                    }
                    undefined = !Comentario.run(t);
                    if (undefined && cont == 1) {
                        undefined = false; //É simbolo, porém não é comentário
                    } else {
                    }
                }
            }
            // Imprime falhas se ocorrerem
            if (undefined) {
                MainLogger.logError("Lexico - Linha: " + TokensList.getActualLine() + " com o token " + t.getToken());
            }
        }
        MainLogger.logInfo("[" + (Helper.getTimeStamp().toString()) + "] Análise lexica concluída");
        //TokensList.debugList();
    }
}
