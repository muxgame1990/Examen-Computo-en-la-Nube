
package mx.NovaCore.vista.Frijol;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import mx.NovaCore.modelos.Categoria;
import mx.NovaCore.modelos.CategoriaConverter;
import mx.NovaCore.modelos.Producto;
import mx.NovaCore.servicio.ProductoServicio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("adminPanelBean")
@ViewScoped
public class AdminPanelBean implements Serializable {
    private List<Producto> listaProductos;
    @Inject
    private ProductoServicio ps;
    private Producto productoSeleccionado;
    private String busqueda;
    private Producto producto;


    @PostConstruct
    public void init() {
        producto = new Producto();
        listaProductos = new ArrayList<>();
        cargarLista();

    }
    public void cargarLista(){
        listaProductos = ps.listaProductos();
    }
    public void prepararNuevoProducto() {

    }

    public void buscarProductos() {

    }

    public void mostrarTodos() {

    }

    public void eliminarProducto(Producto producto) {

    }

    public void editarProducto(Producto producto) {

    }

    public void agregarProducto() {

    }

    public void cerrarSesion() {

    }

    public Categoria[] getCategorias(){
        return Categoria.values();
    }



    public List<Producto> getListaProductos() {
        return listaProductos;
    }

    public void setListaProductos(List<Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }

    public Producto getProductoSeleccionado() {
        return productoSeleccionado;
    }

    public void setProductoSeleccionado(Producto productoSeleccionado) {
        this.productoSeleccionado = productoSeleccionado;
    }

    public String getBusqueda() {
        return busqueda;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public void setBusqueda(String busqueda) {
        this.busqueda = busqueda;
    }
}
