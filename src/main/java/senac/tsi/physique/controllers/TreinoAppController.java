package senac.tsi.physique.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import senac.tsi.physique.apikey.ApiAccessPlan;
import senac.tsi.physique.apikey.RequireApiKey;
import senac.tsi.physique.dto.*;
import senac.tsi.physique.entities.ResultadoTreino;
import senac.tsi.physique.entities.SerieExecutada;
import senac.tsi.physique.entities.Treino;
import senac.tsi.physique.entities.UsuarioTreino;
import senac.tsi.physique.exceptions.ExercicioNotFoundException;
import senac.tsi.physique.idempotency.RequireIdempotency;
import senac.tsi.physique.exceptions.TreinoNotFoundException;
import senac.tsi.physique.exceptions.UsuarioNotFoundException;
import senac.tsi.physique.repositories.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
    @Transactional(readOnly = true)
    public TreinoAtualResponse getTreinoAtual(@PathVariable @Positive(message = "O ID do usuário deve ser maior que zero") Long usuarioId) {
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));

        var usuarioTreino = usuarioTreinoRepository.findByUsuarioIdAndAtivoTrue(usuarioId)
                .orElseThrow(() -> new TreinoNotFoundException(usuarioId));

        return montarTreinoAtualResponse(usuarioTreino.getTreino());
    }

    @Operation(summary = "Iniciar treino do usuário", description = "Marca um treino como ativo para o usuário e desativa qualquer treino ativo anterior")
    @RequireApiKey(minPlan = ApiAccessPlan.ALUNO)
    @PostMapping("/usuarios/{usuarioId}/treinos/{treinoId}/iniciar")
    @Transactional
    public TreinoAtualResponse iniciarTreino(
            @PathVariable @Positive(message = "O ID do usuário deve ser maior que zero") Long usuarioId,
            @PathVariable @Positive(message = "O ID do treino deve ser maior que zero") Long treinoId
    ) {
        var usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));
        var treino = treinoRepository.findByIdComDetalhes(treinoId)
                .orElseThrow(() -> new TreinoNotFoundException(treinoId));

        usuarioTreinoRepository.findAllByUsuarioIdAndAtivoTrue(usuarioId).forEach(vinculoAtivo -> {
            vinculoAtivo.setAtivo(false);
            usuarioTreinoRepository.save(vinculoAtivo);
        });

        UsuarioTreino usuarioTreino = usuarioTreinoRepository.findByUsuarioIdAndTreinoId(usuarioId, treinoId)
                .orElseGet(UsuarioTreino::new);

        usuarioTreino.setUsuario(usuario);
        usuarioTreino.setTreino(treino);
        usuarioTreino.setAtivo(true);
        usuarioTreino.setDataInicio(LocalDate.now());
        usuarioTreinoRepository.save(usuarioTreino);

        return montarTreinoAtualResponse(treino);
    }

    private TreinoAtualResponse montarTreinoAtualResponse(Treino treino) {
        var seriesPrescritas = treinoSerieRepository.findByTreinoBaseIdOrderByOrdemExercicioAscNumeroSerieAsc(treino.getId());

        List<ExercicioTreinoResponse> exercicios;
        if (!seriesPrescritas.isEmpty()) {
            var exerciciosMap = new LinkedHashMap<Long, ExercicioTreinoResponse>();
            var seriesPorExercicio = new LinkedHashMap<Long, List<SeriePrescritaResponse>>();

            for (var serie : seriesPrescritas) {
                var exercicio = serie.getExercicio();
                if (exercicio == null || exercicio.getId() == null) {
                    continue;
                }

                Long exercicioId = exercicio.getId();
                seriesPorExercicio.computeIfAbsent(exercicioId, id -> new ArrayList<>())
                        .add(new SeriePrescritaResponse(
                                serie.getId(),
                                serie.getNumeroSerie(),
                                serie.getRepeticoesMin(),
                                serie.getRepeticoesMax(),
                                serie.getCargaSugerida(),
                                serie.getRir(),
                                serie.getDescansoSegundos(),
                                serie.getTempoExecucao(),
                                serie.getObservacao()
                        ));

                exerciciosMap.computeIfAbsent(exercicioId, id -> new ExercicioTreinoResponse(
                        exercicio.getId(),
                        exercicio.getNome(),
                        0,
                        serie.getRepeticoesMax(),
                        serie.getRepeticoesMin(),
                        serie.getRepeticoesMax(),
                        serie.getDescansoSegundos(),
                        exercicio.getGrupoMuscular() == null ? "-" : exercicio.getGrupoMuscular().getNome(),
                        exercicio.getMusculo() == null ? "-" : exercicio.getMusculo().getNome(),
                        exercicio.getDescricao(),
                        serie.getObservacao(),
                        new ArrayList<>()
                ));
            }

            exercicios = exerciciosMap.values().stream()
                    .peek(exercicio -> {
                        var series = seriesPorExercicio.getOrDefault(exercicio.getId(), List.of());
                        exercicio.setSeries(series);
                        exercicio.setQuantidadeSeries(series.size());
                        exercicio.setRepeticoesMin(series.stream()
                                .map(SeriePrescritaResponse::getRepeticoesMin)
                                .filter(v -> v != null)
                                .min(Integer::compareTo)
                                .orElse(exercicio.getRepeticoesMin()));
                        exercicio.setRepeticoesMax(series.stream()
                                .map(SeriePrescritaResponse::getRepeticoesMax)
                                .filter(v -> v != null)
                                .max(Integer::compareTo)
                                .orElse(exercicio.getRepeticoesMax()));
                        exercicio.setRepeticoes(exercicio.getRepeticoesMax());
                    })
                    .collect(Collectors.toList());
        } else {
            exercicios = treino.getExercicios().stream()
                    .map(exercicio -> new ExercicioTreinoResponse(
                            exercicio.getId(),
                            exercicio.getNome(),
                            exercicio.getQuantidadeSeries(),
                            exercicio.getRepeticoes(),
                            exercicio.getGrupoMuscular() == null ? "-" : exercicio.getGrupoMuscular().getNome(),
                            exercicio.getMusculo() == null ? "-" : exercicio.getMusculo().getNome()
                    ))
                    .collect(Collectors.toList());
        }

        return new TreinoAtualResponse(
                treino.getId(),
                treino.getNome(),
                treino.getObjetivo(),
                treino.getMetodologia(),
                exercicios
        );
    }


    @Operation(summary = "Listar treinos disponíveis para iniciar no app")
    @RequireApiKey(minPlan = ApiAccessPlan.ALUNO)
    @GetMapping("/treinos-disponiveis")
    @Transactional(readOnly = true)
    public List<TreinoAtualResumoResponse> getTreinosDisponiveis() {
        return treinoRepository.findAll().stream()
                .map(treino -> new TreinoAtualResumoResponse(
                        treino.getId(),
                        treino.getNome(),
                        treino.getCriadorNome()
                ))
                .toList();
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
