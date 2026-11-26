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
@ConditionalOnProperty(name = "app.modelo.almacenamiento", havingValue = "local", matchIfMissing = true)
public class AlmacenamientoModeloLocal implements AlmacenamientoModelo {

    private final Path carpeta = Paths.get(System.getProperty("user.home"), "avanti-shop", "ia")
            .toAbsolutePath()
            .normalize();

    @Override
    public void guardar(String clave, String contenido) throws IOException {
        Path archivo = ruta(clave);
        Files.createDirectories(archivo.getParent());
        Files.writeString(archivo, contenido, StandardCharsets.UTF_8);
    }

    @Override
    public Optional<String> leer(String clave) throws IOException {
        Path archivo = ruta(clave);
        if (!Files.isRegularFile(archivo)) {
            return Optional.empty();
        }
        return Optional.of(Files.readString(archivo, StandardCharsets.UTF_8));
    }

    @Override
    public String ubicacion(String clave) {
        return carpeta.resolve(clave).normalize().toString();
    }

    private Path ruta(String clave) throws IOException {
        Path archivo = carpeta.resolve(clave).normalize();
        if (!archivo.startsWith(carpeta)) {
            throw new IOException("Ruta de archivo no válida: " + clave);
        }
        return archivo;
    }
}
