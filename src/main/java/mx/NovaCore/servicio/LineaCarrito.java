package mx.NovaCore.servicio;

public class LineaCarrito {

    private final Integer id;
    private final String nombre;
    private final String descripcion;
    private final String imagenUrl;
    private final double precio;
    private final int cantidad;
    private final double subtotal;
    private final int stock;

    public LineaCarrito(
            Integer id,
            String nombre,
            String descripcion,
            String imagenUrl,
            double precio,
            int cantidad,
            double subtotal,
            int stock
    ) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
        this.precio = precio;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
        this.stock = stock;
    }

    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getImagenUrl() { return imagenUrl; }
    public double getPrecio() { return precio; }
    public int getCantidad() { return cantidad; }
    public double getSubtotal() { return subtotal; }
    public int getStock() { return stock; }
}
