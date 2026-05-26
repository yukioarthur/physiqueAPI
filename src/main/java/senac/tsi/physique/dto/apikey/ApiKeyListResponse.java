package senac.tsi.physique.dto.apikey;

import senac.tsi.physique.apikey.ApiAccessPlan;
import senac.tsi.physique.apikey.ApiKeyStatus;

import java.time.LocalDateTime;

public class ApiKeyListResponse {

    private Long id;
    private String name;
    private String keyPrefix;
    private ApiAccessPlan accessPlan;
    private ApiKeyStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime lastUsedAt;

    public ApiKeyListResponse(Long id,
                              String name,
                              String keyPrefix,
                              ApiAccessPlan accessPlan,
                              ApiKeyStatus status,
                              LocalDateTime createdAt,
                              LocalDateTime expiresAt,
                              LocalDateTime lastUsedAt) {
        this.id = id;
        this.name = name;
        this.keyPrefix = keyPrefix;
        this.accessPlan = accessPlan;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.lastUsedAt = lastUsedAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public LocalDateTime getLastUsedAt() {
        return lastUsedAt;
    }
}
