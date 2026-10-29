package mx.NovaCore.servicio;
//ESTA CLASE ES PARA APALICAR LA IMAGEN DE S3

import java.io.IOException;
import org.primefaces.model.file.UploadedFile;

public interface AlmacenamientoImagenServicio {

    String guardar(UploadedFile archivo) throws IOException;

}
