package mx.NovaCore.vista.Frijol;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import mx.NovaCore.modelos.MovimientoInventario;
import mx.NovaCore.modelos.Producto;
import mx.NovaCore.modelos.TipoMovimiento;
import mx.NovaCore.servicio.MovimientoInventarioServicio;
import mx.NovaCore.servicio.ProductoServicio;
import org.primefaces.PrimeFaces;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Named("flujoInventarioBean")
@ViewScoped
public class FlujoInventarioBean implements Serializable {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Inject
    private MovimientoInventarioServicio movimientoInventarioServicio;
    @Inject
    private ProductoServicio productito;

    private List<MovimientoInventario> listaMovimientos;
    private String busqueda;
    private TipoMovimiento filtroPorTipo;

    private MovimientoInventario movimientoEnEdicion;
    private TipoMovimiento tipoMovimiento;
    private Integer productoId;
    private Integer cantidad;
    private List<Producto> productosDisponibles;

    @PostConstruct
    public void init(){
        listaMovimientos = new ArrayList<>();
        productosDisponibles = new ArrayList<>();
        cargarLista();
    }

    public void cargarLista(){
        listaMovimientos = movimientoInventarioServicio.listaMovimientos();
    }

    public void buscarMovimientos(){
        listaMovimientos = movimientoInventarioServicio.busqueda(busqueda, filtroPorTipo);
    }

    public void prepararEntrada(){
        prepararNuevo(TipoMovimiento.ENTRADA);
    }

    private void prepararNuevo(TipoMovimiento tipo) {
        movimientoEnEdicion = null;
        tipoMovimiento = tipo;
        productoId = null;
        cantidad = null;
        productosDisponibles = productito.listaProductos();
    }

    public void prepararSalida(){
        prepararNuevo(TipoMovimiento.SALIDA);
    }

    public void prepararEdicion(MovimientoInventario movimiento) {
        movimientoEnEdicion = movimiento;
        tipoMovimiento = movimiento.getTipo();
        productoId = movimiento.getProducto().getId();
        cantidad = movimiento.getCantidad();
        productosDisponibles = productito.listaProductos();
    }

    public void guardarMovimiento() {
        boolean guardado = false;
        try {
            if (isModoEdicion()) {
                movimientoInventarioServicio.actualizarCantidad(movimientoEnEdicion.getId(), cantidad);
            } else {
                movimientoInventarioServicio.registrar(productoId, tipoMovimiento, cantidad);
            }
            buscarMovimientos();
            guardado = true;
            mensaje(FacesMessage.SEVERITY_INFO, "Movimiento guardado",
                    isModoEdicion() ? "El movimiento se actualizó"
                            : tipoMovimiento.getEtiqueta() + " registrada correctamente");
        } catch (RuntimeException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo guardar el movimiento", e.getMessage());
        }
        PrimeFaces.current().ajax().addCallbackParam("guardado", guardado);
    }

    public void eliminarMovimiento(MovimientoInventario movimiento) {
        try {
            movimientoInventarioServicio.eliminar(movimiento.getId());
            buscarMovimientos();
            mensaje(FacesMessage.SEVERITY_INFO, "Movimiento eliminado",
                    "El movimiento se eliminó y el stock fue ajustado");
        } catch (RuntimeException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se logró eliminar", e.getMessage());
        }
    }

    public void cerrarSesion() {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.invalidateSession();
        try {
            ec.redirect(ec.getRequestContextPath() + "/login.xhtml");
        } catch (IOException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo cerrar la sesión Gomene", e.getMessage());
        }
    }

    public String formatoFecha(LocalDateTime fecha) {
        return fecha == null ? "" : FORMATO_FECHA.format(fecha);
    }

    public String cantidadConSigno(MovimientoInventario movimiento) {
        return (movimiento.isEntrada() ? "+" : "-") + movimiento.getCantidad();
    }

    public boolean isModoEdicion() {
        return movimientoEnEdicion != null;
    }

    public String getTituloDialogo() {
        if (tipoMovimiento == null) {
            return "Movimiento de inventario";
        }
        return isModoEdicion()
                ? "Editar " + tipoMovimiento.getEtiqueta().toLowerCase()
                : "Registrar " + tipoMovimiento.getEtiqueta().toLowerCase();
    }

    public TipoMovimiento[] getTiposMovimiento() {
        return TipoMovimiento.values();
    }

    private void mensaje(FacesMessage.Severity severidad, String titulo, String detalle) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, titulo, detalle));
    }

    public List<MovimientoInventario> getListaMovimientos(){
        return listaMovimientos;
    }

    public void setListaMovimientos(List<MovimientoInventario> listaMovimientos) {
        this.listaMovimientos = listaMovimientos;
    }

    public String getBusqueda(){
        return busqueda;
    }

    public void setBusqueda(String busqueda){
        this.busqueda = busqueda;
    }

    public TipoMovimiento getFiltroPorTipo() {
        return filtroPorTipo;
    }

    public void setFiltroPorTipo(TipoMovimiento filtroPorTipo) {
        this.filtroPorTipo = filtroPorTipo;
    }

    public MovimientoInventario getMovimientoEnEdicion() {
        return movimientoEnEdicion;
    }

    public void setMovimientoEnEdicion(MovimientoInventario movimientoEnEdicion) {
        this.movimientoEnEdicion = movimientoEnEdicion;
    }

    public TipoMovimiento getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public List<Producto> getProductosDisponibles() {
        return productosDisponibles;
    }

    public void setProductosDisponibles(List<Producto> productosDisponibles) {
        this.productosDisponibles = productosDisponibles;
    }
}
