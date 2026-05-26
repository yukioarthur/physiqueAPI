package senac.tsi.physique.dto.apikey;

import senac.tsi.physique.apikey.ApiAccessPlan;
import senac.tsi.physique.apikey.ApiKeyStatus;

import java.time.LocalDateTime;

public class ApiKeyCreateResponse {

    private Long id;
    private String name;
    private String apiKey;
    private String keyPrefix;
    private ApiAccessPlan accessPlan;
    private ApiKeyStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public ApiKeyCreateResponse(Long id,
                                String name,
                                String apiKey,
                                String keyPrefix,
                                ApiAccessPlan accessPlan,
                                ApiKeyStatus status,
                                LocalDateTime createdAt,
                                LocalDateTime expiresAt) {
        this.id = id;
        this.name = name;
        this.apiKey = apiKey;
        this.keyPrefix = keyPrefix;
        this.accessPlan = accessPlan;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public ApiAccessPlan getAccessPlan() {
        return accessPlan;
    }

    public ApiKeyStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
}
