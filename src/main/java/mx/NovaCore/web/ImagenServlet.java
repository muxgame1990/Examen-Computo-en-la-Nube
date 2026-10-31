package mx.NovaCore.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.NovaCore.servicio.AlmacenamientoImagenServicio;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

@WebServlet("/imagenes/*")
public class ImagenServlet extends HttpServlet {

    private AlmacenamientoImagenServicio almacenamiento;

    @Override
    public void init() throws ServletException {
        almacenamiento = WebApplicationContextUtils
                .getRequiredWebApplicationContext(getServletContext())
                .getBean(AlmacenamientoImagenServicio.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String ruta = request.getPathInfo();

        if (ruta == null || ruta.length() < 2 || ruta.contains("..")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String clave = ruta.substring(1);
        String tipoContenido = obtenerTipoContenido(clave);

        if (tipoContenido == null) {
            response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            return;
        }

        try (InputStream entrada = almacenamiento.leer(clave)) {
            response.setContentType(tipoContenido);
            response.setHeader("X-Content-Type-Options", "nosniff");
            response.setHeader("Cache-Control", "public, max-age=86400");
            entrada.transferTo(response.getOutputStream());
        } catch (FileNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private String obtenerTipoContenido(String clave) {
        String nombre = clave.toLowerCase(Locale.ROOT);
        if (nombre.endsWith(".jpg") || nombre.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (nombre.endsWith(".png")) {
            return "image/png";
        }
        if (nombre.endsWith(".gif")) {
            return "image/gif";
        }
        return null;
    }
}