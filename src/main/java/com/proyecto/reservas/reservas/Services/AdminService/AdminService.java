package com.proyecto.reservas.reservas.Services.AdminService;

import org.springframework.http.ResponseEntity;

public interface AdminService {

    // MÉTODO PARA ACTUALIZAR EL ROL DE LOS QUE QUIEREN SER OWNER, Y TAMBIEN PARA
    // LOS QUE SE CREAN SIENDO USER Y LUEGO QUIEREN CAMBIAR A OWNER
    ResponseEntity<?> actualizarRolAOwner(String usuarioId);


    // PENDIENTE METODO PARA ACTUALIZAR LOS ROLES A TODOS LOS USUARIOS DE MI APLICACION

}
