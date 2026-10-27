package mx.NovaCore.repositorio;

import mx.NovaCore.modelos.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarritoRepositorio extends JpaRepository<Carrito, Integer> {
    List<Carrito> findByidPublicoClienteOrderByFechaOperacionDesc(String id);
    List<Carrito> findAllByOrderByFechaOperacionDesc();
}
