package mx.NovaCore.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "carrito")
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrito")
    private Integer id;

    @Column(name = "id_publico_cliente", nullable = false, length = 100)
    private String idPublicoCliente;

    @Column(name = "id_carrito_producto", nullable = false)
    private Integer idCarritoProducto;

    @Column(name = "fecha_operacion", nullable = false)
    private LocalDate fechaOperacion;

    protected Carrito() {
    }

    public Carrito(String idPublicoCliente, Integer idCarritoProducto, LocalDate fechaOperacion) {
        this.idPublicoCliente = idPublicoCliente;
        this.idCarritoProducto = idCarritoProducto;
        this.fechaOperacion = fechaOperacion;
    }

    public Integer getId() { return id; }
    public String getIdPublicoCliente() { return idPublicoCliente; }
    public Integer getIdCarritoProducto() { return idCarritoProducto; }
    public LocalDate getFechaOperacion() { return fechaOperacion; }
}
