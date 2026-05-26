package senac.tsi.physique.dto;

public class PerformanceResponse {
    private Integer forca;
    private Integer resistencia;
    private Integer mobilidade;

    public PerformanceResponse() {}

    public PerformanceResponse(Integer forca, Integer resistencia, Integer mobilidade) {
        this.forca = forca;
        this.resistencia = resistencia;
        this.mobilidade = mobilidade;
    }

    public Integer getForca() { return forca; }
    public void setForca(Integer forca) { this.forca = forca; }
    public Integer getResistencia() { return resistencia; }
    public void setResistencia(Integer resistencia) { this.resistencia = resistencia; }
    public Integer getMobilidade() { return mobilidade; }
    public void setMobilidade(Integer mobilidade) { this.mobilidade = mobilidade; }
}
