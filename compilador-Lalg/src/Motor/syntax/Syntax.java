package Motor.syntax;

import Logger.MainLogger;
import Motor.lexic.Token;
import Motor.TokensList;
import Motor.automatos.TypeClass;
import Motor.automatos.TypeClass;
import Motor.semantic.Semantico;
import compilador.Helper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import javax.swing.text.StyledEditorKit.ForegroundAction;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 *
 * @author Gustavo Liberatti, Rodicrisller Rodrigues
 */
public abstract class Syntax {

    public static Logger log = Logger.getLogger(Syntax.class);
    private static List<String> error = new ArrayList<String>();
    private static List<String> error_s = new ArrayList<String>();
    private static List<Integer> checkPoint = new ArrayList<Integer>();
    public static Contexto contexto = new Contexto();

    private static void checkPoint() {
        log.log(Priority.DEBUG, "CheckPoint: [" + TokensList.getToken(TokensList.getActualToken()).getToken() + "]");
        checkPoint.add(TokensList.getActualToken());
    }

    private static void rollBack() {
        TokensList.setActualToken(checkPoint.get(checkPoint.size() - 1));
        checkPoint.remove(checkPoint.size() - 1);
        log.log(Priority.DEBUG, "rollBack: [" + TokensList.getToken(TokensList.getActualToken()).getToken() + "]");
    }

    private static boolean P_VAZIA() {
        log.log(Priority.DEBUG, "Iniciando produção P_VAZIA");
        rollBack();
        return true;
    }

    public static void error(int line, String erro) {
        MainLogger.logError("Syntax - " + "Linha: " + line + " [" + erro + "]");
    }

    public static void error_s(int line, String erro) {
        MainLogger.logError("Semantic - " + "Linha: " + line + " [" + erro + "]");
    }

    public static void removeComent() {
        for (Iterator<Token> it = TokensList.iterator(); it.hasNext();) {
            if (it.next().getType() == TypeClass.Comentarios) {
                it.remove();
            }
        }
    }
    /* Estado inicial (Produção Inicial)
     *
     * @return boolean Contendo o resultado da análise sintatica
     */

    public static boolean run() {
        contexto = new Contexto();
        Semantico.run();
        MainLogger.logInfo("[" + Helper.getTimeStamp() + "] Removendo comentários");
        removeComent();
        MainLogger.logInfo("[" + Helper.getTimeStamp() + "] Concluío");
        MainLogger.logInfo("[" + Helper.getTimeStamp() + "] Iniciando análise sintática");
        Semantico.run();
        boolean r = PROGRAMA();
        if (!r) {
            MainLogger.logError("Syntax retornou erro !");
        } else {
            MainLogger.logInfo("Syntax retornou sucesso");
        }
        MainLogger.logInfo("[" + (Helper.getTimeStamp().toString()) + "] Análise sintática concluída");
        MainLogger.logInfo("[" + (Helper.getTimeStamp().toString()) + "] Análise semântica concluída");
        return r;
    }

    public static boolean PROGRAMA() {
        TokensList.reset();
        Token t;
        if ((t = TokensList.next()) != null) {
            if ("program".equals(t.getToken())) {
                t = TokensList.next();
                if (t.getType() == TypeClass.Identificadores) {
                    return CORPO();
                } else {
                    Syntax.error(TokensList.getActualLine(), "Identificador não encontrado");
                    return false;
                }
            } else {
                Syntax.error(TokensList.getActualLine(), "Início inesperado");
                return false;
            }
        }
        return false;
    }

    public static boolean CORPO() {
        log.log(Priority.DEBUG, "Iniciando produção CORPO");
        Token t;
        contexto.escopo = new Procedimento("main");
        if (DC() && (t = TokensList.next()) != null) {
            contexto.proc.add(contexto.escopo);
            contexto.escopo = contexto.proc.get(0);
            if ("begin".equals(t.getToken()) && COMANDOS()) {
                if ((t = TokensList.next()) != null) {
                    if ("end.".equals(t.getToken())) {
                        // contexto.proc.add(contexto.escopo);
                        log.log(Priority.DEBUG, "SEMANTICO:" + Semantico.toStringSemantic());
                        return true;
                    } else {
                        error(TokensList.getActualLine(), "Esperava fim de arquivo encontrou comando!");
                        return false;
                    }
                }
            }
        } else {
            error(TokensList.getActualLine(), "Encontrou final de arquivo inesperadamente");
            return false;
        }
        return false;
    }

