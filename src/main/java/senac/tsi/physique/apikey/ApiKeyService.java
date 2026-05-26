package senac.tsi.physique.apikey;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import senac.tsi.physique.dto.apikey.ApiKeyCreateRequest;
import senac.tsi.physique.dto.apikey.ApiKeyCreateResponse;
import senac.tsi.physique.dto.apikey.ApiKeyListResponse;
import senac.tsi.physique.entities.ApiKey;
import senac.tsi.physique.entities.Usuario;
import senac.tsi.physique.exceptions.UsuarioNotFoundException;
import senac.tsi.physique.repositories.ApiKeyRepository;
import senac.tsi.physique.repositories.UsuarioRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;

@Service
public class ApiKeyService {

    private static final String KEY_PREFIX = "phy_";
    private static final int VISIBLE_PREFIX_LENGTH = 12;

    private final ApiKeyRepository apiKeyRepository;
    private final UsuarioRepository usuarioRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public ApiKeyService(ApiKeyRepository apiKeyRepository, UsuarioRepository usuarioRepository) {
        this.apiKeyRepository = apiKeyRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public ApiKeyCreateResponse createApiKey(ApiKeyCreateRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new UsuarioNotFoundException(request.getUsuarioId()));

        String plainApiKey = generatePlainApiKey();
        ApiKey apiKey = new ApiKey();
        apiKey.setName(request.getName());
        apiKey.setKeyPrefix(extractPrefix(plainApiKey));
        apiKey.setKeyHash(sha256(plainApiKey));
        apiKey.setAccessPlan(request.getAccessPlan());
        apiKey.setStatus(ApiKeyStatus.ACTIVE);
        apiKey.setUsuario(usuario);
        apiKey.setExpiresAt(request.getExpiresAt());

        ApiKey saved = apiKeyRepository.save(apiKey);
        return new ApiKeyCreateResponse(
                saved.getId(),
                saved.getName(),
                plainApiKey,
                saved.getKeyPrefix(),
                saved.getAccessPlan(),
                saved.getStatus(),
                saved.getCreatedAt(),
                saved.getExpiresAt()
        );
    }

    @Transactional(readOnly = true)
    public List<ApiKeyListResponse> listByUsuario(Long usuarioId) {
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));

        return apiKeyRepository.findByUsuarioId(usuarioId).stream()
                .map(this::toListResponse)
                .toList();
    }

    @Transactional
    public void revoke(Long id) {
        ApiKey apiKey = apiKeyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("API key not found: " + id));
        apiKey.setStatus(ApiKeyStatus.REVOKED);
        apiKey.setRevokedAt(LocalDateTime.now());
        apiKeyRepository.save(apiKey);
    }

    @Transactional
    public ApiKeyAuthenticationContext authenticate(String plainApiKey) {
        String keyHash = sha256(plainApiKey);
        ApiKey apiKey = apiKeyRepository.findByKeyHash(keyHash)
                .orElseThrow(() -> new ApiKeyValidationException("Invalid API key"));

        if (apiKey.getStatus() != ApiKeyStatus.ACTIVE) {
            throw new ApiKeyValidationException("Inactive API key");
        }

        if (apiKey.getExpiresAt() != null && apiKey.getExpiresAt().isBefore(LocalDateTime.now())) {
            apiKey.setStatus(ApiKeyStatus.EXPIRED);
            apiKeyRepository.save(apiKey);
            throw new ApiKeyValidationException("Inactive API key");
        }

        apiKey.setLastUsedAt(LocalDateTime.now());
        apiKeyRepository.save(apiKey);

        Usuario usuario = apiKey.getUsuario();
        return new ApiKeyAuthenticationContext(
                apiKey.getId(),
                usuario == null ? null : usuario.getId(),
                usuario == null ? null : usuario.getEmail(),
                apiKey.getAccessPlan(),
                apiKey.getKeyPrefix()
        );
    }

    @Transactional(readOnly = true)
    public boolean hasActiveAdminKey() {
        return apiKeyRepository.existsByAccessPlanAndStatus(ApiAccessPlan.ADMIN, ApiKeyStatus.ACTIVE);
    }

    public String extractApiKey(String headerValue) {
        return headerValue == null || headerValue.isBlank() ? null : headerValue.trim();
    }

    private ApiKeyListResponse toListResponse(ApiKey apiKey) {
        return new ApiKeyListResponse(
                apiKey.getId(),
                apiKey.getName(),
                apiKey.getKeyPrefix(),
                apiKey.getAccessPlan(),
                apiKey.getStatus(),
                apiKey.getCreatedAt(),
                apiKey.getExpiresAt(),
                apiKey.getLastUsedAt()
        );
    }

    private String generatePlainApiKey() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return KEY_PREFIX + Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private String extractPrefix(String plainApiKey) {
        return plainApiKey.substring(0, Math.min(VISIBLE_PREFIX_LENGTH, plainApiKey.length()));
    }

    public static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 algorithm is not available", exception);
        }
    }
}
