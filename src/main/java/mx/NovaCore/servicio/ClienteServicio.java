package mx.NovaCore.servicio;

import mx.NovaCore.modelos.Cliente;
import mx.NovaCore.modelos.Rol;
import mx.NovaCore.modelos.UsuarioSesion;
import mx.NovaCore.repositorio.CarritoRepositorio;
import mx.NovaCore.repositorio.ClienteRepositorio;
import mx.NovaCore.repositorio.ProductoRepositorio;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service

public class ClienteServicio {
    ClienteRepositorio clienteRepositorio;

    public ClienteServicio(ClienteRepositorio clienteRepositorio) {
            this.clienteRepositorio = clienteRepositorio;
    }
    public ClienteRepositorio getClienteRepositorio() {
        return clienteRepositorio;
    }

    public Optional<UsuarioSesion> login (String usuario, String password) {
        if(usuario == null || usuario.trim().isEmpty() || password == null ||  password.trim().isEmpty()) {
            return Optional.empty();
        }
        return clienteRepositorio.findByIdPublico(usuario.trim())
                .filter(c -> c.getPassword().equals(password))
                .map(c -> new UsuarioSesion(c.getIdPublico(), c.getNombre(), c.getRol()));
    }

    public Cliente registrar (Cliente cliente) {
        if (clienteRepositorio.existsByIdPublico(cliente.getIdPublico()))
            throw new RuntimeException("Usuario existente");
        cliente.setRol(Rol.cliente);
        return clienteRepositorio.save(cliente);
    }
}
