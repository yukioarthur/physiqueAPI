package senac.tsi.physique.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import senac.tsi.physique.apikey.ApiKeyService;
import senac.tsi.physique.dto.CadastroRequest;
import senac.tsi.physique.dto.CadastroResponse;
import senac.tsi.physique.dto.LoginRequest;
import senac.tsi.physique.dto.LoginResponse;
import senac.tsi.physique.dto.PlanoTreinoCadastroResponse;
import senac.tsi.physique.dto.TreinoAtualResumoResponse;
import senac.tsi.physique.entities.PlanoTreino;
import senac.tsi.physique.entities.PlanoTreinoItem;
import senac.tsi.physique.entities.Treino;
import senac.tsi.physique.entities.Usuario;
import senac.tsi.physique.entities.UsuarioPlanoTreino;
import senac.tsi.physique.entities.UsuarioTreino;
import senac.tsi.physique.exceptions.TreinoNotFoundException;
import senac.tsi.physique.repositories.PlanoTreinoRepository;
import senac.tsi.physique.repositories.TreinoRepository;
import senac.tsi.physique.repositories.UsuarioPlanoTreinoRepository;
import senac.tsi.physique.repositories.UsuarioRepository;
import senac.tsi.physique.repositories.UsuarioTreinoRepository;
import senac.tsi.physique.services.GamificacaoService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@Tag(name = "auth", description = "Login e cadastro simples para MVP acadêmico, sem JWT")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final TreinoRepository treinoRepository;
    private final UsuarioTreinoRepository usuarioTreinoRepository;
    private final UsuarioPlanoTreinoRepository usuarioPlanoTreinoRepository;
    private final PlanoTreinoRepository planoTreinoRepository;
    private final ApiKeyService apiKeyService;
    private final GamificacaoService gamificacaoService;

    public AuthController(UsuarioRepository usuarioRepository,
                          TreinoRepository treinoRepository,
                          UsuarioTreinoRepository usuarioTreinoRepository,
                          UsuarioPlanoTreinoRepository usuarioPlanoTreinoRepository,
                          PlanoTreinoRepository planoTreinoRepository,
                          ApiKeyService apiKeyService,
                          GamificacaoService gamificacaoService) {
        this.usuarioRepository = usuarioRepository;
        this.treinoRepository = treinoRepository;
        this.usuarioTreinoRepository = usuarioTreinoRepository;
        this.usuarioPlanoTreinoRepository = usuarioPlanoTreinoRepository;
        this.planoTreinoRepository = planoTreinoRepository;
        this.apiKeyService = apiKeyService;
        this.gamificacaoService = gamificacaoService;
    }

    @Operation(summary = "Login simples", description = "Valida email e senha. Retorna uma X-API-Key ALUNO vinculada ao usuário para o app Android.")
    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        var usuarioOptional = usuarioRepository.findByEmail(request.getEmail());

        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(null, null, request.getEmail(), "Email ou senha inválidos"));
        }

        var usuario = usuarioOptional.get();
        if (!usuario.getSenha().equals(request.getSenha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(null, null, request.getEmail(), "Email ou senha inválidos"));
        }

        var apiKey = apiKeyService.createAlunoKeyForUsuario(usuario, "Android - " + usuario.getNome());
        gamificacaoService.inicializarUsuario(usuario.getId());

        return ResponseEntity.ok(new LoginResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                apiKey.getApiKey(),
                apiKey.getAccessPlan().name(),
                "Login realizado com sucesso"
        ));
    }

    @Operation(summary = "Cadastrar aluno", description = "Cria usuário, gera X-API-Key ALUNO, vincula plano de treino inicial e inicializa XP/desafios.")
    @PostMapping("/auth/register")
    @Transactional
    public ResponseEntity<?> cadastrar(@Valid @RequestBody CadastroRequest request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Já existe uma conta com este e-mail"));
        }

        if (request.getPlanoTreinoId() == null && request.getTreinoId() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Escolha um plano de treino inicial"));
        }

        PlanoTreino plano = null;
        List<PlanoTreinoItem> itensPlano = List.of();
        Treino primeiroTreino;

        if (request.getPlanoTreinoId() != null) {
            plano = planoTreinoRepository.findByIdAndAtivoTrue(request.getPlanoTreinoId())
                    .orElseThrow(() -> new IllegalArgumentException("Plano de treino não encontrado: " + request.getPlanoTreinoId()));
            itensPlano = plano.getItens().stream()
                    .sorted(Comparator.comparing(PlanoTreinoItem::getOrdem))
                    .toList();

            if (itensPlano.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "O plano escolhido ainda não possui treinos vinculados"));
            }
            primeiroTreino = itensPlano.get(0).getTreino();
        } else {
            primeiroTreino = treinoRepository.findById(request.getTreinoId())
                    .orElseThrow(() -> new TreinoNotFoundException(request.getTreinoId()));
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setSenha(request.getSenha());
        usuario.setIdade(request.getIdade());
        usuario.setObjetivo(request.getObjetivo());
        usuario.setPesoCorporal(request.getPesoCorporal());
        usuario = usuarioRepository.save(usuario);

        if (plano != null) {
            UsuarioPlanoTreino usuarioPlano = new UsuarioPlanoTreino();
            usuarioPlano.setUsuario(usuario);
            usuarioPlano.setPlanoTreino(plano);
            usuarioPlano.setAtivo(true);
            usuarioPlanoTreinoRepository.save(usuarioPlano);

            boolean primeiro = true;
            for (PlanoTreinoItem item : itensPlano) {
                UsuarioTreino vinculo = new UsuarioTreino();
                vinculo.setUsuario(usuario);
                vinculo.setTreino(item.getTreino());
                vinculo.setAtivo(primeiro);
                vinculo.setDataInicio(LocalDate.now());
                usuarioTreinoRepository.save(vinculo);
                primeiro = false;
            }
        } else {
            UsuarioTreino vinculo = new UsuarioTreino();
            vinculo.setUsuario(usuario);
            vinculo.setTreino(primeiroTreino);
            vinculo.setAtivo(true);
            vinculo.setDataInicio(LocalDate.now());
            usuarioTreinoRepository.save(vinculo);
        }

        gamificacaoService.inicializarUsuario(usuario.getId());
        var apiKey = apiKeyService.createAlunoKeyForUsuario(usuario, "Android - " + usuario.getNome());

        var response = new CadastroResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                apiKey.getApiKey(),
                apiKey.getAccessPlan().name(),
                primeiroTreino.getId(),
                primeiroTreino.getNome(),
                plano == null ? null : plano.getId(),
                plano == null ? null : plano.getNome(),
                "Conta criada com sucesso"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Listar planos públicos para cadastro", description = "Usado antes do login para o novo aluno escolher um plano de treinos inicial.")
    @GetMapping("/auth/planos-treino")
    public List<PlanoTreinoCadastroResponse> planosPublicosCadastro() {
        return planoTreinoRepository.findByAtivoTrueOrderByObjetivoAscOrdemAscNomeAsc().stream()
                .map(this::toPlanoCadastroResponse)
                .toList();
    }

    @Operation(summary = "Listar treinos públicos para cadastro", description = "Mantido por compatibilidade. O onboarding novo deve usar /auth/planos-treino.")
    @GetMapping("/auth/treinos-disponiveis")
    public List<TreinoAtualResumoResponse> treinosPublicosCadastro() {
        return treinoRepository.findAll().stream()
                .map(treino -> new TreinoAtualResumoResponse(
                        treino.getId(),
                        treino.getNome(),
                        treino.getCriadorNome(),
                        treino.getObjetivo(),
                        treino.getMetodologia(),
                        nivelTreino(treino.getNome(), treino.getMetodologia())
                ))
                .toList();
    }

    private PlanoTreinoCadastroResponse toPlanoCadastroResponse(PlanoTreino plano) {
        List<String> treinos = plano.getItens().stream()
                .sorted(Comparator.comparing(PlanoTreinoItem::getOrdem))
                .map(item -> item.getNomeExibicao() == null || item.getNomeExibicao().isBlank()
                        ? nomeTreinoParaAluno(item.getTreino())
                        : item.getNomeExibicao())
                .toList();

        return new PlanoTreinoCadastroResponse(
                plano.getId(),
                plano.getNome(),
                plano.getObjetivo(),
                plano.getResumo(),
                plano.getNivel(),
                plano.getFrequenciaSemanal(),
                plano.getFoco(),
                splitLista(plano.getTags()),
                splitLista(plano.getMetodologias()),
                treinos
        );
    }

    private List<String> splitLista(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }
        return Arrays.stream(texto.split("\\|"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }

    private String nomeTreinoParaAluno(Treino treino) {
        String nome = treino.getNome() == null ? "Treino" : treino.getNome().toLowerCase();
        if (nome.contains("push") || nome.contains("peito")) return "Treino A — Peito, ombros e tríceps";
        if (nome.contains("pull") || nome.contains("costas")) return "Treino B — Costas e bíceps";
        if (nome.contains("legs") || nome.contains("pernas")) return "Treino C — Pernas completas";
        if (nome.contains("full") || nome.contains("corpo inteiro")) return "Treino corpo inteiro";
        return treino.getNome();
    }

    private String nivelTreino(String nome, String metodologia) {
        String texto = ((nome == null ? "" : nome) + " " + (metodologia == null ? "" : metodologia)).toLowerCase();
        if (texto.contains("avanç") || texto.contains("avanc")) return "avançado";
        if (texto.contains("intermedi")) return "intermediário";
        if (texto.contains("iniciante") || texto.contains("base") || texto.contains("adapta")) return "iniciante";
        return "geral";
    }
}
