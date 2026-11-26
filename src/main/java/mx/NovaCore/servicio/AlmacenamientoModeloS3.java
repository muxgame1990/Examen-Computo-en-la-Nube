package mx.NovaCore.servicio;

import jakarta.annotation.PreDestroy;
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
@ConditionalOnProperty(name = "app.modelo.almacenamiento", havingValue = "s3")
public class AlmacenamientoModeloS3 implements AlmacenamientoModelo {

    private static final String CARPETA = "ia/";

    private final S3Client s3;
    private final String bucket;

    public AlmacenamientoModeloS3(@Value("${aws.s3.bucket}") String bucket,
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
    public void guardar(String clave, String contenido) throws IOException {
        String tipo = clave.endsWith(".json") ? "application/json" : "text/csv";

        PutObjectRequest solicitud = PutObjectRequest.builder()
                .bucket(bucket)
                .key(CARPETA + clave)
                .contentType(tipo)
                .build();

        try {
            s3.putObject(solicitud, RequestBody.fromString(contenido, StandardCharsets.UTF_8));
        } catch (SdkException e) {
            throw new IOException("No se pudo guardar " + clave + " en S3: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<String> leer(String clave) throws IOException {
        GetObjectRequest solicitud = GetObjectRequest.builder()
                .bucket(bucket)
                .key(CARPETA + clave)
                .build();

        try {
            return Optional.of(s3.getObjectAsBytes(solicitud).asUtf8String());
        } catch (NoSuchKeyException e) {
            return Optional.empty();
        } catch (SdkException e) {
            throw new IOException("No se pudo leer " + clave + " de S3: " + e.getMessage(), e);
        }
    }

    @Override
    public String ubicacion(String clave) {
        return "s3://" + bucket + "/" + CARPETA + clave;
    }

    @PreDestroy
    public void cerrar() {
        s3.close();
    }
}
