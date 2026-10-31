package mx.NovaCore.vista.Frijol;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
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
            return destinoSegunRol();
        }

        Optional<UsuarioSesion> resultado = clienteServicio.login(usuario, password);
        password = null;

        if (resultado.isEmpty()) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR,
                    "Acceso denegado",
                    "Usuario o contraseña incorrectos.");
            return null;
        }

        usuarioSesion = resultado.get();
        return destinoSegunRol();
    }

    public String logout() {
        FacesContext.getCurrentInstance()
                .getExternalContext()
                .invalidateSession();
        return "index?faces-redirect=true";
    }

    private String destinoSegunRol() {
        return usuarioSesion.isAdmin()
                ? "AdminPanel?faces-redirect=true"
                : "index?faces-redirect=true";
    }

    public boolean isLoggedIn() {
        return usuarioSesion != null;
    }

    public boolean isAdmin() {
        return usuarioSesion != null && usuarioSesion.isAdmin();
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

    private void agregarMensaje(FacesMessage.Severity severidad, String titulo, String detalle) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, titulo, detalle));
    }
}