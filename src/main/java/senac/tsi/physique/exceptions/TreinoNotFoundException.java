package senac.tsi.physique.exceptions;

public class TreinoNotFoundException extends RuntimeException {

    public TreinoNotFoundException(Long id) {
        super("Could not find treino " + id);
    }
}
