package mx.NovaCore.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "entrenamiento_reloj")
public class EntrenamientoReloj {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entrenamiento_id")
    private Integer id;

    @Column(name = "interes_principal", nullable = false)
    private String interesPrincipal;

    @Column(name = "nivel_ejercicio", nullable = false)
    private String nivelEjercicio;

    @Column(name = "motivacion", nullable = false)
    private String motivacion;

    @Column(name = "comodo_tecnologia", nullable = false)
    private String comodoTecnologia;

    @Column(name = "modelo", nullable = false)
    private String modelo;

    public Integer getId() {
        return id;
    }

    public String getInteresPrincipal() {
        return interesPrincipal;
    }

    public void setInteresPrincipal(String interesPrincipal) {
        this.interesPrincipal = interesPrincipal;
    }

    public String getNivelEjercicio() {
        return nivelEjercicio;
    }

    public void setNivelEjercicio(String nivelEjercicio) {
        this.nivelEjercicio = nivelEjercicio;
    }

    public String getMotivacion() {
        return motivacion;
    }

    public void setMotivacion(String motivacion) {
        this.motivacion = motivacion;
    }

    public String getComodoTecnologia() {
        return comodoTecnologia;
    }

    public void setComodoTecnologia(String comodoTecnologia) {
        this.comodoTecnologia = comodoTecnologia;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }
}