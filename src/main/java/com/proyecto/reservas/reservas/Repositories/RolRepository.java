package com.proyecto.reservas.reservas.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import com.proyecto.reservas.reservas.Models.RolModel;

@Repository
public interface RolRepository extends JpaRepository<RolModel, String> {

    // CONSULTA PARA OBTENER EL ROL POR SU NOMBRE
    Optional<RolModel> findByName(String name);

}
