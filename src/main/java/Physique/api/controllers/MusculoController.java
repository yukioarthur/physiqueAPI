package Physique.api.controllers;

import Physique.api.assemblers.MusculoModelAssembler;
import Physique.api.entities.Musculo;
import Physique.api.exceptions.MusculoNaoEncontradoExcecao;
import Physique.api.repositories.MusculoRepository;
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

@Tag(name = "Músculos", description = "Operações CRUD de músculos com HATEOAS.")
@RestController
@RequestMapping("/musculos")
public class MusculoController {

    private final MusculoRepository repository;
    private final MusculoModelAssembler assembler;
    private final PagedResourcesAssembler<Musculo> pagedAssembler;

    public MusculoController(MusculoRepository repository,
                             MusculoModelAssembler assembler,
                             PagedResourcesAssembler<Musculo> pagedAssembler) {
        this.repository = repository;
        this.assembler = assembler;
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping
    public PagedModel<EntityModel<Musculo>> listar(Pageable pageable) {
        return pagedAssembler.toModel(repository.findAll(pageable), assembler);
    }

    @GetMapping("/{id}")
    public EntityModel<Musculo> buscarPorId(@PathVariable Long id) {
        Musculo entity = repository.findById(id)
                .orElseThrow(() -> new MusculoNaoEncontradoExcecao(id));
        return assembler.toModel(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EntityModel<Musculo>> criar(@Valid @RequestBody Musculo body) {
        Musculo salvo = repository.save(body);
        return ResponseEntity.created(URI.create("/musculos/" + salvo.getId())).body(assembler.toModel(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Musculo>> atualizar(@PathVariable Long id, @Valid @RequestBody Musculo body) {
        return repository.findById(id)
                .map(existente -> {
                    existente.setNome(body.getNome());
                    existente.setGrupoMuscular(body.getGrupoMuscular());
                    Musculo salvo = repository.save(existente);
                    return ResponseEntity.ok(assembler.toModel(salvo));
                })
                .orElseThrow(() -> new MusculoNaoEncontradoExcecao(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new MusculoNaoEncontradoExcecao(id);
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/por-grupo/{grupoMuscularId}")
    public PagedModel<EntityModel<Musculo>> buscarPorGrupo(@PathVariable Long grupoMuscularId, Pageable pageable) {
        return pagedAssembler.toModel(repository.findByGrupoMuscularId(grupoMuscularId, pageable), assembler);
    }
}
