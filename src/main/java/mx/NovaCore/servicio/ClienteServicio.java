package mx.NovaCore.servicio;

import mx.NovaCore.modelos.Cliente;
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

    public Optional<Cliente> login (String usuario, String password) {
        return clienteRepositorio.findByIdPublico(usuario).filter(c -> c.getPassword().equals(password));

    }

    public Cliente registrar (Cliente cliente) {
        if (clienteRepositorio.existsByIdPublico(cliente.getIdPublico()))
            throw new RuntimeException("Usuario existente");
        cliente.setRol(Rol.cliente);
        return clienteRepositorio.save(cliente);
    }
}
