package com.proyecto.reservas.reservas.DTO.DTOUsuario;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import com.proyecto.reservas.reservas.Enum.EEstado;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.*;

@Data // getters y setters
@AllArgsConstructor // constructor con todos los atributos
@NoArgsConstructor
public class CrearUsuarioDTO {

    
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

    @NotBlank(message = "La contraseña no puede estar en blanco, ingrese números o letras")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    private String fechaCreacion;

    private String rol;

    @Enumerated(EnumType.STRING)
    private EEstado estado;

    // La validación de @Email no permite espacios al inicio ni al final, así que
    // antes de que salte la validación, aquí le estoy poniendo el .trim() para que
    // se guarde sin espacios
    public void setEmail(String email) {
        this.email = email != null ? email.trim() : null;
    }

    // METODO PARA CREAR LA FECHA EXACTA EN LA QUE SE REGISTRA EL USUARIO
    public void setFechaCreacion() {
        // Obtengo la zona horaria específica
        ZoneId zoneId = ZoneId.of("Europe/Madrid");

        // Obtengo la fecha y hora actuales con la zona horaria especificada
        ZonedDateTime fechaActual = ZonedDateTime.now(zoneId);

        // Defino el formato para la fecha
        DateTimeFormatter formatearFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        // Formateo la fecha y la guardo en la propiedad fechaCreacion
        this.fechaCreacion = fechaActual.format(formatearFecha);
    }
}
