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
import senac.tsi.physique.dto.TreinoRequest;
import senac.tsi.physique.dto.versioning.TreinoV1Response;
import senac.tsi.physique.dto.versioning.TreinoV2Response;
import senac.tsi.physique.versioning.ApiVersionConstants;
import senac.tsi.physique.entities.Exercicio;
import senac.tsi.physique.entities.Treino;
import senac.tsi.physique.exceptions.ExercicioNotFoundException;
import senac.tsi.physique.exceptions.TreinoNotFoundException;
import senac.tsi.physique.idempotency.RequireIdempotency;
import senac.tsi.physique.repositories.ExercicioRepository;
import senac.tsi.physique.repositories.TreinoRepository;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Validated
@RestController
@Tag(name = "treinos", description = "Montagem e consulta de treinos. Cada treino referencia uma lista de exercícios já cadastrados.")
public class TreinoController {

    private final TreinoRepository treinoRepository;
    private final ExercicioRepository exercicioRepository;
    private final PagedResourcesAssembler<Treino> pagedResourcesAssembler;

    @Autowired
    public TreinoController(TreinoRepository treinoRepository,
                            ExercicioRepository exercicioRepository,
                            PagedResourcesAssembler<Treino> pagedResourcesAssembler) {
        this.treinoRepository = treinoRepository;
        this.exercicioRepository = exercicioRepository;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Listar treinos", description = "Retorna treinos paginados. Use o filtro `metodologia` para busca parcial.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping("/treinos")
    public ResponseEntity<PagedModel<EntityModel<Treino>>> getTreinos(
            @Parameter(description = "Filtro opcional por metodologia", example = "Pirâmide")
            @RequestParam(required = false) String metodologia,
            @ParameterObject Pageable pageable) {

        var treinos = metodologia == null || metodologia.isBlank()
                ? treinoRepository.findAll(pageable)
                : treinoRepository.findByMetodologiaContainingIgnoreCase(metodologia, pageable);

        var pagedModel = pagedResourcesAssembler.toModel(treinos, this::toModel);
        return ResponseEntity.ok(pagedModel);
    }

    @Operation(
            summary = "Buscar treino por ID - versão padrão/V1",
            description = "Mantém compatibilidade com clientes existentes. Quando X-API-Version estiver ausente, a API responde como versão 1."
    )
    @Parameter(name = ApiVersionConstants.HEADER_NAME, in = ParameterIn.HEADER, required = false, description = "Versão da API. Ausente = V1", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Treino encontrado na representação V1 compatível"),
            @ApiResponse(responseCode = "400", description = "Versão de API não suportada ou ID inválido"),
            @ApiResponse(responseCode = "404", description = "Treino não encontrado")
    })
    @GetMapping(value = "/treinos/{id}", headers = "!X-API-Version")
    public EntityModel<Treino> getTreinoById(
            @Parameter(description = "ID do treino", example = "1") @PathVariable @Positive(message = "O ID deve ser maior que zero") Long id) {
        var treino = buscarTreino(id);
        return toModel(treino);
    }

