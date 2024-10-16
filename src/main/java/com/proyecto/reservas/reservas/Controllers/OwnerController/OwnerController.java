package com.proyecto.reservas.reservas.Controllers.OwnerController;

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
import com.proyecto.reservas.reservas.Services.OwnerService.OwnerService;
import com.proyecto.reservas.reservas.Services.UserService.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/owner")
// Permito acceso a usuarios con ROLE_OWNER o ROLE_ADMIN(deben de tener acceso a
// toda la app), solo los usuarios con
// este rol pueden acceder a todos los métodos de esta clase
@SecuredByRole({ "ROLE_OWNER", "ROLE_ADMIN" })
public class OwnerController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private OwnerService ownerService;

    // CONTROLADOR PARA PARA ELIMINAR UN USUARIO O UN OWNER
    @DeleteMapping("/borrarUsuario")
    public ResponseEntity<String> borrarUsuario(@RequestHeader("Authorization") String token) {
        return userService.eliminarUsuarioSiendoUsuario(token, jwtUtils);
    }

    // CONTROLADOR PARA ACTUALIZAR LOS CAMPOS DE UN USUARIO O UN OWNER
    @PatchMapping("/actualizarUsuario")
    public ResponseEntity<String> actualizarUsuario(
            @RequestBody @Valid ActualizarUsuarioDTO actualizarUsuarioDTO,
            @RequestHeader("Authorization") String token) {
        return userService.actualizarUsuario(actualizarUsuarioDTO, token, jwtUtils);
    }

    // CONTROLADOR PARA ACTUALIZAR EL ROL DE UN USUARIO, DE OWNER A USER 
    @PatchMapping("/actualizarRolAUser")
    public ResponseEntity<?> actualizarRolAUser(@RequestHeader("Authorization") String token) {
        return ownerService.actualizarRolOwnerAUser(token, jwtUtils);
    }

}
