package senac.tsi.physique.exceptions;

public class GrupoMuscularNotFoundException extends RuntimeException {

    public GrupoMuscularNotFoundException(Long id) {
        super("Could not find grupo muscular " + id);
    }
}
