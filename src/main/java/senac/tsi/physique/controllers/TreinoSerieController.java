package senac.tsi.physique.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import senac.tsi.physique.apikey.ApiAccessPlan;
import senac.tsi.physique.apikey.RequireApiKey;
import senac.tsi.physique.dto.TreinoSerieRequest;
import senac.tsi.physique.entities.TreinoSerie;
import senac.tsi.physique.exceptions.TreinoSerieNotFoundException;
import senac.tsi.physique.idempotency.RequireIdempotency;
import senac.tsi.physique.repositories.TreinoSerieRepository;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Validated
@RestController
@Tag(name = "series-calculadas", description = "Cálculo auxiliar de carga e repetições-alvo para a próxima série. Ao salvar, a API calcula 1RM estimado, carga sugerida e repetições da próxima série.")
public class TreinoSerieController {

    private final TreinoSerieRepository treinoSerieRepository;
    private final PagedResourcesAssembler<TreinoSerie> pagedResourcesAssembler;

    @Autowired
    public TreinoSerieController(TreinoSerieRepository treinoSerieRepository,
                                 PagedResourcesAssembler<TreinoSerie> pagedResourcesAssembler) {
        this.treinoSerieRepository = treinoSerieRepository;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Listar séries calculadas", description = "Retorna registros paginados. Use o filtro `treino` para busca parcial por nome.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping("/series-calculadas")
    public ResponseEntity<PagedModel<EntityModel<TreinoSerie>>> getSeriesCalculadas(
            @Parameter(description = "Filtro opcional por nome da referência de treino", example = "Supino")
            @RequestParam(required = false) String treino,
            @ParameterObject Pageable pageable) {

        var series = treino == null || treino.isBlank()
                ? treinoSerieRepository.findAll(pageable)
                : treinoSerieRepository.findByTreinoContainingIgnoreCase(treino, pageable);

        var pagedModel = pagedResourcesAssembler.toModel(series, this::toModel);
        return ResponseEntity.ok(pagedModel);
    }

    @Operation(summary = "Buscar série calculada por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro encontrado"),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado")
    })
    @GetMapping("/series-calculadas/{id}")
    public EntityModel<TreinoSerie> getTreinoSerieById(
            @Parameter(description = "ID da série calculada", example = "1") @PathVariable @Positive(message = "O ID deve ser maior que zero") Long id) {
        var treinoSerie = treinoSerieRepository.findById(id)
                .orElseThrow(() -> new TreinoSerieNotFoundException(id));

        return toModel(treinoSerie);
    }

    @Operation(summary = "Criar série calculada", description = "Calcula automaticamente `umaRepMax`, `proxSerieMax` e define `proxSerieRep = 8`.")
    @Parameter(name = "Idempotency-Key", in = ParameterIn.HEADER, required = true, description = "Chave única para evitar duplicidade em retries do POST")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Registro criado e cálculos executados"),
            @ApiResponse(responseCode = "400", description = "Payload inválido")
    })
    @RequireApiKey(minPlan = ApiAccessPlan.PROFESSOR)
    @PostMapping("/series-calculadas")
    @RequireIdempotency
    public ResponseEntity<TreinoSerie> createTreinoSerie(@Valid @RequestBody TreinoSerieRequest request) {
        TreinoSerie treinoSerie = new TreinoSerie();
        applyRequest(treinoSerie, request);
        calcularSerie(treinoSerie);

        treinoSerieRepository.save(treinoSerie);
        return ResponseEntity.created(URI.create("/series-calculadas/" + treinoSerie.getId()))
                .body(treinoSerie);
    }

    @Operation(summary = "Atualizar série calculada", description = "Recalcula os campos derivados a partir de `peso` e `reps`.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro atualizado e cálculos refeitos"),
            @ApiResponse(responseCode = "400", description = "Payload inválido"),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado")
    })
    @RequireApiKey(minPlan = ApiAccessPlan.PROFESSOR)
    @PutMapping("/series-calculadas/{id}")
    public ResponseEntity<TreinoSerie> updateTreinoSerie(
            @Parameter(description = "ID da série calculada", example = "1") @PathVariable @Positive(message = "O ID deve ser maior que zero") Long id,
            @Valid @RequestBody TreinoSerieRequest request) {

        var treinoSerie = treinoSerieRepository.findById(id)
                .orElseThrow(() -> new TreinoSerieNotFoundException(id));

        applyRequest(treinoSerie, request);
        calcularSerie(treinoSerie);

        return ResponseEntity.ok(treinoSerieRepository.save(treinoSerie));
    }

    @Operation(summary = "Excluir série calculada")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Registro removido"),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado")
    })
    @RequireApiKey(minPlan = ApiAccessPlan.PROFESSOR)
    @DeleteMapping("/series-calculadas/{id}")
    public ResponseEntity<Void> deleteTreinoSerie(
            @Parameter(description = "ID da série calculada", example = "1") @PathVariable @Positive(message = "O ID deve ser maior que zero") Long id) {
        var treinoSerie = treinoSerieRepository.findById(id)
                .orElseThrow(() -> new TreinoSerieNotFoundException(id));

        treinoSerieRepository.delete(treinoSerie);
        return ResponseEntity.noContent().build();
    }

    private void applyRequest(TreinoSerie treinoSerie, TreinoSerieRequest request) {
        treinoSerie.setTreino(request.getTreino());
        treinoSerie.setPeso(request.getPeso());
        treinoSerie.setReps(request.getReps());
    }

    private void calcularSerie(TreinoSerie treinoSerie) {
        double umaRepMax = treinoSerie.getPeso() * (1 + (treinoSerie.getReps() / 30.0));
        treinoSerie.setUmaRepMax(Math.round(umaRepMax * 100.0) / 100.0);
        treinoSerie.setProxSerieMax(Math.round((umaRepMax * 0.75) * 100.0) / 100.0);
        treinoSerie.setProxSerieRep(8);
    }

    private EntityModel<TreinoSerie> toModel(TreinoSerie treinoSerie) {
        return EntityModel.of(treinoSerie,
                linkTo(methodOn(TreinoSerieController.class).getTreinoSerieById(treinoSerie.getId())).withSelfRel(),
                linkTo(methodOn(TreinoSerieController.class).getSeriesCalculadas(null, Pageable.unpaged())).withRel("series-calculadas"));
    }
}
