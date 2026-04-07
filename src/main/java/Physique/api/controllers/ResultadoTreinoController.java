package Physique.api.controllers;

import Physique.api.assemblers.ResultadoTreinoModelAssembler;
import Physique.api.entities.ResultadoTreino;
import Physique.api.exceptions.ResultadoTreinoNaoEncontradoExcecao;
import Physique.api.repositories.ResultadoTreinoRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/resultados-treino")
public class ResultadoTreinoController {

    private final ResultadoTreinoRepository repository;
    private final ResultadoTreinoModelAssembler assembler;
    private final PagedResourcesAssembler<ResultadoTreino> pagedAssembler;

    public ResultadoTreinoController(ResultadoTreinoRepository repository,
                                     ResultadoTreinoModelAssembler assembler,
                                     PagedResourcesAssembler<ResultadoTreino> pagedAssembler) {
        this.repository = repository;
        this.assembler = assembler;
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping
    public PagedModel<EntityModel<ResultadoTreino>> listar(Pageable pageable) {
        return pagedAssembler.toModel(repository.findAll(pageable), assembler);
    }

    @GetMapping("/{id}")
    public EntityModel<ResultadoTreino> buscarPorId(@PathVariable Long id) {
        ResultadoTreino entity = repository.findById(id)
                .orElseThrow(() -> new ResultadoTreinoNaoEncontradoExcecao(id));
        return assembler.toModel(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EntityModel<ResultadoTreino>> criar(@Valid @RequestBody ResultadoTreino body) {
        calcularPesoRecomendado(body);
        ResultadoTreino salvo = repository.save(body);
        return ResponseEntity.created(URI.create("/resultados-treino/" + salvo.getId())).body(assembler.toModel(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ResultadoTreino>> atualizar(@PathVariable Long id, @Valid @RequestBody ResultadoTreino body) {
        return repository.findById(id)
                .map(existente -> {
                    existente.setTreino(body.getTreino());
                    existente.setUsuario(body.getUsuario());
                    existente.setData(body.getData());
                    existente.setListaSerieRepeticao(body.getListaSerieRepeticao());
                    existente.setQuantidadeSeriesTreino(body.getQuantidadeSeriesTreino());
                    existente.setPesoAnterior(body.getPesoAnterior());
                    calcularPesoRecomendado(existente);
                    ResultadoTreino salvo = repository.save(existente);
                    return ResponseEntity.ok(assembler.toModel(salvo));
                })
                .orElseThrow(() -> new ResultadoTreinoNaoEncontradoExcecao(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new ResultadoTreinoNaoEncontradoExcecao(id);
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/por-usuario/{usuarioId}")
    public PagedModel<EntityModel<ResultadoTreino>> buscarPorUsuario(@PathVariable Long usuarioId, Pageable pageable) {
        return pagedAssembler.toModel(repository.findByUsuarioId(usuarioId, pageable), assembler);
    }

    private void calcularPesoRecomendado(ResultadoTreino body) {
        body.setPesoRecomendado(arredondar(body.getPesoAnterior() * 0.9));
    }

    private double arredondar(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
