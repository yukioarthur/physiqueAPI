package senac.tsi.physique.dto;

public class CadastroResponse {
    private Long usuarioId;
    private String nome;
    private String email;
    private String apiKey;
    private String accessPlan;
    private Long treinoId;
    private String treinoNome;
    private String message;

    public CadastroResponse() {}

    public CadastroResponse(Long usuarioId, String nome, String email, String apiKey, String accessPlan, Long treinoId, String treinoNome, String message) {
        this.usuarioId = usuarioId;
        this.nome = nome;
        this.email = email;
        this.apiKey = apiKey;
        this.accessPlan = accessPlan;
        this.treinoId = treinoId;
        this.treinoNome = treinoNome;
        this.message = message;
    }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getAccessPlan() { return accessPlan; }
    public void setAccessPlan(String accessPlan) { this.accessPlan = accessPlan; }
    public Long getTreinoId() { return treinoId; }
    public void setTreinoId(Long treinoId) { this.treinoId = treinoId; }
    public String getTreinoNome() { return treinoNome; }
    public void setTreinoNome(String treinoNome) { this.treinoNome = treinoNome; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
