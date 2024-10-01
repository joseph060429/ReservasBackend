package com.proyecto.reservas.reservas.Security.Filter;

import java.io.IOException;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.reservas.reservas.Models.UsuarioModel;
import com.proyecto.reservas.reservas.Repositories.UserRepository;
import com.proyecto.reservas.reservas.Security.Jwt.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.*;
import org.springframework.security.core.userdetails.User;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    
    private JwtUtils jwtUtils;

    private UserRepository usuarioRepositorio;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserRepository usuarioRepositorio) {
        this.jwtUtils = jwtUtils;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    // PROCESO DE AUTENTICACION
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {

        UsuarioModel usuario = null;
        String email = "";
        String password = "";

        try {

            usuario = new ObjectMapper().readValue(request.getInputStream(), UsuarioModel.class);
            email = usuario.getEmail().trim();
            password = usuario.getPassword().trim();

        } catch (StreamReadException e) {
            throw new RuntimeException(e);
        } catch (DatabindException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email,
                password);

        return getAuthenticationManager().authenticate(authenticationToken);
    }

    // CUANDO LA AUTENTICACION ES CORRECTA
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

        // Este user es el de Spring Security para obtener los detalles del usuario
        User user = (User) authResult.getPrincipal();

        // Obtén el rol del usuario
        String userRole = user.getAuthorities().iterator().next().getAuthority();

        // Obtenemos el token con el email y el rol del usuario
        String token = jwtUtils.generateJwtToken(user.getUsername(), userRole);

        String userEmail = user.getUsername();

        // Busco al usuario por el email de autorizacion
        Optional<UsuarioModel> usuarioOptional = usuarioRepositorio.findByEmail(userEmail);

        UsuarioModel usuarioModelo = usuarioOptional.get();

        // Traigo el nombre del usuario
        String nombreUsuario = usuarioModelo.getNombre();

        // Traigo el apellido del usuario
        String apellidoUsuario = usuarioModelo.getApellido();

        // Obtén la fecha y hora actual
        // LocalDateTime now = LocalDateTime.now();

        // Calcula la fecha de expiración (30 minutos desde ahora)
        // LocalDateTime expirationTime = now.plusMinutes(30);

        // Definir el formato deseado
        // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        // Formatea la fecha de expiración como una cadena
        // String expirationTimeString = expirationTime.format(formatter);

        response.addHeader("Authorization", token);

        Map<String, Object> httpResponse = new HashMap<>();
        httpResponse.put("Message", "Autenticación Correcta");
        httpResponse.put("token", token);
        httpResponse.put("roles", userRole);
        httpResponse.put("email", userEmail);
        // httpResponse.put("tiempoExpiracion", expirationTimeString);
        httpResponse.put("nombre", nombreUsuario);
        httpResponse.put("apellido", apellidoUsuario);

        // Convertir el mapa en el json
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(new ObjectMapper().writeValueAsString(httpResponse));
        response.getWriter().flush();
    }

    // CUANDO LAS CREDENCIALES SON INVALIDAS
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("message", "Credenciales inválidas");

        ObjectMapper objectMapper = new ObjectMapper();
        String errorDetailsJson = objectMapper.writeValueAsString(errorDetails);

        response.getWriter().write(errorDetailsJson);
        response.getWriter().flush();
    }
}
