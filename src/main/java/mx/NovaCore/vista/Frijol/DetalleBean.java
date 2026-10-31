package mx.NovaCore.vista.Frijol;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import mx.NovaCore.modelos.Producto;
import mx.NovaCore.servicio.ProductoServicio;

import java.io.Serializable;

@Named("detalleBean")
@ViewScoped
public class DetalleBean implements Serializable {

    @Inject
    private ProductoServicio productoServicio;

    private Integer id;
    private Producto producto;

    public void cargar() {
        producto = id == null
                ? null
                : productoServicio.buscarPorId(id);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Producto getProducto() {
        return producto;
    }
}
