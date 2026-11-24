package mx.NovaCore.servicio;

import java.io.IOException;
import java.util.Optional;

public interface AlmacenamientoModeloServicio {

    void guardar(String clave, String contenido) throws IOException;

    Optional<String> leer(String clave) throws IOException;

    String ubicacion(String clave);

}