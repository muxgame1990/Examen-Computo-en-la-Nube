package mx.NovaCore.vista.Frijol;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import mx.NovaCore.modelos.Categoria;
import mx.NovaCore.modelos.Producto;
import mx.NovaCore.servicio.ProductoServicio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Named("indexBean")
@ViewScoped
public class IndexBean implements Serializable {

    @Inject
    private ProductoServicio productoServicio;

    private List<Producto> productos = new ArrayList<>();
    private Categoria categoriaSeleccionada;
    private String busqueda = "";
    private String orden = "featured";

    @PostConstruct
    public void init() {
        cargarProductos();
    }

    public void cargarProductos() {
        String termino = busqueda == null
                ? ""
                : busqueda.trim().toLowerCase(Locale.ROOT);

        List<Producto> resultado = new ArrayList<>();

        for (Producto producto : productoServicio.listaProductos()) {
            if (categoriaSeleccionada != null
                    && producto.getCategoria() != categoriaSeleccionada) {
                continue;
            }

            if (!termino.isEmpty()) {
                String nombre = producto.getNombre();

                if (nombre == null
                        || !nombre.toLowerCase(Locale.ROOT).contains(termino)) {
                    continue;
                }
            }

            resultado.add(producto);
        }

        if ("price-asc".equals(orden)) {
            resultado.sort(Comparator.comparing(Producto::getPrecio));
        } else if ("price-desc".equals(orden)) {
            resultado.sort(
                    Comparator.comparing(Producto::getPrecio).reversed()
            );
        } else if ("name-asc".equals(orden)) {
            resultado.sort(
                    Comparator.comparing(
                            Producto::getNombre,
                            String.CASE_INSENSITIVE_ORDER
                    )
            );
        }

        productos = resultado;
    }

    public void filtrar(Categoria categoria) {
        categoriaSeleccionada = categoria;
        cargarProductos();
    }

    public void mostrarTodos() {
        categoriaSeleccionada = null;
        cargarProductos();
    }

    public void buscar() {
        cargarProductos();
    }

    public void ordenar() {
        cargarProductos();
    }

    public Categoria[] getCategorias() {
        return Categoria.values();
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public Categoria getCategoriaSeleccionada() {
        return categoriaSeleccionada;
    }

    public String getBusqueda() {
        return busqueda;
    }

    public void setBusqueda(String busqueda) {
        this.busqueda = busqueda;
    }

    public String getOrden() {
        return orden;
    }

    public void setOrden(String orden) {
        this.orden = orden;
    }
}
