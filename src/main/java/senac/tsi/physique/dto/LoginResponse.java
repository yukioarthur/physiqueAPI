package senac.tsi.physique.dto;

public class LoginResponse {
    private Long usuarioId;
    private String nome;
    private String email;
    private String message;

    public LoginResponse() {}

    public LoginResponse(Long usuarioId, String nome, String email, String message) {
        this.usuarioId = usuarioId;
        this.nome = nome;
        this.email = email;
        this.message = message;
    }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
