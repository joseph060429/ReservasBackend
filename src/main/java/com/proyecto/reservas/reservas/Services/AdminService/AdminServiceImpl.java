package com.proyecto.reservas.reservas.Services.AdminService;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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

    // IMPLEMENTACIÓN DEL MÉTODO PARA ACTUALIZAR EL ROL DE LOS QUE QUIEREN SER
    // OWNER, Y TAMBIEN PARA
    // LOS QUE SE CREAN SIENDO USER Y LUEGO QUIEREN CAMBIAR A OWNER
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

    // IMPLEMENTACIÓN DEL MÉTODO PARA ACTUALIZAR LOS ROLES(ADMIN, USER, OWNER) A LOS
    // USUARIOS
    @Override
    public ResponseEntity<?> actualizarRolUsuario(String usuarioId, Set<String> nuevosRoles) {

        // Si no pone ningún rol
        if (nuevosRoles.isEmpty()) {
            return ResponseEntity.badRequest().body("Debes proporcionar al menos un rol");
        }

        // Convierto el rol nuevo a mayúsculas y sin espacios
        Set<String> nuevoRolMayus = nuevosRoles.stream()
                .map(role -> role.trim().toUpperCase())
                .collect(Collectors.toSet());

        Set<RolModel> roles = obtenerRolesPorNombresExistentes(nuevoRolMayus);

        // Si pone algún rol que no exista
        if (roles.isEmpty()) {
            return ResponseEntity.badRequest().body("Los roles proporcionados no son válidos");
        }

        Optional<UsuarioModel> optionalUsuario = userRepository.findById(usuarioId);

        if (optionalUsuario.isPresent()) {
            UsuarioModel usuario = optionalUsuario.get();

            // Verifico si el usuario ya tiene el rol proporcionado
            RolModel nuevoRol = roles.iterator().next();
            if (usuario.getRol().getName().toString().toUpperCase()
                    .equals(nuevoRol.getName().toString().toUpperCase())) {
                return ResponseEntity.badRequest().body("El usuario ya tiene ese rol");
            }

            // Si no lo tiene le asigno el nuevo rol
            usuario.setRol(nuevoRol);

            // Esto lo hago para mantener la lógica que tengo en mi App, ya que si el
            // usuario tiene el estado null es porque tiene permisos de USER, si tiene el
            // estado ACEPTADO es porque es OWNER, y si tiene el estado pendiente es porque
            // tiene el rol PENDING_OWNER
            if (nuevoRol.getName().toString().equalsIgnoreCase(ERol.USER.name())
                    || nuevoRol.getName().toString().equalsIgnoreCase(ERol.ADMIN.name())) {
                usuario.setEstado(null); // Si lo actualizo a USER o ADMIN, el estado es NULL
            } else if (nuevoRol.getName().toString().equalsIgnoreCase(ERol.OWNER.name())) {
                usuario.setEstado(EEstado.ACEPTADO); // Si lo actualizo a OWNER, el estado es ACEPTADO
            } else if (nuevoRol.getName().toString().equalsIgnoreCase(ERol.PENDING_OWNER.name())) {
                usuario.setEstado(EEstado.PENDIENTE); // Si lo actualizo a PENDING_OWNER, el estado es PENDIENTE
            }

            userRepository.save(usuario);

            return ResponseEntity.ok("Rol actualizado correctamente");
        } else {
            return ResponseEntity.badRequest().body("Usuario no encontrado");
        }
    }

    // IMPLEMENTACIÓN DEL MÉTODO PARA ELIMINAR A LOS USUARIOS
    @Override
    public ResponseEntity<?> eliminarUsuario(String usuarioId) {

        // Busco al usuario por su ID
        Optional<UsuarioModel> usuarioOptional = userRepository.findById(usuarioId);
        // Si existe lo elimino
        if (usuarioOptional.isPresent()) {
            UsuarioModel usuario = usuarioOptional.get();
            userRepository.deleteById(usuario.getUsuarioId());
            return ResponseEntity.ok("Usuario eliminado correctamente");
            // Si no existe devuelvo un mensaje de usuario no encontrado
        } else {
            return ResponseEntity.badRequest().body("Usuario no encontrado");

        }
    }

}
