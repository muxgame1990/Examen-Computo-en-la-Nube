package mx.NovaCore.vista.Frijol;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Named("confirmacionBean")
@ViewScoped
public class ConfirmacionBean implements Serializable {

    @Inject
    private SesionBean sesionBean;

    @Inject
    private CarritoBean carritoBean;

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern(
                    "dd 'de' MMMM 'de' yyyy",
                    Locale.forLanguageTag("es-MX")
            );

    private String cliente = "Cliente Demo";
    private String idTransaccion = "#000001";
    private String fecha;
    private String total;

    @PostConstruct
    public void init() {
        fecha = LocalDate.now().format(FORMATO_FECHA);

        if (sesionBean.isLoggedIn()) {
            cliente = sesionBean.getNombre();
        }

        NumberFormat formatoMoneda =
                NumberFormat.getNumberInstance(Locale.forLanguageTag("es-MX"));
        formatoMoneda.setMinimumFractionDigits(0);
        formatoMoneda.setMaximumFractionDigits(2);

        total = formatoMoneda.format(carritoBean.getSubtotal()) + " Pesos";
    }

    public String getCliente() {
        return cliente;
    }

    public String getIdTransaccion() {
        return idTransaccion;
    }

    public String getFecha() {
        return fecha;
    }

    public String getTotal() {
        return total;
    }
}
