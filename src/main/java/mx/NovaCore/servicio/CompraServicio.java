package mx.NovaCore.servicio;

import mx.NovaCore.modelos.Carrito;
import mx.NovaCore.modelos.CarritoProducto;
import mx.NovaCore.modelos.Producto;
import mx.NovaCore.modelos.TipoMovimiento;
import mx.NovaCore.repositorio.CarritoRepositorio;
import mx.NovaCore.repositorio.ProductoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class CompraServicio {

    private final CarritoRepositorio carritoRepositorio;
    private final ProductoRepositorio productoRepositorio;
    private final MovimientoInventarioServicio movimientoInventarioServicio;

    public CompraServicio(CarritoRepositorio carritoRepositorio,
                          ProductoRepositorio productoRepositorio,
                          MovimientoInventarioServicio movimientoInventarioServicio) {
        this.carritoRepositorio = carritoRepositorio;
        this.productoRepositorio = productoRepositorio;
        this.movimientoInventarioServicio = movimientoInventarioServicio;
    }

    @Transactional
    public Carrito realizarCompra(String usuario, Map<Integer, Integer> cantidades) {
        if (usuario == null || usuario.isBlank()) {
            throw new IllegalArgumentException("Debes iniciar sesión para comprar.");
        }
        if (cantidades == null || cantidades.isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío.");
        }

        Carrito orden = new Carrito(usuario);

        for (Map.Entry<Integer, Integer> item : cantidades.entrySet()) {
            Integer productoId = item.getKey();
            Integer cantidad = item.getValue();

            movimientoInventarioServicio.registrar(productoId, TipoMovimiento.SALIDA, cantidad);

            Producto producto = productoRepositorio.findById(productoId)
                    .orElseThrow(() -> new IllegalArgumentException("El producto ya no existe."));

            orden.getProductos().add(new CarritoProducto(orden, cantidad, producto));
        }

        return carritoRepositorio.save(orden);
    }

    @Transactional(readOnly = true)
    public List<Carrito> misCompras(String usuario) {
        return carritoRepositorio.findByidPublicoClienteOrderByFechaOperacionDesc(usuario);
    }
}