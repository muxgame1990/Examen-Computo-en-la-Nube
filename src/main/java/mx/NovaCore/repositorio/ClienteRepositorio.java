package mx.NovaCore.repositorio;

import mx.NovaCore.modelos.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepositorio extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByIdPublico(String id);
    boolean existsByIdPublico(String id);


}
