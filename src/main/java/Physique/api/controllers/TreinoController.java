package Physique.api.controllers;

import Physique.api.assemblers.TreinoModelAssembler;
import Physique.api.entities.Treino;
import Physique.api.exceptions.TreinoNaoEncontradoExcecao;
import Physique.api.repositories.TreinoRepository;
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
@RequestMapping("/treinos")
public class TreinoController {

    private final TreinoRepository repository;
    private final TreinoModelAssembler assembler;
    private final PagedResourcesAssembler<Treino> pagedAssembler;

    public TreinoController(TreinoRepository repository,
                            TreinoModelAssembler assembler,
                            PagedResourcesAssembler<Treino> pagedAssembler) {
        this.repository = repository;
        this.assembler = assembler;
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping
    public PagedModel<EntityModel<Treino>> listar(Pageable pageable) {
        return pagedAssembler.toModel(repository.findAll(pageable), assembler);
    }

    @GetMapping("/{id}")
    public EntityModel<Treino> buscarPorId(@PathVariable Long id) {
        Treino entity = repository.findById(id)
                .orElseThrow(() -> new TreinoNaoEncontradoExcecao(id));
        return assembler.toModel(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EntityModel<Treino>> criar(@Valid @RequestBody Treino body) {
        Treino salvo = repository.save(body);
        return ResponseEntity.created(URI.create("/treinos/" + salvo.getId())).body(assembler.toModel(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Treino>> atualizar(@PathVariable Long id, @Valid @RequestBody Treino body) {
        return repository.findById(id)
                .map(existente -> {
                    existente.setNome(body.getNome());
                    existente.setObjetivo(body.getObjetivo());
                    existente.setMetodologia(body.getMetodologia());
                    existente.setExercicios(body.getExercicios());
                    Treino salvo = repository.save(existente);
                    return ResponseEntity.ok(assembler.toModel(salvo));
                })
                .orElseThrow(() -> new TreinoNaoEncontradoExcecao(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new TreinoNaoEncontradoExcecao(id);
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar-por-metodologia")
    public PagedModel<EntityModel<Treino>> buscarPorMetodologia(@RequestParam String metodologia, Pageable pageable) {
        return pagedAssembler.toModel(repository.findByMetodologiaContainingIgnoreCase(metodologia, pageable), assembler);
    }
}
