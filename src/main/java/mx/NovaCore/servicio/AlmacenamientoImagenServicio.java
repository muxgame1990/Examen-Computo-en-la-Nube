package mx.NovaCore.servicio;

import java.io.IOException;
import java.io.InputStream;
import org.primefaces.model.file.UploadedFile;

public interface AlmacenamientoImagenServicio {

    String guardar(UploadedFile archivo) throws IOException;

    InputStream leer(String clave) throws IOException;

}