package mx.NovaCore.servicio;

import jakarta.annotation.PreDestroy;
import org.primefaces.model.file.UploadedFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "app.imagenes.almacenamiento", havingValue = "s3")
public class S3ImagenServicio implements AlmacenamientoImagenServicio {

    private static final long TAMANO_MAXIMO = 5 * 1024 * 1024;

    private static final Map<String, String> TIPOS_PERMITIDOS = Map.of(
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "png", "image/png",
            "gif", "image/gif"
    );

    private final S3Client s3;
    private final String bucket;

    public S3ImagenServicio(@Value("${aws.s3.bucket}") String bucket,
                            @Value("${aws.region}") String region) {
        if (bucket == null || bucket.isBlank()) {
            throw new IllegalStateException("Falta la variable de entorno S3_BUCKET.");
        }
        this.bucket = bucket;
        this.s3 = S3Client.builder()
                .region(Region.of(region))
                .build();
    }

    @Override
    public String guardar(UploadedFile archivo) throws IOException {
        if (archivo == null || archivo.getSize() <= 0) {
            throw new IOException("El archivo está vacío.");
        }
        if (archivo.getSize() > TAMANO_MAXIMO) {
            throw new IOException("La imagen no debe superar los 5 MB.");
        }

        String nombreOriginal = archivo.getFileName();
        if (nombreOriginal == null || nombreOriginal.isBlank()) {
            throw new IOException("El archivo no tiene un nombre válido.");
        }

        String nombreLimpio = Paths.get(nombreOriginal).getFileName().toString();
        int ultimoPunto = nombreLimpio.lastIndexOf('.');
        if (ultimoPunto < 0 || ultimoPunto == nombreLimpio.length() - 1) {
            throw new IOException("El archivo no tiene una extensión válida.");
        }

        String extension = nombreLimpio.substring(ultimoPunto + 1).toLowerCase(Locale.ROOT);
        String tipoContenido = TIPOS_PERMITIDOS.get(extension);
        if (tipoContenido == null) {
            throw new IOException("Formato no permitido. Usa JPG, JPEG, PNG o GIF.");
        }

        String clave = "productos/" + UUID.randomUUID() + "." + extension;

        PutObjectRequest solicitud = PutObjectRequest.builder()
                .bucket(bucket)
                .key(clave)
                .contentType(tipoContenido)
                .build();

        try (InputStream entrada = archivo.getInputStream()) {
            s3.putObject(solicitud, RequestBody.fromInputStream(entrada, archivo.getSize()));
        } catch (SdkException e) {
            throw new IOException("No se pudo subir la imagen a S3: " + e.getMessage(), e);
        }

        return clave;
    }

    @Override
    public InputStream leer(String clave) throws IOException {
        GetObjectRequest solicitud = GetObjectRequest.builder()
                .bucket(bucket)
                .key(clave)
                .build();

        try {
            return s3.getObject(solicitud);
        } catch (NoSuchKeyException e) {
            throw new FileNotFoundException("No existe la imagen: " + clave);
        } catch (SdkException e) {
            throw new IOException("No se pudo leer la imagen de S3: " + e.getMessage(), e);
        }
    }

    @PreDestroy
    public void cerrar() {
        s3.close();
    }
}