    public static boolean COMANDOS() {
        log.log(Priority.DEBUG, "Iniciando produção COMANDOS");
        if (COMANDO() && MAIS_COMANDOS()) {
            log.log(Priority.DEBUG, "COMANDOS: " + true);
            return true;
        } else {
            log.log(Priority.DEBUG, "COMANDOS: " + false);
            return false;
        }
    }

    public static boolean COMANDO() {
        log.log(Priority.DEBUG, "Iniciando produção COMANDO");
        Token t;
        if ((t = TokensList.next()) != null) {
            if ("read".equals(t.getToken())) {
                t = TokensList.next();
                if ("(".equals(t.getToken()) && VARIAVEIS_MEMORY(true)) {
                    t = TokensList.next();
                    if (")".equals(t.getToken())) {
                        log.log(Priority.DEBUG, "COMANDO: " + true);
                        return true;
                    } else {
                        error(TokensList.getActualLine(), "Esperava ) encontrou " + t.getToken());
                        log.log(Priority.DEBUG, "COMANDO: " + false);
                        return false;
                    }
                } else {
                    error(TokensList.getActualLine(), "Esperava ) encontrou " + t.getToken());
                    log.log(Priority.DEBUG, "COMANDO: " + false);
                    return false;
                }
            } else if ("write".equals(t.getToken())) {
                t = TokensList.next();
                if ("(".equals(t.getToken()) && VARIAVEIS_MEMORY(true)) {
                    t = TokensList.next();
                    if (")".equals(t.getToken())) {
                        log.log(Priority.DEBUG, "COMANDO: " + true);
                        return true;
                    } else {
                        error(TokensList.getActualLine(), "Esperava ) encontrou " + t.getToken());
                        log.log(Priority.DEBUG, "COMANDO: " + false);
                        return false;
                    }
                } else {
                    error(TokensList.getActualLine(), "Esperava ) encontrou " + t.getToken());
                    log.log(Priority.DEBUG, "COMANDO: " + false);
                    return false;
                }

            } else if (t.getToken().equals("if")) {
                if (CONDICAO()) {
                    t = TokensList.next();
                    if (t.getToken().equals("then") && COMANDOS()) {
                        if (PFALSA()) {
                            if ((t = TokensList.next()) != null) {
                                if (t.getToken().equals("$")) {
                                    return true;
                                } else {
                                    error(TokensList.getActualLine(), "Esperava-se $ e se encontrou " + t.getToken());
                                    return false;
                                }
                            } else {
                                error(TokensList.getActualLine(), "Esperava-se $ e se encontrou fim inesperado de comando if");
                                return false;
                            }
                        } else {
                            error(TokensList.getActualLine(), "Não forma estrutura ELSE");
                            return false;
                        }
                    } else {
                        error(TokensList.getActualLine(), "Não forma estrutura if then");
                        return false;
                    }
                } else {
                    error(TokensList.getActualLine(), "Não forma condição necessária para comparação");
                    return false;
                }
            } else if (t.getToken().equals("while")) {
                if (CONDICAO()) {
                    t = TokensList.next();
                    if (t.getToken().equals("do") && COMANDOS()) {
                        t = TokensList.next();
                        if (t.getToken().equals("$")) {
                            return true;
                        } else {
                            error(TokensList.getActualLine(), "Laço de repetição não fechado");
                            return false;
                        }
                    } else {
                        error(TokensList.getActualLine(), "Esperava do encontrei: " + TokensList.getActual().getToken());
                        return false;
                    }
                } else {
                    error(TokensList.getActualLine(), "Condição mal formada");
                    return false;
                }
            } else if (t.getType() == TypeClass.Identificadores) {

                return RESTOIDENT(t.getToken());
            } else {
                // Produção vazia
                error(TokensList.getActualLine(), "Comando inesperado encontrado");
                return false;
            }
        }
        return false;
    }

