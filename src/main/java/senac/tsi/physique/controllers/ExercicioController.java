package senac.tsi.physique.controllers;

import io.swagger.v3.oas.annotations.Operation;
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
import senac.tsi.physique.dto.ExercicioRequest;
import senac.tsi.physique.entities.Exercicio;
import senac.tsi.physique.exceptions.ExercicioNotFoundException;
import senac.tsi.physique.exceptions.GrupoMuscularNotFoundException;
import senac.tsi.physique.exceptions.MusculoNotFoundException;
import senac.tsi.physique.repositories.ExercicioRepository;
import senac.tsi.physique.repositories.GrupoMuscularRepository;
import senac.tsi.physique.repositories.MusculoRepository;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Validated
@RestController
@Tag(name = "exercicios", description = "Cadastro e consulta de exercícios. Cada exercício referencia um grupo muscular e um músculo já cadastrados.")
public class ExercicioController {

    private final ExercicioRepository exercicioRepository;
    private final GrupoMuscularRepository grupoMuscularRepository;
    private final MusculoRepository musculoRepository;
    private final PagedResourcesAssembler<Exercicio> pagedResourcesAssembler;

    @Autowired
    public ExercicioController(ExercicioRepository exercicioRepository,
                               GrupoMuscularRepository grupoMuscularRepository,
                               MusculoRepository musculoRepository,
                               PagedResourcesAssembler<Exercicio> pagedResourcesAssembler) {
        this.exercicioRepository = exercicioRepository;
        this.grupoMuscularRepository = grupoMuscularRepository;
        this.musculoRepository = musculoRepository;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Listar exercícios", description = "Retorna exercícios paginados. Use o filtro `nome` para busca parcial.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping("/exercicios")
    public ResponseEntity<PagedModel<EntityModel<Exercicio>>> getExercicios(
            @Parameter(description = "Filtro opcional por nome do exercício", example = "Supino")
            @RequestParam(required = false) String nome,
            @ParameterObject Pageable pageable) {

        var exercicios = nome == null || nome.isBlank()
                ? exercicioRepository.findAll(pageable)
                : exercicioRepository.findByNomeContainingIgnoreCase(nome, pageable);

        var pagedModel = pagedResourcesAssembler.toModel(exercicios, this::toModel);
        return ResponseEntity.ok(pagedModel);
    }

    @Operation(summary = "Buscar exercício por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Exercício encontrado"),
            @ApiResponse(responseCode = "404", description = "Exercício não encontrado")
    })
    @GetMapping("/exercicios/{id}")
    public EntityModel<Exercicio> getExercicioById(
            @Parameter(description = "ID do exercício", example = "1") @PathVariable @Positive(message = "O ID deve ser maior que zero") Long id) {
        var exercicio = exercicioRepository.findById(id)
                .orElseThrow(() -> new ExercicioNotFoundException(id));

        return toModel(exercicio);
    }

    @Operation(summary = "Criar exercício", description = "Cria um exercício usando `grupoMuscularId` e `musculoId` já existentes.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Exercício criado"),
            @ApiResponse(responseCode = "400", description = "Payload inválido"),
            @ApiResponse(responseCode = "404", description = "Grupo muscular ou músculo não encontrado")
    })
    @RequireApiKey(minPlan = ApiAccessPlan.PROFESSOR)
    @PostMapping("/exercicios")
    public ResponseEntity<Exercicio> createExercicio(@Valid @RequestBody ExercicioRequest request) {
        Exercicio exercicio = new Exercicio();
        applyRequest(exercicio, request);

        exercicioRepository.save(exercicio);
        return ResponseEntity.created(URI.create("/exercicios/" + exercicio.getId()))
                .body(exercicio);
    }

    @Operation(summary = "Atualizar exercício")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Exercício atualizado"),
            @ApiResponse(responseCode = "400", description = "Payload inválido"),
            @ApiResponse(responseCode = "404", description = "Exercício, grupo muscular ou músculo não encontrado")
    })
    @RequireApiKey(minPlan = ApiAccessPlan.PROFESSOR)
    @PutMapping("/exercicios/{id}")
    public ResponseEntity<Exercicio> updateExercicio(
            @Parameter(description = "ID do exercício", example = "1") @PathVariable @Positive(message = "O ID deve ser maior que zero") Long id,
            @Valid @RequestBody ExercicioRequest request) {

        var exercicio = exercicioRepository.findById(id)
                .orElseThrow(() -> new ExercicioNotFoundException(id));

        applyRequest(exercicio, request);
        return ResponseEntity.ok(exercicioRepository.save(exercicio));
    }

    @Operation(summary = "Excluir exercício")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Exercício removido"),
            @ApiResponse(responseCode = "404", description = "Exercício não encontrado"),
            @ApiResponse(responseCode = "409", description = "Exercício ainda está em uso")
    })
    @RequireApiKey(minPlan = ApiAccessPlan.PROFESSOR)
    @DeleteMapping("/exercicios/{id}")
    public ResponseEntity<Void> deleteExercicio(
            @Parameter(description = "ID do exercício", example = "1") @PathVariable @Positive(message = "O ID deve ser maior que zero") Long id) {
        var exercicio = exercicioRepository.findById(id)
                .orElseThrow(() -> new ExercicioNotFoundException(id));

        exercicioRepository.delete(exercicio);
        return ResponseEntity.noContent().build();
    }

    private void applyRequest(Exercicio exercicio, ExercicioRequest request) {
        var grupoMuscular = grupoMuscularRepository.findById(request.getGrupoMuscularId())
                .orElseThrow(() -> new GrupoMuscularNotFoundException(request.getGrupoMuscularId()));
        var musculo = musculoRepository.findById(request.getMusculoId())
                .orElseThrow(() -> new MusculoNotFoundException(request.getMusculoId()));

        exercicio.setNome(request.getNome());
        exercicio.setRepeticoes(request.getRepeticoes());
        exercicio.setQuantidadeSeries(request.getQuantidadeSeries());
        exercicio.setGrupoMuscular(grupoMuscular);
        exercicio.setMusculo(musculo);
        exercicio.setDescricao(request.getDescricao());
        exercicio.setVideo(request.getVideo());
    }

    private EntityModel<Exercicio> toModel(Exercicio exercicio) {
        return EntityModel.of(exercicio,
                linkTo(methodOn(ExercicioController.class).getExercicioById(exercicio.getId())).withSelfRel(),
                linkTo(methodOn(ExercicioController.class).getExercicios(null, Pageable.unpaged())).withRel("exercicios"));
    }
}
