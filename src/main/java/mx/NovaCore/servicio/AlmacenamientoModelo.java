package mx.NovaCore.servicio;

import java.io.IOException;
import java.util.Optional;

/**
 * Guarda archivos de texto del módulo de IA (datos de entrenamiento y modelo).
 * Se elige con app.modelo.almacenamiento: "local" (por defecto) o "s3".
 */
public interface AlmacenamientoModelo {

    void guardar(String clave, String contenido) throws IOException;

    Optional<String> leer(String clave) throws IOException;

    String ubicacion(String clave);
}
