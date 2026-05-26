package senac.tsi.physique.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import senac.tsi.physique.entities.Exercicio;
import senac.tsi.physique.entities.GrupoMuscular;
import senac.tsi.physique.entities.Musculo;
import senac.tsi.physique.entities.ResultadoTreino;
import senac.tsi.physique.entities.Treino;
import senac.tsi.physique.entities.TreinoSerie;
import senac.tsi.physique.entities.Usuario;
import senac.tsi.physique.repositories.ExercicioRepository;
import senac.tsi.physique.repositories.GrupoMuscularRepository;
import senac.tsi.physique.repositories.MusculoRepository;
import senac.tsi.physique.repositories.ResultadoTreinoRepository;
import senac.tsi.physique.repositories.TreinoRepository;
import senac.tsi.physique.repositories.TreinoSerieRepository;
import senac.tsi.physique.repositories.UsuarioRepository;

import java.time.LocalDate;
import java.util.List;

@Configuration
@Profile("local")
public class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(
            GrupoMuscularRepository grupoMuscularRepository,
            MusculoRepository musculoRepository,
            UsuarioRepository usuarioRepository,
            ExercicioRepository exercicioRepository,
            TreinoRepository treinoRepository,
            ResultadoTreinoRepository resultadoTreinoRepository,
            TreinoSerieRepository treinoSerieRepository) {

        return args -> {
            GrupoMuscular peito = grupoMuscularRepository.save(new GrupoMuscular("Peito"));
            GrupoMuscular pernas = grupoMuscularRepository.save(new GrupoMuscular("Pernas"));

            Musculo peitoralMaior = musculoRepository.save(new Musculo("Peitoral maior", peito));
            Musculo quadriceps = musculoRepository.save(new Musculo("Quadriceps", pernas));

            Usuario jorge = usuarioRepository.save(new Usuario("Jorge Vieira", "jorge@email.com", "123456", 24, "Hipertrofia", 78.5));

            Exercicio supino = new Exercicio();
            supino.setNome("Supino reto");
            supino.setRepeticoes(8);
            supino.setQuantidadeSeries(4);
            supino.setGrupoMuscular(peito);
            supino.setMusculo(peitoralMaior);
            supino.setDescricao("Descer a barra de forma controlada e empurrar ate a extensao dos bracos.");
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
            treinoA.setMetodologia("Forca");
            treinoA.setCriadorNome("Carlos Eduardo");
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
            resultado.setVolumeTotal(1110.0);
            resultado.setStatus("FINALIZADO");
            log.info("Preloading " + resultadoTreinoRepository.save(resultado));

            TreinoSerie serie = new TreinoSerie("Supino inclinado", 70.0, 8);
            double umaRepMax = serie.getPeso() * (1 + (serie.getReps() / 30.0));
            serie.setUmaRepMax(Math.round(umaRepMax * 100.0) / 100.0);
            serie.setProxSerieMax(Math.round((umaRepMax * 0.75) * 100.0) / 100.0);
            serie.setProxSerieRep(8);
            log.info("Preloading " + treinoSerieRepository.save(serie));
        };
    }
}
