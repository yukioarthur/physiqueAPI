package senac.tsi.physique.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(name = "CadastroRequest", description = "Payload público para cadastro do aluno no app Android")
public class CadastroRequest {

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres")
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "O e-mail deve ser válido")
    @Size(max = 160, message = "O e-mail deve ter no máximo 160 caracteres")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 4, max = 120, message = "A senha deve ter entre 4 e 120 caracteres")
    private String senha;

    @NotNull(message = "A idade é obrigatória")
    @Min(value = 1, message = "A idade deve ser maior que zero")
    @Max(value = 120, message = "A idade deve ser menor ou igual a 120")
    private Integer idade;

    @NotBlank(message = "O objetivo é obrigatório")
    @Size(max = 120, message = "O objetivo deve ter no máximo 120 caracteres")
    private String objetivo;

    @NotNull(message = "O peso corporal é obrigatório")
    @DecimalMin(value = "1.0", message = "O peso corporal deve ser maior que zero")
    private Double pesoCorporal;

    @Positive(message = "O plano de treino deve ser válido")
    private Long planoTreinoId;

    @Positive(message = "O treino inicial deve ser válido")
    private Long treinoId;

    public CadastroRequest() {}

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
    public Long getPlanoTreinoId() { return planoTreinoId; }
    public void setPlanoTreinoId(Long planoTreinoId) { this.planoTreinoId = planoTreinoId; }
    public Long getTreinoId() { return treinoId; }
    public void setTreinoId(Long treinoId) { this.treinoId = treinoId; }
}
