package com.proyecto.reservas.reservas.Services.AuthService.UserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.proyecto.reservas.reservas.Models.UsuarioModel;
import com.proyecto.reservas.reservas.Repositories.UserRepository;


@Service
public class UsuarioDetailsServiceImpl implements UserDetailsService  {

     @Autowired
    private UserRepository usuarioRepositorio;
    
    // CLASE DE SPRING SECURITY PARA CARGAR LOS DETALLES DEL USUARIO A PARTIR DE SU CORREO ELECTRÓNICO
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Busco un usuario por su correo electrónico en el repositorio
        UsuarioModel usuario = usuarioRepositorio.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con email: " + email + " me quieres joder? "));
        
        // Construyo un objeto UserDetails personalizado con los detalles del usuario encontrado
        return UsuariosDetailsPersonalizados.build(usuario);
    }
}
