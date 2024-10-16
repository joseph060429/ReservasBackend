package com.proyecto.reservas.reservas.Services.AuthService;

import org.springframework.http.ResponseEntity;

import com.proyecto.reservas.reservas.DTO.DTOUsuario.CrearUsuarioDTO;


public interface AuthService {
    
    // METODO PARA CREAR UN NUEVO USUARIO
    ResponseEntity<?> crearNuevoUsuario(CrearUsuarioDTO usuarioDTO);

    // METODO PARA NORMALIZAR TEXTOS(QUITARLE LOS TILDES Y PONER LA PRIMERA LETRA DDEL NOMBRE Y APELLIDO EN MAYÚSCULA)
    String normalizarTextos(String text);

}
