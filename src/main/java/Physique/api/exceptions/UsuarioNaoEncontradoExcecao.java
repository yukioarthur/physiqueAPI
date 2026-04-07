package Physique.api.exceptions;

public class UsuarioNaoEncontradoExcecao extends RuntimeException {
    public UsuarioNaoEncontradoExcecao(Long id) {
        super("Could not find Usuario with id: " + id);
    }
}
