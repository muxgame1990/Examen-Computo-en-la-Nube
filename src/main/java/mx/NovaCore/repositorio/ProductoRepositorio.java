package mx.NovaCore.repositorio;

import mx.NovaCore.modelos.Categoria;
import mx.NovaCore.modelos.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductoRepositorio extends JpaRepository<Producto, Integer> {
    List<Producto> findAllByOrderByNombreAsc();

    List<Producto> findByCategoria(Categoria categoria);

    List<Producto> findAllByOrderByNombreDesc();

    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    List<Producto> findByCategoriaAndNombreContainingIgnoreCase(Categoria categoria, String nombre);



    @Modifying
    @Query("UPDATE Producto p SET p.stock = p.stock - :cantidad WHERE p.id = :id AND p.stock >= :cantidad")
    int updateStock(@Param("id") Integer id, @Param("cantidad") int cantidad);

}
