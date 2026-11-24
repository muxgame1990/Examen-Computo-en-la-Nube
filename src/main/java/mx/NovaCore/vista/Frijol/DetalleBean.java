package mx.NovaCore.vista.Frijol;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import mx.NovaCore.modelos.Producto;
import mx.NovaCore.servicio.ProductoServicio;
import mx.NovaCore.servicio.RecomendacionServicio;

import java.io.Serializable;
import java.util.List;

@Named("detalleBean")
@ViewScoped
public class DetalleBean implements Serializable {

    private static final int CANTIDAD_RECOMENDACIONES = 4;

    @Inject
    private ProductoServicio productoServicio;

    @Inject
    private RecomendacionServicio recomendacionServicio;

    private Integer id;
    private Producto producto;
    private List<Producto> recomendaciones = List.of();

    public void cargar() {
        producto = id == null
                ? null
                : productoServicio.buscarPorId(id);

        recomendaciones = producto == null
                ? List.of()
                : recomendacionServicio.recomendarPorProducto(producto.getId(), CANTIDAD_RECOMENDACIONES);
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

    public List<Producto> getRecomendaciones() {
        return recomendaciones;
    }
}