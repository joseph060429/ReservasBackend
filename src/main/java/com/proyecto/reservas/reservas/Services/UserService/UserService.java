package com.proyecto.reservas.reservas.Services.UserService;

import org.springframework.http.ResponseEntity;

import com.proyecto.reservas.reservas.DTO.DTOUsuario.ActualizarUsuarioDTO;
import com.proyecto.reservas.reservas.Security.Jwt.JwtUtils;

public interface UserService {

    // MÉTODO PARA ELIMINAR USUARIO
    ResponseEntity<String> eliminarUsuarioSiendoUsuario(String token, JwtUtils jwtUtils);

    // MÉTODO PARA ACTUALIZAR LOS CAMPOS DE UN USUARIO O UN OWNER
    ResponseEntity<String> actualizarUsuario(ActualizarUsuarioDTO actualizarUsuarioDTO, String token,
            JwtUtils jwtUtils);

    // MÉTODO PARA ACTUALIZAR EL ROL DE UN USUARIO, DE USER A OWNER
    ResponseEntity<?> actualizarRolUsuarioAOwner(String token,
    JwtUtils jwtUtils);

}
