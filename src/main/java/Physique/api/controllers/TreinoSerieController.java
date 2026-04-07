package Physique.api.controllers;

import Physique.api.assemblers.TreinoSerieModelAssembler;
import Physique.api.entities.TreinoSerie;
import Physique.api.exceptions.TreinoSerieNaoEncontradoExcecao;
import Physique.api.repositories.TreinoSerieRepository;
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
@RequestMapping("/series-calculadas")
public class TreinoSerieController {

    private final TreinoSerieRepository repository;
    private final TreinoSerieModelAssembler assembler;
    private final PagedResourcesAssembler<TreinoSerie> pagedAssembler;

    public TreinoSerieController(TreinoSerieRepository repository,
                                 TreinoSerieModelAssembler assembler,
                                 PagedResourcesAssembler<TreinoSerie> pagedAssembler) {
        this.repository = repository;
        this.assembler = assembler;
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping
    public PagedModel<EntityModel<TreinoSerie>> listar(Pageable pageable) {
        return pagedAssembler.toModel(repository.findAll(pageable), assembler);
    }

    @GetMapping("/{id}")
    public EntityModel<TreinoSerie> buscarPorId(@PathVariable Long id) {
        TreinoSerie entity = repository.findById(id)
                .orElseThrow(() -> new TreinoSerieNaoEncontradoExcecao(id));
        return assembler.toModel(entity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EntityModel<TreinoSerie>> criar(@Valid @RequestBody TreinoSerie body) {
        calcularSerie(body);
        TreinoSerie salvo = repository.save(body);
        return ResponseEntity.created(URI.create("/series-calculadas/" + salvo.getId())).body(assembler.toModel(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<TreinoSerie>> atualizar(@PathVariable Long id, @Valid @RequestBody TreinoSerie body) {
        return repository.findById(id)
                .map(existente -> {
                    existente.setTreino(body.getTreino());
                    existente.setPeso(body.getPeso());
                    existente.setReps(body.getReps());
                    calcularSerie(existente);
                    TreinoSerie salvo = repository.save(existente);
                    return ResponseEntity.ok(assembler.toModel(salvo));
                })
                .orElseThrow(() -> new TreinoSerieNaoEncontradoExcecao(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new TreinoSerieNaoEncontradoExcecao(id);
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    public PagedModel<EntityModel<TreinoSerie>> buscarPorTreino(@RequestParam String treino, Pageable pageable) {
        return pagedAssembler.toModel(repository.findByTreinoContainingIgnoreCase(treino, pageable), assembler);
    }

    private void calcularSerie(TreinoSerie treinoSerie) {
        double umaRepMax = treinoSerie.getPeso() * (1 + (treinoSerie.getReps() / 30.0));
        double proxSerieMax = umaRepMax * 0.75;
        treinoSerie.setUmaRepMax(arredondar(umaRepMax));
        treinoSerie.setProxSerieMax(arredondar(proxSerieMax));
        treinoSerie.setProxSerieRep(8);
    }

    private double arredondar(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
