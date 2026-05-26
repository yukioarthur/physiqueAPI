package senac.tsi.physique.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import senac.tsi.physique.apikey.ApiAccessPlan;
import senac.tsi.physique.apikey.RequireApiKey;
import senac.tsi.physique.dto.*;
import senac.tsi.physique.entities.ResultadoTreino;
import senac.tsi.physique.entities.SerieExecutada;
import senac.tsi.physique.exceptions.ExercicioNotFoundException;
import senac.tsi.physique.idempotency.RequireIdempotency;
import senac.tsi.physique.exceptions.TreinoNotFoundException;
import senac.tsi.physique.exceptions.UsuarioNotFoundException;
import senac.tsi.physique.repositories.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@Tag(name = "app-treinos", description = "Endpoints simples para integração com o app Android")
public class TreinoAppController {

    private final UsuarioRepository usuarioRepository;
    private final TreinoRepository treinoRepository;
    private final ExercicioRepository exercicioRepository;
    private final UsuarioTreinoRepository usuarioTreinoRepository;
    private final ResultadoTreinoRepository resultadoTreinoRepository;
    private final SerieExecutadaRepository serieExecutadaRepository;
    private final UsuarioDesafioRepository usuarioDesafioRepository;
    private final DesafioRepository desafioRepository;

    public TreinoAppController(UsuarioRepository usuarioRepository,
                               TreinoRepository treinoRepository,
                               ExercicioRepository exercicioRepository,
                               UsuarioTreinoRepository usuarioTreinoRepository,
                               ResultadoTreinoRepository resultadoTreinoRepository,
                               SerieExecutadaRepository serieExecutadaRepository,
                               UsuarioDesafioRepository usuarioDesafioRepository,
                               DesafioRepository desafioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.treinoRepository = treinoRepository;
        this.exercicioRepository = exercicioRepository;
        this.usuarioTreinoRepository = usuarioTreinoRepository;
        this.resultadoTreinoRepository = resultadoTreinoRepository;
        this.serieExecutadaRepository = serieExecutadaRepository;
        this.usuarioDesafioRepository = usuarioDesafioRepository;
        this.desafioRepository = desafioRepository;
    }

    @Operation(summary = "Buscar treino atual do usuário")
    @RequireApiKey(minPlan = ApiAccessPlan.ALUNO)
    @GetMapping("/usuarios/{usuarioId}/treino-atual")
    public TreinoAtualResponse getTreinoAtual(@PathVariable @Positive(message = "O ID do usuário deve ser maior que zero") Long usuarioId) {
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));

        var usuarioTreino = usuarioTreinoRepository.findByUsuarioIdAndAtivoTrue(usuarioId)
                .orElseThrow(() -> new TreinoNotFoundException(usuarioId));

        var treino = usuarioTreino.getTreino();
        List<ExercicioTreinoResponse> exercicios = treino.getExercicios().stream()
                .map(exercicio -> new ExercicioTreinoResponse(
                        exercicio.getId(),
                        exercicio.getNome(),
                        exercicio.getQuantidadeSeries(),
                        exercicio.getRepeticoes(),
                        exercicio.getGrupoMuscular().getNome(),
                        exercicio.getMusculo().getNome()
                ))
                .collect(Collectors.toList());

        return new TreinoAtualResponse(
                treino.getId(),
                treino.getNome(),
                treino.getObjetivo(),
                treino.getMetodologia(),
                exercicios
        );
    }

    @Operation(summary = "Finalizar treino", description = "Recebe as séries preenchidas no Android e salva a sessão finalizada no banco")
    @Parameter(name = "Idempotency-Key", in = ParameterIn.HEADER, required = true, description = "Chave única para evitar duplicidade em retries do POST")
    @RequireApiKey(minPlan = ApiAccessPlan.ALUNO)
    @PostMapping("/treinos/finalizar")
    @RequireIdempotency
    public ResponseEntity<FinalizarTreinoResponse> finalizarTreino(@Valid @RequestBody FinalizarTreinoRequest request) {
        var usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new UsuarioNotFoundException(request.getUsuarioId()));
        var treino = treinoRepository.findById(request.getTreinoId())
                .orElseThrow(() -> new TreinoNotFoundException(request.getTreinoId()));

        double volumeTotal = request.getSeries().stream()
                .mapToDouble(serie -> serie.getPeso() * serie.getRepeticoes())
                .sum();

        Double primeiroPeso = request.getSeries().get(0).getPeso();
        String resumoSeries = request.getSeries().stream()
                .map(serie -> "S" + serie.getNumeroSerie() + ": " + serie.getRepeticoes() + "x" + serie.getPeso() + "kg")
                .collect(Collectors.joining("; "));

        ResultadoTreino resultado = new ResultadoTreino();
        resultado.setUsuario(usuario);
        resultado.setTreino(treino);
        resultado.setData(request.getData() == null ? LocalDate.now() : request.getData());
        resultado.setListaSerieRepeticao(resumoSeries);
        resultado.setQuantidadeSeriesTreino(request.getSeries().size());
        resultado.setPesoAnterior(primeiroPeso);
        resultado.setPesoRecomendado(primeiroPeso);
        resultado.setVolumeTotal(Math.round(volumeTotal * 100.0) / 100.0);
        resultado.setStatus("FINALIZADO");
        resultado = resultadoTreinoRepository.save(resultado);

        for (SerieExecutadaRequest serieRequest : request.getSeries()) {
            var exercicio = exercicioRepository.findById(serieRequest.getExercicioId())
                    .orElseThrow(() -> new ExercicioNotFoundException(serieRequest.getExercicioId()));

            SerieExecutada serieExecutada = new SerieExecutada();
            serieExecutada.setResultadoTreino(resultado);
            serieExecutada.setExercicio(exercicio);
            serieExecutada.setNumeroSerie(serieRequest.getNumeroSerie());
            serieExecutada.setRepeticoes(serieRequest.getRepeticoes());
            serieExecutada.setPeso(serieRequest.getPeso());
            serieExecutadaRepository.save(serieExecutada);
        }

        return ResponseEntity.ok(new FinalizarTreinoResponse(resultado.getId(), "Treino finalizado com sucesso"));
    }

    @Operation(summary = "Buscar desafios do usuário")
    @RequireApiKey(minPlan = ApiAccessPlan.ALUNO)
    @GetMapping("/usuarios/{usuarioId}/desafios")
    public List<DashboardItemResponse> getDesafios(@PathVariable @Positive(message = "O ID do usuário deve ser maior que zero") Long usuarioId) {
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));

        var usuarioDesafios = usuarioDesafioRepository.findByUsuarioIdAndDesafioAtivoTrue(usuarioId);
        if (!usuarioDesafios.isEmpty()) {
            return usuarioDesafios.stream()
                    .map(usuarioDesafio -> new DashboardItemResponse(
                            usuarioDesafio.getDesafio().getId(),
                            usuarioDesafio.getDesafio().getTitulo(),
                            "Progresso: " + usuarioDesafio.getProgresso() + " de " + usuarioDesafio.getDesafio().getMeta()
                    ))
                    .toList();
        }

        return desafioRepository.findByAtivoTrue().stream()
                .map(desafio -> new DashboardItemResponse(
                        desafio.getId(),
                        desafio.getTitulo(),
                        desafio.getDescricao()
                ))
                .toList();
    }
}
