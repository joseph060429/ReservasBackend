package com.proyecto.reservas.reservas.Services.OwnerService;

import org.springframework.http.ResponseEntity;

import com.proyecto.reservas.reservas.Security.Jwt.JwtUtils;

public interface OwnerService {

    // METODO PARA ACTUALIZAR EL ROL DE UN USUARIO, DE OWNER A USER
    ResponseEntity<?> actualizarRolOwnerAUser(String token,
            JwtUtils jwtUtils);

}
