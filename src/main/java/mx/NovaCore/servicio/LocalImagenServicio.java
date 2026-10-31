
package mx.NovaCore.servicio;

import org.primefaces.model.file.UploadedFile;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import java.io.FileNotFoundException;

@Service
@ConditionalOnProperty(name = "app.imagenes.almacenamiento", havingValue = "local", matchIfMissing = true)
public class LocalImagenServicio
        implements AlmacenamientoImagenServicio {

    private static final long TAMANO_MAXIMO = 5 * 1024 * 1024;

    private static final Set<String> EXTENSIONES_PERMITIDAS =
            Set.of("jpg", "jpeg", "png", "gif");

    private Path obtenerDirectorioBase() {
        String directorioConfigurado =
                System.getProperty("avanti.imagenes.dir");

        if (directorioConfigurado != null
                && !directorioConfigurado.isBlank()) {
            return Paths.get(directorioConfigurado)
                    .toAbsolutePath()
                    .normalize();
        }

        return Paths.get(
                System.getProperty("user.home"),
                "avanti-shop",
                "imagenes"
        ).toAbsolutePath().normalize();
    }

    @Override
    public String guardar(UploadedFile archivo) throws IOException {

        if (archivo == null || archivo.getSize() <= 0) {
            throw new IOException("El archivo está vacío.");
        }

        if (archivo.getSize() > TAMANO_MAXIMO) {
            throw new IOException(
                    "La imagen no debe superar los 5 MB."
            );
        }

        String nombreOriginal = archivo.getFileName();

        if (nombreOriginal == null || nombreOriginal.isBlank()) {
            throw new IOException("El archivo no tiene un nombre válido.");
        }

        String nombreLimpio = Paths.get(nombreOriginal)
                .getFileName()
                .toString();

        int ultimoPunto = nombreLimpio.lastIndexOf('.');

        if (ultimoPunto < 0 || ultimoPunto == nombreLimpio.length() - 1) {
            throw new IOException("El archivo no tiene una extensión válida.");
        }

        String extension = nombreLimpio
                .substring(ultimoPunto + 1)
                .toLowerCase(Locale.ROOT);

        if (!EXTENSIONES_PERMITIDAS.contains(extension)) {
            throw new IOException(
                    "Formato no permitido. Usa JPG, JPEG, PNG o GIF."
            );
        }

        Path directorioProductos = obtenerDirectorioBase()
                .resolve("productos")
                .normalize();

        Files.createDirectories(directorioProductos);

        String nombreNuevo =
                UUID.randomUUID() + "." + extension;

        Path destino = directorioProductos
                .resolve(nombreNuevo)
                .normalize();

        if (!destino.startsWith(directorioProductos)) {
            throw new IOException("Ruta de archivo no válida.");
        }

        try (InputStream entrada = archivo.getInputStream()) {
            Files.copy(
                    entrada,
                    destino);
        } catch (IOException e) {
            Files.deleteIfExists(destino);
            throw e;
        }

        return "productos/" + nombreNuevo;
    }
    @Override
    public InputStream leer(String clave) throws IOException {
        Path base = obtenerDirectorioBase();
        Path archivo = base.resolve(clave).normalize();

        if (!archivo.startsWith(base) || !Files.isRegularFile(archivo)) {
            throw new FileNotFoundException("No existe la imagen: " + clave);
        }

        return Files.newInputStream(archivo);
    }
}
