package mx.NovaCore.vista.Frijol;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import mx.NovaCore.ia.ModeloRandomForest;
import mx.NovaCore.modelos.EncuestaReloj;
import mx.NovaCore.servicio.RecomendacionRelojServicio;
import mx.NovaCore.servicio.ResultadoRecomendacionReloj;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Named("encuestaRelojBean")
@ViewScoped
public class EncuestaRelojBean implements Serializable {

    private static final Map<String, String> NOMBRES = Map.of(
            "i100", "Reloj i100",
            "i500", "Reloj i500"
    );

    private static final Map<String, String> DESCRIPCIONES = Map.of(
            "i100", "Monitor de actividad sencillo: cuenta pasos, calorías y horas de sueño, con batería de larga duración. Ideal para empezar a moverse sin complicarse.",
            "i500", "Reloj deportivo avanzado: ritmo cardiaco continuo, GPS, planes de entrenamiento y notificaciones del celular. Ideal para quien entrena con metas claras."
    );

    private static final Map<String, String> ETIQUETAS_INTERES = Map.of(
            "health", "Mi salud",
            "appearance", "Mi apariencia",
            "both", "Ambas"
    );

    private static final Map<String, String> ETIQUETAS_NIVEL = Map.of(
            "sedentary", "Casi nada",
            "moderate", "Algo",
            "active", "Mucho"
    );

    private static final Map<String, String> ETIQUETAS_MOTIVACION = Map.of(
            "moderate", "Normal",
            "aggressive", "Muy motivado"
    );

    private static final Map<String, String> ETIQUETAS_TECNOLOGIA = Map.of(
            "yes", "Sí",
            "no", "No"
    );

    @Inject
    private RecomendacionRelojServicio recomendacionRelojServicio;

    @Inject
    private SesionBean sesionBean;

    private String interesPrincipal;
    private String nivelEjercicio;
    private String motivacion;
    private String comodoTecnologia;

    private ResultadoRecomendacionReloj resultado;
    private List<EncuestaReloj> historial = List.of();
    private List<EncuestaReloj> ultimasEncuestas = List.of();

    @PostConstruct
    public void init() {
        cargarEncuestas();
    }

    public void recomendar() {
        try {
            String usuario = sesionBean.isLoggedIn()
                    ? sesionBean.getUsuarioSesion().getIdPublico()
                    : null;

            resultado = recomendacionRelojServicio.recomendar(
                    interesPrincipal,
                    nivelEjercicio,
                    motivacion,
                    comodoTecnologia,
                    usuario
            );
            cargarEncuestas();
        } catch (IOException | RuntimeException e) {
            resultado = null;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "No se pudo generar la recomendación", e.getMessage()));
        }
    }

    public void nuevaEncuesta() {
        interesPrincipal = null;
        nivelEjercicio = null;
        motivacion = null;
        comodoTecnologia = null;
        resultado = null;
    }

    public void reentrenar() {
        if (!sesionBean.isAdmin()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_WARN, "Sin permiso", "Solo un administrador puede reentrenar el modelo."));
            return;
        }

        try {
            ModeloRandomForest modelo = recomendacionRelojServicio.entrenar();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_INFO, "Modelo reentrenado",
                    modelo.getNumeroArboles() + " árboles entrenados con "
                            + modelo.getRegistrosEntrenamiento() + " registros. Guardado en "
                            + recomendacionRelojServicio.getUbicacionModelo()));
        } catch (IOException | RuntimeException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "No se pudo reentrenar el modelo", e.getMessage()));
        }
    }

    private void cargarEncuestas() {
        historial = sesionBean.isLoggedIn()
                ? recomendacionRelojServicio.historialDe(sesionBean.getUsuarioSesion().getIdPublico())
                : List.of();

        ultimasEncuestas = sesionBean.isAdmin()
                ? recomendacionRelojServicio.ultimasEncuestas()
                : List.of();
    }

    public ModeloRandomForest getModelo() {
        try {
            return recomendacionRelojServicio.obtenerModelo();
        } catch (IOException | RuntimeException e) {
            return null;
        }
    }

    public int getPrecisionModelo() {
        ModeloRandomForest modelo = getModelo();
        if (modelo == null || modelo.getPrecisionFueraDeBolsa() == null) {
            return 0;
        }
        return (int) Math.round(modelo.getPrecisionFueraDeBolsa() * 100);
    }

    public String getUbicacionModelo() {
        return recomendacionRelojServicio.getUbicacionModelo();
    }

    public String getNombreModelo() {
        return resultado == null ? null : NOMBRES.get(resultado.getModelo());
    }

    public String getDescripcionModelo() {
        return resultado == null ? null : DESCRIPCIONES.get(resultado.getModelo());
    }

    public String nombreDe(String modelo) {
        return NOMBRES.getOrDefault(modelo, modelo);
    }

    public Map<String, String> getEtiquetasInteres() {
        return ETIQUETAS_INTERES;
    }

    public Map<String, String> getEtiquetasNivel() {
        return ETIQUETAS_NIVEL;
    }

    public Map<String, String> getEtiquetasMotivacion() {
        return ETIQUETAS_MOTIVACION;
    }

    public Map<String, String> getEtiquetasTecnologia() {
        return ETIQUETAS_TECNOLOGIA;
    }

    public List<EncuestaReloj> getHistorial() {
        return historial;
    }

    public List<EncuestaReloj> getUltimasEncuestas() {
        return ultimasEncuestas;
    }

    public String getInteresPrincipal() {
        return interesPrincipal;
    }

    public void setInteresPrincipal(String interesPrincipal) {
        this.interesPrincipal = interesPrincipal;
    }

    public String getNivelEjercicio() {
        return nivelEjercicio;
    }

    public void setNivelEjercicio(String nivelEjercicio) {
        this.nivelEjercicio = nivelEjercicio;
    }

    public String getMotivacion() {
        return motivacion;
    }

    public void setMotivacion(String motivacion) {
        this.motivacion = motivacion;
    }

    public String getComodoTecnologia() {
        return comodoTecnologia;
    }

    public void setComodoTecnologia(String comodoTecnologia) {
        this.comodoTecnologia = comodoTecnologia;
    }

    public ResultadoRecomendacionReloj getResultado() {
        return resultado;
    }
}