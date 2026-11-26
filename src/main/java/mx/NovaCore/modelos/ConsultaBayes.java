package mx.NovaCore.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/** Cada vez que alguien contesta el formulario del recomendador. */
@Entity
@Table(name = "consulta_bayes")
public class ConsultaBayes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "consulta_id")
    private Integer id;

    @Column(name = "id_publico_cliente", length = 45)
    private String idPublicoCliente;

    @Column(name = "interes", nullable = false, length = 20)
    private String interes;

    @Column(name = "ejercicio", nullable = false, length = 20)
    private String ejercicio;

    @Column(name = "motivacion", nullable = false, length = 20)
    private String motivacion;

    @Column(name = "tecnologia", nullable = false, length = 5)
    private String tecnologia;

    @Column(name = "recomendado", nullable = false, length = 10)
    private String recomendado;

    @Column(name = "prob_i100", nullable = false)
    private Double probI100;

    @Column(name = "prob_i500", nullable = false)
    private Double probI500;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    protected ConsultaBayes() {
    }

    public ConsultaBayes(String idPublicoCliente, String interes, String ejercicio, String motivacion,
                         String tecnologia, String recomendado, Double probI100, Double probI500) {
        this.idPublicoCliente = idPublicoCliente;
        this.interes = interes;
        this.ejercicio = ejercicio;
        this.motivacion = motivacion;
        this.tecnologia = tecnologia;
        this.recomendado = recomendado;
        this.probI100 = probI100;
        this.probI500 = probI500;
        this.fechaHora = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public String getIdPublicoCliente() {
        return idPublicoCliente;
    }

    public String getInteres() {
        return interes;
    }

    public String getEjercicio() {
        return ejercicio;
    }

    public String getMotivacion() {
        return motivacion;
    }

    public String getTecnologia() {
        return tecnologia;
    }

    public String getRecomendado() {
        return recomendado;
    }

    public Double getProbI100() {
        return probI100;
    }

    public Double getProbI500() {
        return probI500;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    /** Probabilidad del modelo recomendado, en porcentaje. */
    public int getPorcentaje() {
        double p = "i500".equals(recomendado) ? probI500 : probI100;
        return (int) Math.round(p * 100);
    }
}
