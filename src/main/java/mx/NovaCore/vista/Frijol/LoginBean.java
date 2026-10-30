package mx.NovaCore.vista.Frijol;
import jakarta.inject.Named;
import jakarta.inject.Inject;
import mx.NovaCore.modelos.Cliente;
import org.primefaces.PrimeFaces;
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
    private String nuevoNombre;
    private String nuevaPassword;
    private String confirmPassword;
    private String nuevoUsuario;
    private String usuario;
    private String password;

    public void Registrar(){
        if (!nuevaPassword.equals(confirmPassword)){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "La contraseña no coincide", "Ambos campos deben tener la misma contraseña"
            ));
            return;
        }
        Cliente cliente = new Cliente();
        cliente.setNombre(nuevoNombre);
        cliente.setPassword(nuevaPassword);
        cliente.setIdPublico(nuevoUsuario.trim());
        try{
            clienteServicio.registrar(cliente);
        } catch (RuntimeException e){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "No se logro crear la cuenta",
                    "Usuario ya existente"
            ));
            return;
        }
        usuario = cliente.getIdPublico();
        nuevoUsuario = null;
        nuevoNombre = null;
        nuevaPassword = null;
        confirmPassword = null;
        FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
                FacesMessage.SEVERITY_INFO,"Cuenta creada",
                "Ya puedes iniciar sesion"));
        PrimeFaces.current().ajax().addCallbackParam("success",true);
        PrimeFaces.current().ajax().update("formLogin:mensajes", "form:Login:usuario");
    }
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
    public String getNuevoNombre() {
        return nuevoNombre;
    }
    public void setNuevoNombre(String nuevoNombre) {
        this.nuevoNombre = nuevoNombre;
    }
    public String getNuevoPassword() {
        return nuevaPassword;
    }
    public void setNuevoPassword(String nuevaPassword) {
        this.nuevaPassword = nuevaPassword;
    }
    public String getConfirmPassword() {
        return confirmPassword;
    }
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    public String getNuevoUsuario() {
        return nuevoUsuario;
    }
    public void setNuevoUsuario(String nuevoUsuario) {
        this.nuevoUsuario = nuevoUsuario;
    }
}
