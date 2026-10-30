package mx.NovaCore.repositorio;

import mx.NovaCore.modelos.MovimientoInventario;
import mx.NovaCore.modelos.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovimientoInventarioRepositorio  extends JpaRepository<MovimientoInventario,Integer> {

    @Query("SELECT m FROM MovimientoInventario m JOIN FETCH m.producto p " +
            "ORDER BY m.fechaHora DESC, m.id DESC")
    List<MovimientoInventario> listarTodos();

    @Query("""
            SELECT m FROM MovimientoInventario m
            JOIN FETCH m.producto p
            WHERE m.tipo = :tipo
            ORDER BY m.fechaHora DESC, m.id DESC
            """)
    List<MovimientoInventario> listarPorTipo(@Param("tipo")TipoMovimiento tipo);

    @Query("SELECT m FROM MovimientoInventario m JOIN FETCH m.producto p " +
            "WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) " +
            "OR LOWER(p.identificador) LIKE LOWER(CONCAT('%', :texto, '%')) " +
            "ORDER BY m.fechaHora DESC, m.id DESC")
    List<MovimientoInventario> buscarPorTexto(@Param("texto") String texto);

    @Query("SELECT m FROM MovimientoInventario m JOIN FETCH m.producto p " +
            "WHERE m.tipo = :tipo " +
            "  AND (LOWER(p.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) " +
            "    OR LOWER(p.identificador) LIKE LOWER(CONCAT('%', :texto, '%'))) " +
            "ORDER BY m.fechaHora DESC, m.id DESC")
    List<MovimientoInventario> buscarPorTipoYTexto(@Param("tipo") TipoMovimiento tipo,
                                                   @Param("texto") String texto);
}
