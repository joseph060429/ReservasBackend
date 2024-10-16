package com.proyecto.reservas.reservas.Services.OwnerPendingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.proyecto.reservas.reservas.DTO.DTOPendingOwner.DiaConHorarioDTO;
import com.proyecto.reservas.reservas.DTO.DTOPendingOwner.FormuForOwnerDetailsDTO;
import com.proyecto.reservas.reservas.Enum.EEstado;
import com.proyecto.reservas.reservas.Enum.EPista;
import com.proyecto.reservas.reservas.Models.OwnerDetailsModel;
import com.proyecto.reservas.reservas.Models.RolModel;
import com.proyecto.reservas.reservas.Models.UsuarioModel;
import com.proyecto.reservas.reservas.Repositories.OwnerPendingRepository;
import com.proyecto.reservas.reservas.Repositories.UserRepository;
import com.proyecto.reservas.reservas.Security.Jwt.JwtUtils;
import com.proyecto.reservas.reservas.Services.AuthService.AuthService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OwnerPendingServiceImpl implements OwnerPendingService {

    @Autowired
    private AuthService authService;

    @Autowired
    private OwnerPendingRepository ownerPendingRepository;

    @Autowired
    private UserRepository userRepository;

    // METODO PARA LA CONSTRUCCION DEL FORMULARIO DE LOS QUE QUIEREN SER OWNER
    private ResponseEntity<?> construirFormuForOwnerDetails(FormuForOwnerDetailsDTO formuForOwnerDetailsDTO,
            MultipartFile file) {

        // Tabla OwnerDetails
        OwnerDetailsModel ownerDetails = new OwnerDetailsModel();

        try {

            // Obtengo los días disponibles ya ordenados
            // String diasDisponiblesOrdenados =
            // validarDiasDisponibles(formuForOwnerDetailsDTO.getDiasDisponibles());

            // Convierto el String a EPista usando el método valueOf() y luego llamo a
            // setTipoPista
            ownerDetails.setTipoPista(EPista.valueOf(formuForOwnerDetailsDTO.getTipoPista().toString().toUpperCase()));

            // Normalizo y establezco los campos
            ownerDetails.setNombreInstalacion(
                    authService.normalizarTextos(formuForOwnerDetailsDTO.getNombreInstalacion().trim()));
            ownerDetails.setTelefonoContacto(formuForOwnerDetailsDTO.getTelefonoContacto().trim());
            ownerDetails.setNumeroPistas(formuForOwnerDetailsDTO.getNumeroPistas());
            ownerDetails.setPrecioPorHora(formuForOwnerDetailsDTO.getPrecioPorHora());

            // Construyo la dirección completa usando el método del modelo y la
            // normalizacion de textos
            String direccionCompleta = ownerDetails.construirDireccionCompleta(
                    authService.normalizarTextos(formuForOwnerDetailsDTO.getDireccion().trim()),
                    authService.normalizarTextos(formuForOwnerDetailsDTO.getMunicipio().trim()),
                    authService.normalizarTextos(formuForOwnerDetailsDTO.getProvincia().trim()),
                    authService.normalizarTextos(formuForOwnerDetailsDTO.getCodigoPostal().trim()),
                    (formuForOwnerDetailsDTO.getNumero()));

            // Establezco la dirección completa en el modelo
            ownerDetails.setDireccionCompleta(direccionCompleta);

            // Construyo el campo horario_disponible
            List<DiaConHorarioDTO> diasConHorariosValidos = validarDiasDisponibles(
                    formuForOwnerDetailsDTO.getDiasConHorarios());

            // Construyo el string del horario disponible
            String horarioDisponible = ownerDetails.construirHorarioDisponible(diasConHorariosValidos);

            // Se me almacena la A en mayusucula por la normalizacion de textos
            ownerDetails.setHorarioDisponible(authService.normalizarTextos(horarioDisponible));

            // Subo la imagen (opcional)
            // ResponseEntity<String> responseImagen = subirImagen(file);
            // if (responseImagen.getStatusCode().is2xxSuccessful()) {
            // String nombreImagen = responseImagen.getBody();
            // ownerDetails.setImagenPista(nombreImagen);
            // } else {
            // return ResponseEntity.badRequest().body("Error al construir el producto: " +
            // responseImagen.getBody());
            // }

            // Asignar ID único si no existe
            if (ownerDetails.getOwnerId() == null) {
                ownerDetails.setOwnerId(UUID.randomUUID().toString());
            }

            return ResponseEntity.ok(ownerDetails);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al enviar el formulario: " + e.getMessage());
        }
    }

    // IMPLEMENTACION DEL METODO PARA CREAR EL FORMULARIO CUANDO QUIERES SER OWNER
    @Override
    public ResponseEntity<?> crearFormuOwnerDetails(String token, JwtUtils jwtUtils,
            FormuForOwnerDetailsDTO formuForOwnerDetailsDTO, MultipartFile file) {
        // Extraigo el token JWT
        String jwtToken = token.replace("Bearer ", "");

        // Obtengo el email del token
        String emailFromToken = jwtUtils.getEmailFromToken(jwtToken);

        // Busco el usuario basado en el email del token
        Optional<UsuarioModel> usuarioOptional = userRepository.findByEmail(emailFromToken);

        // Verifico si el usuario existe
        if (!usuarioOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuario no encontrado.");
        }

        try {
            // Construyo el formulario para owner details
            ResponseEntity<?> construirFormuForOwnerDetailsResponse = construirFormuForOwnerDetails(
                    formuForOwnerDetailsDTO, file);

            // Verifico si la construcción del formulario fue exitosa
            if (construirFormuForOwnerDetailsResponse.getStatusCode().is2xxSuccessful()) {
                OwnerDetailsModel ownerDetailsModel = (OwnerDetailsModel) construirFormuForOwnerDetailsResponse
                        .getBody();

                // Si fue exitosa verifico si el modelo no es nulo antes de guardar
                if (ownerDetailsModel != null) {
                    // Guardo el id del usuario en la tabla ownerDetails
                    UsuarioModel usuario = usuarioOptional.get();
                    ownerDetailsModel.setUsuario(usuario);
                    ownerPendingRepository.save(ownerDetailsModel);
                    return ResponseEntity.ok("Formulario registrado exitosamente");
                } else {
                    return ResponseEntity.badRequest().body("Error al construir el formulario para owner details.");
                }
            } else {
                return construirFormuForOwnerDetailsResponse;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar el formulario: " + e.getMessage());
        }
    }

    // MÉTODO PARA VALIDAR LOS DIAS DE LA SEMANA
    private List<DiaConHorarioDTO> validarDiasDisponibles(String diasConHorarios) {
        // Días válidos en minúsculas para la validación
        List<String> diasOrdenados = Arrays.asList("lunes", "martes", "miercoles", "jueves", "viernes", "sabado",
                "domingo");

        // Uso LinkedHashMap para almacenar días y sus horarios, y asi mantener el orden
        // de insercion
        Map<String, List<String>> diasYHorariosMap = new LinkedHashMap<>();

        // Divido la cadena por comas o espacios para obtener cada día con horario
        String[] diasHorarios = diasConHorarios.split("[, ]+");

        for (String diaHorario : diasHorarios) {
            // Divido cada entrada por el guion para separar el día, hora de inicio y fin
            String[] partes = diaHorario.split("-");
            if (partes.length != 3) {
                throw new IllegalArgumentException("Formato de día y horarios incorrecto: " + diaHorario);
            }

            String dia = partes[0].toLowerCase().trim(); // El día
            String horaInicio = partes[1].trim(); // La hora de inicio
            String horaFin = partes[2].trim(); // La hora de fin

            // Valido si el día es válido
            if (!diasOrdenados.contains(dia)) {
                throw new IllegalArgumentException("Día inválido: " + dia);
            }

            // Valido el formato de la hora usando la expresión regular
            if (!horaInicio.matches("^([01]?\\d|2[0-3]):(00|30)$") || !horaFin.matches("^([01]?\\d|2[0-3]):(00|30)$")) {
                throw new IllegalArgumentException("Formato de hora incorrecto para el día: " + dia
                        + " solo puedes poner horas en punto o y media");
            }

            // Compruebo que la hora de inicio es menor que la hora de fin
            if (!esHoraInicioMenorQueFin(horaInicio, horaFin)) {
                throw new IllegalArgumentException(
                        "La hora de inicio debe ser menor que la hora de fin para el día: " + dia);
            }

            // Agrego el horario al mapa
            diasYHorariosMap.putIfAbsent(dia, new ArrayList<>());
            diasYHorariosMap.get(dia).add(horaInicio + "-" + horaFin);
        }

        // Creo la lista de objetos DTO y los ordeno según la lista de días
        List<DiaConHorarioDTO> listaDiasConHorarios = new ArrayList<>();
        for (String dia : diasOrdenados) {
            if (diasYHorariosMap.containsKey(dia)) {
                for (String horarios : diasYHorariosMap.get(dia)) {
                    String[] horaPartes = horarios.split("-");
                    String horaInicio = horaPartes[0];
                    String horaFin = horaPartes[1];
                    listaDiasConHorarios.add(new DiaConHorarioDTO(dia, horaInicio, horaFin));
                }
            }
        }

        // Devolvemos la lista de días con horarios ordenados
        return listaDiasConHorarios;
    }


    // METODO PARA COMPROBAR QUE LA HORA DE INICIO SEA MENOR QUE LA HORA DE FIN
    private boolean esHoraInicioMenorQueFin(String horaInicio, String horaFin) {
        String[] partesInicio = horaInicio.split(":");
        String[] partesFin = horaFin.split(":");

        int minutosInicio = Integer.parseInt(partesInicio[0]) * 60 + Integer.parseInt(partesInicio[1]);
        int minutosFin = Integer.parseInt(partesFin[0]) * 60 + Integer.parseInt(partesFin[1]);

        return minutosInicio < minutosFin;
    }

}
