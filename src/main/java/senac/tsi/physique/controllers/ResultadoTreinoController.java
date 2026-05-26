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
import senac.tsi.physique.dto.ResultadoTreinoRequest;
import senac.tsi.physique.entities.ResultadoTreino;
import senac.tsi.physique.exceptions.ResultadoTreinoNotFoundException;
import senac.tsi.physique.idempotency.RequireIdempotency;
import senac.tsi.physique.exceptions.TreinoNotFoundException;
import senac.tsi.physique.exceptions.UsuarioNotFoundException;
import senac.tsi.physique.repositories.ResultadoTreinoRepository;
import senac.tsi.physique.repositories.TreinoRepository;
import senac.tsi.physique.repositories.UsuarioRepository;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Validated
@RestController
@Tag(name = "resultados-treino", description = "Registro de sessões de treino por usuário. Ao salvar, a API calcula automaticamente o peso recomendado para a próxima sessão com base no peso anterior informado.")
public class ResultadoTreinoController {

    private final ResultadoTreinoRepository resultadoTreinoRepository;
    private final TreinoRepository treinoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PagedResourcesAssembler<ResultadoTreino> pagedResourcesAssembler;

    @Autowired
    public ResultadoTreinoController(ResultadoTreinoRepository resultadoTreinoRepository,
                                     TreinoRepository treinoRepository,
                                     UsuarioRepository usuarioRepository,
                                     PagedResourcesAssembler<ResultadoTreino> pagedResourcesAssembler) {
        this.resultadoTreinoRepository = resultadoTreinoRepository;
        this.treinoRepository = treinoRepository;
        this.usuarioRepository = usuarioRepository;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "Listar resultados de treino", description = "Retorna resultados de treino paginados. Use `usuarioId` para listar somente os registros de um usuário.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping("/resultados-treino")
    public ResponseEntity<PagedModel<EntityModel<ResultadoTreino>>> getResultadosTreino(
            @Parameter(description = "Filtro opcional pelo ID do usuário", example = "1")
            @RequestParam(required = false) Long usuarioId,
            @ParameterObject Pageable pageable) {

        var resultados = usuarioId == null
                ? resultadoTreinoRepository.findAll(pageable)
                : resultadoTreinoRepository.findByUsuarioId(usuarioId, pageable);

        var pagedModel = pagedResourcesAssembler.toModel(resultados, this::toModel);
        return ResponseEntity.ok(pagedModel);
    }

    @Operation(summary = "Buscar resultado de treino por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultado encontrado"),
            @ApiResponse(responseCode = "404", description = "Resultado não encontrado")
    })
    @GetMapping("/resultados-treino/{id}")
    public EntityModel<ResultadoTreino> getResultadoTreinoById(
            @Parameter(description = "ID do resultado de treino", example = "1") @PathVariable @Positive(message = "O ID deve ser maior que zero") Long id) {
        var resultado = resultadoTreinoRepository.findById(id)
                .orElseThrow(() -> new ResultadoTreinoNotFoundException(id));

        return toModel(resultado);
    }

    @Operation(summary = "Criar resultado de treino", description = "Registra uma sessão de treino e calcula automaticamente `pesoRecomendado = pesoAnterior * 0.9`.")
    @Parameter(name = "Idempotency-Key", in = ParameterIn.HEADER, required = true, description = "Chave única para evitar duplicidade em retries do POST")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Resultado registrado"),
            @ApiResponse(responseCode = "400", description = "Payload inválido"),
            @ApiResponse(responseCode = "404", description = "Treino ou usuário não encontrado")
    })
    @RequireApiKey(minPlan = ApiAccessPlan.ALUNO)
    @PostMapping("/resultados-treino")
    @RequireIdempotency
    public ResponseEntity<ResultadoTreino> createResultadoTreino(@Valid @RequestBody ResultadoTreinoRequest request) {
        ResultadoTreino resultado = new ResultadoTreino();
        applyRequest(resultado, request);
        calcularPesoRecomendado(resultado);

        resultadoTreinoRepository.save(resultado);
        return ResponseEntity.created(URI.create("/resultados-treino/" + resultado.getId()))
                .body(resultado);
    }

    @Operation(summary = "Atualizar resultado de treino", description = "Atualiza o registro e recalcula o peso recomendado a partir do novo `pesoAnterior`.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultado atualizado"),
            @ApiResponse(responseCode = "400", description = "Payload inválido"),
            @ApiResponse(responseCode = "404", description = "Resultado, treino ou usuário não encontrado")
    })
    @PutMapping("/resultados-treino/{id}")
    public ResponseEntity<ResultadoTreino> updateResultadoTreino(
            @Parameter(description = "ID do resultado de treino", example = "1") @PathVariable @Positive(message = "O ID deve ser maior que zero") Long id,
            @Valid @RequestBody ResultadoTreinoRequest request) {

        var resultado = resultadoTreinoRepository.findById(id)
                .orElseThrow(() -> new ResultadoTreinoNotFoundException(id));

        applyRequest(resultado, request);
        calcularPesoRecomendado(resultado);

        return ResponseEntity.ok(resultadoTreinoRepository.save(resultado));
    }

    @Operation(summary = "Excluir resultado de treino")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Resultado removido"),
            @ApiResponse(responseCode = "404", description = "Resultado não encontrado")
    })
    @DeleteMapping("/resultados-treino/{id}")
    public ResponseEntity<Void> deleteResultadoTreino(
            @Parameter(description = "ID do resultado de treino", example = "1") @PathVariable @Positive(message = "O ID deve ser maior que zero") Long id) {
        var resultado = resultadoTreinoRepository.findById(id)
                .orElseThrow(() -> new ResultadoTreinoNotFoundException(id));

        resultadoTreinoRepository.delete(resultado);
        return ResponseEntity.noContent().build();
    }

    private void applyRequest(ResultadoTreino resultado, ResultadoTreinoRequest request) {
        var treino = treinoRepository.findById(request.getTreinoId())
                .orElseThrow(() -> new TreinoNotFoundException(request.getTreinoId()));
        var usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new UsuarioNotFoundException(request.getUsuarioId()));

        resultado.setTreino(treino);
        resultado.setUsuario(usuario);
        resultado.setData(request.getData());
        resultado.setListaSerieRepeticao(request.getListaSerieRepeticao());
        resultado.setQuantidadeSeriesTreino(request.getQuantidadeSeriesTreino());
        resultado.setPesoAnterior(request.getPesoAnterior());
        resultado.setStatus("FINALIZADO");
        resultado.setVolumeTotal(0.0);
    }

    private void calcularPesoRecomendado(ResultadoTreino resultadoTreino) {
        if (resultadoTreino.getPesoAnterior() != null) {
            resultadoTreino.setPesoRecomendado(Math.round(resultadoTreino.getPesoAnterior() * 0.9 * 100.0) / 100.0);
        }
    }

    private EntityModel<ResultadoTreino> toModel(ResultadoTreino resultadoTreino) {
        return EntityModel.of(resultadoTreino,
                linkTo(methodOn(ResultadoTreinoController.class).getResultadoTreinoById(resultadoTreino.getId())).withSelfRel(),
                linkTo(methodOn(ResultadoTreinoController.class).getResultadosTreino(null, Pageable.unpaged())).withRel("resultados-treino"));
    }
}
