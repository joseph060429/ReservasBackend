package com.proyecto.reservas.reservas.Controllers.UserController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.proyecto.reservas.reservas.DTO.DTOUsuario.ActualizarUsuarioDTO;
import com.proyecto.reservas.reservas.Security.Annotations.SecuredByRole;
import com.proyecto.reservas.reservas.Security.Jwt.JwtUtils;
import com.proyecto.reservas.reservas.Services.UserService.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
// Permito acceso a usuarios con ROLE_USER o ROLE_ADMIN(deben de tener acceso a
// toda la app), solo los usuarios con
// este rol pueden acceder a todos los métodos de esta clase
@SecuredByRole({ "ROLE_USER", "ROLE_ADMIN" })
public class UserController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    // CONTROLADOR PARA BORRAR UN USUARIO SIENDO USUARIO O OWNER
    @DeleteMapping("/borrarUsuario")
    public ResponseEntity<String> borrarUsuario(@RequestHeader("Authorization") String token) {
        return userService.eliminarUsuarioSiendoUsuario(token, jwtUtils);
    }

    // CONTROLADOR PARA ACTUALIZAR EL USUARIO SIENDO USUARIO O OWNER
    @PatchMapping("/actualizarUsuario")
    public ResponseEntity<String> actualizarUsuario(
            @RequestBody @Valid ActualizarUsuarioDTO actualizarUsuarioDTO,
            @RequestHeader("Authorization") String token) {
        return userService.actualizarUsuario(actualizarUsuarioDTO, token, jwtUtils);
    }

    // CONTROLADOR PARA ACTUALIZAR EL ROL A OWNER
    @PatchMapping("/actualizarRolAOwner")
    public ResponseEntity<?> actualizarRolAOwner(@RequestHeader("Authorization") String token) {
        return userService.actualizarRolUsuarioAOwner(token, jwtUtils);
    }

}
