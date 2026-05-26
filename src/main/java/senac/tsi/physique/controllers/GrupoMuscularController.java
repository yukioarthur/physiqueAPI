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
import senac.tsi.physique.dto.GrupoMuscularRequest;
import senac.tsi.physique.entities.GrupoMuscular;
import senac.tsi.physique.exceptions.GrupoMuscularNotFoundException;
import senac.tsi.physique.repositories.GrupoMuscularRepository;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Validated
@RestController
@Tag(name = "grupos-musculares", description = "Cadastro e consulta de grupos musculares. Este recurso é base para o cadastro de músculos e exercícios.")
public class GrupoMuscularController {

    private final GrupoMuscularRepository grupoMuscularRepository;
    private final PagedResourcesAssembler<GrupoMuscular> pagedResourcesAssembler;

    @Autowired
    public GrupoMuscularController(GrupoMuscularRepository grupoMuscularRepository,
                                   PagedResourcesAssembler<GrupoMuscular> pagedResourcesAssembler) {
        this.grupoMuscularRepository = grupoMuscularRepository;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Listar grupos musculares", description = "Retorna grupos musculares paginados. Use o filtro `nome` para busca parcial.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping("/grupos-musculares")
    public ResponseEntity<PagedModel<EntityModel<GrupoMuscular>>> getGruposMusculares(
            @Parameter(description = "Filtro opcional por nome do grupo muscular", example = "Peito")
            @RequestParam(required = false) String nome,
            @ParameterObject Pageable pageable) {

        var grupos = nome == null || nome.isBlank()
                ? grupoMuscularRepository.findAll(pageable)
                : grupoMuscularRepository.findByNomeContainingIgnoreCase(nome, pageable);

        var pagedModel = pagedResourcesAssembler.toModel(grupos, this::toModel);
        return ResponseEntity.ok(pagedModel);
    }

    @Operation(summary = "Buscar grupo muscular por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Grupo muscular encontrado"),
            @ApiResponse(responseCode = "404", description = "Grupo muscular não encontrado")
    })
    @GetMapping("/grupos-musculares/{id}")
    public EntityModel<GrupoMuscular> getGrupoMuscularById(
            @Parameter(description = "ID do grupo muscular", example = "1") @PathVariable @Positive(message = "O ID deve ser maior que zero") Long id) {
        var grupo = grupoMuscularRepository.findById(id)
                .orElseThrow(() -> new GrupoMuscularNotFoundException(id));

        return toModel(grupo);
    }

    @Operation(summary = "Criar grupo muscular", description = "Cria um novo grupo muscular que poderá ser referenciado por músculos e exercícios.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Grupo muscular criado"),
            @ApiResponse(responseCode = "400", description = "Payload inválido")
    })
    @RequireApiKey(minPlan = ApiAccessPlan.PROFESSOR)
    @PostMapping("/grupos-musculares")
    public ResponseEntity<GrupoMuscular> createGrupoMuscular(@Valid @RequestBody GrupoMuscularRequest request) {
        GrupoMuscular grupo = new GrupoMuscular();
        grupo.setNome(request.getNome());

        grupoMuscularRepository.save(grupo);
        return ResponseEntity.created(URI.create("/grupos-musculares/" + grupo.getId()))
                .body(grupo);
    }

    @Operation(summary = "Atualizar grupo muscular")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Grupo muscular atualizado"),
            @ApiResponse(responseCode = "400", description = "Payload inválido"),
            @ApiResponse(responseCode = "404", description = "Grupo muscular não encontrado")
    })
    @RequireApiKey(minPlan = ApiAccessPlan.PROFESSOR)
    @PutMapping("/grupos-musculares/{id}")
    public ResponseEntity<GrupoMuscular> updateGrupoMuscular(
            @Parameter(description = "ID do grupo muscular", example = "1") @PathVariable @Positive(message = "O ID deve ser maior que zero") Long id,
            @Valid @RequestBody GrupoMuscularRequest request) {

        var grupo = grupoMuscularRepository.findById(id)
                .orElseThrow(() -> new GrupoMuscularNotFoundException(id));

        grupo.setNome(request.getNome());

        return ResponseEntity.ok(grupoMuscularRepository.save(grupo));
    }

    @Operation(summary = "Excluir grupo muscular", description = "Remove o grupo muscular. Pode retornar conflito se houver músculos ou exercícios ainda vinculados.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Grupo muscular removido"),
            @ApiResponse(responseCode = "404", description = "Grupo muscular não encontrado"),
            @ApiResponse(responseCode = "409", description = "Grupo muscular ainda está em uso")
    })
    @RequireApiKey(minPlan = ApiAccessPlan.PROFESSOR)
    @DeleteMapping("/grupos-musculares/{id}")
    public ResponseEntity<Void> deleteGrupoMuscular(
            @Parameter(description = "ID do grupo muscular", example = "1") @PathVariable @Positive(message = "O ID deve ser maior que zero") Long id) {
        var grupo = grupoMuscularRepository.findById(id)
                .orElseThrow(() -> new GrupoMuscularNotFoundException(id));

        grupoMuscularRepository.delete(grupo);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<GrupoMuscular> toModel(GrupoMuscular grupoMuscular) {
        return EntityModel.of(grupoMuscular,
                linkTo(methodOn(GrupoMuscularController.class).getGrupoMuscularById(grupoMuscular.getId())).withSelfRel(),
                linkTo(methodOn(GrupoMuscularController.class).getGruposMusculares(null, Pageable.unpaged())).withRel("grupos-musculares"));
    }
}
