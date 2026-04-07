package Physique.api.infrastructure;

import Physique.api.entities.*;
import Physique.api.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class CarregaDados {

    private static final Logger log = LoggerFactory.getLogger(CarregaDados.class);

    @Bean
    CommandLineRunner initDatabase(GrupoMuscularRepository grupoRepository,
                                   MusculoRepository musculoRepository,
                                   UsuarioRepository usuarioRepository,
                                   ExercicioRepository exercicioRepository,
                                   TreinoRepository treinoRepository,
                                   ResultadoTreinoRepository resultadoRepository,
                                   TreinoSerieRepository treinoSerieRepository) {
        return args -> {
            GrupoMuscular peito = grupoRepository.save(new GrupoMuscular("Peito"));
            GrupoMuscular pernas = grupoRepository.save(new GrupoMuscular("Pernas"));

            Musculo peitoralMaior = musculoRepository.save(new Musculo("Peitoral maior", peito));
            Musculo quadriceps = musculoRepository.save(new Musculo("Quadríceps", pernas));

            Usuario jorge = usuarioRepository.save(new Usuario("Jorge Vieira", 24, "Hipertrofia", 78.5));

            Exercicio supino = new Exercicio();
            supino.setNome("Supino reto");
            supino.setRepeticoes(8);
            supino.setQuantidadeSeries(4);
            supino.setGrupoMuscular(peito);
            supino.setMusculo(peitoralMaior);
            supino.setDescricao("Descer a barra de forma controlada e empurrar até a extensão dos braços.");
            supino.setVideo("");
            supino = exercicioRepository.save(supino);

            Exercicio agachamento = new Exercicio();
            agachamento.setNome("Agachamento livre");
            agachamento.setRepeticoes(6);
            agachamento.setQuantidadeSeries(4);
            agachamento.setGrupoMuscular(pernas);
            agachamento.setMusculo(quadriceps);
            agachamento.setDescricao("Agachar mantendo coluna neutra e subir com controle.");
            agachamento.setVideo("");
            agachamento = exercicioRepository.save(agachamento);

            Treino treinoA = new Treino();
            treinoA.setNome("Treino A");
            treinoA.setObjetivo("Hipertrofia");
            treinoA.setMetodologia("Força");
            treinoA.setExercicios(List.of(supino, agachamento));
            treinoA = treinoRepository.save(treinoA);

            ResultadoTreino resultado = new ResultadoTreino();
            resultado.setTreino(treinoA);
            resultado.setUsuario(jorge);
            resultado.setData(LocalDate.now());
            resultado.setListaSerieRepeticao("S1: 10x40kg; S2: 8x45kg; S3: 6x50kg");
            resultado.setQuantidadeSeriesTreino(4);
            resultado.setPesoAnterior(80.0);
            resultado.setPesoRecomendado(72.0);
            resultadoRepository.save(resultado);

            TreinoSerie serie = new TreinoSerie("Supino inclinado", 70.0, 8);
            double umaRepMax = serie.getPeso() * (1 + (serie.getReps() / 30.0));
            serie.setUmaRepMax(Math.round(umaRepMax * 100.0) / 100.0);
            serie.setProxSerieMax(Math.round((umaRepMax * 0.75) * 100.0) / 100.0);
            serie.setProxSerieRep(8);
            treinoSerieRepository.save(serie);

            log.info("Dados iniciais carregados com sucesso.");
        };
    }
}
