package mx.NovaCore.vista.Frijol;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import mx.NovaCore.modelos.Cliente;
import mx.NovaCore.modelos.UsuarioSesion;
import mx.NovaCore.servicio.ClienteServicio;

import java.io.Serializable;
import java.util.Optional;

@Named("sesionBean")
@SessionScoped
public class SesionBean implements Serializable {

    @Inject
    private ClienteServicio clienteServicio;

    private String usuario;
    private String password;
    private UsuarioSesion usuarioSesion;

    public String login() {
        if (usuarioSesion != null) {
            return "index?faces-redirect=true";
        }

        if (usuario == null || usuario.isBlank()
                || password == null || password.isBlank()) {
            agregarMensaje(
                    FacesMessage.SEVERITY_ERROR,
                    "Datos incompletos",
                    "Ingresa tu usuario y contraseña."
            );
            return null;
        }

        Optional<Cliente> cliente =
                clienteServicio.login(usuario.trim(), password);

        if (cliente.isEmpty()) {
            agregarMensaje(
                    FacesMessage.SEVERITY_ERROR,
                    "No se pudo iniciar sesión",
                    "El usuario o la contraseña no son correctos."
            );
            return null;
        }

        Cliente encontrado = cliente.get();
        usuarioSesion = new UsuarioSesion(
                encontrado.getIdPublico(),
                encontrado.getNombre(),
                encontrado.getRol()
        );
        password = null;

        return "index?faces-redirect=true";
    }

    public String logout() {
        FacesContext.getCurrentInstance()
                .getExternalContext()
                .invalidateSession();

        return "index?faces-redirect=true";
    }

    public boolean isLoggedIn() {
        return usuarioSesion != null;
    }

    public String getNombre() {
        return usuarioSesion == null ? "" : usuarioSesion.getNombre();
    }

    public UsuarioSesion getUsuarioSesion() {
        return usuarioSesion;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private void agregarMensaje(
            FacesMessage.Severity severidad,
            String titulo,
            String detalle
    ) {
        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(severidad, titulo, detalle)
        );
    }
}
