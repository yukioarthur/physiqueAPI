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
import senac.tsi.physique.dto.TreinoAtualResumoResponse;
import senac.tsi.physique.entities.Usuario;
import senac.tsi.physique.entities.UsuarioTreino;
import senac.tsi.physique.exceptions.TreinoNotFoundException;
import senac.tsi.physique.repositories.TreinoRepository;
import senac.tsi.physique.repositories.UsuarioRepository;
import senac.tsi.physique.repositories.UsuarioTreinoRepository;
import senac.tsi.physique.services.GamificacaoService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@Tag(name = "auth", description = "Login e cadastro simples para MVP acadêmico, sem JWT")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final TreinoRepository treinoRepository;
    private final UsuarioTreinoRepository usuarioTreinoRepository;
    private final ApiKeyService apiKeyService;
    private final GamificacaoService gamificacaoService;

    public AuthController(UsuarioRepository usuarioRepository,
                          TreinoRepository treinoRepository,
                          UsuarioTreinoRepository usuarioTreinoRepository,
                          ApiKeyService apiKeyService,
                          GamificacaoService gamificacaoService) {
        this.usuarioRepository = usuarioRepository;
        this.treinoRepository = treinoRepository;
        this.usuarioTreinoRepository = usuarioTreinoRepository;
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

    @Operation(summary = "Cadastrar aluno", description = "Cria usuário, gera X-API-Key ALUNO, vincula treino inicial e inicializa XP/desafios.")
    @PostMapping("/auth/register")
    @Transactional
    public ResponseEntity<?> cadastrar(@Valid @RequestBody CadastroRequest request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Já existe uma conta com este e-mail"));
        }

        var treino = treinoRepository.findById(request.getTreinoId())
                .orElseThrow(() -> new TreinoNotFoundException(request.getTreinoId()));

        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setSenha(request.getSenha());
        usuario.setIdade(request.getIdade());
        usuario.setObjetivo(request.getObjetivo());
        usuario.setPesoCorporal(request.getPesoCorporal());
        usuario = usuarioRepository.save(usuario);

        UsuarioTreino vinculo = new UsuarioTreino();
        vinculo.setUsuario(usuario);
        vinculo.setTreino(treino);
        vinculo.setAtivo(true);
        vinculo.setDataInicio(LocalDate.now());
        usuarioTreinoRepository.save(vinculo);

        gamificacaoService.inicializarUsuario(usuario.getId());
        var apiKey = apiKeyService.createAlunoKeyForUsuario(usuario, "Android - " + usuario.getNome());

        var response = new CadastroResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                apiKey.getApiKey(),
                apiKey.getAccessPlan().name(),
                treino.getId(),
                treino.getNome(),
                "Conta criada com sucesso"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Listar treinos públicos para cadastro", description = "Usado antes do login para o novo aluno escolher um treino inicial.")
    @GetMapping("/auth/treinos-disponiveis")
    public List<TreinoAtualResumoResponse> treinosPublicosCadastro() {
        return treinoRepository.findAll().stream()
                .map(treino -> new TreinoAtualResumoResponse(
                        treino.getId(),
                        treino.getNome(),
                        treino.getCriadorNome()
                ))
                .toList();
    }
}
