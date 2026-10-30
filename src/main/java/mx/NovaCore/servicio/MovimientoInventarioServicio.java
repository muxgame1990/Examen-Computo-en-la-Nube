package mx.NovaCore.servicio;

import org.springframework.transaction.annotation.Transactional;
import mx.NovaCore.modelos.MovimientoInventario;
import mx.NovaCore.modelos.Producto;
import mx.NovaCore.modelos.TipoMovimiento;
import mx.NovaCore.repositorio.MovimientoInventarioRepositorio;
import mx.NovaCore.repositorio.ProductoRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovimientoInventarioServicio {

    private final MovimientoInventarioRepositorio repositorio;
    private final ProductoRepositorio productoRepositorio;

    public MovimientoInventarioServicio(MovimientoInventarioRepositorio repositorio, ProductoRepositorio productoRepositorio) {
        this.repositorio = repositorio;
        this.productoRepositorio = productoRepositorio;
    }

    @Transactional(readOnly = true)
    public List<MovimientoInventario> listaMovimientos() {
        return repositorio.listarTodos();
    }

    @Transactional(readOnly = true)
    public List<MovimientoInventario> busqueda(String texto, TipoMovimiento tipo) {
        boolean existeTexto = texto != null && !texto.trim().isEmpty();
        boolean existeTipo = tipo != null;

        if (!existeTexto && !existeTipo) {
            return repositorio.listarTodos();
        }
        if (existeTexto && !existeTipo) {
            return repositorio.buscarPorTexto(texto.trim());
        }
        if (!existeTexto) {
            return repositorio.listarPorTipo(tipo);
        }
        return repositorio.buscarPorTipoYTexto(tipo, texto.trim());
    }

    @Transactional
    public void registrar(Integer productoId, TipoMovimiento tipo, Integer cantidad) {
        if(productoId == null){
            throw new IllegalArgumentException("Seleccione un producto");
        }
        if(tipo == null){
            throw new IllegalArgumentException("Seleccione un tipo de movimiento");
        }
        validarCantidad(cantidad);

        Producto producto = productoRepositorio.findById(productoId).orElseThrow(() -> new IllegalArgumentException("El Producto seleccionado ya no existe"));
        ajustarStock(producto, tipo.getSigno() + cantidad);
        repositorio.save(new MovimientoInventario(producto, tipo, cantidad));
    }

    @Transactional
    public void actualizarCantidad(Integer movimientoId, Integer nuevaCantidad) {
        validarCantidad(nuevaCantidad);

        MovimientoInventario movimiento = buscarPorId(movimientoId);
        int diferencia = nuevaCantidad - movimiento.getCantidad();

        ajustarStock(movimiento.getProducto(), movimiento.getTipo().getSigno() * diferencia);
        movimiento.setCantidad(nuevaCantidad);
        repositorio.save(movimiento);
    }

    @Transactional
    public void eliminar(Integer movimientoId) {
        MovimientoInventario movimiento = buscarPorId(movimientoId);

        ajustarStock(movimiento.getProducto(),
                -movimiento.getTipo().getSigno() * movimiento.getCantidad());
        repositorio.delete(movimiento);
    }

    private MovimientoInventario buscarPorId(Integer id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El movimiento ya no existe"));
    }

    private void validarCantidad(Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
    }

    private void ajustarStock(Producto producto, int diferencia) {
        int nuevoStock = producto.getStock() + diferencia;
        if (nuevoStock < 0) {
            throw new IllegalArgumentException("Stock insuficiente para " + producto.getNombre()
                    + ". Disponible: " + producto.getStock() + ".");
        }
        producto.setStock(nuevoStock);
        productoRepositorio.save(producto);
    }
}
