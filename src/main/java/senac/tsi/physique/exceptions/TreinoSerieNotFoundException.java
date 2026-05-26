package senac.tsi.physique.exceptions;

public class TreinoSerieNotFoundException extends RuntimeException {

    public TreinoSerieNotFoundException(Long id) {
        super("Could not find treino serie " + id);
    }
}
