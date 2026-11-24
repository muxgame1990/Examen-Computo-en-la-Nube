package mx.NovaCore.servicio;

import com.fasterxml.jackson.databind.ObjectMapper;
import mx.NovaCore.ia.ModeloRandomForest;
import mx.NovaCore.ia.RandomForest;
import mx.NovaCore.modelos.EncuestaReloj;
import mx.NovaCore.modelos.EntrenamientoReloj;
import mx.NovaCore.repositorio.EncuestaRelojRepositorio;
import mx.NovaCore.repositorio.EntrenamientoRelojRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class RecomendacionRelojServicio {

    public static final String CLAVE_MODELO = "modelos/random-forest-relojes.json";

    private static final int NUMERO_ARBOLES = 25;
    private static final long SEMILLA = 42L;

    private static final String INTERES_PRINCIPAL = "interesPrincipal";
    private static final String NIVEL_EJERCICIO = "nivelEjercicio";
    private static final String MOTIVACION = "motivacion";
    private static final String COMODO_TECNOLOGIA = "comodoTecnologia";
    private static final String MODELO = "modelo";

    private static final List<String> ATRIBUTOS = List.of(
            INTERES_PRINCIPAL, NIVEL_EJERCICIO, MOTIVACION, COMODO_TECNOLOGIA
    );

    private static final Map<String, Set<String>> VALORES_PERMITIDOS = Map.of(
            INTERES_PRINCIPAL, Set.of("both", "health", "appearance"),
            NIVEL_EJERCICIO, Set.of("sedentary", "moderate", "active"),
            MOTIVACION, Set.of("moderate", "aggressive"),
            COMODO_TECNOLOGIA, Set.of("yes", "no")
    );

    private final EntrenamientoRelojRepositorio entrenamientoRepositorio;
    private final EncuestaRelojRepositorio encuestaRepositorio;
    private final AlmacenamientoModeloServicio almacenamientoModelo;
    private final ObjectMapper objectMapper;

    private volatile ModeloRandomForest modelo;

    public RecomendacionRelojServicio(EntrenamientoRelojRepositorio entrenamientoRepositorio,
                                      EncuestaRelojRepositorio encuestaRepositorio,
                                      AlmacenamientoModeloServicio almacenamientoModelo,
                                      ObjectMapper objectMapper) {
        this.entrenamientoRepositorio = entrenamientoRepositorio;
        this.encuestaRepositorio = encuestaRepositorio;
        this.almacenamientoModelo = almacenamientoModelo;
        this.objectMapper = objectMapper;
    }

    public synchronized ModeloRandomForest entrenar() throws IOException {
        List<Map<String, String>> registros = entrenamientoRepositorio.findAll()
                .stream()
                .map(this::convertirRegistro)
                .toList();

        if (registros.isEmpty()) {
            throw new IllegalStateException("No hay datos en la tabla entrenamiento_reloj.");
        }

        ModeloRandomForest nuevoModelo = RandomForest.entrenar(
                registros, ATRIBUTOS, MODELO, NUMERO_ARBOLES, SEMILLA
        );

        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(nuevoModelo);
        almacenamientoModelo.guardar(CLAVE_MODELO, json);

        modelo = nuevoModelo;
        return nuevoModelo;
    }

    public synchronized ModeloRandomForest obtenerModelo() throws IOException {
        if (modelo == null) {
            Optional<String> json = almacenamientoModelo.leer(CLAVE_MODELO);
            modelo = json.isPresent()
                    ? objectMapper.readValue(json.get(), ModeloRandomForest.class)
                    : entrenar();
        }
        return modelo;
    }

    @Transactional
    public ResultadoRecomendacionReloj recomendar(String interesPrincipal,
                                                  String nivelEjercicio,
                                                  String motivacion,
                                                  String comodoTecnologia,
                                                  String idPublicoCliente) throws IOException {
        Map<String, String> respuestas = new HashMap<>();
        respuestas.put(INTERES_PRINCIPAL, interesPrincipal);
        respuestas.put(NIVEL_EJERCICIO, nivelEjercicio);
        respuestas.put(MOTIVACION, motivacion);
        respuestas.put(COMODO_TECNOLOGIA, comodoTecnologia);

        validarRespuestas(respuestas);

        ModeloRandomForest modeloActual = obtenerModelo();
        Map<String, Integer> votos = modeloActual.votar(respuestas);

        String modeloGanador = null;
        int votosGanador = -1;
        for (Map.Entry<String, Integer> voto : votos.entrySet()) {
            if (voto.getValue() > votosGanador) {
                votosGanador = voto.getValue();
                modeloGanador = voto.getKey();
            }
        }

        ResultadoRecomendacionReloj resultado = new ResultadoRecomendacionReloj(
                modeloGanador, votosGanador, modeloActual.getNumeroArboles(), votos
        );

        encuestaRepositorio.save(new EncuestaReloj(
                idPublicoCliente,
                interesPrincipal,
                nivelEjercicio,
                motivacion,
                comodoTecnologia,
                modeloGanador,
                resultado.getConfianza()
        ));

        return resultado;
    }

    public List<EncuestaReloj> historialDe(String idPublicoCliente) {
        if (idPublicoCliente == null) {
            return List.of();
        }
        return encuestaRepositorio.findByIdPublicoClienteOrderByFechaHoraDesc(idPublicoCliente);
    }

    public List<EncuestaReloj> ultimasEncuestas() {
        return encuestaRepositorio.findTop10ByOrderByFechaHoraDesc();
    }

    public String getUbicacionModelo() {
        return almacenamientoModelo.ubicacion(CLAVE_MODELO);
    }

    private void validarRespuestas(Map<String, String> respuestas) {
        for (String atributo : ATRIBUTOS) {
            String valor = respuestas.get(atributo);
            if (valor == null || !VALORES_PERMITIDOS.get(atributo).contains(valor)) {
                throw new IllegalArgumentException("Responde todas las preguntas de la encuesta.");
            }
        }
    }

    private Map<String, String> convertirRegistro(EntrenamientoReloj fila) {
        Map<String, String> registro = new HashMap<>();
        registro.put(INTERES_PRINCIPAL, fila.getInteresPrincipal());
        registro.put(NIVEL_EJERCICIO, fila.getNivelEjercicio());
        registro.put(MOTIVACION, fila.getMotivacion());
        registro.put(COMODO_TECNOLOGIA, fila.getComodoTecnologia());
        registro.put(MODELO, fila.getModelo());
        return registro;
    }
}