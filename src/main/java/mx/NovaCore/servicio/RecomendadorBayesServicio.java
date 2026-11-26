package mx.NovaCore.servicio;

import com.fasterxml.jackson.databind.ObjectMapper;
import mx.NovaCore.ia.NaiveBayes;
import mx.NovaCore.modelos.ConsultaBayes;
import mx.NovaCore.repositorio.ConsultaBayesRepositorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Recomendador de pulsera (i100 / i500) con Naive Bayes.
 *
 * Los datos de entrenamiento y el modelo entrenado viven en el almacenamiento
 * (carpeta local o S3). Las respuestas de los usuarios se guardan en la base de datos.
 */
@Service
public class RecomendadorBayesServicio {

    private static final Logger LOG = LoggerFactory.getLogger(RecomendadorBayesServicio.class);

    public static final String ARCHIVO_DATOS = "naive-bayes/datos-entrenamiento.csv";
    public static final String ARCHIVO_MODELO = "naive-bayes/modelo.json";
    private static final String DATOS_INICIALES = "ia/datos-entrenamiento.csv";

    public static final String MODELO = "modelo";
    public static final List<String> MODELOS = List.of("i100", "i500");

    // Preguntas del formulario y sus respuestas válidas, en el orden en que se muestran
    public static final Map<String, List<String>> RESPUESTAS = new LinkedHashMap<>();

    static {
        RESPUESTAS.put("interes", List.of("salud", "apariencia", "ambos"));
        RESPUESTAS.put("ejercicio", List.of("sedentario", "moderado", "activo"));
        RESPUESTAS.put("motivacion", List.of("normal", "muy_motivado"));
        RESPUESTAS.put("tecnologia", List.of("si", "no"));
    }

    private final AlmacenamientoModelo almacenamiento;
    private final ConsultaBayesRepositorio consultaRepositorio;
    private final ObjectMapper objectMapper;

    private volatile NaiveBayes modelo;

    public RecomendadorBayesServicio(AlmacenamientoModelo almacenamiento,
                                     ConsultaBayesRepositorio consultaRepositorio,
                                     ObjectMapper objectMapper) {
        this.almacenamiento = almacenamiento;
        this.consultaRepositorio = consultaRepositorio;
        this.objectMapper = objectMapper;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void alArrancar() {
        try {
            obtenerModelo();
            LOG.info("Modelo Naive Bayes listo ({})", almacenamiento.ubicacion(ARCHIVO_MODELO));
        } catch (Exception e) {
            LOG.warn("No se pudo preparar el modelo Naive Bayes al arrancar: {}", e.getMessage());
        }
    }

    public synchronized NaiveBayes obtenerModelo() throws IOException {
        if (modelo == null) {
            Optional<String> json = almacenamiento.leer(ARCHIVO_MODELO);
            if (json.isPresent()) {
                modelo = objectMapper.readValue(json.get(), NaiveBayes.class);
            } else {
                entrenar();
            }
        }
        return modelo;
    }

    public synchronized NaiveBayes entrenar() throws IOException {
        List<Map<String, String>> datos = cargarDatos();

        NaiveBayes nuevo = NaiveBayes.entrenar(datos, RESPUESTAS, MODELOS, MODELO);
        nuevo.setPrecisionValidacion(NaiveBayes.validacionCruzada(datos, RESPUESTAS, MODELOS, MODELO));
        nuevo.setFechaEntrenamiento(LocalDateTime.now().withNano(0).toString().replace('T', ' '));

        almacenamiento.guardar(ARCHIVO_MODELO,
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(nuevo));

        modelo = nuevo;
        return nuevo;
    }

    @Transactional
    public ResultadoBayes recomendar(String interes, String ejercicio, String motivacion,
                                     String tecnologia, String idPublicoCliente) throws IOException {
        Map<String, String> respuestas = new LinkedHashMap<>();
        respuestas.put("interes", interes);
        respuestas.put("ejercicio", ejercicio);
        respuestas.put("motivacion", motivacion);
        respuestas.put("tecnologia", tecnologia);

        for (Map.Entry<String, String> respuesta : respuestas.entrySet()) {
            if (!RESPUESTAS.get(respuesta.getKey()).contains(respuesta.getValue())) {
                throw new IllegalArgumentException("Contesta las cuatro preguntas.");
            }
        }

        NaiveBayes actual = obtenerModelo();
        Map<String, Double> probabilidades = actual.predecir(respuestas);
        double probI100 = probabilidades.get("i100");
        double probI500 = probabilidades.get("i500");
        String recomendado = probI500 > probI100 ? "i500" : "i100";

        consultaRepositorio.save(new ConsultaBayes(idPublicoCliente, interes, ejercicio, motivacion,
                tecnologia, recomendado, probI100, probI500));

        return new ResultadoBayes(recomendado, probI100, probI500, respuestas);
    }

    public List<ConsultaBayes> historialDe(String idPublicoCliente) {
        if (idPublicoCliente == null) {
            return List.of();
        }
        return consultaRepositorio.findTop20ByIdPublicoClienteOrderByFechaHoraDesc(idPublicoCliente);
    }

    public List<ConsultaBayes> ultimasConsultas() {
        return consultaRepositorio.findTop10ByOrderByFechaHoraDesc();
    }

    /**
     * Lee el CSV de entrenamiento del almacenamiento. La primera vez no existe,
     * así que se copia el que viene dentro del proyecto.
     */
    public List<Map<String, String>> cargarDatos() throws IOException {
        Optional<String> guardado = almacenamiento.leer(ARCHIVO_DATOS);
        String csv;

        if (guardado.isPresent()) {
            csv = guardado.get();
        } else {
            try (InputStream entrada = new ClassPathResource(DATOS_INICIALES).getInputStream()) {
                csv = new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
            }
            almacenamiento.guardar(ARCHIVO_DATOS, csv);
        }

        return leerCsv(csv);
    }

    public String getUbicacionDatos() {
        return almacenamiento.ubicacion(ARCHIVO_DATOS);
    }

    public String getUbicacionModelo() {
        return almacenamiento.ubicacion(ARCHIVO_MODELO);
    }

    private List<Map<String, String>> leerCsv(String csv) {
        String[] lineas = csv.strip().split("\\R");
        String[] columnas = lineas[0].split(",");
        List<Map<String, String>> datos = new ArrayList<>();

        for (int i = 1; i < lineas.length; i++) {
            if (lineas[i].isBlank()) {
                continue;
            }

            String[] celdas = lineas[i].split(",");
            if (celdas.length != columnas.length) {
                throw new IllegalStateException("Línea " + (i + 1) + " del CSV no tiene "
                        + columnas.length + " columnas.");
            }

            Map<String, String> dato = new LinkedHashMap<>();
            for (int c = 0; c < columnas.length; c++) {
                dato.put(columnas[c].trim(), celdas[c].trim());
            }
            validarDato(dato, i + 1);
            datos.add(dato);
        }
        return datos;
    }

    private void validarDato(Map<String, String> dato, int linea) {
        for (Map.Entry<String, List<String>> pregunta : RESPUESTAS.entrySet()) {
            String valor = dato.get(pregunta.getKey());
            if (!pregunta.getValue().contains(valor)) {
                throw new IllegalStateException("Línea " + linea + " del CSV: valor no válido '"
                        + valor + "' en la columna " + pregunta.getKey() + ".");
            }
        }
        if (!MODELOS.contains(dato.get(MODELO))) {
            throw new IllegalStateException("Línea " + linea + " del CSV: modelo no válido '"
                    + dato.get(MODELO) + "'.");
        }
    }
}
