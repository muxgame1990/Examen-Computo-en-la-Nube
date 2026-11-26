package mx.NovaCore.vista.Frijol;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import mx.NovaCore.ia.NaiveBayes;
import mx.NovaCore.modelos.ConsultaBayes;
import mx.NovaCore.servicio.RecomendadorBayesServicio;
import mx.NovaCore.servicio.ResultadoBayes;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Named("recomendadorBayesBean")
@ViewScoped
public class RecomendadorBayesBean implements Serializable {

    private static final Map<String, String> PREGUNTAS = new LinkedHashMap<>();
    private static final Map<String, String> ETIQUETAS = new LinkedHashMap<>();

    static {
        PREGUNTAS.put("interes", "Interés");
        PREGUNTAS.put("ejercicio", "Ejercicio");
        PREGUNTAS.put("motivacion", "Motivación");
        PREGUNTAS.put("tecnologia", "Tecnología");

        ETIQUETAS.put("salud", "Mi salud");
        ETIQUETAS.put("apariencia", "Mi apariencia");
        ETIQUETAS.put("ambos", "Ambas");
        ETIQUETAS.put("sedentario", "Casi nada");
        ETIQUETAS.put("moderado", "Algo");
        ETIQUETAS.put("activo", "Mucho");
        ETIQUETAS.put("normal", "Normal");
        ETIQUETAS.put("muy_motivado", "Muy motivado");
        ETIQUETAS.put("si", "Sí");
        ETIQUETAS.put("no", "No");
    }

    private static final Map<String, String> DESCRIPCIONES = Map.of(
            "i100", "Pulsera sencilla: cuenta pasos, calorías y horas de sueño, con batería que dura semanas. "
                    + "Buena para empezar a moverse sin complicaciones.",
            "i500", "Pulsera avanzada: ritmo cardiaco continuo, GPS, planes de entrenamiento y notificaciones "
                    + "del celular. Buena para quien entrena con metas claras."
    );

    @Inject
    private RecomendadorBayesServicio servicio;

    @Inject
    private SesionBean sesionBean;

    private String interes;
    private String ejercicio;
    private String motivacion;
    private String tecnologia;

    private ResultadoBayes resultado;
    private NaiveBayes modelo;
    private List<Map<String, String>> datos = List.of();
    private List<ConsultaBayes> historial = List.of();
    private List<ConsultaBayes> ultimas = List.of();

    @PostConstruct
    public void init() {
        cargarModelo();
        cargarConsultas();
    }

    public void recomendar() {
        try {
            String usuario = sesionBean.isLoggedIn() ? sesionBean.getUsuarioSesion().getIdPublico() : null;
            resultado = servicio.recomendar(interes, ejercicio, motivacion, tecnologia, usuario);
            cargarConsultas();
        } catch (IOException | RuntimeException e) {
            resultado = null;
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo recomendar", e.getMessage());
        }
    }

    public void contestarDeNuevo() {
        interes = null;
        ejercicio = null;
        motivacion = null;
        tecnologia = null;
        resultado = null;
    }

    public void reentrenar() {
        if (!sesionBean.isAdmin()) {
            mensaje(FacesMessage.SEVERITY_WARN, "Sin permiso", "Solo un administrador puede reentrenar el modelo.");
            return;
        }
        try {
            servicio.entrenar();
            cargarModelo();
            mensaje(FacesMessage.SEVERITY_INFO, "Modelo reentrenado",
                    modelo.getRegistros() + " registros. Guardado en " + servicio.getUbicacionModelo());
        } catch (IOException | RuntimeException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo reentrenar", e.getMessage());
        }
    }

    private void cargarModelo() {
        try {
            modelo = servicio.obtenerModelo();
            datos = servicio.cargarDatos();
        } catch (IOException | RuntimeException e) {
            modelo = null;
            datos = List.of();
            mensaje(FacesMessage.SEVERITY_WARN, "Modelo no disponible", e.getMessage());
        }
    }

    private void cargarConsultas() {
        try {
            historial = sesionBean.isLoggedIn()
                    ? servicio.historialDe(sesionBean.getUsuarioSesion().getIdPublico())
                    : List.of();
            ultimas = sesionBean.isAdmin() ? servicio.ultimasConsultas() : List.of();
        } catch (RuntimeException e) {
            historial = List.of();
            ultimas = List.of();
            mensaje(FacesMessage.SEVERITY_WARN, "Sin historial",
                    "No se pudo leer la tabla consulta_bayes. ¿Se corrió db/naive_bayes.sql?");
        }
    }

