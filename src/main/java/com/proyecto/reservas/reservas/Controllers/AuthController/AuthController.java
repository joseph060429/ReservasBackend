package com.proyecto.reservas.reservas.Controllers.AuthController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.proyecto.reservas.reservas.DTO.DTOUsuario.CrearUsuarioDTO;
import com.proyecto.reservas.reservas.Services.AuthService.AuthService;
import jakarta.validation.Valid;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    // CONTROLADOR PARA CREAR UN USUARIO, LO PONGO ASI PORQUE EN MI CLASE
    // EXCEPTIONSGLOBLAS, YA MANEJO LOS ERRORES GLOBALES
    @PostMapping("/crearUsuario")
    public ResponseEntity<?> crearUsuario(@Valid @RequestBody CrearUsuarioDTO crearUsuarioDTO) {
        ResponseEntity<?> response = authService.crearNuevoUsuario(crearUsuarioDTO);
        return ResponseEntity.ok(response.getBody());
    }

}
