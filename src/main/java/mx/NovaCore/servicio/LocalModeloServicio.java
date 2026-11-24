package mx.NovaCore.servicio;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@ConditionalOnProperty(name = "app.imagenes.almacenamiento", havingValue = "local", matchIfMissing = true)
public class LocalModeloServicio implements AlmacenamientoModeloServicio {

    private final Path directorioBase = Paths.get(System.getProperty("user.home"), "avanti-shop")
            .toAbsolutePath()
            .normalize();

    @Override
    public void guardar(String clave, String contenido) throws IOException {
        Path archivo = resolver(clave);
        Files.createDirectories(archivo.getParent());
        Files.writeString(archivo, contenido, StandardCharsets.UTF_8);
    }

    @Override
    public Optional<String> leer(String clave) throws IOException {
        Path archivo = resolver(clave);

        if (!Files.isRegularFile(archivo)) {
            return Optional.empty();
        }

        return Optional.of(Files.readString(archivo, StandardCharsets.UTF_8));
    }

    @Override
    public String ubicacion(String clave) {
        return directorioBase.resolve(clave).normalize().toString();
    }

    private Path resolver(String clave) throws IOException {
        Path archivo = directorioBase.resolve(clave).normalize();

        if (!archivo.startsWith(directorioBase)) {
            throw new IOException("Ruta de archivo no válida.");
        }

        return archivo;
    }
}