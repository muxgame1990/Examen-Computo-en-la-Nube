package mx.NovaCore.repositorio;

import mx.NovaCore.modelos.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepositorio extends JpaRepository<Cliente, Integer> {

    Optional<Cliente> findByIdPublicoCliente(String id);
    boolean existsByIdPublicoCliente(String id);


}
