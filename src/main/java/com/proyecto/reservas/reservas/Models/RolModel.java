package com.proyecto.reservas.reservas.Models;


import jakarta.persistence.Id;
import com.proyecto.reservas.reservas.Enum.ERol;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "roles")
public class RolModel {

    @Id
    private String rolId;

    private String name;

    // Método para añadir el nombre del rol del enum ERol
    public void setName(ERol eRol) {
        this.name = eRol.toString();
    }

    // Método para obtener el nombre del rol como enum ERol
    public ERol getName() {
        return ERol.valueOf(this.name);
    }

    

}
