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
import senac.tsi.physique.dto.MusculoRequest;
import senac.tsi.physique.entities.Musculo;
import senac.tsi.physique.exceptions.GrupoMuscularNotFoundException;
import senac.tsi.physique.exceptions.MusculoNotFoundException;
import senac.tsi.physique.repositories.GrupoMuscularRepository;
import senac.tsi.physique.repositories.MusculoRepository;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Validated
@RestController
@Tag(name = "musculos", description = "Cadastro e consulta de músculos. Um músculo sempre referencia um grupo muscular já cadastrado.")
public class MusculoController {

    private final MusculoRepository musculoRepository;
    private final GrupoMuscularRepository grupoMuscularRepository;
    private final PagedResourcesAssembler<Musculo> pagedResourcesAssembler;

    @Autowired
    public MusculoController(MusculoRepository musculoRepository,
                             GrupoMuscularRepository grupoMuscularRepository,
                             PagedResourcesAssembler<Musculo> pagedResourcesAssembler) {
        this.musculoRepository = musculoRepository;
        this.grupoMuscularRepository = grupoMuscularRepository;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Listar músculos", description = "Retorna músculos paginados. Use `grupoMuscularId` para listar apenas os músculos de um grupo muscular específico.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping("/musculos")
    public ResponseEntity<PagedModel<EntityModel<Musculo>>> getMusculos(
            @Parameter(description = "Filtro opcional pelo ID do grupo muscular", example = "1")
            @RequestParam(required = false) Long grupoMuscularId,
            @ParameterObject Pageable pageable) {

        var musculos = grupoMuscularId == null
                ? musculoRepository.findAll(pageable)
                : musculoRepository.findByGrupoMuscularId(grupoMuscularId, pageable);

        var pagedModel = pagedResourcesAssembler.toModel(musculos, this::toModel);
        return ResponseEntity.ok(pagedModel);
    }

    @Operation(summary = "Buscar músculo por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Músculo encontrado"),
            @ApiResponse(responseCode = "404", description = "Músculo não encontrado")
    })
    @GetMapping("/musculos/{id}")
    public EntityModel<Musculo> getMusculoById(
            @Parameter(description = "ID do músculo", example = "1") @PathVariable @Positive(message = "O ID deve ser maior que zero") Long id) {
        var musculo = musculoRepository.findById(id)
                .orElseThrow(() -> new MusculoNotFoundException(id));

        return toModel(musculo);
    }

    @Operation(summary = "Criar músculo", description = "Cria um músculo informando o nome e o `grupoMuscularId` de um grupo já existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Músculo criado"),
            @ApiResponse(responseCode = "400", description = "Payload inválido"),
            @ApiResponse(responseCode = "404", description = "Grupo muscular informado não existe")
    })
    @RequireApiKey(minPlan = ApiAccessPlan.PROFESSOR)
    @PostMapping("/musculos")
    public ResponseEntity<Musculo> createMusculo(@Valid @RequestBody MusculoRequest request) {
        Musculo musculo = new Musculo();
        applyRequest(musculo, request);

        musculoRepository.save(musculo);
        return ResponseEntity.created(URI.create("/musculos/" + musculo.getId()))
                .body(musculo);
    }

    @Operation(summary = "Atualizar músculo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Músculo atualizado"),
            @ApiResponse(responseCode = "400", description = "Payload inválido"),
            @ApiResponse(responseCode = "404", description = "Músculo ou grupo muscular não encontrado")
    })
    @RequireApiKey(minPlan = ApiAccessPlan.PROFESSOR)
    @PutMapping("/musculos/{id}")
    public ResponseEntity<Musculo> updateMusculo(
            @Parameter(description = "ID do músculo", example = "1") @PathVariable @Positive(message = "O ID deve ser maior que zero") Long id,
            @Valid @RequestBody MusculoRequest request) {

        var musculo = musculoRepository.findById(id)
                .orElseThrow(() -> new MusculoNotFoundException(id));

        applyRequest(musculo, request);
        return ResponseEntity.ok(musculoRepository.save(musculo));
    }

    @Operation(summary = "Excluir músculo", description = "Remove o músculo. Pode retornar conflito se houver exercícios ou outros registros ainda vinculados.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Músculo removido"),
            @ApiResponse(responseCode = "404", description = "Músculo não encontrado"),
            @ApiResponse(responseCode = "409", description = "Músculo ainda está em uso")
    })
    @RequireApiKey(minPlan = ApiAccessPlan.PROFESSOR)
    @DeleteMapping("/musculos/{id}")
    public ResponseEntity<Void> deleteMusculo(
            @Parameter(description = "ID do músculo", example = "1") @PathVariable @Positive(message = "O ID deve ser maior que zero") Long id) {
        var musculo = musculoRepository.findById(id)
                .orElseThrow(() -> new MusculoNotFoundException(id));

        musculoRepository.delete(musculo);
        return ResponseEntity.noContent().build();
    }

    private void applyRequest(Musculo musculo, MusculoRequest request) {
        var grupoMuscular = grupoMuscularRepository.findById(request.getGrupoMuscularId())
                .orElseThrow(() -> new GrupoMuscularNotFoundException(request.getGrupoMuscularId()));

        musculo.setNome(request.getNome());
        musculo.setGrupoMuscular(grupoMuscular);
    }

    private EntityModel<Musculo> toModel(Musculo musculo) {
        return EntityModel.of(musculo,
                linkTo(methodOn(MusculoController.class).getMusculoById(musculo.getId())).withSelfRel(),
                linkTo(methodOn(MusculoController.class).getMusculos(null, Pageable.unpaged())).withRel("musculos"));
    }
}