    /** Una fila por respuesta del usuario: qué tan común es entre compradores de cada modelo. */
    public List<Factor> getExplicacion() {
        List<Factor> factores = new ArrayList<>();
        if (resultado == null || modelo == null) {
            return factores;
        }

        for (Map.Entry<String, String> respuesta : resultado.getRespuestas().entrySet()) {
            String pregunta = respuesta.getKey();
            String valor = respuesta.getValue();
            factores.add(new Factor(
                    PREGUNTAS.get(pregunta),
                    ETIQUETAS.get(valor),
                    modelo.probabilidadRespuesta(pregunta, valor, "i100"),
                    modelo.probabilidadRespuesta(pregunta, valor, "i500")));
        }
        return factores;
    }

    /** Lo que "aprendió" el modelo: cuántos compradores de cada modelo dieron cada respuesta. */
    public List<FilaConteo> getTablaConteos() {
        List<FilaConteo> filas = new ArrayList<>();
        if (modelo == null) {
            return filas;
        }

        for (String pregunta : modelo.getAtributos()) {
            boolean primera = true;
            for (Map.Entry<String, Map<String, Integer>> respuesta : modelo.getConteos().get(pregunta).entrySet()) {
                filas.add(new FilaConteo(
                        primera ? PREGUNTAS.get(pregunta) : "",
                        ETIQUETAS.get(respuesta.getKey()),
                        respuesta.getValue().get("i100"),
                        respuesta.getValue().get("i500")));
                primera = false;
            }
        }
        return filas;
    }

    public int getPorcentajeInicialI100() {
        return modelo == null ? 0 : (int) Math.round(modelo.probabilidadInicial("i100") * 100);
    }

    public int getPorcentajeInicialI500() {
        return modelo == null ? 0 : 100 - getPorcentajeInicialI100();
    }

    public int getPrecision() {
        if (modelo == null || modelo.getPrecisionValidacion() == null) {
            return 0;
        }
        return (int) Math.round(modelo.getPrecisionValidacion() * 100);
    }

    public String getDescripcion() {
        return resultado == null ? null : DESCRIPCIONES.get(resultado.getRecomendado());
    }

    public String etiqueta(String valor) {
        return ETIQUETAS.getOrDefault(valor, valor);
    }

    public String getUbicacionDatos() {
        return servicio.getUbicacionDatos();
    }

    public String getUbicacionModelo() {
        return servicio.getUbicacionModelo();
    }

    private void mensaje(FacesMessage.Severity severidad, String titulo, String detalle) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidad, titulo, detalle));
    }

    public NaiveBayes getModelo() {
        return modelo;
    }

    public ResultadoBayes getResultado() {
        return resultado;
    }

    public List<Map<String, String>> getDatos() {
        return datos;
    }

    public List<ConsultaBayes> getHistorial() {
        return historial;
    }

    public List<ConsultaBayes> getUltimas() {
        return ultimas;
    }

    public String getInteres() {
        return interes;
    }

    public void setInteres(String interes) {
        this.interes = interes;
    }

    public String getEjercicio() {
        return ejercicio;
    }

    public void setEjercicio(String ejercicio) {
        this.ejercicio = ejercicio;
    }

    public String getMotivacion() {
        return motivacion;
    }

    public void setMotivacion(String motivacion) {
        this.motivacion = motivacion;
    }

    public String getTecnologia() {
        return tecnologia;
    }

    public void setTecnologia(String tecnologia) {
        this.tecnologia = tecnologia;
    }

    public static class Factor implements Serializable {
        private final String pregunta;
        private final String respuesta;
        private final int porcentajeI100;
        private final int porcentajeI500;

        public Factor(String pregunta, String respuesta, double pI100, double pI500) {
            this.pregunta = pregunta;
            this.respuesta = respuesta;
            this.porcentajeI100 = (int) Math.round(pI100 * 100);
            this.porcentajeI500 = (int) Math.round(pI500 * 100);
        }

        public String getPregunta() { return pregunta; }
        public String getRespuesta() { return respuesta; }
        public int getPorcentajeI100() { return porcentajeI100; }
        public int getPorcentajeI500() { return porcentajeI500; }

        public String getFavorece() {
            if (porcentajeI500 == porcentajeI100) {
                return "neutral";
            }
            return porcentajeI500 > porcentajeI100 ? "i500" : "i100";
        }
    }

    public static class FilaConteo implements Serializable {
        private final String pregunta;
        private final String respuesta;
        private final int i100;
        private final int i500;

        public FilaConteo(String pregunta, String respuesta, int i100, int i500) {
            this.pregunta = pregunta;
            this.respuesta = respuesta;
            this.i100 = i100;
            this.i500 = i500;
        }

        public String getPregunta() { return pregunta; }
        public String getRespuesta() { return respuesta; }
        public int getI100() { return i100; }
        public int getI500() { return i500; }
    }
}
