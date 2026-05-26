package senac.tsi.physique.exceptions;

public class ExercicioNotFoundException extends RuntimeException {

    public ExercicioNotFoundException(Long id) {
        super("Could not find exercicio " + id);
    }
}
