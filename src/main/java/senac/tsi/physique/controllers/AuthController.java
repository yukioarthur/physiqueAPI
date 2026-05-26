package senac.tsi.physique.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import senac.tsi.physique.dto.LoginRequest;
import senac.tsi.physique.dto.LoginResponse;
import senac.tsi.physique.repositories.UsuarioRepository;

@Validated
@RestController
@Tag(name = "auth", description = "Login simples para MVP acadêmico, sem JWT")
public class AuthController {

    private final UsuarioRepository usuarioRepository;

    public AuthController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Operation(summary = "Login simples", description = "Valida email e senha. MVP sem JWT; em produção, usar Spring Security e senha com hash.")
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

        return ResponseEntity.ok(new LoginResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                "Login realizado com sucesso"
        ));
    }
}
