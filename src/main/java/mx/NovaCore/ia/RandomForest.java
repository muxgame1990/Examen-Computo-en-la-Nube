package mx.NovaCore.ia;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public final class RandomForest {

    private static final int MINIMO_REGISTROS_PARA_DIVIDIR = 2;

    private RandomForest() {
    }

    public static ModeloRandomForest entrenar(List<Map<String, String>> registros,
                                              List<String> atributos,
                                              String atributoClase,
                                              int numeroArboles,
                                              long semilla) {
        if (registros.isEmpty()) {
            throw new IllegalArgumentException("No hay registros para entrenar el modelo.");
        }

        Random aleatorio = new Random(semilla);
        int atributosPorDivision = Math.max(1, (int) Math.round(Math.sqrt(atributos.size())));

        Set<String> clases = new TreeSet<>();
        for (Map<String, String> registro : registros) {
            clases.add(registro.get(atributoClase));
        }

        List<NodoArbol> arboles = new ArrayList<>();
        Map<Integer, Map<String, Integer>> votosFueraDeBolsa = new HashMap<>();

        for (int i = 0; i < numeroArboles; i++) {
            List<Map<String, String>> muestra = new ArrayList<>();
            Set<Integer> indicesUsados = new HashSet<>();

            for (int j = 0; j < registros.size(); j++) {
                int indice = aleatorio.nextInt(registros.size());
                muestra.add(registros.get(indice));
                indicesUsados.add(indice);
            }

            NodoArbol arbol = construirNodo(
                    muestra,
                    new ArrayList<>(atributos),
                    atributoClase,
                    atributosPorDivision,
                    aleatorio
            );
            arboles.add(arbol);

            for (int j = 0; j < registros.size(); j++) {
                if (!indicesUsados.contains(j)) {
                    String prediccion = arbol.clasificar(registros.get(j));
                    votosFueraDeBolsa
                            .computeIfAbsent(j, clave -> new HashMap<>())
                            .merge(prediccion, 1, Integer::sum);
                }
            }
        }

        ModeloRandomForest modelo = new ModeloRandomForest();
        modelo.setFechaEntrenamiento(LocalDateTime.now().withNano(0).toString());
        modelo.setNumeroArboles(numeroArboles);
        modelo.setAtributosPorDivision(atributosPorDivision);
        modelo.setSemilla(semilla);
        modelo.setRegistrosEntrenamiento(registros.size());
        modelo.setPrecisionFueraDeBolsa(calcularPrecision(registros, atributoClase, votosFueraDeBolsa));
        modelo.setAtributos(new ArrayList<>(atributos));
        modelo.setClases(new ArrayList<>(clases));
        modelo.setArboles(arboles);
        return modelo;
    }

    private static NodoArbol construirNodo(List<Map<String, String>> registros,
                                           List<String> atributosDisponibles,
                                           String atributoClase,
                                           int atributosPorDivision,
                                           Random aleatorio) {
        Map<String, Integer> conteo = contarClases(registros, atributoClase);

        NodoArbol nodo = new NodoArbol();
        nodo.setConteo(conteo);
        nodo.setClase(claseMayoritaria(conteo));

        if (conteo.size() <= 1
                || atributosDisponibles.isEmpty()
                || registros.size() < MINIMO_REGISTROS_PARA_DIVIDIR) {
            return nodo;
        }

        List<String> candidatos = new ArrayList<>(atributosDisponibles);
        Collections.shuffle(candidatos, aleatorio);
        candidatos = candidatos.subList(0, Math.min(atributosPorDivision, candidatos.size()));

        String mejorAtributo = null;
        double mejorGanancia = 0;
        double giniActual = gini(conteo, registros.size());

        for (String candidato : candidatos) {
            double ganancia = giniActual - giniPonderado(registros, candidato, atributoClase);
            if (ganancia > mejorGanancia) {
                mejorGanancia = ganancia;
                mejorAtributo = candidato;
            }
        }

        if (mejorAtributo == null) {
            return nodo;
        }

        nodo.setAtributo(mejorAtributo);

        List<String> restantes = new ArrayList<>(atributosDisponibles);
        restantes.remove(mejorAtributo);

        for (Map.Entry<String, List<Map<String, String>>> grupo : agrupar(registros, mejorAtributo).entrySet()) {
            nodo.getHijos().put(
                    grupo.getKey(),
                    construirNodo(grupo.getValue(), restantes, atributoClase, atributosPorDivision, aleatorio)
            );
        }

        return nodo;
    }

    private static double giniPonderado(List<Map<String, String>> registros,
                                        String atributo,
                                        String atributoClase) {
        double total = 0;

        for (List<Map<String, String>> grupo : agrupar(registros, atributo).values()) {
            double peso = (double) grupo.size() / registros.size();
            total += peso * gini(contarClases(grupo, atributoClase), grupo.size());
        }

        return total;
    }

    private static double gini(Map<String, Integer> conteo, int total) {
        double suma = 0;

        for (int cantidad : conteo.values()) {
            double proporcion = (double) cantidad / total;
            suma += proporcion * proporcion;
        }

        return 1 - suma;
    }

    private static Map<String, List<Map<String, String>>> agrupar(List<Map<String, String>> registros,
                                                                  String atributo) {
        Map<String, List<Map<String, String>>> grupos = new LinkedHashMap<>();

        for (Map<String, String> registro : registros) {
            grupos.computeIfAbsent(registro.get(atributo), clave -> new ArrayList<>()).add(registro);
        }

        return grupos;
    }

    private static Map<String, Integer> contarClases(List<Map<String, String>> registros,
                                                     String atributoClase) {
        Map<String, Integer> conteo = new LinkedHashMap<>();

        for (Map<String, String> registro : registros) {
            conteo.merge(registro.get(atributoClase), 1, Integer::sum);
        }

        return conteo;
    }

    private static String claseMayoritaria(Map<String, Integer> conteo) {
        String mejorClase = null;
        int mejorCantidad = -1;

        for (String clase : new TreeSet<>(conteo.keySet())) {
            int cantidad = conteo.get(clase);
            if (cantidad > mejorCantidad) {
                mejorCantidad = cantidad;
                mejorClase = clase;
            }
        }

        return mejorClase;
    }

    private static Double calcularPrecision(List<Map<String, String>> registros,
                                            String atributoClase,
                                            Map<Integer, Map<String, Integer>> votosFueraDeBolsa) {
        if (votosFueraDeBolsa.isEmpty()) {
            return null;
        }

        int aciertos = 0;

        for (Map.Entry<Integer, Map<String, Integer>> entrada : votosFueraDeBolsa.entrySet()) {
            String real = registros.get(entrada.getKey()).get(atributoClase);
            if (real.equals(claseMayoritaria(entrada.getValue()))) {
                aciertos++;
            }
        }

        return (double) aciertos / votosFueraDeBolsa.size();
    }
}