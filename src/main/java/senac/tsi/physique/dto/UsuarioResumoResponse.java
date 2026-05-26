package senac.tsi.physique.dto;

public class UsuarioResumoResponse {
    private Long id;
    private String nome;
    private String primeiroNome;

    public UsuarioResumoResponse() {}

    public UsuarioResumoResponse(Long id, String nome, String primeiroNome) {
        this.id = id;
        this.nome = nome;
        this.primeiroNome = primeiroNome;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getPrimeiroNome() { return primeiroNome; }
    public void setPrimeiroNome(String primeiroNome) { this.primeiroNome = primeiroNome; }
}
