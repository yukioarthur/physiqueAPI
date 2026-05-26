package senac.tsi.physique.dto.apikey;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import senac.tsi.physique.apikey.ApiAccessPlan;

import java.time.LocalDateTime;

@Schema(name = "ApiKeyCreateRequest", description = "Payload para gerar uma nova chave de API")
public class ApiKeyCreateRequest {

    @Schema(description = "ID do usuário dono da chave", example = "1")
    @NotNull(message = "O usuário é obrigatório")
    @Positive(message = "O ID do usuário deve ser maior que zero")
    private Long usuarioId;

    @Schema(description = "Nome amigável da chave", example = "Chave do professor João")
    @NotBlank(message = "O nome da chave é obrigatório")
    @Size(max = 120, message = "O nome da chave deve ter no máximo 120 caracteres")
    private String name;

    @Schema(description = "Plano de acesso da chave", example = "PROFESSOR")
    @NotNull(message = "O plano de acesso é obrigatório")
    private ApiAccessPlan accessPlan;

    @Schema(description = "Data opcional de expiração da chave", example = "2026-12-31T23:59:59")
    @FutureOrPresent(message = "A expiração da chave deve ser presente ou futura")
    private LocalDateTime expiresAt;

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public ApiAccessPlan getAccessPlan() { return accessPlan; }
    public void setAccessPlan(ApiAccessPlan accessPlan) { this.accessPlan = accessPlan; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}
