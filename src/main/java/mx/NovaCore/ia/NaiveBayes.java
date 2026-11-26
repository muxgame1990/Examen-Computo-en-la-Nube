package mx.NovaCore.ia;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Clasificador Naive Bayes para atributos de opción múltiple.
 *
 * Entrenar es solo contar: cuántas veces aparece cada respuesta entre los
 * compradores de cada modelo. Para predecir se multiplica la probabilidad
 * inicial de cada modelo por la probabilidad de cada respuesta dado ese modelo.
 * Se usa suavizado de Laplace (+1) para que una respuesta nunca vista no deje
 * la probabilidad en cero.
 */
public class NaiveBayes {

    private List<String> atributos = new ArrayList<>();
    private List<String> clases = new ArrayList<>();
    private Map<String, List<String>> valores = new LinkedHashMap<>();
    private Map<String, Integer> totalPorClase = new LinkedHashMap<>();
    // atributo -> respuesta -> clase -> veces
    private Map<String, Map<String, Map<String, Integer>>> conteos = new LinkedHashMap<>();
    private int registros;
    private String fechaEntrenamiento;
    private Double precisionValidacion;

    public static NaiveBayes entrenar(List<Map<String, String>> datos,
                                      Map<String, List<String>> valores,
                                      List<String> clases,
                                      String atributoClase) {
        if (datos.isEmpty()) {
            throw new IllegalArgumentException("No hay datos para entrenar el modelo.");
        }

        NaiveBayes modelo = new NaiveBayes();
        modelo.atributos = new ArrayList<>(valores.keySet());
        modelo.clases = new ArrayList<>(clases);
        modelo.valores = new LinkedHashMap<>(valores);
        modelo.registros = datos.size();

        for (String clase : clases) {
            modelo.totalPorClase.put(clase, 0);
        }

        for (String atributo : modelo.atributos) {
            Map<String, Map<String, Integer>> porRespuesta = new LinkedHashMap<>();
            for (String valor : valores.get(atributo)) {
                Map<String, Integer> porClase = new LinkedHashMap<>();
                for (String clase : clases) {
                    porClase.put(clase, 0);
                }
                porRespuesta.put(valor, porClase);
            }
            modelo.conteos.put(atributo, porRespuesta);
        }

        for (Map<String, String> dato : datos) {
            String clase = dato.get(atributoClase);
            modelo.totalPorClase.merge(clase, 1, Integer::sum);

            for (String atributo : modelo.atributos) {
                modelo.conteos.get(atributo).get(dato.get(atributo)).merge(clase, 1, Integer::sum);
            }
        }

        return modelo;
    }

    /** P(clase): qué tan común es cada modelo antes de ver las respuestas. */
    public double probabilidadInicial(String clase) {
        return (totalPorClase.get(clase) + 1.0) / (registros + clases.size());
    }

    /** P(respuesta | clase): de los que compraron esa clase, cuántos contestaron así. */
    public double probabilidadRespuesta(String atributo, String valor, String clase) {
        int veces = conteos.get(atributo).get(valor).get(clase);
        return (veces + 1.0) / (totalPorClase.get(clase) + valores.get(atributo).size());
    }

    /** Devuelve la probabilidad de cada clase para las respuestas dadas (suman 1). */
    public Map<String, Double> predecir(Map<String, String> respuestas) {
        Map<String, Double> resultado = new LinkedHashMap<>();
        double suma = 0;

        for (String clase : clases) {
            double p = probabilidadInicial(clase);
            for (String atributo : atributos) {
                p *= probabilidadRespuesta(atributo, respuestas.get(atributo), clase);
            }
            resultado.put(clase, p);
            suma += p;
        }

        for (String clase : clases) {
            resultado.put(clase, resultado.get(clase) / suma);
        }
        return resultado;
    }

    public String clasificar(Map<String, String> respuestas) {
        String mejor = null;
        double mejorProbabilidad = -1;

        for (Map.Entry<String, Double> entrada : predecir(respuestas).entrySet()) {
            if (entrada.getValue() > mejorProbabilidad) {
                mejorProbabilidad = entrada.getValue();
                mejor = entrada.getKey();
            }
        }
        return mejor;
    }

    /**
     * Validación cruzada "uno fuera": se entrena con todos los datos menos uno
     * y se revisa si acierta el que quedó fuera. Se repite con cada dato.
     */
    public static double validacionCruzada(List<Map<String, String>> datos,
                                           Map<String, List<String>> valores,
                                           List<String> clases,
                                           String atributoClase) {
        if (datos.size() < 2) {
            return 0;
        }

        int aciertos = 0;
        for (int i = 0; i < datos.size(); i++) {
            List<Map<String, String>> entrenamiento = new ArrayList<>(datos);
            Map<String, String> prueba = entrenamiento.remove(i);

            NaiveBayes modelo = entrenar(entrenamiento, valores, clases, atributoClase);
            if (modelo.clasificar(prueba).equals(prueba.get(atributoClase))) {
                aciertos++;
            }
        }
        return (double) aciertos / datos.size();
    }

    public List<String> getAtributos() {
        return atributos;
    }

    public void setAtributos(List<String> atributos) {
        this.atributos = atributos;
    }

    public List<String> getClases() {
        return clases;
    }

    public void setClases(List<String> clases) {
        this.clases = clases;
    }

    public Map<String, List<String>> getValores() {
        return valores;
    }

    public void setValores(Map<String, List<String>> valores) {
        this.valores = valores;
    }

    public Map<String, Integer> getTotalPorClase() {
        return totalPorClase;
    }

    public void setTotalPorClase(Map<String, Integer> totalPorClase) {
        this.totalPorClase = totalPorClase;
    }

    public Map<String, Map<String, Map<String, Integer>>> getConteos() {
        return conteos;
    }

    public void setConteos(Map<String, Map<String, Map<String, Integer>>> conteos) {
        this.conteos = conteos;
    }

    public int getRegistros() {
        return registros;
    }

    public void setRegistros(int registros) {
        this.registros = registros;
    }

    public String getFechaEntrenamiento() {
        return fechaEntrenamiento;
    }

    public void setFechaEntrenamiento(String fechaEntrenamiento) {
        this.fechaEntrenamiento = fechaEntrenamiento;
    }

    public Double getPrecisionValidacion() {
        return precisionValidacion;
    }

    public void setPrecisionValidacion(Double precisionValidacion) {
        this.precisionValidacion = precisionValidacion;
    }
}
