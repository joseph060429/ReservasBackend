package com.proyecto.reservas.reservas.DTO.DTOPendingOwner;

import java.util.List;

import com.proyecto.reservas.reservas.Enum.EPista;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data // getters y setters
@AllArgsConstructor // constructor con todos los atributos
@NoArgsConstructor
public class FormuForOwnerDetailsDTO {

    @NotBlank(message = "El nombre de la instalación no puede estar en blanco")
    @Size(min = 3, max = 100, message = "El nombre de la instalación debe tener entre 3 y 100 caracteres")
    private String nombreInstalacion;

    @NotBlank(message = "La dirección no puede estar en blanco")
    @Size(min = 3, max = 200, message = "La dirección debe tener entre 3 y 200 caracteres")
    private String direccion;

    @NotBlank(message = "El municipio no puede estar en blanco")
    private String municipio;

    @NotBlank(message = "La provincia no puede estar en blanco")
    private String provincia;

    @NotBlank(message = "El código postal no puede estar en blanco")
    @Pattern(regexp = "\\d{5}", message = "El código postal debe tener 5 dígitos")
    private String codigoPostal;

    @NotNull(message = "El número no puede estar en blanco")
    @Min(value = 0, message = "El número no puede ser negativo")
    @Digits(integer = 5, fraction = 0, message = "El número no puede tener más de 5 dígitos")
    private Integer numero;

    @NotBlank(message = "El teléfono de contacto no puede estar en blanco")
    @Pattern(regexp = "^(6\\d{8}|7\\d{8}|8\\d{8}|9\\d{8})$", message = "El teléfono debe ser un número válido de España, con 9 dígitos y comenzar por 6, 7, 8 o 9")
    private String telefonoContacto;

    // Campo obligatorio para el número de pistas en la instalación
    @Min(value = 1, message = "Debe haber al menos una pista")
    private int numeroPistas;

    // Tipo de pistas, puede seleccionar de momento, FUTBOL, PADEL O TENIS
    @NotNull(message = "Debe especificar al menos un tipo de pista")
    @Enumerated(EnumType.STRING)
    private EPista tipoPista;

    // Campo obligatorio para el precio por hora de las pistas
    @Min(value = 1, message = "El precio por hora debe ser mayor que cero")
    private double precioPorHora;

    // Campo para almacenar el id
    // @NotBlank(message = "La imagen del producto no puede estar en blanco")
    // @Size(min = 2, message = "La imagen del producto no puede tener menos de 2
    // caracteres")
    private String imagenPista; // Aquí podrías guardar la URL o el ID de la imagen que sube el dueño

    @NotBlank(message = "Debe proporcionar los días y sus horarios")
    private String diasConHorarios; 

    // Metodos para convertir el tipo de pista que es un ENUM a STRING
    public void setTipoPista(String tipoPista) {
        this.tipoPista = EPista.valueOf(tipoPista.toUpperCase()); // Convierto la cadena a mayúsculas
    }

    // Getter para obtener el tipoPista
    public EPista getTipoPista() {
        return tipoPista;
    }

}
