package com.proyecto.reservas.reservas.Repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.proyecto.reservas.reservas.Models.UsuarioModel;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, String> {

    // CONSULTA PARA BUSCAR UN USUARIO POR SU CORREO ELECTRONICO
    Optional<UsuarioModel> findByEmail(String email);

    // CONSULTA PARA COMPROBAR SI EXISTE UN USUARIO CON ESE EMAIL
    boolean existsByEmail(String email);
}
