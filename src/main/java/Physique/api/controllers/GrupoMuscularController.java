package Physique.api.controllers;

import Physique.api.assemblers.GrupoMuscularModelAssembler;
import Physique.api.entities.GrupoMuscular;
import Physique.api.exceptions.GrupoMuscularNaoEncontradoExcecao;
import Physique.api.repositories.GrupoMuscularRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Grupos Musculares", description = "Operações CRUD de grupos musculares com HATEOAS.")
@RestController
@RequestMapping("/grupos-musculares")
public class GrupoMuscularController {

    private final GrupoMuscularRepository repository;
    private final GrupoMuscularModelAssembler assembler;
    private final PagedResourcesAssembler<GrupoMuscular> pagedAssembler;

    public GrupoMuscularController(GrupoMuscularRepository repository,
                                   GrupoMuscularModelAssembler assembler,
                                   PagedResourcesAssembler<GrupoMuscular> pagedAssembler) {
        this.repository = repository;
        this.assembler = assembler;
        this.pagedAssembler = pagedAssembler;
    }

    @Operation(summary = "Listar grupos musculares")
    @GetMapping
    public PagedModel<EntityModel<GrupoMuscular>> listar(Pageable pageable) {
        return pagedAssembler.toModel(repository.findAll(pageable), assembler);
    }

    @Operation(summary = "Buscar grupo muscular por id")
    @GetMapping("/{id}")
    public EntityModel<GrupoMuscular> buscarPorId(@PathVariable Long id) {
        GrupoMuscular entity = repository.findById(id)
                .orElseThrow(() -> new GrupoMuscularNaoEncontradoExcecao(id));
        return assembler.toModel(entity);
    }

    @Operation(summary = "Criar grupo muscular")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EntityModel<GrupoMuscular>> criar(@Valid @RequestBody GrupoMuscular body) {
        GrupoMuscular salvo = repository.save(body);
        return ResponseEntity.created(URI.create("/grupos-musculares/" + salvo.getId()))
                .body(assembler.toModel(salvo));
    }

    @Operation(summary = "Atualizar grupo muscular")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<GrupoMuscular>> atualizar(@PathVariable Long id, @Valid @RequestBody GrupoMuscular body) {
        return repository.findById(id)
                .map(existente -> {
                    existente.setNome(body.getNome());
                    GrupoMuscular salvo = repository.save(existente);
                    return ResponseEntity.ok(assembler.toModel(salvo));
                })
                .orElseThrow(() -> new GrupoMuscularNaoEncontradoExcecao(id));
    }

    @Operation(summary = "Deletar grupo muscular")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new GrupoMuscularNaoEncontradoExcecao(id);
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Pesquisar grupos musculares por nome")
    @GetMapping("/buscar")
    public PagedModel<EntityModel<GrupoMuscular>> buscarPorNome(@RequestParam String nome, Pageable pageable) {
        return pagedAssembler.toModel(repository.findByNomeContainingIgnoreCase(nome, pageable), assembler);
    }
}
