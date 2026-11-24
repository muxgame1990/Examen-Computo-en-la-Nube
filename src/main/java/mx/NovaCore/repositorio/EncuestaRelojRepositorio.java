package mx.NovaCore.repositorio;

import mx.NovaCore.modelos.EncuestaReloj;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EncuestaRelojRepositorio extends JpaRepository<EncuestaReloj, Integer> {

    List<EncuestaReloj> findByIdPublicoClienteOrderByFechaHoraDesc(String idPublicoCliente);

    List<EncuestaReloj> findTop10ByOrderByFechaHoraDesc();
}