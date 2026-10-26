package mx.NovaCore.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CarritoProductoId implements Serializable {
    private static final long serialVersionUID = -5164423498868205163L;
    @NotNull
    @Column(name = "carrito_id", nullable = false)
    private Integer carritoId;

    @NotNull
    @Column(name = "producto_id", nullable = false)
    private Integer productoId;

    public Integer getCarritoId() {
        return carritoId;
    }

    public void setCarritoId(Integer carritoId) {
        this.carritoId = carritoId;
    }

    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarritoProductoId entity = (CarritoProductoId) o;
        return Objects.equals(this.carritoId, entity.carritoId) &&
                Objects.equals(this.productoId, entity.productoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(carritoId, productoId);
    }
}