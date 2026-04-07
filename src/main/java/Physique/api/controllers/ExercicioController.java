package Physique.api.controllers;

import Physique.api.assemblers.ExercicioModelAssembler;
import Physique.api.entities.Exercicio;
import Physique.api.exceptions.ExercicioNaoEncontradoExcecao;
import Physique.api.repositories.ExercicioRepository;
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
@RequestMapping("/exercicios")
public class ExercicioController {

    private final ExercicioRepository repository;
    private final ExercicioModelAssembler assembler;
    private final PagedResourcesAssembler<Exercicio> pagedAssembler;

    public ExercicioController(ExercicioRepository repository,
                               ExercicioModelAssembler assembler,
                               PagedResourcesAssembler<Exercicio> pagedAssembler) {
        this.repository = repository;
        this.assembler = assembler;
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping
    public PagedModel<EntityModel<Exercicio>> listar(Pageable pageable) {
        return pagedAssembler.toModel(repository.findAll(pageable), assembler);
    }

    @GetMapping("/{id}")
    public EntityModel<Exercicio> buscarPorId(@PathVariable Long id) {
        Exercicio entity = repository.findById(id)
                .orElseThrow(() -> new ExercicioNaoEncontradoExcecao(id));
        return assembler.toModel(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EntityModel<Exercicio>> criar(@Valid @RequestBody Exercicio body) {
        Exercicio salvo = repository.save(body);
        return ResponseEntity.created(URI.create("/exercicios/" + salvo.getId())).body(assembler.toModel(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Exercicio>> atualizar(@PathVariable Long id, @Valid @RequestBody Exercicio body) {
        return repository.findById(id)
                .map(existente -> {
                    existente.setNome(body.getNome());
                    existente.setRepeticoes(body.getRepeticoes());
                    existente.setQuantidadeSeries(body.getQuantidadeSeries());
                    existente.setGrupoMuscular(body.getGrupoMuscular());
                    existente.setMusculo(body.getMusculo());
                    existente.setDescricao(body.getDescricao());
                    existente.setVideo(body.getVideo());
                    Exercicio salvo = repository.save(existente);
                    return ResponseEntity.ok(assembler.toModel(salvo));
                })
                .orElseThrow(() -> new ExercicioNaoEncontradoExcecao(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new ExercicioNaoEncontradoExcecao(id);
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    public PagedModel<EntityModel<Exercicio>> buscarPorNome(@RequestParam String nome, Pageable pageable) {
        return pagedAssembler.toModel(repository.findByNomeContainingIgnoreCase(nome, pageable), assembler);
    }
}
