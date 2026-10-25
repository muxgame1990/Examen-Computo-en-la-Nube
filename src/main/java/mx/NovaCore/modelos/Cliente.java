package mx.NovaCore.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cliente_id")
    private Integer id;

    @Column(name = "id_publico", nullable = false, unique = true, length = 100)
    private String idPublico;

    @Column(nullable = false, length = 20)
    private String password;

    @Column(name = "nombre_cliente", nullable = false, length = 40)
    private String nombre;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getIdPublico() { return idPublico; }
    public void setIdPublico(String idPublico) { this.idPublico = idPublico; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
}
