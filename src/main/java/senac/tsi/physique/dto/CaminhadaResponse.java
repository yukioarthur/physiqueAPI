package senac.tsi.physique.dto;

import java.time.LocalDateTime;

public class CaminhadaResponse {
    private Long id;
    private String tipo;
    private Double distanciaMetros;
    private Integer duracaoSegundos;
    private Integer xpGerado;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;

    public CaminhadaResponse() {}

    public CaminhadaResponse(Long id, String tipo, Double distanciaMetros, Integer duracaoSegundos, Integer xpGerado, LocalDateTime dataInicio, LocalDateTime dataFim) {
        this.id = id;
        this.tipo = tipo;
        this.distanciaMetros = distanciaMetros;
        this.duracaoSegundos = duracaoSegundos;
        this.xpGerado = xpGerado;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Double getDistanciaMetros() { return distanciaMetros; }
    public void setDistanciaMetros(Double distanciaMetros) { this.distanciaMetros = distanciaMetros; }
    public Integer getDuracaoSegundos() { return duracaoSegundos; }
    public void setDuracaoSegundos(Integer duracaoSegundos) { this.duracaoSegundos = duracaoSegundos; }
    public Integer getXpGerado() { return xpGerado; }
    public void setXpGerado(Integer xpGerado) { this.xpGerado = xpGerado; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }
}
