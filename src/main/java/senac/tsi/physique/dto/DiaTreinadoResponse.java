package senac.tsi.physique.dto;

public class DiaTreinadoResponse {
    private String dia;
    private Boolean treinou;

    public DiaTreinadoResponse() {}

    public DiaTreinadoResponse(String dia, Boolean treinou) {
        this.dia = dia;
        this.treinou = treinou;
    }

    public String getDia() { return dia; }
    public void setDia(String dia) { this.dia = dia; }
    public Boolean getTreinou() { return treinou; }
    public void setTreinou(Boolean treinou) { this.treinou = treinou; }
}
