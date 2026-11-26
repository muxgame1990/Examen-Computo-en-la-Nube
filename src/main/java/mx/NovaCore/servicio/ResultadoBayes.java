package mx.NovaCore.servicio;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResultadoBayes implements Serializable {

    private final String recomendado;
    private final double probI100;
    private final double probI500;
    private final Map<String, String> respuestas;

    public ResultadoBayes(String recomendado, double probI100, double probI500, Map<String, String> respuestas) {
        this.recomendado = recomendado;
        this.probI100 = probI100;
        this.probI500 = probI500;
        this.respuestas = new LinkedHashMap<>(respuestas);
    }

    public String getRecomendado() {
        return recomendado;
    }

    public double getProbI100() {
        return probI100;
    }

    public double getProbI500() {
        return probI500;
    }

    public int getPorcentajeI100() {
        return (int) Math.round(probI100 * 100);
    }

    public int getPorcentajeI500() {
        return 100 - getPorcentajeI100();
    }

    public int getPorcentaje() {
        return "i500".equals(recomendado) ? getPorcentajeI500() : getPorcentajeI100();
    }

    public Map<String, String> getRespuestas() {
        return respuestas;
    }
}
