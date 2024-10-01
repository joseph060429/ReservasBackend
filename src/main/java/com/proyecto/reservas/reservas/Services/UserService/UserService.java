package com.proyecto.reservas.reservas.Services.UserService;

import org.springframework.http.ResponseEntity;

import com.proyecto.reservas.reservas.DTO.DTOUsuario.ActualizarUsuarioDTO;
import com.proyecto.reservas.reservas.Security.Jwt.JwtUtils;

public interface UserService {

    // METODO PARA ELIMINAR USUARIO
    ResponseEntity<String> eliminarUsuarioSiendoUsuario(String token, JwtUtils jwtUtils);

    // String actualizarUsuario(ActualizarUsuarioDTO actualizarUsuarioDTO, String token,
    //         JwtUtils jwtUtils);

    ResponseEntity<String> actualizarUsuario(ActualizarUsuarioDTO actualizarUsuarioDTO, String token,
            JwtUtils jwtUtils);

}
