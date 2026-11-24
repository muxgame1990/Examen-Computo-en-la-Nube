package mx.NovaCore.servicio;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResultadoRecomendacionReloj implements Serializable {

    private final String modelo;
    private final int votosModelo;
    private final int totalArboles;
    private final Map<String, Integer> votos;

    public ResultadoRecomendacionReloj(String modelo,
                                       int votosModelo,
                                       int totalArboles,
                                       Map<String, Integer> votos) {
        this.modelo = modelo;
        this.votosModelo = votosModelo;
        this.totalArboles = totalArboles;
        this.votos = new LinkedHashMap<>(votos);
    }

    public String getModelo() {
        return modelo;
    }

    public int getVotosModelo() {
        return votosModelo;
    }

    public int getTotalArboles() {
        return totalArboles;
    }

    public Map<String, Integer> getVotos() {
        return votos;
    }

    public double getConfianza() {
        return totalArboles == 0 ? 0 : (double) votosModelo / totalArboles;
    }

    public int getPorcentaje() {
        return (int) Math.round(getConfianza() * 100);
    }
}