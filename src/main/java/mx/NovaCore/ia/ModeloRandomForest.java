package mx.NovaCore.ia;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ModeloRandomForest {

    private String fechaEntrenamiento;
    private int numeroArboles;
    private int atributosPorDivision;
    private long semilla;
    private int registrosEntrenamiento;
    private Double precisionFueraDeBolsa;
    private List<String> atributos = new ArrayList<>();
    private List<String> clases = new ArrayList<>();
    private List<NodoArbol> arboles = new ArrayList<>();

    public Map<String, Integer> votar(Map<String, String> registro) {
        Map<String, Integer> votos = new LinkedHashMap<>();

        for (String clase : clases) {
            votos.put(clase, 0);
        }

        for (NodoArbol arbol : arboles) {
            votos.merge(arbol.clasificar(registro), 1, Integer::sum);
        }

        return votos;
    }

    public String getFechaEntrenamiento() {
        return fechaEntrenamiento;
    }

    public void setFechaEntrenamiento(String fechaEntrenamiento) {
        this.fechaEntrenamiento = fechaEntrenamiento;
    }

    public int getNumeroArboles() {
        return numeroArboles;
    }

    public void setNumeroArboles(int numeroArboles) {
        this.numeroArboles = numeroArboles;
    }

    public int getAtributosPorDivision() {
        return atributosPorDivision;
    }

    public void setAtributosPorDivision(int atributosPorDivision) {
        this.atributosPorDivision = atributosPorDivision;
    }

    public long getSemilla() {
        return semilla;
    }

    public void setSemilla(long semilla) {
        this.semilla = semilla;
    }

    public int getRegistrosEntrenamiento() {
        return registrosEntrenamiento;
    }

    public void setRegistrosEntrenamiento(int registrosEntrenamiento) {
        this.registrosEntrenamiento = registrosEntrenamiento;
    }

    public Double getPrecisionFueraDeBolsa() {
        return precisionFueraDeBolsa;
    }

    public void setPrecisionFueraDeBolsa(Double precisionFueraDeBolsa) {
        this.precisionFueraDeBolsa = precisionFueraDeBolsa;
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

    public List<NodoArbol> getArboles() {
        return arboles;
    }

    public void setArboles(List<NodoArbol> arboles) {
        this.arboles = arboles;
    }
}