    public static boolean RESTOIDENT(String token) {
        log.log(Priority.DEBUG, "Iniciando produção RESTOIDENT");
        Token t;
        checkPoint();
        boolean r = false;
        if ((t = TokensList.next()) != null) {
            if (t.getToken().equals(":=")) {
                r = EXPRESSAO();
            } else {
                rollBack();
                r = LISTA_ARG(token);
            }
        } else {
            error(TokensList.getActualLine(), "Esperava-se := <EXPRESSAO> ou <LISTA DE ARGUMENTOS> e se encontrou fim inesperado de comando");
            r = false;
        }
        log.log(Priority.DEBUG, "RESTOIDENT: " + r);
        return r;
    }

    public static boolean EXPRESSAO() {
        log.log(Priority.DEBUG, "Iniciando produção EXPRESSAO");
        checkPoint();
        /*:::::::::::::::::::::: Parte semântica ::::::::::::::::::::::*/
        /*:::::::::::::::::::::: Identificadores ::::::::::::::::::::::*/
        Semantico.pilha_identificadores = new Stack();
        Semantico.preenchePilhaIdent(contexto);
        //  Semantico.CheckSemanticAtrib();
        /*:::::::::::::::::::::: Fim da Parte semântica ::::::::::::::::::::::*/


        if (TERMO() && OUTROS_TERMOS()) {
            log.log(Priority.DEBUG, "EXPRESSAO: " + true);
            Semantico.pilha_expressao = new Stack();
            return true;
        } else {

            error(TokensList.getActualLine(), "Esperava-se <TERMOS> <OUTROS TERMOS> e se encontrou fim inesperado de expressão");
            log.log(Priority.DEBUG, "EXPRESSAO: " + false);
            rollBack();
            return false;
        }
    }

    public static boolean OP_UN() {
        log.log(Priority.DEBUG, "Iniciando produção OP_UN");
        Token t;
        checkPoint();
        if ((t = TokensList.next()) != null) {
            if (t.getToken().equals("+") || t.getToken().equals("-")) {
                log.log(Priority.DEBUG, "OP_UN: " + true);
                return true;
            } else {
                return P_VAZIA();
            }
        } else {
            log.log(Priority.DEBUG, "OP_UN: " + false);
            return false;
        }
    }

    public static boolean TERMO() {
        log.log(Priority.DEBUG, "Iniciando producao TERMO");
        if (OP_UN()) {
            if (FATOR()) {
                if (MAIS_FATORES()) {
                    log.log(Priority.DEBUG, "TERMO: " + true);
                    return true;
                } else {
                    log.log(Priority.DEBUG, "TERMO: " + false);
                    return false;
                }
            } else {
                error(TokensList.getActualLine(), " Token " + TokensList.getActual().getToken() + " não forma expressão");
                log.log(Priority.DEBUG, "TERMO: " + false);
                return false;
            }
        } else {
            log.log(Priority.DEBUG, "TERMO: " + false);
            return false;
        }

    }

    public static boolean FATOR() {
        log.log(Priority.DEBUG, "Iniciando produção FATOR");
        Token t;
        if ((t = TokensList.next()) != null) {
            if (t.getType() == TypeClass.Identificadores || t.getType() == TypeClass.InteirosSemSinal || t.getType() == TypeClass.NumerosReais) {
                log.log(Priority.DEBUG, "FATOR: " + true);

                /*:::::::::::::::::::::: Parte semântica ::::::::::::::::::::::*/
                /*:::::::::::::::::::::: Atribuição e Expressões ::::::::::::::::::::::*/
                Semantico.preenchePilha(Semantico.pilha_expressao, contexto);
                Semantico.CheckSemanticExp();
                /*:::::::::::::::::::::: Fim da Parte semântica ::::::::::::::::::::::*/

                return true;
            } else if (t.getToken().equals("(")) {
                if (EXPRESSAO()) {
                    t = TokensList.next();
                    if (t.getToken().equals(")")) {
                        log.log(Priority.DEBUG, "FATOR: " + true);
                        return true;
                    } else {
                        // error(TokensList.getActualLine(), "Falha na declaração de Expressão, esperava ) e encontrou " + t.getToken());
                        log.log(Priority.DEBUG, "FATOR: " + false);
                        return false;
                    }
                } else {
                    log.log(Priority.DEBUG, "FATOR: " + false);
                    return false;
                }
            } else {
                log.log(Priority.DEBUG, "FATOR: " + false);
                return false;
            }
        } else {
            log.log(Priority.DEBUG, "FATOR: " + false);
            return false;
        }
    }

