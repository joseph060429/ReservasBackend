package com.proyecto.reservas.reservas.Services.AuthService;

import java.text.Normalizer;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
// import org.springframework.validation.annotation.Validated;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.proyecto.reservas.reservas.DTO.DTOUsuario.CrearUsuarioDTO;
import com.proyecto.reservas.reservas.Enum.EEstado;
import com.proyecto.reservas.reservas.Enum.ERol;
import com.proyecto.reservas.reservas.Models.RolModel;
import com.proyecto.reservas.reservas.Models.UsuarioModel;
import com.proyecto.reservas.reservas.Repositories.RolRepository;
import com.proyecto.reservas.reservas.Repositories.UserRepository;
import java.util.Optional;

@Service
// @Validated
public class AuthServiceImpl implements AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    // MÉTODO PARA NORMALIZAR TEXTOS(QUITARLE LOS TILDES Y PONER
    // LA PRIMERA LETRA DDEL NOMBRE Y APELLIDO EN MAYÚSCULA)
    public String normalizarTextos(String text) {

        // Elimino primero los acentos
        String textoNormalizado = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // Convierto to el texto normalizado a minúsculas
        textoNormalizado = textoNormalizado.toLowerCase();

        // Separo las palabras
        String[] palabras = textoNormalizado.split("\\s+");

        // Cojo la primera letra de cada palabra y la convierto en mayúscula
        StringBuilder textoConPrimeraLetraMayus = new StringBuilder();
        for (String palabra : palabras) {
            if (palabra.length() > 0) {
                textoConPrimeraLetraMayus.append(Character.toUpperCase(palabra.charAt(0)))
                        .append(palabra.substring(1))
                        .append(" ");
            }
        }

        // Elimino el y devuelvo el resultado
        return textoConPrimeraLetraMayus.toString().trim();

    }

    // MÉTODO PARA VER SI ESE ROL EXISTE EN LA BASE DE DATOS
    private void existeElRol() {
        verificarYCrearRol(ERol.ADMIN);
        verificarYCrearRol(ERol.OWNER);
        verificarYCrearRol(ERol.USER);
        verificarYCrearRol(ERol.PENDING_OWNER);
        // verificarYCrearRol(ERol.INVITED); // Creo que no hace falta porque en el
        // front se hace una comprobación de que si no tiene token que lo envie al login
    }

    // MÉTODO PARA VERIFICAR SI EL ROL EXISTE, SI NO EXISTE LO HE PUESTO POR DEFECTO PARA SE CREEN TODOS LOS TIPOS DE
    // ROL EN MI APP
    private void verificarYCrearRol(ERol rol) {
        // Verifico si el rol ya existe en la base de datos
        Optional<RolModel> optionalRol = rolRepository.findByName(rol.name());

        // Si el rol no existe, se crea uno nuevo
        if (optionalRol.isEmpty()) {
            RolModel nuevoRol = new RolModel();
            nuevoRol.setName(rol);
            nuevoRol.setRolId(UUID.randomUUID().toString()); // Genero un UUID para el rol
            rolRepository.save(nuevoRol);
        }
    }

    // MÉTODO PARA COMPROBAR QUE LA CONTRASEÑA SEA VÁLIDA
    public ResponseEntity<?> validarPassword(String password) {
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

        // Si todas son letras pido que mi contraseña tenga al menos un número
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

        String nombre = crearUsuarioDTO.getNombre().trim();
        String apellido = crearUsuarioDTO.getApellido().trim();
        String email = crearUsuarioDTO.getEmail().trim();
        String password = crearUsuarioDTO.getPassword().trim();
        String rolSeleccionado = crearUsuarioDTO.getRol().trim().toUpperCase(); // Asi el usuario seleccione user en
                                                                                // minúscula se pondrá siempre en
                                                                                // mayúscula porque lo tengo con
                                                                                // mayúsculas en el ENUM

        if (usuarioRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body("Prueba con otro email");
        }

        // Valido la contraseña
        ResponseEntity<?> validacionPassword = validarPassword(password);
        if (!validacionPassword.getStatusCode().is2xxSuccessful()) {
            // Si la validación no fue exitosa, devuelvo el error
            return validacionPassword;
        }

        existeElRol(); // Me aseguro si ese rol existe en la base de datos, y si no existe que se cree

        // Valido si el rol seleccionado es válido
        if (!rolSeleccionado.equals(ERol.USER.name()) && !rolSeleccionado.equals(ERol.OWNER.name())) {
            return ResponseEntity.badRequest().body("El rol seleccionado no es válido");
        }

        // Si selecciona el rol OWNER
        if (rolSeleccionado.equals(ERol.OWNER.name())) {
            // Asigno rol PENDING_OWNER en lugar de OWNER, y estado PENDIENTE
            RolModel rolPendingOwner = rolRepository.findByName(ERol.PENDING_OWNER.name())
                    .orElseThrow(() -> new RuntimeException("Rol PENDING_OWNER no encontrado"));
            crearUsuarioDTO.setNombre(nombre);
            crearUsuarioDTO.setApellido(apellido);
            crearUsuarioDTO.setEmail(email);
            crearUsuarioDTO.setPassword(password);
            crearUsuarioDTO.setRol(ERol.PENDING_OWNER.name()); // Asigno rol PENDING_OWNER
            crearUsuarioDTO.setEstado(EEstado.PENDIENTE); // Estado PENDIENTE
            UsuarioModel usuario = construirUsuario(crearUsuarioDTO, rolPendingOwner);
            usuarioRepository.save(usuario);

            return ResponseEntity.ok("Usuario creado correctamente, pendiente de confirmación por el administrador");

        } else if (rolSeleccionado.equals(ERol.USER.name())) {
            // Si selecciona USER, asigno directamente el rol USER
            RolModel rolUser = rolRepository.findByName(ERol.USER.name())
                    .orElseThrow(() -> new RuntimeException("Rol USER no encontrado"));

            crearUsuarioDTO.setNombre(nombre);
            crearUsuarioDTO.setApellido(apellido);
            crearUsuarioDTO.setEmail(email);
            crearUsuarioDTO.setPassword(password);
            crearUsuarioDTO.setRol(ERol.USER.name());
            UsuarioModel usuario = construirUsuario(crearUsuarioDTO, rolUser);
            usuarioRepository.save(usuario);

            return ResponseEntity.ok("Usuario creado correctamente");
        }

        return ResponseEntity.badRequest().body("Error inesperado");

    }

    //MÉTODO PARA CONSTRUIR UN NUEVO USUARIO
    private UsuarioModel construirUsuario(CrearUsuarioDTO crearUsuarioDTO, RolModel rol) {
        UsuarioModel usuario = UsuarioModel.builder()
                .nombre(normalizarTextos(crearUsuarioDTO.getNombre().trim()))
                .apellido(normalizarTextos(crearUsuarioDTO.getApellido().trim()))
                .email(crearUsuarioDTO.getEmail().trim())
                .password(passwordEncoder.encode(crearUsuarioDTO.getPassword().trim()))
                .fechaModificacion("")
                .fechaCreacion("")
                .estado(crearUsuarioDTO.getEstado())
                .rol(rol) // Asignar el objeto RolModel al campo rol del usuario
                .build();

        // Si no hay ID asignado, generar uno nuevo
        if (usuario.getUsuarioId() == null) {
            usuario.setUsuarioId(UUID.randomUUID().toString());
        }

        // Asignar la fecha de creación
        CrearUsuarioDTO usuarioDTO = new CrearUsuarioDTO();
        usuarioDTO.setFechaCreacion();
        usuario.setFechaCreacion(usuarioDTO.getFechaCreacion());

        return usuario;
    }

}
