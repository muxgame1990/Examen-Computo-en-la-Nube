package mx.NovaCore.servicio;

import mx.NovaCore.modelos.CarritoProducto;
import mx.NovaCore.modelos.Producto;
import mx.NovaCore.repositorio.CarritoProductoRepositorio;
import mx.NovaCore.repositorio.ProductoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RecomendacionServicio {

    private static final double PESO_COMPRAS = 0.6;
    private static final double PESO_CONTENIDO = 0.4;

    private static final double PESO_CATEGORIA = 2.0;
    private static final double PESO_RANGO_PRECIO = 1.0;
    private static final double PESO_PALABRA = 1.0;

    private static final Set<String> PALABRAS_IGNORADAS = Set.of(
            "con", "para", "los", "las", "del", "una", "uno", "por", "sus", "hasta", "que", "mas"
    );

    private final ProductoRepositorio productoRepositorio;
    private final CarritoProductoRepositorio carritoProductoRepositorio;

    public RecomendacionServicio(ProductoRepositorio productoRepositorio,
                                 CarritoProductoRepositorio carritoProductoRepositorio) {
        this.productoRepositorio = productoRepositorio;
        this.carritoProductoRepositorio = carritoProductoRepositorio;
    }

    @Transactional(readOnly = true)
    public List<Producto> recomendarPorProducto(Integer productoId, int k) {
        if (productoId == null) {
            return List.of();
        }
        return recomendar(Set.of(productoId), k);
    }

    @Transactional(readOnly = true)
    public List<Producto> recomendarPorCarrito(Collection<Integer> productosEnCarrito, int k) {
        if (productosEnCarrito == null) {
            return List.of();
        }
        return recomendar(new HashSet<>(productosEnCarrito), k);
    }

    private List<Producto> recomendar(Set<Integer> idsBase, int k) {
        if (idsBase.isEmpty() || k <= 0) {
            return List.of();
        }

        List<Producto> productos = productoRepositorio.findAll();
        Map<Integer, Producto> productosPorId = productos.stream()
                .collect(Collectors.toMap(Producto::getId, Function.identity()));

        Set<Integer> base = new HashSet<>(idsBase);
        base.retainAll(productosPorId.keySet());
        if (base.isEmpty()) {
            return List.of();
        }

        Map<Integer, Map<String, Double>> vectores = new HashMap<>();
        for (Producto producto : productos) {
            vectores.put(producto.getId(), construirVector(producto));
        }

        Map<Integer, Set<Integer>> ordenesPorProducto = obtenerOrdenesPorProducto();
        boolean hayCompras = !ordenesPorProducto.isEmpty();

        Map<Integer, Double> puntajes = new HashMap<>();
        for (Producto candidato : productos) {
            if (base.contains(candidato.getId()) || candidato.getStock() <= 0) {
                continue;
            }

            double suma = 0;
            for (Integer idBase : base) {
                double contenido = similitudCoseno(
                        vectores.get(idBase),
                        vectores.get(candidato.getId()));

                double compras = similitudCompras(
                        ordenesPorProducto.getOrDefault(idBase, Set.of()),
                        ordenesPorProducto.getOrDefault(candidato.getId(), Set.of()));

                suma += hayCompras
                        ? PESO_COMPRAS * compras + PESO_CONTENIDO * contenido
                        : contenido;
            }

            puntajes.put(candidato.getId(), suma / base.size());
        }

        return puntajes.entrySet().stream()
                .filter(entrada -> entrada.getValue() > 0)
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(k)
                .map(entrada -> productosPorId.get(entrada.getKey()))
                .toList();
    }

    private Map<String, Double> construirVector(Producto producto) {
        Map<String, Double> vector = new HashMap<>();

        if (producto.getCategoria() != null) {
            vector.put("categoria:" + producto.getCategoria().name(), PESO_CATEGORIA);
        }

        vector.put("precio:" + rangoDePrecio(producto.getPrecio()), PESO_RANGO_PRECIO);

        String texto = producto.getNombre() + " " + producto.getDescripcion();
        for (String palabra : separarPalabras(texto)) {
            vector.put("palabra:" + palabra, PESO_PALABRA);
        }

        return vector;
    }

    private String rangoDePrecio(Double precio) {
        if (precio == null || precio < 1000) {
            return "economico";
        }
        if (precio < 3000) {
            return "medio";
        }
        if (precio < 7000) {
            return "alto";
        }
        return "premium";
    }

    private Set<String> separarPalabras(String texto) {
        String sinAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);

        Set<String> palabras = new HashSet<>();
        for (String palabra : sinAcentos.split("[^a-z0-9]+")) {
            if (palabra.length() >= 3 && !PALABRAS_IGNORADAS.contains(palabra)) {
                palabras.add(palabra);
            }
        }
        return palabras;
    }

    private double similitudCoseno(Map<String, Double> a, Map<String, Double> b) {
        double productoPunto = 0;
        for (Map.Entry<String, Double> entrada : a.entrySet()) {
            Double valorEnB = b.get(entrada.getKey());
            if (valorEnB != null) {
                productoPunto += entrada.getValue() * valorEnB;
            }
        }

        double normaA = norma(a);
        double normaB = norma(b);
        if (normaA == 0 || normaB == 0) {
            return 0;
        }
        return productoPunto / (normaA * normaB);
    }

    private double norma(Map<String, Double> vector) {
        double suma = 0;
        for (double valor : vector.values()) {
            suma += valor * valor;
        }
        return Math.sqrt(suma);
    }

    private Map<Integer, Set<Integer>> obtenerOrdenesPorProducto() {
        Map<Integer, Set<Integer>> ordenesPorProducto = new HashMap<>();
        for (CarritoProducto linea : carritoProductoRepositorio.findAll()) {
            Integer productoId = linea.getId().getProductoId();
            Integer carritoId = linea.getId().getCarritoId();
            ordenesPorProducto
                    .computeIfAbsent(productoId, clave -> new HashSet<>())
                    .add(carritoId);
        }
        return ordenesPorProducto;
    }

    private double similitudCompras(Set<Integer> ordenesA, Set<Integer> ordenesB) {
        if (ordenesA.isEmpty() || ordenesB.isEmpty()) {
            return 0;
        }

        Set<Integer> enComun = new HashSet<>(ordenesA);
        enComun.retainAll(ordenesB);

        return enComun.size() / Math.sqrt((double) ordenesA.size() * ordenesB.size());
    }
}
