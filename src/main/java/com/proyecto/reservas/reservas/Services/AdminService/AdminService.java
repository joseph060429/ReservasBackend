package com.proyecto.reservas.reservas.Services.AdminService;

import java.util.Set;

import org.springframework.http.ResponseEntity;

public interface AdminService {

    // MÉTODO PARA ACTUALIZAR EL ROL DE LOS QUE QUIEREN SER OWNER, Y TAMBIEN PARA
    // LOS QUE SE CREAN SIENDO USER Y LUEGO QUIEREN CAMBIAR A OWNER
    ResponseEntity<?> actualizarRolAOwner(String usuarioId);

    // MÉTODO PARA ACTUALIZAR LOS ROLES(ADMIN, USER, OWNER) A LOS USUARIOS
    ResponseEntity<?> actualizarRolUsuario(String usuarioId, Set<String> nuevosRoles);

    // MÉTODO PARA ELIMINAR A LOS USUARIOS
    ResponseEntity<?> eliminarUsuario(String usuarioId);

    // MÉTODO PARA LISTAR UN USUARIO POR SU ID
    ResponseEntity<?> listarUsuarioById(String usuarioId);

    // MÉTODO PARA LISTAR A TODOS LOS USUARIOS
    ResponseEntity<?> listarUsuarios();
}
