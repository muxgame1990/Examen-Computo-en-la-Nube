package mx.NovaCore.repositorio;

import mx.NovaCore.modelos.CarritoProducto;
import mx.NovaCore.modelos.CarritoProductoId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarritoProductoRepositorio extends JpaRepository<CarritoProducto, CarritoProductoId> {
}
