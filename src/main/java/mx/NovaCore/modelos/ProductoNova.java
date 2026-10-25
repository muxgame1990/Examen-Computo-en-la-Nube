package mx.NovaCore.modelos;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProductoNova {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 30, message = "El nombre mide máximo 30 caracteres")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 40, message = "La descripción mide máximo 40 caracteres")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    @DecimalMax(value = "99999999.99", message = "El precio es demasiado grande")
    private Double precio;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Max(value = 1000000, message = "El stock es demasiado grande")
    private Integer stock;

    @NotNull(message = "Elige una categoría")
    private Categoria categoria;

    public static ProductoNova productito(Producto p) {
        ProductoNova f = new ProductoNova();
        f.nombre = p.getNombre();
        f.descripcion = p.getDescripcion();
        f.precio = p.getPrecio();
        f.stock = p.getStock();
        f.categoria = p.getCategoria();
        return f;
    }

    public void copiar(Producto p) {
        p.setNombre(nombre.trim());
        p.setDescripcion(descripcion.trim());
        p.setPrecio(Math.round(precio * 100.0) / 100.0);
        p.setStock(stock);
        p.setCategoria(categoria);
    }

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
}
