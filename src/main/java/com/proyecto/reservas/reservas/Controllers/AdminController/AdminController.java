package com.proyecto.reservas.reservas.Controllers.AdminController;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.proyecto.reservas.reservas.Security.Annotations.SecuredByRole;
import com.proyecto.reservas.reservas.Services.AdminService.AdminService;

@RestController
@RequestMapping("/admin")
@SecuredByRole({ "ROLE_ADMIN" })
public class AdminController {

    @Autowired
    private AdminService adminService;

    // CONTROLADOR PARA ACTUALIZAR EL ROL DE LOS QUE QUIEREN SER
    // OWNER, Y TAMBIEN PARA
    // LOS QUE SE CREAN SIENDO USER Y LUEGO QUIEREN CAMBIAR A OWNER
    @PatchMapping("/actualizarRolUsuarioOwner")
    public ResponseEntity<?> actualizarRolAOwner(@RequestParam("id") String usuarioId) {
        return adminService.actualizarRolAOwner(usuarioId);
    }

    // CONTROLADOR PARA ACTUALIZAR LOS ROLES(ADMIN, USER, OWNER) A LOS USUARIOS, 
    @PatchMapping("/actualizarRolUsuario")
    public ResponseEntity<?> actualizarRolUsuario(@RequestParam("id") String usuarioId,
            @RequestBody Map<String, String> requestBody) {
        String nuevoRol = requestBody.get("nuevoRol");

        Set<String> nuevosRoles = new HashSet<>();
        nuevosRoles.add(nuevoRol);

        return adminService.actualizarRolUsuario(usuarioId, nuevosRoles);
    }

}
