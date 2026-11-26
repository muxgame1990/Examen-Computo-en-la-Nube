package mx.NovaCore.repositorio;

import mx.NovaCore.modelos.ConsultaBayes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsultaBayesRepositorio extends JpaRepository<ConsultaBayes, Integer> {

    List<ConsultaBayes> findTop20ByIdPublicoClienteOrderByFechaHoraDesc(String idPublicoCliente);

    List<ConsultaBayes> findTop10ByOrderByFechaHoraDesc();
}
