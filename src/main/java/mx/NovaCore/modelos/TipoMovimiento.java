package mx.NovaCore.modelos;

public enum TipoMovimiento {
    ENTRADA("entrada", "Entrada", "Entradas", 1),
    SALIDA("salida", "Salida", "Salidas", -1);

    private final String valor;
    private final String etiqueta;
    private final String etiquetas;
    private final int signo;

    TipoMovimiento(String valor, String etiqueta, String etiquetas, int signo) {
        this.valor = valor;
        this.etiqueta = etiqueta;
        this.etiquetas = etiquetas;
        this.signo = signo;
    }

    public String getValor() {
        return valor;
    }
    public String getEtiqueta() {
        return etiqueta;
    }
    public String getEtiquetas() {
        return etiquetas;
    }

    public int getSigno() {
        return signo;
    }

    public boolean isEntrada() {
        return this == ENTRADA;
    }

    public static TipoMovimiento movimientos (String valor) {
        if (valor == null) return null;
        for (TipoMovimiento t : values()) {
            if (t.valor.equalsIgnoreCase(valor)) return t;
        }
        throw new IllegalArgumentException("Tipo de movimiento desconocido: " + valor);
    }
}