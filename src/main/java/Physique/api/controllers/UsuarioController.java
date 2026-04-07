package Physique.api.controllers;

import Physique.api.assemblers.UsuarioModelAssembler;
import Physique.api.entities.Usuario;
import Physique.api.exceptions.UsuarioNaoEncontradoExcecao;
import Physique.api.repositories.UsuarioRepository;
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
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository repository;
    private final UsuarioModelAssembler assembler;
    private final PagedResourcesAssembler<Usuario> pagedAssembler;

    public UsuarioController(UsuarioRepository repository,
                             UsuarioModelAssembler assembler,
                             PagedResourcesAssembler<Usuario> pagedAssembler) {
        this.repository = repository;
        this.assembler = assembler;
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping
    public PagedModel<EntityModel<Usuario>> listar(Pageable pageable) {
        return pagedAssembler.toModel(repository.findAll(pageable), assembler);
    }

    @GetMapping("/{id}")
    public EntityModel<Usuario> buscarPorId(@PathVariable Long id) {
        Usuario entity = repository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoExcecao(id));
        return assembler.toModel(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EntityModel<Usuario>> criar(@Valid @RequestBody Usuario body) {
        Usuario salvo = repository.save(body);
        return ResponseEntity.created(URI.create("/usuarios/" + salvo.getId())).body(assembler.toModel(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> atualizar(@PathVariable Long id, @Valid @RequestBody Usuario body) {
        return repository.findById(id)
                .map(existente -> {
                    existente.setNome(body.getNome());
                    existente.setIdade(body.getIdade());
                    existente.setObjetivo(body.getObjetivo());
                    existente.setPesoCorporal(body.getPesoCorporal());
                    Usuario salvo = repository.save(existente);
                    return ResponseEntity.ok(assembler.toModel(salvo));
                })
                .orElseThrow(() -> new UsuarioNaoEncontradoExcecao(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new UsuarioNaoEncontradoExcecao(id);
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar-por-objetivo")
    public PagedModel<EntityModel<Usuario>> buscarPorObjetivo(@RequestParam String objetivo, Pageable pageable) {
        return pagedAssembler.toModel(repository.findByObjetivoContainingIgnoreCase(objetivo, pageable), assembler);
    }
}
