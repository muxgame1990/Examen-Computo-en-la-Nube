package mx.NovaCore.servicio;

import mx.NovaCore.modelos.Carrito;
import mx.NovaCore.repositorio.CarritoRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CarritoServicio {
    CarritoRepositorio carritoRepositorio;
    public CarritoServicio() {

    }
    public List<Carrito> getCarritos() {
        return carritoRepositorio.findAll();
    }
}
