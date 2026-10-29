
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

import jakarta.faces.context.FacesContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import java.io.IOException;
import mx.NovaCore.servicio.AlmacenamientoImagenServicio;

@Named("adminPanelBean")
@ViewScoped
public class AdminPanelBean implements Serializable {
    private List<Producto> listaProductos;
    @Inject
    private ProductoServicio ps;
    private Producto productoSeleccionado;
    private String busqueda;
    private Producto producto;

    @Inject
    private AlmacenamientoImagenServicio almacenamientoImagenServicio;

    private String urlImagen;


    @PostConstruct
    public void init() {

        producto = new Producto();
        listaProductos = new ArrayList<>();
        cargarLista();

    }
    public void cargarLista(){
        listaProductos = ps.listaProductos();
    }
    public void prepararNuevoProducto() { }

    public void subirImagen(FileUploadEvent event) {

        try {
            UploadedFile archivo = event.getFile();

            String clave = almacenamientoImagenServicio.guardar(archivo);

            String contexto = FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .getRequestContextPath();

            urlImagen = contexto + "/imagenes/" + clave;

            FacesContext.getCurrentInstance()
                    .addMessage(null,
                            new jakarta.faces.application.FacesMessage(
                                    jakarta.faces.application.FacesMessage.SEVERITY_INFO,
                                    "Imagen cargada",
                                    "La imagen se guardó correctamente."
                            ));

        } catch (IOException | IllegalArgumentException e) {

            FacesContext.getCurrentInstance()
                    .addMessage(null,
                            new jakarta.faces.application.FacesMessage(
                                    jakarta.faces.application.FacesMessage.SEVERITY_ERROR,
                                    "No se pudo cargar la imagen",
                                    e.getMessage()
                            ));
        }
    }

    public void buscarProductos() {

    }

    public void mostrarTodos() {

    }

    public void eliminarProducto(Producto producto) {
        ps.delete(producto.getId());
    }

    public void editarProducto(Producto producto) {
        ps.update(producto.getId(), producto);
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
    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }
}
