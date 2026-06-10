package senac.tsi.physique.dto;

public class TreinoAtualResumoResponse {
    private Long id;
    private String nome;
    private String criadorPrimeiroNome;
    private String objetivo;
    private String metodologia;
    private String nivel;

    public TreinoAtualResumoResponse() {}

    public TreinoAtualResumoResponse(Long id, String nome, String criadorPrimeiroNome) {
        this(id, nome, criadorPrimeiroNome, null, null, null);
    }

    public TreinoAtualResumoResponse(Long id, String nome, String criadorPrimeiroNome, String objetivo, String metodologia, String nivel) {
        this.id = id;
        this.nome = nome;
        this.criadorPrimeiroNome = criadorPrimeiroNome;
        this.objetivo = objetivo;
        this.metodologia = metodologia;
        this.nivel = nivel;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCriadorPrimeiroNome() { return criadorPrimeiroNome; }
    public void setCriadorPrimeiroNome(String criadorPrimeiroNome) { this.criadorPrimeiroNome = criadorPrimeiroNome; }
    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }
    public String getMetodologia() { return metodologia; }
    public void setMetodologia(String metodologia) { this.metodologia = metodologia; }
    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }
}
