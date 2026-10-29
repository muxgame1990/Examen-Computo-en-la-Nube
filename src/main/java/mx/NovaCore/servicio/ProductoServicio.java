package mx.NovaCore.servicio;

import mx.NovaCore.modelos.Carrito;
import mx.NovaCore.modelos.Categoria;
import mx.NovaCore.modelos.Producto;
import mx.NovaCore.repositorio.ProductoRepositorio;
import mx.NovaCore.repositorio.CarritoRepositorio;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public void  insert(Producto producto){
        repositorio.save(producto);
    }
    public void  update(Integer id,Producto producto){
        Producto p = repositorio.findById(id).get();
        p.setIdentificador(producto.getIdentificador());
        p.setNombre(producto.getNombre());
        p.setStock(producto.getStock());
        p.setCategoria(producto.getCategoria());
        p.setPrecio(producto.getPrecio());
        p.setImagenUrl(producto.getImagenUrl());
        repositorio.save(p);
    }
    public void delete(Integer id){
        Producto p = repositorio.findById(id).orElse(null);
        repositorio.delete(p);
    }
    public void updateStock(Integer id,int cantidad){
        repositorio.updateStock(id, cantidad);
    }
}
