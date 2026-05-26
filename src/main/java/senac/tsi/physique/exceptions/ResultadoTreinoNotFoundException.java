package senac.tsi.physique.exceptions;

public class ResultadoTreinoNotFoundException extends RuntimeException {

    public ResultadoTreinoNotFoundException(Long id) {
        super("Could not find resultado treino " + id);
    }
}
