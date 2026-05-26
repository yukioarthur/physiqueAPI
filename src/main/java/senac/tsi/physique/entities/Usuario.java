package senac.tsi.physique.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 120)
    private String nome;

    @NotBlank
    @Email
    @Size(max = 160)
    @Column(nullable = false, unique = true, length = 160)
    private String email;

    // MVP acadêmico: senha simples para integração rápida com Android.
    // Em produção, usar hash seguro, por exemplo BCrypt.
    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String senha;

    @NotNull
    @Min(1)
    @Max(120)
    private Integer idade;

    @NotBlank
    @Size(max = 120)
    private String objetivo;

    @NotNull
    @DecimalMin("1.0")
    private Double pesoCorporal;

    @JsonIgnore
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResultadoTreino> resultadosTreino = new ArrayList<>();

    public Usuario() {
    }

    public Usuario(String nome, String email, String senha, Integer idade, String objetivo, Double pesoCorporal) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.idade = idade;
        this.objetivo = objetivo;
        this.pesoCorporal = pesoCorporal;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public Integer getIdade() { return idade; }
    public void setIdade(Integer idade) { this.idade = idade; }
    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }
    public Double getPesoCorporal() { return pesoCorporal; }
    public void setPesoCorporal(Double pesoCorporal) { this.pesoCorporal = pesoCorporal; }
    public List<ResultadoTreino> getResultadosTreino() { return resultadosTreino; }
    public void setResultadosTreino(List<ResultadoTreino> resultadosTreino) { this.resultadosTreino = resultadosTreino; }
}
