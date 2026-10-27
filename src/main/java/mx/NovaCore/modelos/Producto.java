package mx.NovaCore.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "producto_id")
    private Integer id;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    private Integer stock;

    @Convert(converter = CategoriaConverter.class)
    @Column(nullable = false)
    private Categoria categoria;

    @Column(name = "imagenProducto", length = 255)
    private String imagenUrl;
    @Size(max = 45)
    @NotNull
    @Column(name = "nombreProducto", nullable = false, length = 45)
    private String nombreProducto;
    @OneToMany(mappedBy = "producto")
    private Set<CarritoProducto> carritoProductos = new LinkedHashSet<>();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Set<CarritoProducto> getCarritoProductos() {
        return carritoProductos;
    }

    public void setCarritoProductos(Set<CarritoProducto> carritoProductos) {
        this.carritoProductos = carritoProductos;
    }
}
