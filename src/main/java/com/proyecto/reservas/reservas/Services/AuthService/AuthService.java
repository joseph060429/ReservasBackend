package com.proyecto.reservas.reservas.Services.AuthService;

import org.springframework.http.ResponseEntity;

import com.proyecto.reservas.reservas.DTO.DTOUsuario.CrearUsuarioDTO;


public interface AuthService {
    
    // METODO PARA CREAR UN NUEVO USUARIO
    ResponseEntity<?> crearNuevoUsuario(CrearUsuarioDTO usuarioDTO);

}
