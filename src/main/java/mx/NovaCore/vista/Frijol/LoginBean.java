package mx.NovaCore.vista.Frijol;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import mx.NovaCore.modelos.Cliente;
import mx.NovaCore.servicio.ClienteServicio;
import org.primefaces.PrimeFaces;

import java.io.Serializable;

@Named("loginBean")
@ViewScoped
public class LoginBean implements Serializable {

    @Inject
    private ClienteServicio clienteServicio;

    @Inject
    private SesionBean sesionBean;

    private String nuevoNombre;
    private String nuevoUsuario;
    private String nuevaPassword;
    private String confirmPassword;

    public void registrar() {
        FacesContext contexto = FacesContext.getCurrentInstance();

        if (!nuevaPassword.equals(confirmPassword)) {
            contexto.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "La contraseña no coincide",
                    "Ambos campos deben tener la misma contraseña."));
            return;
        }

        Cliente cliente = new Cliente();
        cliente.setNombre(nuevoNombre.trim());
        cliente.setIdPublico(nuevoUsuario.trim());
        cliente.setPassword(nuevaPassword);

        try {
            clienteServicio.registrar(cliente);
        } catch (RuntimeException e) {
            contexto.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "No se logró crear la cuenta",
                    "Ese usuario ya existe, elige otro."));
            return;
        }

        sesionBean.setUsuario(cliente.getIdPublico());
        nuevoNombre = null;
        nuevoUsuario = null;
        nuevaPassword = null;
        confirmPassword = null;

        contexto.addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_INFO,
                "Cuenta creada",
                "Ya puedes iniciar sesión."));

        PrimeFaces.current().ajax().addCallbackParam("registrado", true);
        PrimeFaces.current().ajax().update("formLogin:mensajes", "formLogin:usuario");
    }

    public String getNuevoNombre() { return nuevoNombre; }
    public void setNuevoNombre(String nuevoNombre) { this.nuevoNombre = nuevoNombre; }

    public String getNuevoUsuario() { return nuevoUsuario; }
    public void setNuevoUsuario(String nuevoUsuario) { this.nuevoUsuario = nuevoUsuario; }

    public String getNuevoPassword() { return nuevaPassword; }
    public void setNuevoPassword(String nuevaPassword) { this.nuevaPassword = nuevaPassword; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}