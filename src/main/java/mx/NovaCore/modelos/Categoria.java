package mx.NovaCore.modelos;


public enum Categoria {
    PERIFERICOS("perifericos", "Perifericos","PER"),
    TARJETAS_DE_VIDEO("tarjetas de video", "Tarjetas de video", "GPU"),
    PROCESADORES("procesadores", "Procesadores", "CPU"),
    MEMORIA_RAM("memoria ram", "Memoria RAM","RAM"),
    ACCESORIOS("accesorios", "Accesorios","ACC");

    private final String valorBd;
    private final String etiqueta;
    private final String prefijo;
    private CategoriaConverter cv = new CategoriaConverter();
    Categoria(String valorBd, String etiqueta, String prefijo) {
        this.valorBd = valorBd;
        this.etiqueta = etiqueta;
        this.prefijo = prefijo;
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

    public String getPrefijo() {
        return prefijo;
    }

    public Categoria getEtiquetas(String et){
        return cv.convertToEntityAttribute(et);
    }
}
