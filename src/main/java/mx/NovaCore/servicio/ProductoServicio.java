package mx.NovaCore.servicio;

import mx.NovaCore.modelos.Carrito;
import mx.NovaCore.modelos.Categoria;
import mx.NovaCore.modelos.Producto;
import mx.NovaCore.repositorio.ProductoRepositorio;
import mx.NovaCore.repositorio.CarritoRepositorio;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Locale;

import java.util.List;
import java.util.UUID;

@Service
public class ProductoServicio {
   private  ProductoRepositorio repositorio;
   private CarritoRepositorio carritoRepositorio;
   public ProductoServicio(ProductoRepositorio repositorio, CarritoRepositorio carritoRepositorio) {
       this.repositorio = repositorio;
       this.carritoRepositorio = carritoRepositorio;
   }


    public List<Producto> listaProductos(){
        System.out.println("Llegue");
        return repositorio.findAll();
    }
    public List<Producto> Busqueda(String text, Categoria categoria){
       boolean existeTexto = text != null && !text.trim().isEmpty();
       boolean existeCategoria = categoria != null;
       if(!existeTexto && !existeCategoria) {
           return repositorio.findAll();
       }
       if (existeTexto && !existeCategoria){
           return repositorio.findByNombreContainingIgnoreCase(text);
       }
       if (!existeTexto && existeCategoria){
           return repositorio.findByCategoria(categoria);
       }
       return findByCategoriaAndNombreContainingIgnoreCase(categoria,text);
    }
    public List<Producto> findByCaregoria(Categoria categoria){
        return repositorio.findByCategoria(categoria);
    }
    public List<Producto> findByNombreContainingIgnoreCase(String nombre){
       return repositorio.findByNombreContainingIgnoreCase(nombre);
    }
    public List<Producto> findByCategoriaAndNombreContainingIgnoreCase(Categoria categoria, String nombre){
       return repositorio.findByCategoriaAndNombreContainingIgnoreCase(categoria,nombre);
    }

    public List<Producto> orderByAsc(){
        return repositorio.findAllByOrderByNombreAsc();
    }
    public List<Producto> orderByDesc(){
        return repositorio.findAllByOrderByNombreDesc();
    }
    @Transactional
    public void insert(Producto producto) {

        String temporal = "TMP-" + UUID.randomUUID();

        producto.setIdentificador(temporal);

        Producto productoGuardado = repositorio.saveAndFlush(producto);

        String identificadorFinal = String.format(
                Locale.ROOT,
                "%s - %03d",
                productoGuardado.getCategoria().getPrefijo(),
                productoGuardado.getId()
        );

        productoGuardado.setIdentificador(identificadorFinal);
        repositorio.save(productoGuardado);
    }
    public Producto buscarPorId(Integer id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El producto ya no existe"));
    }
    public void update(Integer id, Producto datos) {
        Producto p = buscarPorId(id);
        p.setNombre(datos.getNombre().trim());
        p.setDescripcion(datos.getDescripcion().trim());
        p.setPrecio(datos.getPrecio());
        if (datos.getImagenUrl() != null && !datos.getImagenUrl().isBlank()) {
            p.setImagenUrl(datos.getImagenUrl());
        }
        repositorio.save(p);
    }
    public void delete(Integer id){
        Producto p = repositorio.findById(id).orElse(null);
        repositorio.delete(p);
    }

    public String generarIdentificadorProvisional(Categoria categoria) {
        if (categoria == null) {
            return null;
        }
        Long siguienteId = repositorio.obtenerSiguienteId();

        if (siguienteId == null) {
            throw new IllegalStateException("No se pudo obtener el siguiente ID de producto.");
        }

        return String.format(Locale.ROOT, "%s - %03d", categoria.getPrefijo(), siguienteId);
    }

    public void updateStock(Integer id,int cantidad){
        repositorio.updateStock(id, cantidad);
    }
}
