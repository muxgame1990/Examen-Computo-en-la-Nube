package mx.NovaCore.vista.Frijol;
import jakarta.inject.Named;
import jakarta.inject.Inject;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import mx.NovaCore.modelos.UsuarioSesion;
import mx.NovaCore.servicio.ClienteServicio;

import java.util.Optional;

@Named("loginBean")
@ViewScoped
public class LoginBean {
    @Inject
    private ClienteServicio clienteServicio;

    private String usuario;
    private String password;

    public String InicioSesion(){
        Optional<UsuarioSesion> sesion = clienteServicio.login(usuario, password);
        if(sesion.isPresent()){
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Acceso denegado",
                            "Usuario o contraseña incorrectos"
                    ));
        }
        UsuarioSesion usuarioSesion = sesion.get();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(UsuarioSesion.CLAVE, usuarioSesion);
        if (usuarioSesion.isAdmin()) {
            return "AdminPanel?faces-redirect=true";
        }
        return null;
    }
    public String getUsuario() {
        return usuario;
    }
    public void setUsuario(String username) {
        this.usuario = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
