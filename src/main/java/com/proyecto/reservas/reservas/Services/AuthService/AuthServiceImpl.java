package com.proyecto.reservas.reservas.Services.AuthService;

import java.text.Normalizer;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.proyecto.reservas.reservas.DTO.DTOUsuario.CrearUsuarioDTO;
import com.proyecto.reservas.reservas.Enum.ERol;
import com.proyecto.reservas.reservas.Models.RolModel;
import com.proyecto.reservas.reservas.Models.UsuarioModel;
import com.proyecto.reservas.reservas.Repositories.RolRepository;
import com.proyecto.reservas.reservas.Repositories.UsuarioRepository;
import java.util.Optional;

@Service
@Validated
public class AuthServiceImpl implements AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    // IMPLEMENTACION DEL METODO PARA NORMALIZAR TEXTOS Y GUARDAR A LOS USUARIOS SIN
    // TILDES
    private String normalizarTextos(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    // IMPLEMENTACION DEL METODO PARA VER SI ESE ROL EXISTE EN LA BASE DE DATOS, SI
    // NO EXISTE LO HE
    // PUESTO QUE SE CREE POR DEFECTO CON EL ROL USER
    private void existeElRol() {
        String roleName = ERol.USER.name();
        Optional<RolModel> optionalRol = rolRepository.findByName(roleName);

        if (optionalRol.isEmpty()) {
            RolModel rolUsuario = new RolModel();
            rolUsuario.setName(ERol.USER);
            rolUsuario.setRolId(UUID.randomUUID().toString()); // Genero un UUID para el rol
            rolRepository.save(rolUsuario);
        }
    }

    // IMPLEMENTACION DEL METODO PARA COMPROBAR QUE LA CONTRASEÑA SEA VÁLIDA
    private ResponseEntity<?> validarPassword(String password) {
        // VerificO que la contraseña no tenga más de 15 caracteres
        if (password.length() > 15) {
            return ResponseEntity.badRequest().body("La contraseña no puede tener más de 15 caracteres.");
        }

        // Variables para las condiciones
        boolean tieneLetras = false;
        boolean tieneNumeros = false;
        boolean tieneMayuscula = false;
        boolean tieneMinuscula = false;

        // Recorro la contraseña para determinar los tipos de caracteres que contiene
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                tieneLetras = true;
                if (Character.isUpperCase(c)) {
                    tieneMayuscula = true;
                } else if (Character.isLowerCase(c)) {
                    tieneMinuscula = true;
                }
            } else if (Character.isDigit(c)) {
                tieneNumeros = true;
            }
        }

        // Si todas son letras piedo que mi contraseña tenga al menos un número
        if (tieneLetras && !tieneNumeros) {
            return ResponseEntity.badRequest().body("La contraseña debe contener al menos un número.");
        }

        // Si todos son números pido que deba tener al menos una letra mayúscula y otra
        // minúscula
        if (tieneNumeros && !tieneLetras) {
            if (!tieneMayuscula || !tieneMinuscula) {
                return ResponseEntity.badRequest().body(
                        "La contraseña debe contener al menos una letra mayúscula y una letra minúscula.");
            }
        }

        // Si la contraseña tiene tanto números como letras verifico que tenga al
        // menos una letra mayúscula y una minúscula
        if (tieneLetras && tieneNumeros) {
            if (!tieneMayuscula || !tieneMinuscula) {
                return ResponseEntity.badRequest().body(
                        "La contraseña debe contener al menos una letra mayúscula y una letra minúscula.");
            }
        }

        // Si pasó todas las verificaciones, la contraseña es válida
        return ResponseEntity.ok("La contraseña es válida.");
    }

    // IMPLEMENTACION DEL METODO PARA CREAR UN NUEVO USUARIO
    @Override
    public ResponseEntity<?> crearNuevoUsuario(CrearUsuarioDTO crearUsuarioDTO) {

        String nombre = crearUsuarioDTO.getNombre().trim().toUpperCase();
        String apellido = crearUsuarioDTO.getApellido().trim().toUpperCase();
        String email = crearUsuarioDTO.getEmail().trim();
        String password = crearUsuarioDTO.getPassword().trim();

        if (usuarioRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body("Prueba con otro email");
        }

        // Valido la contraseña
        ResponseEntity<?> validacionPassword = validarPassword(password);
        if (!validacionPassword.getStatusCode().is2xxSuccessful()) {
            // Si la validación no fue exitosa, devuelvo el error
            return validacionPassword;
        }

        existeElRol(); // Me aseguro si ese rol existe en la base de datos

        // Obtengo el rol de usuario directamente usando el nombre del rol (USER en este
        // caso)
        RolModel rolUsuario = rolRepository.findByName(ERol.USER.name())
                .orElseThrow(() -> new RuntimeException("Rol de usuario no encontrado"));

        // Modifico el DTO antes de construir el usuario
        crearUsuarioDTO.setEmail(email);
        crearUsuarioDTO.setNombre(nombre);
        crearUsuarioDTO.setApellido(apellido);
        crearUsuarioDTO.setPassword(password);

        // Construir el usuario asignándole el rol encontrado
        UsuarioModel usuario = construirUsuario(crearUsuarioDTO, rolUsuario);

        // Guardar el nuevo usuario en la base de datos
        usuarioRepository.save(usuario);

        return ResponseEntity.ok("Usuario creado correctamente");
    }

    // IMPLEMENTACION DEL METODO PARA CONSTRUIR UN NUEVO USUARIO
    private UsuarioModel construirUsuario(CrearUsuarioDTO crearUsuarioDTO, RolModel rol) {
        UsuarioModel usuario = UsuarioModel.builder()
                .nombre(normalizarTextos(crearUsuarioDTO.getNombre().trim().toUpperCase()))
                .apellido(normalizarTextos(crearUsuarioDTO.getApellido().trim().toUpperCase()))
                .email(crearUsuarioDTO.getEmail().trim())
                .password(passwordEncoder.encode(crearUsuarioDTO.getPassword().trim()))
                .fechaModificacion("")
                .fechaCreacion("")
                .rol(rol) // Asignar el objeto RolModel al campo rol del usuario
                .build();

        // Si no hay ID asignado, generar uno nuevo
        if (usuario.getUsuarioId() == null) {
            usuario.setUsuarioId(UUID.randomUUID().toString());
        }

        // Asignar la fecha de creación
        CrearUsuarioDTO usuarioDTO = new CrearUsuarioDTO();
        usuarioDTO.setFechaCreacion(); // Asegúrate de que este método esté correctamente implementado
        usuario.setFechaCreacion(usuarioDTO.getFechaCreacion());

        return usuario;
    }

}
