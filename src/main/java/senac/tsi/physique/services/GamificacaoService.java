package senac.tsi.physique.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import senac.tsi.physique.dto.*;
import senac.tsi.physique.entities.*;
import senac.tsi.physique.exceptions.UsuarioNotFoundException;
import senac.tsi.physique.repositories.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class GamificacaoService {

    private final UsuarioRepository usuarioRepository;
    private final DesafioRepository desafioRepository;
    private final UsuarioDesafioRepository usuarioDesafioRepository;
    private final UsuarioXpRepository usuarioXpRepository;
    private final ResultadoTreinoRepository resultadoTreinoRepository;
    private final AtividadeAerobicaRepository atividadeAerobicaRepository;

    public GamificacaoService(UsuarioRepository usuarioRepository,
                              DesafioRepository desafioRepository,
                              UsuarioDesafioRepository usuarioDesafioRepository,
                              UsuarioXpRepository usuarioXpRepository,
                              ResultadoTreinoRepository resultadoTreinoRepository,
                              AtividadeAerobicaRepository atividadeAerobicaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.desafioRepository = desafioRepository;
        this.usuarioDesafioRepository = usuarioDesafioRepository;
        this.usuarioXpRepository = usuarioXpRepository;
        this.resultadoTreinoRepository = resultadoTreinoRepository;
        this.atividadeAerobicaRepository = atividadeAerobicaRepository;
    }


    @Transactional
    public void inicializarUsuario(Long usuarioId) {
        var usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));

        getOuCriarXp(usuarioId);
        desafioRepository.findByAtivoTrue().forEach(desafio ->
                usuarioDesafioRepository.findByUsuarioIdAndDesafioId(usuarioId, desafio.getId())
                        .orElseGet(() -> criarUsuarioDesafio(usuario, desafio))
        );
    }

    @Transactional(readOnly = true)
    public GamificacaoResumoResponse getResumo(Long usuarioId) {
        var xp = usuarioXpRepository.findByUsuarioId(usuarioId).orElse(null);
        int totalDesafios = desafioRepository.findByAtivoTrue().size();
        int concluidos = (int) usuarioDesafioRepository.countByUsuarioIdAndConcluidoTrue(usuarioId);

        int xpTotal = xp == null ? 0 : nvl(xp.getXpTotal());
        int nivel = xp == null ? calcularNivel(xpTotal) : nvl(xp.getNivel());
        int xpSemana = xp == null ? 0 : nvl(xp.getXpSemana());
        int meta = xp == null ? 1000 : Math.max(1, nvl(xp.getMetaXpSemana()));
        int percentual = Math.min(100, Math.round((xpSemana * 100f) / meta));
        String tituloNivel = nivel >= 4 ? "Atleta master" : nivel >= 3 ? "Ritmo forte" : nivel >= 2 ? "Em evolução" : "Começando bem";
        String texto = xpSemana + " de " + meta + " XP da semana. " + concluidos + " de " + totalDesafios + " desafios concluídos.";

        return new GamificacaoResumoResponse(xpTotal, nivel, xpSemana, meta, percentual, concluidos, totalDesafios, tituloNivel, texto);
    }

    @Transactional
    public List<DesafioUsuarioResponse> listarDesafios(Long usuarioId) {
        var usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));

        return desafioRepository.findByAtivoTrue().stream()
                .map(desafio -> usuarioDesafioRepository.findByUsuarioIdAndDesafioId(usuarioId, desafio.getId())
                        .orElseGet(() -> criarUsuarioDesafio(usuario, desafio)))
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public DesafioUsuarioResponse concluirManual(Long usuarioId, Long desafioId, boolean concluir) {
        var usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));
        var desafio = desafioRepository.findById(desafioId)
                .orElseThrow(() -> new IllegalArgumentException("Desafio não encontrado: " + desafioId));

        if (!Boolean.TRUE.equals(desafio.getManual())) {
            throw new IllegalArgumentException("Este desafio é concluído automaticamente pelo app.");
        }

        var usuarioDesafio = usuarioDesafioRepository.findByUsuarioIdAndDesafioId(usuarioId, desafioId)
                .orElseGet(() -> criarUsuarioDesafio(usuario, desafio));

        if (concluir) {
            concluirDesafio(usuarioDesafio);
        } else {
            reabrirDesafio(usuarioDesafio);
        }
        return toResponse(usuarioDesafioRepository.save(usuarioDesafio));
    }

    @Transactional
    public void processarTreinoFinalizado(Long usuarioId) {
        desafioRepository.findByRegra("FINALIZAR_TREINO_ATUAL").ifPresent(desafio -> concluirPorRegra(usuarioId, desafio));
        desafioRepository.findByRegra("REGISTRAR_TODAS_SERIES").ifPresent(desafio -> concluirPorRegra(usuarioId, desafio));

        LocalDate hoje = LocalDate.now();
        LocalDate segunda = hoje.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate domingo = hoje.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        long treinosSemana = resultadoTreinoRepository.countByUsuarioIdAndDataBetweenAndStatus(usuarioId, segunda, domingo, "FINALIZADO");
        if (treinosSemana >= 3) {
            desafioRepository.findByRegra("CONSISTENCIA_3_TREINOS").ifPresent(desafio -> concluirPorRegra(usuarioId, desafio));
        }
    }

    @Transactional
    public CaminhadaResponse registrarCaminhada(Long usuarioId, CaminhadaRequest request) {
        var usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));

        int duracao = Math.max(0, request.getDuracaoSegundos());
        double distancia = Math.max(0.0, request.getDistanciaMetros());
        int xp = calcularXpCaminhada(duracao, distancia);

        var atividade = new AtividadeAerobica();
        atividade.setUsuario(usuario);
        atividade.setTipo(request.getTipo() == null || request.getTipo().isBlank() ? "CAMINHADA" : request.getTipo());
        atividade.setDistanciaMetros(distancia);
        atividade.setDuracaoSegundos(duracao);
        atividade.setXpGerado(xp);
        atividade.setDataFim(LocalDateTime.now());
        atividade.setDataInicio(LocalDateTime.now().minusSeconds(duracao));
        atividade.setObservacao(request.getObservacao());
        atividade = atividadeAerobicaRepository.save(atividade);

        adicionarXp(usuarioId, xp);
        if (duracao >= 20 * 60) {
            desafioRepository.findByRegra("CAMINHADA_20_MIN").ifPresent(desafio -> concluirPorRegra(usuarioId, desafio));
        }

        return new CaminhadaResponse(atividade.getId(), atividade.getTipo(), atividade.getDistanciaMetros(), atividade.getDuracaoSegundos(), atividade.getXpGerado(), atividade.getDataInicio(), atividade.getDataFim());
    }

    @Transactional(readOnly = true)
    public List<CaminhadaResponse> listarCaminhadas(Long usuarioId) {
        return atividadeAerobicaRepository.findTop10ByUsuarioIdOrderByDataFimDesc(usuarioId)
                .stream()
                .map(a -> new CaminhadaResponse(a.getId(), a.getTipo(), a.getDistanciaMetros(), a.getDuracaoSegundos(), a.getXpGerado(), a.getDataInicio(), a.getDataFim()))
                .toList();
    }

    private UsuarioDesafio criarUsuarioDesafio(Usuario usuario, Desafio desafio) {
        var ud = new UsuarioDesafio();
        ud.setUsuario(usuario);
        ud.setDesafio(desafio);
        ud.setProgresso(0);
        ud.setProgressoMeta(desafio.getMeta() == null ? 1 : desafio.getMeta());
        ud.setStatus("PENDENTE");
        return usuarioDesafioRepository.save(ud);
    }

    private void concluirPorRegra(Long usuarioId, Desafio desafio) {
        var usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));
        var ud = usuarioDesafioRepository.findByUsuarioIdAndDesafioId(usuarioId, desafio.getId())
                .orElseGet(() -> criarUsuarioDesafio(usuario, desafio));
        concluirDesafio(ud);
        usuarioDesafioRepository.save(ud);
    }

    private void concluirDesafio(UsuarioDesafio ud) {
        if (Boolean.TRUE.equals(ud.getConcluido())) {
            return;
        }
        int xp = ud.getDesafio().getXpAplicado();
        ud.setConcluido(true);
        ud.setStatus("CONCLUIDO");
        ud.setProgresso(ud.getProgressoMeta() == null ? 1 : ud.getProgressoMeta());
        ud.setXpGanho(xp);
        ud.setConcluidoEm(LocalDateTime.now());
        adicionarXp(ud.getUsuario().getId(), xp);
    }

    private void reabrirDesafio(UsuarioDesafio ud) {
        if (!Boolean.TRUE.equals(ud.getConcluido())) {
            return;
        }
        int xp = nvl(ud.getXpGanho());
        ud.setConcluido(false);
        ud.setStatus("PENDENTE");
        ud.setProgresso(0);
        ud.setXpGanho(0);
        ud.setConcluidoEm(null);
        removerXp(ud.getUsuario().getId(), xp);
    }

    private void adicionarXp(Long usuarioId, int xpGanho) {
        var xp = getOuCriarXp(usuarioId);
        xp.setXpTotal(nvl(xp.getXpTotal()) + xpGanho);
        xp.setXpSemana(nvl(xp.getXpSemana()) + xpGanho);
        xp.setNivel(calcularNivel(xp.getXpTotal()));
        xp.setDesafiosConcluidos((int) usuarioDesafioRepository.countByUsuarioIdAndConcluidoTrue(usuarioId));
        xp.setAtualizadoEm(LocalDateTime.now());
        usuarioXpRepository.save(xp);
    }

    private void removerXp(Long usuarioId, int xpRemovido) {
        var xp = getOuCriarXp(usuarioId);
        xp.setXpTotal(Math.max(0, nvl(xp.getXpTotal()) - xpRemovido));
        xp.setXpSemana(Math.max(0, nvl(xp.getXpSemana()) - xpRemovido));
        xp.setNivel(calcularNivel(xp.getXpTotal()));
        xp.setDesafiosConcluidos((int) usuarioDesafioRepository.countByUsuarioIdAndConcluidoTrue(usuarioId));
        xp.setAtualizadoEm(LocalDateTime.now());
        usuarioXpRepository.save(xp);
    }

    private UsuarioXp getOuCriarXp(Long usuarioId) {
        return usuarioXpRepository.findByUsuarioId(usuarioId).orElseGet(() -> {
            var usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));
            var xp = new UsuarioXp();
            xp.setUsuario(usuario);
            xp.setXpTotal(0);
            xp.setXpSemana(0);
            xp.setMetaXpSemana(1000);
            xp.setNivel(1);
            return usuarioXpRepository.save(xp);
        });
    }

    private DesafioUsuarioResponse toResponse(UsuarioDesafio ud) {
        var d = ud.getDesafio();
        return new DesafioUsuarioResponse(
                d.getId(),
                d.getTitulo(),
                d.getDescricao(),
                d.getTipo(),
                d.getCategoria(),
                d.getXpAplicado(),
                d.getRegra(),
                d.getManual(),
                d.getDica(),
                ud.getStatus(),
                ud.getProgresso(),
                ud.getProgressoMeta(),
                ud.getConcluido()
        );
    }

    private int calcularNivel(int xpTotal) {
        return Math.max(1, (xpTotal / 500) + 1);
    }

    private int calcularXpCaminhada(int duracaoSegundos, double distanciaMetros) {
        int xpPorTempo = Math.min(80, duracaoSegundos / 30);
        int xpPorDistancia = Math.min(50, (int) Math.round(distanciaMetros / 100.0));
        return Math.max(10, xpPorTempo + xpPorDistancia);
    }

    private int nvl(Integer valor) {
        return valor == null ? 0 : valor;
    }
}
