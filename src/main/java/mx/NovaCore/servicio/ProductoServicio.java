package mx.NovaCore.servicio;

import mx.NovaCore.modelos.Categoria;
import mx.NovaCore.modelos.Producto;
import mx.NovaCore.repositorio.ProductoRepositorio;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ProductoServicio {
   private  ProductoRepositorio repositorio;

   public ProductoServicio(ProductoRepositorio repositorio){
       this.repositorio = repositorio;
   }


    public List<Producto> listaProductos(){
        return repositorio.findAll();
    }

    public Producto buscarPorId(Integer id){
        return repositorio.findById(id).orElse(null);
    }

    public List<Producto> findByCaregoria(Categoria categoria){
        return repositorio.findByCategoria(categoria);
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
        Producto p = repositorio.findById(id).get();
        repositorio.delete(p);
    }
    public void updateStock(Integer id,int cantidad){
        repositorio.updateStock(id, cantidad);
    }
}
