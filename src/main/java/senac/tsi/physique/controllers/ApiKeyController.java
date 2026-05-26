package senac.tsi.physique.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import senac.tsi.physique.apikey.ApiAccessPlan;
import senac.tsi.physique.apikey.RequireApiKey;
import senac.tsi.physique.dto.apikey.ApiKeyCreateRequest;
import senac.tsi.physique.dto.apikey.ApiKeyCreateResponse;
import senac.tsi.physique.dto.apikey.ApiKeyListResponse;
import senac.tsi.physique.apikey.ApiKeyService;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@Tag(name = "api-keys", description = "Geração, listagem e revogação de chaves de API")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    public ApiKeyController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @Operation(summary = "Bootstrap da primeira chave ADMIN", description = "Uso acadêmico: permite criar a primeira chave ADMIN quando ainda não existe nenhuma chave ADMIN ativa. Depois disso, retorna 409.")
    @PostMapping("/api-keys/bootstrap-admin")
    public ResponseEntity<?> bootstrapAdmin(@Valid @RequestBody ApiKeyCreateRequest request) {
        if (apiKeyService.hasActiveAdminKey()) {
            return ResponseEntity.status(409).body(Map.of("error", "Admin API key already exists"));
        }
        request.setAccessPlan(ApiAccessPlan.ADMIN);
        ApiKeyCreateResponse response = apiKeyService.createApiKey(request);
        return ResponseEntity.created(URI.create("/api-keys/" + response.getId())).body(response);
    }

    @Operation(summary = "Criar chave de API", description = "Requer X-API-Key ADMIN. A chave completa é retornada apenas neste momento.")
    @PostMapping("/api-keys")
    @RequireApiKey(minPlan = ApiAccessPlan.ADMIN)
    public ResponseEntity<ApiKeyCreateResponse> createApiKey(@Valid @RequestBody ApiKeyCreateRequest request) {
        ApiKeyCreateResponse response = apiKeyService.createApiKey(request);
        return ResponseEntity.created(URI.create("/api-keys/" + response.getId())).body(response);
    }

    @Operation(summary = "Listar chaves de API por usuário", description = "Requer X-API-Key ADMIN. Não revela a chave completa, apenas o prefixo.")
    @GetMapping("/api-keys/usuarios/{usuarioId}")
    @RequireApiKey(minPlan = ApiAccessPlan.ADMIN)
    public List<ApiKeyListResponse> listByUsuario(@PathVariable @Positive(message = "O ID do usuário deve ser maior que zero") Long usuarioId) {
        return apiKeyService.listByUsuario(usuarioId);
    }

    @Operation(summary = "Revogar chave de API", description = "Requer X-API-Key ADMIN. Não apaga fisicamente a chave; altera status para REVOKED.")
    @DeleteMapping("/api-keys/{id}")
    @RequireApiKey(minPlan = ApiAccessPlan.ADMIN)
    public ResponseEntity<Void> revoke(@PathVariable @Positive(message = "O ID deve ser maior que zero") Long id) {
        apiKeyService.revoke(id);
        return ResponseEntity.noContent().build();
    }
}
