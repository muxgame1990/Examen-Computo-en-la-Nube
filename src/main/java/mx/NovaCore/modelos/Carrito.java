package mx.NovaCore.modelos;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carrito")
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "carrito_id")
    private Integer id;

    @Column(name = "id_publico_cliente", nullable = false, length = 45)
    private String idPublicoCliente;

    @Column(name = "fecha_operacion", nullable = false)
    private LocalDateTime fechaOperacion;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarritoProducto> productos = new ArrayList<>();

    protected Carrito() {
    }

    public Carrito(String idPublicoCliente) {
        this.idPublicoCliente = idPublicoCliente;
        this.fechaOperacion = LocalDateTime.now();
    }

    public Integer getId() { return id; }
    public String getIdPublicoCliente() { return idPublicoCliente; }
    public LocalDateTime getFechaOperacion() { return fechaOperacion; }
    public List<CarritoProducto> getProductos() { return productos; }
}
