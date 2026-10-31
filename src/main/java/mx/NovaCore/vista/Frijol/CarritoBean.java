package mx.NovaCore.vista.Frijol;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import mx.NovaCore.modelos.Producto;
import mx.NovaCore.servicio.LineaCarrito;
import mx.NovaCore.servicio.ProductoServicio;
import mx.NovaCore.modelos.Carrito;
import mx.NovaCore.servicio.CompraServicio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Named("carritoBean")
@SessionScoped
public class CarritoBean implements Serializable {

    @Inject
    private ProductoServicio productoServicio;

    @Inject
    private SesionBean sesionBean;

    @Inject
    private CompraServicio compraServicio;

    private Integer ultimaOrdenId;
    private double ultimoTotal;

    private final Map<Integer, Integer> cantidades = new LinkedHashMap<>();

    public String agregar(Integer productoId) {
        if (!sesionBean.isLoggedIn()) {
            return requerirLogin();
        }

        Producto producto = productoServicio.buscarPorId(productoId);

        if (producto == null) {
            agregarMensaje(
                    FacesMessage.SEVERITY_ERROR,
                    "Producto no disponible",
                    "No se encontró el producto seleccionado."
            );
            return null;
        }

        if (producto.getStock() == null || producto.getStock() <= 0) {
            agregarMensaje(
                    FacesMessage.SEVERITY_WARN,
                    "Sin stock",
                    "Este producto no tiene existencias disponibles."
            );
            return null;
        }

        int cantidadActual = getCantidad(productoId);

        if (cantidadActual >= producto.getStock()) {
            agregarMensaje(
                    FacesMessage.SEVERITY_WARN,
                    "Stock insuficiente",
                    "No hay más unidades disponibles de este producto."
            );
            return null;
        }

        cantidades.put(productoId, cantidadActual + 1);

        agregarMensaje(
                FacesMessage.SEVERITY_INFO,
                "Producto agregado",
                producto.getNombre() + " se agregó al carrito."
        );

        return null;
    }

    public String incrementar(Integer productoId) {
        if (!sesionBean.isLoggedIn()) {
            return requerirLogin();
        }

        Producto producto = productoServicio.buscarPorId(productoId);

        if (producto == null) {
            return null;
        }

        int cantidadActual = getCantidad(productoId);
        int stock = producto.getStock() == null ? 0 : producto.getStock();

        if (cantidadActual >= stock) {
            agregarMensaje(
                    FacesMessage.SEVERITY_WARN,
                    "Stock insuficiente",
                    "No hay más unidades disponibles de este producto."
            );
            return null;
        }

        cantidades.put(productoId, cantidadActual + 1);

        return null;
    }

    public String decrementar(Integer productoId) {
        if (!sesionBean.isLoggedIn()) {
            return requerirLogin();
        }

        int cantidadActual = getCantidad(productoId);

        if (cantidadActual <= 1) {
            cantidades.remove(productoId);
        } else {
            cantidades.put(productoId, cantidadActual - 1);
        }

        return null;
    }

    public String eliminar(Integer productoId) {
        if (!sesionBean.isLoggedIn()) {
            return requerirLogin();
        }

        cantidades.remove(productoId);

        agregarMensaje(
                FacesMessage.SEVERITY_INFO,
                "Producto eliminado",
                "El producto se quitó del carrito."
        );

        return null;
    }

    public String finalizarCompra() {
        if (!sesionBean.isLoggedIn()) {
            return requerirLogin();
        }

        if (isVacio()) {
            agregarMensaje(
                    FacesMessage.SEVERITY_WARN,
                    "Carrito vacío",
                    "Agrega productos antes de finalizar la compra."
            );
            return null;
        }

        double totalCompra = getSubtotal();

        try {
            Carrito orden = compraServicio.realizarCompra(
                    sesionBean.getUsuarioSesion().getIdPublico(),
                    new LinkedHashMap<>(cantidades)
            );

            ultimaOrdenId = orden.getId();
            ultimoTotal = totalCompra;
            cantidades.clear();

            return "confirmacion?faces-redirect=true";

        } catch (RuntimeException e) {
            agregarMensaje(
                    FacesMessage.SEVERITY_ERROR,
                    "No se pudo completar la compra",
                    e.getMessage()
            );
            return null;
        }
    }

    public int getCantidad(Integer productoId) {
        if (productoId == null) {
            return 0;
        }

        return cantidades.getOrDefault(productoId, 0);
    }

    public List<LineaCarrito> getLineas() {
        List<LineaCarrito> lineas = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entrada : cantidades.entrySet()) {
            Producto producto = productoServicio.buscarPorId(entrada.getKey());

            if (producto == null) {
                continue;
            }

            double precio = producto.getPrecio() == null
                    ? 0
                    : producto.getPrecio();
            int cantidad = entrada.getValue();

            lineas.add(new LineaCarrito(
                    producto.getId(),
                    producto.getNombre(),
                    producto.getDescripcion(),
                    producto.getImagenUrl(),
                    precio,
                    cantidad,
                    precio * cantidad,
                    producto.getStock() == null ? 0 : producto.getStock()
            ));
        }

        return lineas;
    }

    public double getSubtotal() {
        double subtotal = 0;

        for (LineaCarrito linea : getLineas()) {
            subtotal += linea.getSubtotal();
        }

        return subtotal;
    }

    public double getTotal() {
        return getSubtotal();
    }

    public int getCantidadLineas() {
        return cantidades.size();
    }

    public int getCantidadTotal() {
        int total = 0;

        for (int cantidad : cantidades.values()) {
            total += cantidad;
        }

        return total;
    }

    public boolean isVacio() {
        return cantidades.isEmpty();
    }

    private String requerirLogin() {
        agregarMensaje(
                FacesMessage.SEVERITY_WARN,
                "Inicia sesión",
                "Necesitas iniciar sesión para agregar productos al carrito."
        );

        FacesContext.getCurrentInstance()
                .getExternalContext()
                .getFlash()
                .setKeepMessages(true);

        return "login?faces-redirect=true";
    }

    private void agregarMensaje(
            FacesMessage.Severity severidad,
            String titulo,
            String detalle
    ) {
        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(severidad, titulo, detalle)
        );
    }
    public Integer getUltimaOrdenId() {
        return ultimaOrdenId;
    }

    public double getUltimoTotal() {
        return ultimoTotal;
    }
}
