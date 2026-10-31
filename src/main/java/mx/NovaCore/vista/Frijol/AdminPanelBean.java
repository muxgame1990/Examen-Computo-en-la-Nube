
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
import jakarta.faces.application.FacesMessage;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import java.io.IOException;
import mx.NovaCore.servicio.AlmacenamientoImagenServicio;
import org.primefaces.PrimeFaces;

@Named("adminPanelBean")
@ViewScoped
public class AdminPanelBean implements Serializable {
    private List<Producto> listaProductos;
    @Inject
    private ProductoServicio ps;
    private Producto productoSeleccionado;
    private String busqueda;
    private Producto producto;
    private Categoria filtroPorCategoria;
    @Inject
    private AlmacenamientoImagenServicio almacenamientoImagenServicio;

    private String urlImagen;
    private String urlImagenDefinitiva;
    private String idTemporal;
    private Producto productoEnEdicion;
    private String urlImagenEdicion;

    @PostConstruct
    public void init() {

        producto = new Producto();
        productoEnEdicion = new Producto();
        listaProductos = new ArrayList<>();
        cargarLista();

    }
    public void cargarLista(){
        listaProductos = ps.listaProductos();
    }
    public void prepararEdicion(Producto seleccionado) {
        productoEnEdicion = ps.buscarPorId(seleccionado.getId());
        urlImagenEdicion = productoEnEdicion.getImagenUrl();
    }

    public void subirImagenEdicion(FileUploadEvent event) {
        try {
            String clave = almacenamientoImagenServicio.guardar(event.getFile());
            String contexto = FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .getRequestContextPath();
            urlImagenEdicion = contexto + "/imagenes/" + clave;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_INFO, "Imagen cargada", "La nueva imagen se guardará al confirmar los cambios."));
        } catch (IOException | IllegalArgumentException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "No se pudo cargar la imagen", e.getMessage()));
        }
    }

    public void guardarEdicion() {
        try {
            productoEnEdicion.setImagenUrl(urlImagenEdicion);
            ps.update(productoEnEdicion.getId(), productoEnEdicion);
            buscarProductos();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_INFO, "Producto actualizado",
                    productoEnEdicion.getNombre() + " se actualizó correctamente."));
            PrimeFaces.current().ajax().addCallbackParam("editado", true);
        } catch (RuntimeException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "No se pudo actualizar", e.getMessage()));
        }
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
        listaProductos = ps.Busqueda(busqueda,filtroPorCategoria);
    }

    public void mostrarTodos() {
        busqueda = null;
        filtroPorCategoria = null;
        cargarLista();
    }

    public void eliminarProducto(Producto producto) {
       try {
           ps.delete(producto.getId());
           buscarProductos();
           FacesContext.getCurrentInstance().addMessage(
                   null, new jakarta.faces.application.FacesMessage(
                           FacesMessage.SEVERITY_INFO, "Producto Eliminado", producto.getNombre() + " Ha sido eliminado correctamente"
                   )
           );
       }catch (RuntimeException e){
           FacesContext.getCurrentInstance().addMessage(
                   null, new jakarta.faces.application.FacesMessage(
                           FacesMessage.SEVERITY_ERROR, "No se logro eliminar", e.getMessage()
                   )
           );
       }
    }

    public void editarProducto(Producto producto) {
        ps.update(producto.getId(), producto);
    }

    public void agregarProducto() {
        try {
            if (producto.getCategoria() == null) {
                throw new IllegalArgumentException("Debes seleccionar una categoría primero.");
            }
            if (urlImagen == null || urlImagen.isBlank()) {
                throw new IllegalArgumentException("Sube una imagen del producto antes de guardarlo.");
            }

            producto.setImagenUrl(urlImagen);
            producto.setIdentificador(idTemporal);

            ps.insert(producto);
            buscarProductos();

            producto = new Producto();
            idTemporal = null;
            urlImagen = null;

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_INFO, "Producto registrado", "El producto se registró correctamente."));
            PrimeFaces.current().ajax().addCallbackParam("agregado", true);

        } catch (RuntimeException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "No se pudo registrar el producto", e.getMessage()));
        }
    }

    public String cerrarSesion() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "login?faces-redirect=true";
    }

    public Categoria[] getCategorias(){
        return Categoria.values();
    }

    public void actualizarIdentificadorProvisional() {
        if (producto.getCategoria() == null) {
            idTemporal = null;
            return;
        }
        idTemporal = ps.generarIdentificadorProvisional(producto.getCategoria());
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

    public Categoria getFiltroPorCategoria() {
        return filtroPorCategoria;
    }
    public void setFiltroPorCategoria(Categoria filtroPorCategoria) {
        this.filtroPorCategoria = filtroPorCategoria;
    }

    public String getIdTemporal() {
        return idTemporal;
    }

    public void setIdTemporal(String idTemporal) {
        this.idTemporal = idTemporal;
    }
    public Producto getProductoEnEdicion() { return productoEnEdicion; }
    public void setProductoEnEdicion(Producto productoEnEdicion) { this.productoEnEdicion = productoEnEdicion; }

    public String getUrlImagenEdicion() { return urlImagenEdicion; }
    public void setUrlImagenEdicion(String urlImagenEdicion) { this.urlImagenEdicion = urlImagenEdicion; }
}
