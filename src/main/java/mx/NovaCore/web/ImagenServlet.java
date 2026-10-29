package mx.NovaCore.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.inject.Inject;
import jakarta.faces.context.FacesContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import java.io.IOException;
import mx.NovaCore.servicio.AlmacenamientoImagenServicio;

@WebServlet("/imagenes/*")
public class ImagenServlet extends HttpServlet {

    private Path directorioBase;

    @Override
    public void init() throws ServletException {
        String configurado =
                System.getProperty("avanti.imagenes.dir");

        if (configurado != null && !configurado.isBlank()) {
            directorioBase = Paths.get(configurado);
        } else {
            directorioBase = Paths.get(
                    System.getProperty("user.home"),
                    "avanti-shop",
                    "imagenes"
            );
        }

        directorioBase = directorioBase
                .toAbsolutePath()
                .normalize();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        String rutaSolicitada = request.getPathInfo();

        if (rutaSolicitada == null || rutaSolicitada.equals("/")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Path archivo = directorioBase
                .resolve(rutaSolicitada.substring(1))
                .normalize();

        if (!archivo.startsWith(directorioBase)
                || !Files.isRegularFile(archivo)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String nombre = archivo.getFileName()
                .toString()
                .toLowerCase();

        String tipoContenido;

        if (nombre.endsWith(".jpg") || nombre.endsWith(".jpeg")) {
            tipoContenido = "image/jpeg";
        } else if (nombre.endsWith(".png")) {
            tipoContenido = "image/png";
        } else if (nombre.endsWith(".gif")) {
            tipoContenido = "image/gif";
        } else {
            response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            return;
        }

        response.setContentType(tipoContenido);
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("Cache-Control", "private, max-age=3600");
        response.setContentLengthLong(Files.size(archivo));

        try (InputStream entrada = Files.newInputStream(archivo)) {
            entrada.transferTo(response.getOutputStream());
        }
    }
}