    public static boolean MAIS_FATORES() {
        log.log(Priority.DEBUG, "Iniciando produção MAIS_FATORES");
        checkPoint();
        if (OP_MUL() && FATOR() && MAIS_FATORES()) {
            log.log(Priority.DEBUG, "MAIS_FATORES: " + true);
            return true;
        } else {
            return P_VAZIA();
        }
    }

    public static boolean OP_MUL() {
        log.log(Priority.DEBUG, "Iniciando produção OP_MUL");
        Token t;
        if ((t = TokensList.next()) != null) {
            if (t.getToken().equals("*") || t.getToken().equals("/")) {
                log.log(Priority.DEBUG, "OP_MUL: " + true);
                return true;
            } else {
                TokensList.prev();
                log.log(Priority.DEBUG, "OP_MUL: " + false);
                return false;
            }
        } else {
            error(TokensList.getActualLine(), "Esperava-se * ou / e se encontrou fim inesperado de operador");
            log.log(Priority.DEBUG, "OP_MUL: " + false);
            return false;
        }

    }

    public static boolean OUTROS_TERMOS() {
        log.log(Priority.DEBUG, "Iniciando produção OUTROS_TERMOS");
        checkPoint();
        if (OP_AD() && TERMO() && OUTROS_TERMOS()) {
            log.log(Priority.DEBUG, "OUTROS_TERMOS: " + true);
            return true;
        }
        return P_VAZIA();
    }

    public static boolean OP_AD() {
        log.log(Priority.DEBUG, "Iniciando produção OP_AD");
        Token t;
        if ((t = TokensList.next()) != null) {
            if (t.getToken().equals("+") || t.getToken().equals("-")) {
                log.log(Priority.DEBUG, "OP_AD: " + true);
                return true;
            } else {
                TokensList.prev();
                log.log(Priority.DEBUG, "OP_AD: " + false);
                return false;
            }
        } else {
            log.log(Priority.DEBUG, "OP_AD: " + false);
            return false;
        }
    }

    public static boolean CONDICAO() {
        log.log(Priority.DEBUG, "Iniciando produção CONDICAO");
        checkPoint();
        if (EXPRESSAO()) {
            if (RELACAO()) {
                if (EXPRESSAO()) {
                    return true;
                } else {
                    rollBack();
                    return false;
                }
            } else {
                error(TokensList.getActualLine(), "Esperava-se = ou <> ou >= ou <= ou > ou < e se encontrou " + TokensList.getActual().getToken());
                rollBack();
                return false;
            }
        } else {
            error(TokensList.getActualLine(), "Expressão formada não corresponde com o esperado");
            rollBack();
            return false;
        }
    }

    public static boolean RELACAO() {
        log.log(Priority.DEBUG, "Iniciando produção RELACAO");
        Token t;
        boolean r = false;
        if ((t = TokensList.next()) != null) {
            if (t.getToken().equals("=") || t.getToken().equals("<>") || t.getToken().equals(">=") || t.getToken().equals("<=") || t.getToken().equals(">") || t.getToken().equals("<")) {
                r = true;
            } else {
                TokensList.prev();

            }
        } else {
            error(TokensList.getActualLine(), "Esperava-se = ou <> ou >= ou <= ou > ou < e se encontrou fim inesperado de relação");
            r = false;
        }
        return r;
    }

