package senac.tsi.physique.exceptions;

public class MusculoNotFoundException extends RuntimeException {

    public MusculoNotFoundException(Long id) {
        super("Could not find musculo " + id);
    }
}
