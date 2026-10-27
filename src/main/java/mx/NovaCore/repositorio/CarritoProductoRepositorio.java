package mx.NovaCore.repositorio;

import mx.NovaCore.modelos.CarritoProducto;
import mx.NovaCore.modelos.CarritoProductoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CarritoProductoRepositorio extends JpaRepository<CarritoProducto, CarritoProductoId> {

    Optional<CarritoProducto> existsByProducto_Id(Integer id);

    List<CarritoProducto> findByCarrito_Id(Integer id);

    @Query("SELECT SUM (cp.cantidad * cp.precioUnitario) FROM CarritoProducto cp WHERE cp.carrito.id = : carritoId")
    Double getTotal(@Param("carritoID") Integer carritoId);
}