    public static boolean MAIS_COMANDOS() {
        log.log(Priority.DEBUG, "Iniciando produção MAIS_COMANDOS");
        Token t;
        checkPoint();
        if ((t = TokensList.next()) != null) {
            if (";".equals(t.getToken()) && COMANDOS()) {
                log.log(Priority.DEBUG, "MAIS_COMANDOS: " + true);
                return true;
            } else {
                return P_VAZIA();
            }
        } else {
            error(TokensList.getActualLine(), "Encontrou final de arquivo inesperadamente");
            log.log(Priority.DEBUG, "MAIS_COMANDOS: " + false);
            return false;
        }
    }

    public static boolean DC() {
        log.log(Priority.DEBUG, "Iniciando DC");
        Syntax.checkPoint();
        if (DC_V() && MAIS_DC()) {
            log.log(Priority.DEBUG, "DC: " + true);
            return true;
        } else {
            rollBack();
            checkPoint();
            if (DC_P() && MAIS_DC()) {
                log.log(Priority.DEBUG, "DC: " + true);
                return true;
            } else {
                log.log(Priority.DEBUG, "DC: " + true);
                return Syntax.P_VAZIA();
            }
        }
    }

    public static boolean MAIS_DC() {
        log.log(Priority.DEBUG, "Iniciado produção MAIS_DC");
        Token t;
        checkPoint();
        if ((t = TokensList.next()) != null) {
            if (";".equals(t.getToken()) && DC()) {
                log.log(Priority.DEBUG, "MAIS_DC: " + true);
                return true;
            } else {
                log.log(Priority.DEBUG, "MAIS_DC: " + true);
                return P_VAZIA();
            }
        }
        log.log(Priority.DEBUG, "MAIS_DC: " + false);
        return false;
    }

    public static boolean DC_P() {
        log.log(Priority.DEBUG, "Iniciando DC_P");
        Token t;
        if ((t = TokensList.next()) != null) {
            if (t.getToken().equals("procedure")) {
                t = TokensList.next();
                if (t.getType() == TypeClass.Identificadores) {
                    contexto.proc.add(contexto.escopo);
                    contexto.escopo = new Procedimento(t.getToken());
                    if (PARAMETROS() && CORPO_P()) {
                        log.log(Priority.DEBUG, "DC_P:" + true);
                        return true;
                    } else {
                        log.log(Priority.DEBUG, "DC_P:" + false);
                        return false;
                    }
                } else {
                    TokensList.prev();
                    error(TokensList.getActualLine(), "Esperava-se Identificador e se encontrou " + t.getToken());
                    log.log(Priority.DEBUG, "DC_P:" + false);
                    return false;
                }
            } else {
                TokensList.prev();
                log.log(Priority.DEBUG, "DC_P:" + false);
                return false;
            }
        } else {
            error(TokensList.getActualLine(), "Encontrou final de procedimento inesperadamente");
            log.log(Priority.DEBUG, "DC_P:" + false);
            return false;
        }
    }

    public static boolean PARAMETROS() {
        log.log(Priority.DEBUG, "Iniciando produção PARAMETROS");
        Token t;
        checkPoint();
        if ((t = TokensList.next()) != null) {
            temp_var = new ArrayList<Var>();
            if (t.getToken().equals("(") && LISTA_PAR()) {
                t = TokensList.next();
                if (")".equals(t.getToken())) {
                    log.log(Priority.DEBUG, "PARAMETROS:" + true);
                    contexto.escopo.add_par(temp_var);
                    contexto.proc.add(contexto.escopo);
                    return true;
                } else {
                    error(TokensList.getActualLine(), "Falha na declaração de parametros");
                    log.log(Priority.DEBUG, "PARAMETROS:" + false);
                    return P_VAZIA();
                }
            } else {
                log.log(Priority.DEBUG, "PARAMETROS:" + true);
                return P_VAZIA();
            }

        }
        log.log(Priority.DEBUG, "PARAMETROS:" + false);
        return false;
    }

