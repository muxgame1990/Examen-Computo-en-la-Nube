package mx.NovaCore.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "encuesta_reloj")
public class EncuestaReloj {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "encuesta_id")
    private Integer id;

    @Column(name = "id_publico_cliente", length = 45)
    private String idPublicoCliente;

    @Column(name = "interes_principal", nullable = false)
    private String interesPrincipal;

    @Column(name = "nivel_ejercicio", nullable = false)
    private String nivelEjercicio;

    @Column(name = "motivacion", nullable = false)
    private String motivacion;

    @Column(name = "comodo_tecnologia", nullable = false)
    private String comodoTecnologia;

    @Column(name = "modelo_recomendado", nullable = false)
    private String modeloRecomendado;

    @Column(name = "confianza", nullable = false)
    private Double confianza;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    public EncuestaReloj() {
    }

    public EncuestaReloj(String idPublicoCliente,
                         String interesPrincipal,
                         String nivelEjercicio,
                         String motivacion,
                         String comodoTecnologia,
                         String modeloRecomendado,
                         Double confianza) {
        this.idPublicoCliente = idPublicoCliente;
        this.interesPrincipal = interesPrincipal;
        this.nivelEjercicio = nivelEjercicio;
        this.motivacion = motivacion;
        this.comodoTecnologia = comodoTecnologia;
        this.modeloRecomendado = modeloRecomendado;
        this.confianza = confianza;
        this.fechaHora = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public String getIdPublicoCliente() {
        return idPublicoCliente;
    }

    public String getInteresPrincipal() {
        return interesPrincipal;
    }

    public String getNivelEjercicio() {
        return nivelEjercicio;
    }

    public String getMotivacion() {
        return motivacion;
    }

    public String getComodoTecnologia() {
        return comodoTecnologia;
    }

    public String getModeloRecomendado() {
        return modeloRecomendado;
    }

    public Double getConfianza() {
        return confianza;
    }

    public int getPorcentaje() {
        return confianza == null ? 0 : (int) Math.round(confianza * 100);
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
}