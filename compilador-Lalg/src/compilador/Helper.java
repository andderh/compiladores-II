/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import Logger.MainLogger;
import Motor.TokensList;
import Motor.automatos.TypeClass;
import Motor.TSR;
import Motor.lexic.Token;
import Motor.syntax.Contexto;
import Motor.syntax.Procedimento;
import Motor.syntax.Syntax;
import Motor.syntax.Var;
import com.thoughtworks.xstream.XStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 *
 * @author Gustavo Liberatti , Rodicrisller Rodrigues
 * @version 1.0
 *
 */
public abstract class Helper {

    static Logger looger = Logger.getLogger("Logger");
    static FileWriter fileW;

    public static Timestamp getTimeStamp() {
        Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
        return currentTimestamp;
    }

    public static String to_xml(Contexto c) {
        XStream xs = new XStream();
        xs.alias("Procedimento", Procedimento.class);
        xs.alias("Contexto", List.class);
        xs.alias("Variavel", Var.class);
        xs.aliasField("Parametros", Procedimento.class, "par");
        xs.aliasField("Variáveis", Procedimento.class, "var");
        String saida = xs.toXML(Syntax.contexto.proc);
        return saida;
    }

    public static void write_xml_context(File file, Contexto context) {
        FileWriter arquivo = null;
        XStream xstream = new XStream();
        try {
            arquivo = new FileWriter(file);
        } catch (IOException ex) {
            looger.log(Priority.ERROR, ex.getMessage());
        }

        xstream.toXML(context, arquivo);//armazenando uma lista
    }

    public static Contexto read_xml_context(File file) {
        XStream xstream = new XStream();
        FileReader arquivo = null;
        try {
            arquivo = new FileReader(file);
        } catch (FileNotFoundException ex) {
            looger.log(Priority.ERROR, ex.getMessage());
        }
        Contexto membros = (Contexto) xstream.fromXML(arquivo);
        return null;
    }

    /* Metodo para abrir e extrair linhas de um arquivo texto
     *
     * @param path Caminho absoluto para o arquivo de codigo fonte a ser carregado
     * @return ArrayList contendo todas as linhas do arquivo indicado
     */
    public static List<String> readFile(String path) {
        TokensList.clear();
        List<String> sentences = new ArrayList<String>();

        FileReader fr = null;
        try {
            fr = new FileReader(new File(path));
        } catch (FileNotFoundException ex) {
            looger.log(Priority.ERROR, ex.getMessage());

        }
        BufferedReader in = new BufferedReader(fr);
        try {
            while (in.ready()) {
                sentences.add(in.readLine());
            }
            in.close();
        } catch (IOException ex) {
            looger.log(Priority.ERROR, ex.getMessage());
        }
        return sentences;
    }

    /*
     * Armazena na classe estatica TokensList todos os tokens estraidos de uma string
     * @param id Informa o numero ou identificacao da linha
     * @param sequence String da qual serão extraidos os tokens
     */
    public static void stractTokens(int id, String sentence) {
        String token = "";
        TokensList.setActualLine(id);
        int i = 0;
        while (sentence.length() > i) {
            char c = sentence.charAt(i); //Pega-se o proximo char da sentença
            if (TSR.isDelim(c)) {
                TokensList.add(token, TypeClass.UNDEFINED);
                TokensList.add(String.valueOf(c), TypeClass.UNDEFINED); // Adiciona o ; que é um delimitador mas pertence ao vocabulario
                token = new String();
            } else {
                if (TSR.isS(c) && c != '.') {// Trata aglomeração de código, onde o unico aglomerado permitido é o . (numero real)
                    TokensList.add(token, TypeClass.UNDEFINED);
                    token = new String();
                    while (TSR.isS(c) && c != '.' && c != ';' && c != '(' && c != ')') {
                        token += String.valueOf(c);
                        if (sentence.length() > (i + 1) && TSR.isS(sentence.charAt(i + 1))) {
                            i++;
                            c = sentence.charAt(i);
                        } else {
                            break;
                        }
                    }
                    TokensList.add(token, TypeClass.UNDEFINED);
                    if (c == ';' || c == '(' || c == ')') {
                        TokensList.add(String.valueOf(c), TypeClass.UNDEFINED);
                    }
                    token = new String();
                } else { // Aglomera numero real
                    if ((TSR.isD(c) || TSR.isL(c)) || c == '.') {
                        token += String.valueOf(c);
                    } else {
                        MainLogger.logError("Linha: " + id + "Caractere [" + c + "]" + " não pertence a linguagem");
                    }
                }
            }
            i++;
        }
        TokensList.add(token, TypeClass.UNDEFINED);
    }

    public static String printTokenList() {
        String tstr = new String();
        TokensList.reset(); // Set a linha atual do TokenList no ínicio do código se escrever no arquivo
        while (TokensList.hasNext()) {
            Token t = TokensList.next();
            tstr += ("<" + t.getToken() + ", " + t.getType() + "> ");
            tstr += '\n';
        }
        return tstr;
    }

    public static boolean saveTokenList(File file) {
        MainLogger.logInfo(TokensList.size() + " - Tokens encontrados");
        MainLogger.logInfo("[" + Helper.getTimeStamp() + "] Salvando TokenList em: " + file.getAbsolutePath());

        try {
            fileW = new FileWriter(file);
            PrintWriter pw = new PrintWriter(fileW);
            int linha = 1;
            TokensList.reset(); // Set a linha atual do TokenList no ínicio do código se escrever no arquivo
            while (TokensList.hasNext()) {
                Token t = TokensList.next();
                pw.print("<" + t.getToken() + ", " + t.getType() + "> ");
                pw.println();
            }
            pw.close();
        } catch (IOException ex) {
            looger.log(Priority.ERROR, ex.getMessage());
        }
        MainLogger.logInfo("[" + Helper.getTimeStamp() + "] Operação concluída");
        return true;
    }
}
