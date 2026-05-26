package senac.tsi.physique.dto;

import java.util.List;

public class DashboardResponse {
    private UsuarioResumoResponse usuario;
    private List<DiaTreinadoResponse> semana;
    private TreinoAtualResumoResponse treinoAtual;
    private List<DashboardItemResponse> ultimosTreinos;
    private List<DashboardItemResponse> desafios;
    private PerformanceResponse performance;

    public DashboardResponse() {}

    public DashboardResponse(UsuarioResumoResponse usuario, List<DiaTreinadoResponse> semana, TreinoAtualResumoResponse treinoAtual, List<DashboardItemResponse> ultimosTreinos, List<DashboardItemResponse> desafios, PerformanceResponse performance) {
        this.usuario = usuario;
        this.semana = semana;
        this.treinoAtual = treinoAtual;
        this.ultimosTreinos = ultimosTreinos;
        this.desafios = desafios;
        this.performance = performance;
    }

    public UsuarioResumoResponse getUsuario() { return usuario; }
    public void setUsuario(UsuarioResumoResponse usuario) { this.usuario = usuario; }
    public List<DiaTreinadoResponse> getSemana() { return semana; }
    public void setSemana(List<DiaTreinadoResponse> semana) { this.semana = semana; }
    public TreinoAtualResumoResponse getTreinoAtual() { return treinoAtual; }
    public void setTreinoAtual(TreinoAtualResumoResponse treinoAtual) { this.treinoAtual = treinoAtual; }
    public List<DashboardItemResponse> getUltimosTreinos() { return ultimosTreinos; }
    public void setUltimosTreinos(List<DashboardItemResponse> ultimosTreinos) { this.ultimosTreinos = ultimosTreinos; }
    public List<DashboardItemResponse> getDesafios() { return desafios; }
    public void setDesafios(List<DashboardItemResponse> desafios) { this.desafios = desafios; }
    public PerformanceResponse getPerformance() { return performance; }
    public void setPerformance(PerformanceResponse performance) { this.performance = performance; }
}
