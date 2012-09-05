package Motor.automatos;

/**
 *
 * @author Gustavo Liberatti , Rodicrisller Rodrigues
 * @version 1.0
 *
 */
public enum TypeClass {

    UNDEFINED(-1),
    ERRO(0),
    Identificadores(1),
    PalavrasReservadas(2),
    InteirosSemSinal(3),
    SimbolosEspeciais(4),
    Comentarios(5),
    NumerosReais(6),
    SimbolosDuplos(7);
    private int id;

    private TypeClass(int id) {
        this.id = id;
    }
}