    @Operation(
            summary = "Buscar treino por ID - V1",
            description = "Versão 1 do endpoint por header X-API-Version=1. Mantém o formato compatível com a resposta original."
    )
    @Parameter(name = ApiVersionConstants.HEADER_NAME, in = ParameterIn.HEADER, required = true, description = "Versão da API", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Treino encontrado na representação V1"),
            @ApiResponse(responseCode = "400", description = "Versão de API não suportada ou ID inválido"),
            @ApiResponse(responseCode = "404", description = "Treino não encontrado")
    })
    @GetMapping(value = "/treinos/{id}", headers = "X-API-Version=1")
    public EntityModel<Treino> getTreinoByIdV1(
            @Parameter(description = "ID do treino", example = "1") @PathVariable @Positive(message = "O ID deve ser maior que zero") Long id) {
        var treino = buscarTreino(id);
        return toModel(treino);
    }

    @Operation(
            summary = "Buscar treino por ID - V2",
            description = "Versão 2 do endpoint por header X-API-Version=2. Inclui apiVersion, quantidade de exercícios, resumo dos exercícios e links simples."
    )
    @Parameter(name = ApiVersionConstants.HEADER_NAME, in = ParameterIn.HEADER, required = true, description = "Versão da API", example = "2")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Treino encontrado na representação V2"),
            @ApiResponse(responseCode = "400", description = "Versão de API não suportada ou ID inválido"),
            @ApiResponse(responseCode = "404", description = "Treino não encontrado")
    })
    @GetMapping(value = "/treinos/{id}", headers = "X-API-Version=2")
    public TreinoV2Response getTreinoByIdV2(
            @Parameter(description = "ID do treino", example = "1") @PathVariable @Positive(message = "O ID deve ser maior que zero") Long id) {
        var treino = buscarTreino(id);
        return TreinoV2Response.from(treino);
    }

    @Operation(summary = "Criar treino", description = "Cria um treino usando uma lista de `exercicioIds` já cadastrados.")
    @Parameter(name = "Idempotency-Key", in = ParameterIn.HEADER, required = true, description = "Chave única para evitar duplicidade em retries do POST")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Treino criado"),
            @ApiResponse(responseCode = "400", description = "Payload inválido"),
            @ApiResponse(responseCode = "404", description = "Um ou mais exercícios não foram encontrados")
    })
    @RequireApiKey(minPlan = ApiAccessPlan.PROFESSOR)
    @PostMapping("/treinos")
    @RequireIdempotency
    public ResponseEntity<Treino> createTreino(@Valid @RequestBody TreinoRequest request) {
        Treino treino = new Treino();
        applyRequest(treino, request);

        treinoRepository.save(treino);
        return ResponseEntity.created(URI.create("/treinos/" + treino.getId()))
                .body(treino);
    }

    @Operation(summary = "Atualizar treino")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Treino atualizado"),
            @ApiResponse(responseCode = "400", description = "Payload inválido"),
            @ApiResponse(responseCode = "404", description = "Treino ou exercício não encontrado")
    })
    @RequireApiKey(minPlan = ApiAccessPlan.PROFESSOR)
    @PutMapping("/treinos/{id}")
    public ResponseEntity<Treino> updateTreino(
            @Parameter(description = "ID do treino", example = "1") @PathVariable @Positive(message = "O ID deve ser maior que zero") Long id,
            @Valid @RequestBody TreinoRequest request) {

        var treino = treinoRepository.findById(id)
                .orElseThrow(() -> new TreinoNotFoundException(id));

        applyRequest(treino, request);
        return ResponseEntity.ok(treinoRepository.save(treino));
    }

    @Operation(summary = "Excluir treino")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Treino removido"),
            @ApiResponse(responseCode = "404", description = "Treino não encontrado"),
            @ApiResponse(responseCode = "409", description = "Treino ainda está vinculado a outros registros")
    })
    @RequireApiKey(minPlan = ApiAccessPlan.PROFESSOR)
    @DeleteMapping("/treinos/{id}")
    public ResponseEntity<Void> deleteTreino(
            @Parameter(description = "ID do treino", example = "1") @PathVariable @Positive(message = "O ID deve ser maior que zero") Long id) {
        var treino = treinoRepository.findById(id)
                .orElseThrow(() -> new TreinoNotFoundException(id));

        treinoRepository.delete(treino);
        return ResponseEntity.noContent().build();
    }

    private Treino buscarTreino(Long id) {
        return treinoRepository.findById(id)
                .orElseThrow(() -> new TreinoNotFoundException(id));
    }

    private void applyRequest(Treino treino, TreinoRequest request) {
        List<Exercicio> exercicios = exercicioRepository.findAllById(request.getExercicioIds());
        Set<Long> encontrados = new HashSet<>();
        for (Exercicio exercicio : exercicios) {
            encontrados.add(exercicio.getId());
        }

        for (Long exercicioId : request.getExercicioIds()) {
            if (!encontrados.contains(exercicioId)) {
                throw new ExercicioNotFoundException(exercicioId);
            }
        }

        treino.setNome(request.getNome());
        treino.setObjetivo(request.getObjetivo());
        treino.setMetodologia(request.getMetodologia());
        treino.setCriadorNome(request.getCriadorNome());
        treino.setExercicios(exercicios);
    }

    private EntityModel<Treino> toModel(Treino treino) {
        return EntityModel.of(treino,
                linkTo(methodOn(TreinoController.class).getTreinoById(treino.getId())).withSelfRel(),
                linkTo(methodOn(TreinoController.class).getTreinos(null, Pageable.unpaged())).withRel("treinos"));
    }
}
