package senac.tsi.physique.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "atividade_aerobica_ponto")
public class AtividadeAerobicaPonto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "atividade_id")
    @NotNull
    private AtividadeAerobica atividade;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    @NotNull
    private LocalDateTime registradoEm = LocalDateTime.now();

    public AtividadeAerobicaPonto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public AtividadeAerobica getAtividade() { return atividade; }
    public void setAtividade(AtividadeAerobica atividade) { this.atividade = atividade; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public LocalDateTime getRegistradoEm() { return registradoEm; }
    public void setRegistradoEm(LocalDateTime registradoEm) { this.registradoEm = registradoEm; }
}
