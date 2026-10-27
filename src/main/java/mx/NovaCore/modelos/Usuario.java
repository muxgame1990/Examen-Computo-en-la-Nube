package mx.NovaCore.modelos;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Integer id;

    @Column(name = "id_publico", nullable = false, unique = true, length = 45)
    private String idPublico;

    @Column(nullable = false, length = 45)
    private String password;

    @Column(name = "nombre_usuario", nullable = false, length = 80)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol = Rol.cliente;
    @OneToMany
    @JoinColumn(name = "id_publico_cliente")
    private Set<Carrito> carritos = new LinkedHashSet<>();

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

    public Set<Carrito> getCarritos() {
        return carritos;
    }

    public void setCarritos(Set<Carrito> carritos) {
        this.carritos = carritos;
    }
}
