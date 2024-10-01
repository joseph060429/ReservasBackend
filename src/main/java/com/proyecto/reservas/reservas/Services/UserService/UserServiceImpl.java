package com.proyecto.reservas.reservas.Services.UserService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.proyecto.reservas.reservas.DTO.DTOUsuario.ActualizarUsuarioDTO;
import com.proyecto.reservas.reservas.Models.UsuarioModel;
import com.proyecto.reservas.reservas.Repositories.UserRepository;
import com.proyecto.reservas.reservas.Security.Jwt.JwtUtils;
import com.proyecto.reservas.reservas.Services.AuthService.AuthServiceImpl;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository usuarioRepositorio;

    @Autowired
    private AuthServiceImpl authServiceImpl;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // IMPLEMENTACIÓN DEL MÉTODO PARA ELIMINAR UN USUARIO SIENDO USUARIO
    @Override
    public ResponseEntity<String> eliminarUsuarioSiendoUsuario(String token, JwtUtils jwtUtils) {

        // Obtengo el email del token, quitandole la palabra Bearer
        String jwtToken = token.replace("Bearer ", "");
        String emailFromToken = jwtUtils.getEmailFromToken(jwtToken);

        // Busco al usuario por el email que me salió en el token
        Optional<UsuarioModel> usuarioOptional = usuarioRepositorio.findByEmail(emailFromToken);

        // Si está lo traigo
        if (usuarioOptional.isPresent()) {
            UsuarioModel usuario = usuarioOptional.get();
            // Comparo si el email encontrado es igual al email del token
            if (usuario.getEmail().equals(emailFromToken)) {
                // Si es igual, elimino el usuario
                usuarioRepositorio.deleteById(usuario.getUsuarioId());
                return ResponseEntity.ok("Usuario eliminado correctamente");
            } else {
                // Si no es igual, no estás autorizado, porque solo podrá eliminar su usuario
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("No estás autorizado para eliminar este usuario");
            }
        } else {
            // Si no, usuario no encontrado
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }

    // IMPLEMENTACION DEL MÉTODO PARA ACTUALIZAR UN USUARIO SIENDO USUARIO
    @Override
    public ResponseEntity<String> actualizarUsuario(ActualizarUsuarioDTO actualizarUsuarioDTO, String token,
            JwtUtils jwtUtils) {

        String jwtToken = token.replace("Bearer ", "");
        String emailFromToken = jwtUtils.getEmailFromToken(jwtToken);
        System.out.println("EMAIL DEL TOKEN: " + emailFromToken);

        Optional<UsuarioModel> usuarioOptional = usuarioRepositorio.findByEmail(emailFromToken);

        if (usuarioOptional.isPresent()) {
            UsuarioModel usuario = usuarioOptional.get();

            // Valido y actualizo los campos que sean diferentes de null
            if (actualizarUsuarioDTO.getNombre() != null && !actualizarUsuarioDTO.getNombre().isEmpty()) {
                usuario.setNombre(authServiceImpl.normalizarTextos(actualizarUsuarioDTO.getNombre().trim()));
            }

            if (actualizarUsuarioDTO.getApellido() != null && !actualizarUsuarioDTO.getApellido().isEmpty()) {
                usuario.setApellido(authServiceImpl.normalizarTextos(actualizarUsuarioDTO.getApellido().trim()));
            }

            // Para que me actualice el email si coincide con el que ya tiene
            if (actualizarUsuarioDTO.getEmail() != null && !actualizarUsuarioDTO.getEmail().isEmpty()) {
                // Validar que el nuevo email no exista
                Optional<UsuarioModel> existeEmail = usuarioRepositorio
                        .findByEmail(actualizarUsuarioDTO.getEmail().trim());
                if (existeEmail.isPresent()) {
                    if (emailFromToken.equals(actualizarUsuarioDTO.getEmail().trim())) {
                        System.out.println("email token: " + emailFromToken);
                        System.out.println("email nuevo: " + actualizarUsuarioDTO.getEmail());
                        usuario.setEmail(actualizarUsuarioDTO.getEmail().trim());
                    } else {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body("El email ya está en uso");
                    }
                } else {
                    usuario.setEmail(actualizarUsuarioDTO.getEmail().trim());
                }
            }

            // Validar la contraseña
            if (actualizarUsuarioDTO.getPassword() != null && !actualizarUsuarioDTO.getPassword().isEmpty()) {
                ResponseEntity<?> response = authServiceImpl.validarPassword(actualizarUsuarioDTO.getPassword().trim());

                // Si la validación de la contraseña no es correcta, devuelvo el error
                if (!response.getStatusCode().is2xxSuccessful()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response.getBody().toString());
                }

                // Si pasa la validación, la guardo
                usuario.setPassword(passwordEncoder.encode(actualizarUsuarioDTO.getPassword().trim()));
            }

            // Actualizo la fecha de modificación
            actualizarUsuarioDTO.setFechaModificacion();
            usuario.setFechaModificacion(actualizarUsuarioDTO.getFechaModificacion());

            // Guardar los cambios en la base de datos
            usuarioRepositorio.save(usuario);

            // Responder con éxito
            return ResponseEntity.ok("Usuario actualizado correctamente");
        } else {
            // Responder con error si no se encuentra el usuario
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se pudo actualizar el usuario debido a un problema desconocido");
        }
    }

    // Ver si tambien se puede actualizar roles, tipo si es OWNER a USER Y viceversa

}
