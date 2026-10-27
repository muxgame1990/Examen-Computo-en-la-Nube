package mx.NovaCore.vista.Frijol;

import jakarta.annotation.PostConstruct;
import mx.NovaCore.modelos.Producto;
import mx.NovaCore.servicio.ProductoServicio;

import java.io.Serializable;
import java.util.List;

public class AdminPanelBean implements Serializable {
    private List<Producto> listaProductos;
    private ProductoServicio ps;

    @PostConstruct
    public void init(){
        listaProductos = ps.listaProductos();

    }

    public void eliminarProducto(){

    }
    public void editarProducto(){



    }
    public void agregarProducto(){


    }
}
