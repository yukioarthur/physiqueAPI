package senac.tsi.physique.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.*;
import senac.tsi.physique.apikey.ApiAccessPlan;
import senac.tsi.physique.apikey.RequireApiKey;
import senac.tsi.physique.dto.*;
import senac.tsi.physique.services.GamificacaoService;

import java.util.List;

@RestController
@Tag(name = "gamificacao", description = "XP, desafios gamificados e atividades aeróbicas do app")
public class GamificacaoController {

    private final GamificacaoService gamificacaoService;

    public GamificacaoController(GamificacaoService gamificacaoService) {
        this.gamificacaoService = gamificacaoService;
    }

    @Operation(summary = "Buscar resumo gamificado do usuário")
    @RequireApiKey(minPlan = ApiAccessPlan.ALUNO)
    @GetMapping("/usuarios/{usuarioId}/gamificacao")
    public GamificacaoResumoResponse getResumo(@PathVariable @Positive Long usuarioId) {
        return gamificacaoService.getResumo(usuarioId);
    }

    @Operation(summary = "Listar desafios gamificados do usuário")
    @RequireApiKey(minPlan = ApiAccessPlan.ALUNO)
    @GetMapping("/usuarios/{usuarioId}/desafios-gamificados")
    public List<DesafioUsuarioResponse> getDesafios(@PathVariable @Positive Long usuarioId) {
        return gamificacaoService.listarDesafios(usuarioId);
    }

    @Operation(summary = "Concluir ou desmarcar desafio manual")
    @RequireApiKey(minPlan = ApiAccessPlan.ALUNO)
    @PostMapping("/usuarios/{usuarioId}/desafios/{desafioId}/concluir")
    public DesafioUsuarioResponse concluirDesafio(
            @PathVariable @Positive Long usuarioId,
            @PathVariable @Positive Long desafioId,
            @Valid @RequestBody ConcluirDesafioRequest request
    ) {
        return gamificacaoService.concluirManual(usuarioId, desafioId, Boolean.TRUE.equals(request.getConcluido()));
    }

    @Operation(summary = "Registrar caminhada rastreada pelo app")
    @RequireApiKey(minPlan = ApiAccessPlan.ALUNO)
    @PostMapping("/usuarios/{usuarioId}/caminhadas")
    public CaminhadaResponse registrarCaminhada(
            @PathVariable @Positive Long usuarioId,
            @Valid @RequestBody CaminhadaRequest request
    ) {
        return gamificacaoService.registrarCaminhada(usuarioId, request);
    }

    @Operation(summary = "Listar caminhadas recentes do usuário")
    @RequireApiKey(minPlan = ApiAccessPlan.ALUNO)
    @GetMapping("/usuarios/{usuarioId}/caminhadas")
    public List<CaminhadaResponse> listarCaminhadas(@PathVariable @Positive Long usuarioId) {
        return gamificacaoService.listarCaminhadas(usuarioId);
    }
}
