package com.proyecto.reservas.reservas.Models;

import java.util.List;

import com.proyecto.reservas.reservas.DTO.DTOPendingOwner.DiaConHorarioDTO;
import com.proyecto.reservas.reservas.Enum.EPista;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "owner_details") // Tabla para los detalles del propietario
public class OwnerDetailsModel {

    @Id
    private String ownerId;

    @NotBlank(message = "El nombre de la instalación no puede estar en blanco")
    @Size(min = 3, max = 100, message = "El nombre de la instalación debe tener entre 3 y 100 caracteres")
    private String nombreInstalacion;

    private String direccionCompleta;

    @NotBlank(message = "El teléfono de contacto no puede estar en blanco")
    @Size(min = 9, max = 9, message = "El teléfono debe tener exactamente 9 dígitos")
    @Pattern(regexp = "^(6\\d{8}|7\\d{8}|8\\d{8}|9\\d{8})$", message = "El teléfono debe ser un número válido de España, comenzando por 6, 7, 8 o 9")
    private String telefonoContacto;

    // Campo obligatorio para el número de pistas en la instalación
    @Min(value = 1, message = "Debe haber al menos una pista")
    private int numeroPistas;

    // Tipo de pistas, puede seleccionar de momento, FUTBOL, PADEL O TENIS
    @NotNull(message = "Debe especificar al menos un tipo de pista")
    @Enumerated(EnumType.STRING)
    private EPista tipoPista;

    // Campo obligatorio para los horarios de disponibilidad de las pistas
    private String horarioDisponible;

    // Campo obligatorio para el precio por hora de las pistas
    @Min(value = 1, message = "El precio por hora debe ser mayor que cero")
    private double precioPorHora;

    // Campo para almacenar el id de la imagen de la instalación (puede ser una URL
    // o ID de la imagen almacenada)
    // @NotBlank(message = "Debe proporcionar un ID o URL para la imagen")
    private String imagenPista; // Aquí podrías guardar la URL o el ID de la imagen que sube el dueño

    // Relación con la tabla Usuario
    @ManyToOne
    @JoinColumn(name = "usuario_id") // Relacion con el usuario (dueño)
    private UsuarioModel usuario;

    // METODO PARA AÑADIR LA DIRECCION COMPLETA
    public String construirDireccionCompleta(String direccion, String municipio, String provincia,
            String codigoPostal, int numero) {
        // Usar StringBuilder para construir la dirección completa
        StringBuilder direccionCompleta = new StringBuilder();

        direccionCompleta.append(direccion.trim()).append(", ");
        direccionCompleta.append("Nº ").append(numero).append(", ");
        direccionCompleta.append(codigoPostal.trim().toUpperCase()).append(", ");
        direccionCompleta.append(municipio.trim()).append(", ");
        direccionCompleta.append(provincia.trim());

        return direccionCompleta.toString(); // Devuelvo la dirección completa como cadena
    }

    // METODO PARA CONSTUIR EL HORARIO DISPONIBLE
    public String construirHorarioDisponible(List<DiaConHorarioDTO> diasConHorarios) {
        StringBuilder horarioDisponible = new StringBuilder();

        for (DiaConHorarioDTO diaConHorario : diasConHorarios) {
            horarioDisponible.append("Día: ").append(diaConHorario.getDia())
                    .append(", Hora: ").append(diaConHorario.getHoraInicio())
                    .append(" a ").append(diaConHorario.getHoraFin())
                    .append("; ");
        }

        return horarioDisponible.toString();
    }

}
