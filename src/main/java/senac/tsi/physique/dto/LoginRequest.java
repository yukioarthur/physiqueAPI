package senac.tsi.physique.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "LoginRequest", description = "Payload de login simples do MVP acadêmico")
public class LoginRequest {
    @Schema(description = "E-mail do usuário", example = "jorge@email.com")
    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "O e-mail deve ser válido")
    private String email;

    @Schema(description = "Senha do usuário", example = "123456")
    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 4, max = 120, message = "A senha deve ter entre 4 e 120 caracteres")
    private String senha;

    public LoginRequest() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}
