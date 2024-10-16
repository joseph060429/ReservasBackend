package com.proyecto.reservas.reservas.Security.Filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.proyecto.reservas.reservas.Security.Jwt.JwtUtils;
import com.proyecto.reservas.reservas.Services.AuthService.UserDetails.UsuarioDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private UsuarioDetailsServiceImpl userDetailsService;

    // FILTRO DE AUTORIZACIÓN JWT PARA VERIFICAR Y AUTENTICAR LAS SOLICITUDES CON
    // TOKENS JWT.
    // EXTIENDE ONCEPERREQUESTFILTER PARA GARANTIZAR LA EJECUCIÓN UNA VEZ POR CADA
    // SOLICITUD.
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String tokenHeader = request.getHeader("Authorization");

        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            String token = tokenHeader.substring(7); // obtenemos el token quitandole la palabra bearer, por eso le
                                                     // pongo el 7 porque Bearer tiene 6 letras y el espacio

            if (jwtUtils.isTokenValid(token)) {
                String email = jwtUtils.getEmailFromToken(token);
                // String _idUsuario = jwtUtils.getUserIdFromToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email); // Este es el de spring security
                // UserDetails userDetails = userDetailsService.loadUserByUsername(_idUsuario);

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email,
                        null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

        }
        // Si no entra por el if quiere decir que no tenemos un token y nos deniega el
        // acceso
        filterChain.doFilter(request, response);

    }
}
