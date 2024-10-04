package com.proyecto.reservas.reservas.Controllers.AdminController;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
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

    //CONTROLADOR CUANDO UN USUARIO SE QUIERE REGISTRAR COMO OWNER, O DESEA CAMBIARSE DE USER A OWNER
    @PatchMapping("/actualizarRolUsuarioOwner")
    public ResponseEntity<?> actualizarRolAOwner(@RequestParam("id") String usuarioId) {
        return adminService.actualizarRolAOwner(usuarioId);
    }

}
