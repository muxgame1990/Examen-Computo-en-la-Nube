package mx.NovaCore.servicio;

import mx.NovaCore.modelos.Carrito;
import mx.NovaCore.repositorio.CarritoRepositorio;

import java.util.List;

public class CarritoServicio {
    CarritoRepositorio carritoRepositorio;
    public CarritoServicio() {

    }
    public List<Carrito> getCarritos() {
        return carritoRepositorio.findAll();
    }
}
