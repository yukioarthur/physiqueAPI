package Physique.api.exceptions;

public class ExercicioNaoEncontradoExcecao extends RuntimeException {
    public ExercicioNaoEncontradoExcecao(Long id) {
        super("Could not find Exercicio with id: " + id);
    }
}
