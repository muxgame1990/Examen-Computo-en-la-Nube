package mx.NovaCore.repositorio;

import mx.NovaCore.modelos.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarritoRepositorio extends JpaRepository<Carrito, Integer> {
}
