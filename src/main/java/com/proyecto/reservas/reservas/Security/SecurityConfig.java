package com.proyecto.reservas.reservas.Security;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
// import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.proyecto.reservas.reservas.Repositories.UserRepository;
import com.proyecto.reservas.reservas.Security.Filter.JwtAuthenticationFilter;
import com.proyecto.reservas.reservas.Security.Filter.JwtAuthorizationFilter;
import com.proyecto.reservas.reservas.Security.Jwt.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.beans.factory.annotation.Value;

//Importaciones de Cors
// import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
// @EnableWebSecurity
// @EnableGlobalMethodSecurity(prePostEnabled = true) // Esto es para habilitar
// el uso de @PreAuthorize
public class SecurityConfig {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserRepository usuarioRepositorio;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    JwtAuthorizationFilter jwtAuthorizationFilter;

    // CORS
    // Variable para almacenar la URL del frontend obtenida del archivo
    // application.properties
    @Value("${frontend.url}")
    private String frontendUrl;

    // ESTO SE ENCARGA DE ADMINISTRAR LA AUTENTICACIÓN EN LA APLICACIÓN Y NOS EXIGE
    // UN PASSWORD ENCODER.
    @Bean
    AuthenticationManager authenticationManager(HttpSecurity httpSecurity, PasswordEncoder passwordEncoder)
            throws Exception {

        return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder)
                .and().build();
    }

    // Defino un bean que configura el filtro CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Arrays.asList(frontendUrl));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;

    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpsSecurity, AuthenticationManager authenticationManager)
            throws Exception {

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtils, usuarioRepositorio);
        jwtAuthenticationFilter.setAuthenticationManager(authenticationManager);
        jwtAuthenticationFilter.setFilterProcessesUrl("/login");

        return httpsSecurity
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .csrf(config -> config.disable())
                .authorizeHttpRequests(auth -> {

                    auth.requestMatchers("/crearUsuario").permitAll();

                    auth.anyRequest().authenticated();
                })
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .addFilter(jwtAuthenticationFilter)
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }

    // Contraseña encriptada
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
