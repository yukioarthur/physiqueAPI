package senac.tsi.physique.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CaminhadaRequest {
    @Size(max = 60)
    private String tipo = "CAMINHADA";

    @NotNull
    @DecimalMin("0.0")
    private Double distanciaMetros;

    @NotNull
    @Min(1)
    private Integer duracaoSegundos;

    @Size(max = 500)
    private String observacao;

    public CaminhadaRequest() {}

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Double getDistanciaMetros() { return distanciaMetros; }
    public void setDistanciaMetros(Double distanciaMetros) { this.distanciaMetros = distanciaMetros; }
    public Integer getDuracaoSegundos() { return duracaoSegundos; }
    public void setDuracaoSegundos(Integer duracaoSegundos) { this.duracaoSegundos = duracaoSegundos; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}
