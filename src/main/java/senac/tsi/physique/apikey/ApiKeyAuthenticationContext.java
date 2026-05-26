package senac.tsi.physique.apikey;

public class ApiKeyAuthenticationContext {

    public static final String REQUEST_ATTRIBUTE_NAME = "apiKeyAuthenticationContext";

    private final Long apiKeyId;
    private final Long usuarioId;
    private final String usuarioEmail;
    private final ApiAccessPlan accessPlan;
    private final String keyPrefix;

    public ApiKeyAuthenticationContext(Long apiKeyId,
                                       Long usuarioId,
                                       String usuarioEmail,
                                       ApiAccessPlan accessPlan,
                                       String keyPrefix) {
        this.apiKeyId = apiKeyId;
        this.usuarioId = usuarioId;
        this.usuarioEmail = usuarioEmail;
        this.accessPlan = accessPlan;
        this.keyPrefix = keyPrefix;
    }

    public Long getApiKeyId() {
        return apiKeyId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getUsuarioEmail() {
        return usuarioEmail;
    }

    public ApiAccessPlan getAccessPlan() {
        return accessPlan;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }
}
