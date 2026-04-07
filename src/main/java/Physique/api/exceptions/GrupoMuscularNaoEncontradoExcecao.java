package Physique.api.exceptions;

public class GrupoMuscularNaoEncontradoExcecao extends RuntimeException {
    public GrupoMuscularNaoEncontradoExcecao(Long id) {
        super("Could not find GrupoMuscular with id: " + id);
    }
}
