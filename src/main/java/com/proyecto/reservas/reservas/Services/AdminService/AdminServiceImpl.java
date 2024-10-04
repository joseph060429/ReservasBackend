package com.proyecto.reservas.reservas.Services.AdminService;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.proyecto.reservas.reservas.Enum.EEstado;
import com.proyecto.reservas.reservas.Enum.ERol;
import com.proyecto.reservas.reservas.Models.RolModel;
import com.proyecto.reservas.reservas.Models.UsuarioModel;
import com.proyecto.reservas.reservas.Repositories.RolRepository;
import com.proyecto.reservas.reservas.Repositories.UserRepository;


@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolRepository rolRepository;

    // IMPLEMENTACIÓN DEL MÉTODO PARA CUANDO UN USUARIO SE QUIERA REGISTRAR COMO
    // OWNER, O CUANDO SE QUIERA ACTUALIZAR DE OWNER A USER
    @Override
    public ResponseEntity<?> actualizarRolAOwner(String usuarioId) {
        // Busco al usuario por su ID
        Optional<UsuarioModel> optionalUsuario = userRepository.findById(usuarioId);

        if (optionalUsuario.isPresent()) {
            UsuarioModel usuario = optionalUsuario.get();

            // Verifico si el estado del usuario es PENDIENTE y su rol es PENDING_OWNER
            if (usuario.getEstado() == EEstado.PENDIENTE &&
                    usuario.getRol().getName().equals(ERol.PENDING_OWNER)) {

                // Actualizo el rol del usuario a OWNER
                RolModel nuevoRolModel = rolRepository.findByName(ERol.OWNER.name())
                        .orElseThrow(() -> new RuntimeException("Rol OWNER no encontrado"));
                usuario.setRol(nuevoRolModel);

                // Actualizo el estado del usuario a ACEPTADO si fue actualizado a OWNER
                usuario.setEstado(EEstado.ACEPTADO);

                // Guardo los cambios hechos
                userRepository.save(usuario);
                return ResponseEntity.ok("Rol actualizado correctamente a OWNER");
            } else {
                return ResponseEntity.badRequest()
                        .body("El usuario no está en estado PENDIENTE o su rol no es PENDING_OWNER");
            }
        } else {
            return ResponseEntity.badRequest().body("Usuario no encontrado");
        }

    }

    // MÉTODO PARA BUSCAR LOS ROLES POR SU NOMBRE Y VER SI EXISTEN
    public Set<RolModel> obtenerRolesPorNombresExistentes(Set<String> nombresRoles) {
        Set<RolModel> rolesExistentes = new HashSet<>();

        for (String nombreRol : nombresRoles) {
            try {
                ERol rolEnum = ERol.valueOf(nombreRol);
                RolModel rol = rolRepository.findByName(rolEnum.name()).orElse(null);

                if (rol != null) {
                    rolesExistentes.add(rol);
                }
            } catch (IllegalArgumentException ignored) {
                // Esta excecption como es de un for y no quiero que coja un rol malo y salga
                // del programa, le
                // pongo un continue para que siga su flujo normal.
                continue;
            }
        }
        return rolesExistentes;
    }

}