    public static boolean LISTA_PAR() {
        log.log(Priority.DEBUG, "Iniciando produção LISTA_PAR");
        Token t = TokensList.getToken(TokensList.getActualToken());
        contexto.escopo.add_par(temp_var);
        temp_var = new ArrayList<Var>();
        if (VARIAVEIS()) {
            t = TokensList.next();
            if (":".endsWith(t.getToken())) {
                if (TIPO_VAR()) {
                    boolean r = MAIS_PAR();
                    log.log(Priority.DEBUG, "LISTA_PAR:" + r);
                    return r;
                } else {
                    error(TokensList.getActualLine(), "Falha na declaração de tipo");
                    log.log(Priority.DEBUG, "LISTA_PAR:" + false);
                    return false;
                }
            } else {
                error(TokensList.getActualLine(), "Tipo não declarado corretamente");
                log.log(Priority.DEBUG, "LISTA_PAR:" + false);
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean MAIS_PAR() {
        log.log(Priority.DEBUG, "Iniciando produção MAIS_PAR");
        Token t;
        checkPoint();
        if ((t = TokensList.next()) != null) {
            if (t.getToken().equals(";") && LISTA_PAR()) {
                return true;
            } else {
                return P_VAZIA();
            }
        }
        return false;
    }

    public static boolean CORPO_P() {
        log.log(Priority.DEBUG, "Iniciando produção CORPO_P");
        Token t;
        boolean r = false;
        // TRata a declaração de variáveis
        r = DC_LOC();
        if ((t = TokensList.next()) != null) {
            if (r && t.getToken().equals("begin")) {
                if (COMANDOS() && ((t = TokensList.next()) != null)) {
                    if (t.getToken().equals("end")) {
                        log.log(Priority.DEBUG, "CORPO_P:" + true);
                        return true;
                    } else {
                        TokensList.prev();
                        error(TokensList.getActualLine(), "Encontrou fim inesperado " + t.getToken());
                        log.log(Priority.DEBUG, "CORPO_P:" + false);
                        return false;
                    }
                } else {
                    error(TokensList.getActualLine(), "Esperava-se end e se encontrou fim inesperado de corpo de procedimento!");
                    log.log(Priority.DEBUG, "CORPO_P:" + false);
                    return false;
                }
            } else {
                error(TokensList.getActualLine(), "Esperava-se begin e se encontrou " + t.getToken());
                log.log(Priority.DEBUG, "CORPO_P:" + false);
                return false;
            }

        }
        log.log(Priority.DEBUG, "CORPO_P:" + false);
        return false;
    }

    public static boolean DC_LOC() {
        log.log(Priority.DEBUG, "Iniciando DC_LOC");
        checkPoint();
        if (DC_V() && MAIS_DCLOC()) {
            log.log(Priority.DEBUG, "Erro na arvore <DC_V> <DC_LOC");
            return true;
        } else {
            return P_VAZIA();
        }
    }

    public static boolean MAIS_DCLOC() {
        log.log(Priority.DEBUG, "Iniciando produção MAIS_DCLOC");
        Token t;
        boolean r = false;
        if ((t = TokensList.next()) != null) {
            if (t.getToken().equals(";")) {
                r = DC_LOC();
            } else {
                TokensList.prev();
                error(TokensList.getActualLine(), "Esperava-se ; e se encontrou " + t.getToken());
                r = false;
            }
        } else {
            r = true; //Admite-se aqui o caracter nulo
        }
        return r;
    }

    public static boolean LISTA_ARG(String token) {
        int a = 10 - -1;
        log.log(Priority.DEBUG, "Iniciando produção LISTA_ARG");
        Token t;
        boolean r = false;

        checkPoint();

        /*:::::::::::::::::::::: Parte semântica ::::::::::::::::::::::*/
        Semantico.preenchePilhaProc(contexto);

        /*:::::::::::::::::::::: Fim da Parte semântica ::::::::::::::::::::::*/
        if ((t = TokensList.next()) != null) {
            if (t.getToken().equals("(") && ARGUMENTOS(token)) {
                t = TokensList.next();
                if (t.getToken().equals(")")) {
                    log.log(Priority.DEBUG, "LISTA_ARG:" + true);
                    //    Token special = new Token(TokensList.getActualLine(), "$", TypeClass.SimbolosEspeciais);
                    //     Semantico.pilha_par_reais_proc_usuario.push(special);
                    Semantico.CheckSemanticParReaisUsr(contexto);
                    return true;
                } else {
                    return P_VAZIA();
                }
            } else {

                Semantico.CheckSemanticParReaisUsr(contexto);
                return P_VAZIA();
            }
        } else {
            log.log(Priority.DEBUG, "LISTA_ARG:" + false);
            return false;
        }
    }

    public static boolean ARGUMENTOS(String token) {
        log.log(Priority.DEBUG, "Iniciando produção ARGUMENTOS");
        Token t;
        boolean r = false;
        checkPoint();
        if ((t = TokensList.next()) != null) {
            if (t.getType() == TypeClass.Identificadores) {
                //t = TokensList.next();

                /*:::::::::::::::::::::: Parte semântica ::::::::::::::::::::::*/
                /*:::::::::::::::::::::: Variáveis e Parâmetros Formais transferidos como Parâmetros Reais das Funções do Usuário ::::::::::::::::::::::*/

                Procedimento proc = contexto.request_proc(token);
                if (proc.ident != null) {
                    Semantico.preenchePilha(Semantico.pilha_par_reais_proc_usuario, contexto);
                }

                /*:::::::::::::::::::::: Fim da Parte semântica ::::::::::::::::::::::*/


                log.log(Priority.DEBUG, "ARGUMENTOS:" + true);
                r = MAIS_IDENT(token);
            } else {
                rollBack();
                error(TokensList.getActualLine(), "Esperava-se Identificador e se encontrou " + t.getToken());
                log.log(Priority.DEBUG, "ARGUMENTOS:" + false);
                r = false;
            }
        } else {
            error(TokensList.getActualLine(), "Esperava-se Identificador e se encontrou fim inesperado de argumento!");
            log.log(Priority.DEBUG, "ARGUMENTOS:" + false);
            r = false;
        }
        return r;
    }

    public static boolean MAIS_IDENT(String token) {
        log.log(Priority.DEBUG, "Iniciando produção MAIS_IDENT");
        Token t;
        checkPoint();
        if ((t = TokensList.next()) != null) {
            if (t.getToken().equals(";") && ARGUMENTOS(token)) {
                log.log(Priority.DEBUG, "MAIS_IDENT: " + true);
                return true;
            } else {
                return P_VAZIA();
            }
        }
        log.log(Priority.DEBUG, "MAIS_IDENT:" + false);
        return false;
    }

    public static boolean PFALSA() {
        log.log(Priority.DEBUG, "Iniciando produção PFALSA");
        Token t;
        checkPoint();
        if ((t = TokensList.next()) != null) {
            if (t.getToken().equals("else") && COMANDOS()) {
                log.log(Priority.DEBUG, "PFALSA: " + true);
                return true;
            } else {
                return P_VAZIA();
            }
        } else {
            return false;
        }
    }

    /* Metodo para tratar as declarações de variável
     *
     * @return boolean resposta da arvore sintatica
     */
    private static List<Var> temp_var = new ArrayList<Var>();

    public static boolean DC_V() {
        log.log(Priority.DEBUG, "Iniciando DC_V");
        Token t;
        if ((t = TokensList.next()) != null) {
            if ("var".equals(t.getToken())) {
                temp_var = new ArrayList<Var>();
                if (VARIAVEIS()) {
                    t = TokensList.next();
                    if (t.getToken().equals(":")) {
                        boolean r = TIPO_VAR();
                        log.log(Priority.DEBUG, "DC_V:" + r);
                        if (r) {
                            contexto.escopo.add_var(temp_var);
                        }
                        return r;
                    } else {
                        TokensList.prev();
                        log.log(Priority.DEBUG, "DC_V:" + true);
                        return true;
                    }
                } else {
                    log.log(Priority.DEBUG, "DC_V:" + false);
                    return false;
                }
            } else {
                TokensList.prev();
                log.log(Priority.DEBUG, "DC_V:" + false);
                return false;
            }
        }
        return false;
    }

    /* Metodo para tratar as declarações de variável, consome os identificadores
     *
     * @return boolean resposta da arvore sintatica
     */
    public static boolean VARIAVEIS() {
        log.log(Priority.DEBUG, "Iniciando produção VARIAVEIS");
        Token t;
        if ((t = TokensList.next()) != null) {
            if (t.getType() == TypeClass.Identificadores) {
                temp_var.add(new Var(t.getToken()));
                boolean r = MAIS_VAR();
                log.log(Priority.DEBUG, "VARIAVEIS:" + r);
                return r;
            } else {
                TokensList.prev();
                error(TokensList.getActualLine(), "Problema na declaração de variáveis");
                log.log(Priority.DEBUG, "VARIAVEIS:" + false);
                return false;
            }
        }
        log.log(Priority.DEBUG, "VARIAVEIS:" + false);
        return false;
    }

    /* Metodo para tratar as declarações de variável (identifica cadeia de identificadores)
     *
     * @return boolean resposta da arvore sintatica
     */
    public static boolean MAIS_VAR() {
        log.log(Priority.DEBUG, "Iniciando produção MAIS_VAR");
        Token t;
        checkPoint();
        if ((t = TokensList.next()) != null) {
            if (t.getToken().equals(",")) {
                boolean r = VARIAVEIS();
                log.log(Priority.DEBUG, "MAIS_VAR:" + r);
                return r;
            } else {
                log.log(Priority.DEBUG, "MAIS_VAR:" + true);
                return P_VAZIA();
            }
        }
        log.log(Priority.DEBUG, "MAIS_VAR:" + false);
        return false;
    }

    public static boolean VARIAVEIS_MEMORY(boolean memory) {
        log.log(Priority.DEBUG, "Iniciando produção VARIAVEIS");
        Token t;
        if ((t = TokensList.next()) != null) {
            if (t.getType() == TypeClass.Identificadores) {
                temp_var.add(new Var(t.getToken()));

                /*:::::::::::::::::::::: Parte semântica ::::::::::::::::::::::*/
                /*:::::::::::::::::::::: Variáveis e Parâmetros Formais transferidos como Parâmetros Reais das Funções de Sistema ::::::::::::::::::::::*/
                if (memory) {
                    Semantico.preenchePilha(Semantico.pilha_par_reais_proc_sistema, contexto);
                }
                /*:::::::::::::::::::::: Fim da Parte semântica ::::::::::::::::::::::*/
                boolean r = MAIS_VAR_MEMORY(memory);
                log.log(Priority.DEBUG, "VARIAVEIS:" + r);
                return r;
            } else {
                TokensList.prev();
                error(TokensList.getActualLine(), "Problema na declaração de variáveis");
                log.log(Priority.DEBUG, "VARIAVEIS:" + false);
                return false;
            }
        }
        log.log(Priority.DEBUG, "VARIAVEIS:" + false);
        return false;
    }

    /* Metodo para tratar as declarações de variável (identifica cadeia de identificadores)
     *
     * @return boolean resposta da arvore sintatica
     */
    public static boolean MAIS_VAR_MEMORY(boolean memory) {
        log.log(Priority.DEBUG, "Iniciando produção MAIS_VAR");
        Token t;
        checkPoint();
        if ((t = TokensList.next()) != null) {
            if (t.getToken().equals(",")) {
                boolean r = VARIAVEIS_MEMORY(memory);
                log.log(Priority.DEBUG, "MAIS_VAR:" + r);
                return r;
            } else {
                log.log(Priority.DEBUG, "MAIS_VAR:" + true);
                return P_VAZIA();
            }
        }
        log.log(Priority.DEBUG, "MAIS_VAR:" + false);
        return false;
    }

    public static boolean TIPO_VAR() {
        log.log(Priority.DEBUG, "Iniciando produção TIPO_VAR");
        Token t;
        if ((t = TokensList.next()) != null) {
            if (t.getToken().equals("real") || t.getToken().equals("integer")) {
                log.log(Priority.DEBUG, "TIPO_VAR:" + true);
                contexto.to_type_var(t.getToken(), temp_var);
                return true;
            } else {
                TokensList.prev();
                error(TokensList.getActualLine(), "Tipo encontrado não existe");
                log.log(Priority.DEBUG, "TIPO_VAR:" + false);
                return false;
            }
        }
        return false;
    }
}
