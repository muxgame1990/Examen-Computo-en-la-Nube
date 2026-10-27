package mx.NovaCore.servicio;

import mx.NovaCore.modelos.Usuario;
import mx.NovaCore.modelos.Rol;
import mx.NovaCore.repositorio.ClienteRepositorio;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service

public class ClienteServicio {
    ClienteRepositorio clienteRepositorio;

    public ClienteRepositorio getClienteRepositorio() {
        return clienteRepositorio;
    }

    public Optional<Usuario> login (String usuario, String password) {
        return clienteRepositorio.findByIdPublico(usuario).filter(c -> c.getPassword().equals(password));

    }

    public Usuario registrar (Usuario cliente) {
        if (clienteRepositorio.existsByIdPublico(cliente.getIdPublico()))
            throw new RuntimeException("Usuario existente");
        cliente.setRol(Rol.cliente);
        return clienteRepositorio.save(cliente);
    }
}
