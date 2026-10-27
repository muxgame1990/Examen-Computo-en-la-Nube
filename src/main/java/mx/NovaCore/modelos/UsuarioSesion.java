package mx.NovaCore.modelos;

import java.io.Serializable;

public class UsuarioSesion implements Serializable {

    public static final String CLAVE = "usuario";

    private final String idPublico;
    private final String nombre;
    private final Rol rol;

    public UsuarioSesion(String idPublico, String nombre, Rol rol) {
        this.idPublico = idPublico;
        this.nombre = nombre;
        this.rol = rol;
    }

    public String getIdPublico() { return idPublico; }
    public String getNombre() { return nombre; }
    public Rol getRol() { return rol; }
    public boolean isAdmin() { return rol == Rol.admin; }
}
