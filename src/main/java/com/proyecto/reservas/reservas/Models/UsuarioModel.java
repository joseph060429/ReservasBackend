package com.proyecto.reservas.reservas.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;

@Data // getters y setters
@AllArgsConstructor // constructor con todos los atributos
@NoArgsConstructor // constructor sin atributos
@Entity // Indicarle que es una entidad, es decir una tabla
@Builder
@Table(name = "usuarios") // Creacion de la tabla
public class UsuarioModel {

    @Id
    private String usuarioId;

    @NotBlank(message = "El nombre no puede estar en blanco")
    @Pattern(regexp = "[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+", message = "El nombre solo puede contener letras y caracteres especiales como tildes")
    @Size(min = 2, max = 70, message = "El nombre debe tener entre 2 y 70 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido no puede estar en blanco")
    @Pattern(regexp = "[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+", message = "El apellido solo puede contener letras y caracteres especiales como tildes")
    @Size(min = 2, max = 70, message = "El apellido debe tener entre 2 y 70 caracteres")
    private String apellido;

    @Email(message = "El formato del correo electrónico no es válido")
    @NotBlank(message = "El email no puede estar en blanco")
    private String email;

    // @NotBlank(message = "La contraseña no puede estar en blanco, ingrese números o letras")
    // private String password;

    @NotBlank(message = "La contraseña no puede estar en blanco, ingrese números o letras")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;
    

    private String fechaCreacion;

    private String fechaModificacion;

    @ManyToOne
    @JoinColumn(name = "rol_id")
    private RolModel rol;

}
