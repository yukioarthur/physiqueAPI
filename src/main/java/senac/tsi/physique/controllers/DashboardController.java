package senac.tsi.physique.controllers;

import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import senac.tsi.physique.apikey.ApiAccessPlan;
import senac.tsi.physique.apikey.RequireApiKey;
import senac.tsi.physique.dto.*;
import senac.tsi.physique.entities.ResultadoTreino;
import senac.tsi.physique.entities.UsuarioDesafio;
import senac.tsi.physique.exceptions.UsuarioNotFoundException;
import senac.tsi.physique.repositories.ResultadoTreinoRepository;
import senac.tsi.physique.repositories.UsuarioDesafioRepository;
import senac.tsi.physique.repositories.UsuarioRepository;
import senac.tsi.physique.repositories.UsuarioTreinoRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Validated
@RestController
@Tag(name = "dashboard", description = "Dados agregados para a Home do app Android")
public class DashboardController {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioTreinoRepository usuarioTreinoRepository;
    private final ResultadoTreinoRepository resultadoTreinoRepository;
    private final UsuarioDesafioRepository usuarioDesafioRepository;

    public DashboardController(UsuarioRepository usuarioRepository,
                               UsuarioTreinoRepository usuarioTreinoRepository,
                               ResultadoTreinoRepository resultadoTreinoRepository,
                               UsuarioDesafioRepository usuarioDesafioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioTreinoRepository = usuarioTreinoRepository;
        this.resultadoTreinoRepository = resultadoTreinoRepository;
        this.usuarioDesafioRepository = usuarioDesafioRepository;
    }

    @Operation(summary = "Buscar dashboard do usuário")
    @RequireApiKey(minPlan = ApiAccessPlan.ALUNO)
    @GetMapping("/dashboard/{usuarioId}")
    public DashboardResponse getDashboard(@PathVariable @Positive(message = "O ID do usuário deve ser maior que zero") Long usuarioId) {
        var usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));

        var usuarioResumo = new UsuarioResumoResponse(
                usuario.getId(),
                usuario.getNome(),
                primeiroNome(usuario.getNome())
        );

        var treinoAtual = usuarioTreinoRepository.findByUsuarioIdAndAtivoTrue(usuarioId)
                .map(usuarioTreino -> new TreinoAtualResumoResponse(
                        usuarioTreino.getTreino().getId(),
                        usuarioTreino.getTreino().getNome(),
                        primeiroNome(usuarioTreino.getTreino().getCriadorNome())
                ))
                .orElse(null);

        return new DashboardResponse(
                usuarioResumo,
                montarSemana(usuarioId),
                treinoAtual,
                montarUltimosTreinos(usuarioId),
                montarDesafios(usuarioId),
                new PerformanceResponse(85, 60, 40)
        );
    }

    private List<DiaTreinadoResponse> montarSemana(Long usuarioId) {
        LocalDate hoje = LocalDate.now();
        LocalDate segunda = hoje.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate domingo = hoje.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        List<ResultadoTreino> resultados = resultadoTreinoRepository
                .findByUsuarioIdAndDataBetweenAndStatusOrderByDataAsc(usuarioId, segunda, domingo, "FINALIZADO");

        Set<DayOfWeek> diasTreinados = new HashSet<>();
        for (ResultadoTreino resultado : resultados) {
            diasTreinados.add(resultado.getData().getDayOfWeek());
        }

        List<DiaTreinadoResponse> semana = new ArrayList<>();
        semana.add(new DiaTreinadoResponse("SEG", diasTreinados.contains(DayOfWeek.MONDAY)));
        semana.add(new DiaTreinadoResponse("TER", diasTreinados.contains(DayOfWeek.TUESDAY)));
        semana.add(new DiaTreinadoResponse("QUA", diasTreinados.contains(DayOfWeek.WEDNESDAY)));
        semana.add(new DiaTreinadoResponse("QUI", diasTreinados.contains(DayOfWeek.THURSDAY)));
        semana.add(new DiaTreinadoResponse("SEX", diasTreinados.contains(DayOfWeek.FRIDAY)));
        semana.add(new DiaTreinadoResponse("SAB", diasTreinados.contains(DayOfWeek.SATURDAY)));
        semana.add(new DiaTreinadoResponse("DOM", diasTreinados.contains(DayOfWeek.SUNDAY)));
        return semana;
    }

    private List<DashboardItemResponse> montarUltimosTreinos(Long usuarioId) {
        return resultadoTreinoRepository.findTop5ByUsuarioIdAndStatusOrderByDataDesc(usuarioId, "FINALIZADO")
                .stream()
                .map(resultado -> new DashboardItemResponse(
                        resultado.getId(),
                        resultado.getTreino().getNome(),
                        descricaoData(resultado.getData())
                ))
                .toList();
    }

    private List<DashboardItemResponse> montarDesafios(Long usuarioId) {
        List<UsuarioDesafio> desafios = usuarioDesafioRepository.findByUsuarioIdAndDesafioAtivoTrue(usuarioId);
        return desafios.stream()
                .map(usuarioDesafio -> new DashboardItemResponse(
                        usuarioDesafio.getDesafio().getId(),
                        usuarioDesafio.getDesafio().getTitulo(),
                        "Progresso: " + usuarioDesafio.getProgresso() + " de " + usuarioDesafio.getDesafio().getMeta()
                ))
                .toList();
    }

    private String primeiroNome(String nomeCompleto) {
        if (nomeCompleto == null || nomeCompleto.isBlank()) {
            return "";
        }
        return nomeCompleto.trim().split("\\s+")[0];
    }

    private String descricaoData(LocalDate data) {
        LocalDate hoje = LocalDate.now();
        long dias = java.time.temporal.ChronoUnit.DAYS.between(data, hoje);
        if (dias == 0) return "Finalizado hoje";
        if (dias == 1) return "Finalizado ontem";
        return "Finalizado há " + dias + " dias";
    }
}
