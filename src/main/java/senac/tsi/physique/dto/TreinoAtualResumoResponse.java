package senac.tsi.physique.dto;

public class TreinoAtualResumoResponse {
    private Long id;
    private String nome;
    private String criadorPrimeiroNome;

    public TreinoAtualResumoResponse() {}

    public TreinoAtualResumoResponse(Long id, String nome, String criadorPrimeiroNome) {
        this.id = id;
        this.nome = nome;
        this.criadorPrimeiroNome = criadorPrimeiroNome;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCriadorPrimeiroNome() { return criadorPrimeiroNome; }
    public void setCriadorPrimeiroNome(String criadorPrimeiroNome) { this.criadorPrimeiroNome = criadorPrimeiroNome; }
}
