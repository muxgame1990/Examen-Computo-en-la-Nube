package mx.NovaCore.modelos;


public enum Categoria {
    PERIFERICOS("perifericos", "Periféricos"),
    TARJETAS_DE_VIDEO("tarjetas de video", "Tarjetas de video"),
    PROCESADORES("procesadores", "Procesadores"),
    MEMORIA_RAM("memoria ram", "Memoria RAM"),
    ACCESORIOS("accesorios", "Accesorios");

    private final String valorBd;
    private final String etiqueta;

    Categoria(String valorBd, String etiqueta) {
        this.valorBd = valorBd;
        this.etiqueta = etiqueta;
    }

    public String getValorBd() { return valorBd; }
    public String getEtiqueta() { return etiqueta; }

    public static Categoria desdeBd(String valor) {
        if (valor == null) return null;
        for (Categoria c : values()) {
            if (c.valorBd.equalsIgnoreCase(valor)) return c;
        }
        throw new IllegalArgumentException("Categoría desconocida: " + valor);
    }
}
