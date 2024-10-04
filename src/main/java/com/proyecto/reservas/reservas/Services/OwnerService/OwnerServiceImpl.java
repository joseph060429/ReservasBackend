package com.proyecto.reservas.reservas.Services.OwnerService;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.proyecto.reservas.reservas.Enum.ERol;
import com.proyecto.reservas.reservas.Models.RolModel;
import com.proyecto.reservas.reservas.Models.UsuarioModel;
import com.proyecto.reservas.reservas.Repositories.RolRepository;
import com.proyecto.reservas.reservas.Repositories.UserRepository;
import com.proyecto.reservas.reservas.Security.Jwt.JwtUtils;

@Service
public class OwnerServiceImpl implements OwnerService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolRepository rolRepository;


     // IMPLEMENTACIÓN DEL MÉTODO PARA ACTUALIZAR ROL DE OWNER A USER
    @Override
    public ResponseEntity<?> actualizarRolOwnerAUser(String token, JwtUtils jwtUtils) {
        // Obtengo el JWT y extraigo el email del token
        String jwtToken = token.replace("Bearer ", "");
        String emailFromToken = jwtUtils.getEmailFromToken(jwtToken);
        System.out.println("EMAIL DEL TOKEN: " + emailFromToken);

        // Busco el usuario basado en el email del token
        Optional<UsuarioModel> usuarioOptional = userRepository.findByEmail(emailFromToken);

        // Verifico si el usuario existe
        if (usuarioOptional.isPresent()) {
            UsuarioModel usuario = usuarioOptional.get();
            // Obtengo el rol actual del usuario
            ERol rolUsuarioActual = usuario.getRol().getName();
            System.out.println("ROL DEL USUARIO: " + rolUsuarioActual);

            // Si el rol actual es OWNER, procedo a actualizarlo a USER
            if (rolUsuarioActual.equals(ERol.OWNER)) {
                // Busco el rol PENDING_OWNER en la base de datos
                RolModel rolUser = rolRepository.findByName(ERol.USER.name())
                        .orElseThrow(() -> new RuntimeException("Rol USER no encontrado"));

                // Actualizo el rol del usuario a PENDING_OWNER y establezco el estado a
                // NULL
                usuario.setRol(rolUser);
                usuario.setEstado(null);

                // Guardo los cambios en el repositorio
                userRepository.save(usuario);

                // Retorno una respuesta exitosa
                return ResponseEntity
                        .ok("Rol actualizado correctamente a USER");
            }

            // Si el rol no es OWNER, devuelvo un mensaje de error
            return ResponseEntity.badRequest().body("No se puede actualizar el rol desde el estado actual.");
        } else {
            // Si no encuentro al usuario, retorno un mensaje de error
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se pudo actualizar el usuario debido a un problema desconocido.");
        }
    }

}
