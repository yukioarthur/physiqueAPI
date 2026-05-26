package senac.tsi.physique.exceptions;

public class UsuarioNotFoundException extends RuntimeException {

    public UsuarioNotFoundException(Long id) {
        super("Could not find usuario " + id);
    }
}
