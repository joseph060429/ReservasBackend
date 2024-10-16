package com.proyecto.reservas.reservas.DTO.DTOPendingOwner;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data // getters y setters
@AllArgsConstructor // constructor con todos los atributos
@NoArgsConstructor
public class DiaConHorarioDTO {

    @NotBlank(message = "El día no puede estar en blanco")
    private String dia;

    @NotBlank(message = "La hora de inicio no puede estar en blanco")
    @Pattern(regexp = "^([01]?\\d|2[0-3]):([00|30])$", message = "La hora de inicio debe estar en el formato HH:00 o HH:30")
    private String horaInicio;

    @NotBlank(message = "La hora de fin no puede estar en blanco")
    @Pattern(regexp = "^([01]?\\d|2[0-3]):([00|30])$", message = "La hora de fin debe estar en el formato HH:00 o HH:30")
    private String horaFin;

}
