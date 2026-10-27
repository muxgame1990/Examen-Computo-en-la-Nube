package mx.NovaCore.servicio;

import jakarta.transaction.Transactional;
import mx.NovaCore.modelos.Carrito;
import mx.NovaCore.modelos.CarritoProducto;
import mx.NovaCore.modelos.Producto;
import mx.NovaCore.repositorio.CarritoRepositorio;
import mx.NovaCore.repositorio.ProductoRepositorio;

import java.util.List;
import java.util.Map;

public class CompraServicio {
    CarritoRepositorio carritoRepositorio;
    ProductoRepositorio productoRepositorio;

    public CompraServicio(ProductoRepositorio productos, CarritoRepositorio carritos) {
        this.productoRepositorio = productos;
        this.carritoRepositorio = carritos;
    }

    @Transactional
    public Carrito comprar(String usuario, Map<Integer, Integer> carrito) {
        Carrito orden = new Carrito(usuario);
        double total = 0;
        for (var item : carrito.entrySet()) {
            if (productoRepositorio.updateStock(item.getKey(), item.getValue()) == 0)
                throw new RuntimeException("No se puede comprar un producto");
            Producto p = productoRepositorio.findById(item.getKey()).orElse(null);
            orden.getProductos().add(new CarritoProducto(orden, item.getValue(),p));
        }
        return carritoRepositorio.save(orden);
    }
    public List<Carrito> compras(String usuario) {
        return carritoRepositorio.findByidPublicoClienteOrderByFechaOperacionDesc(usuario);
    }
}
