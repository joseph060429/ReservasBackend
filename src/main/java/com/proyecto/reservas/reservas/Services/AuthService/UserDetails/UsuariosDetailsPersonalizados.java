package com.proyecto.reservas.reservas.Services.AuthService.UserDetails;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import com.proyecto.reservas.reservas.Models.UsuarioModel;
import java.util.Collection;
import java.util.Collections;

public class UsuariosDetailsPersonalizados extends User {

    private String userId; // Agrego un campo para el ID del usuario

    // OBTIENE LOS DETALLES DEL USUARIO POR SU EMAIL.
    // SI EL USUARIO NO ES ENCONTRADO, SE LANZA UNA EXCEPCIÓN DE NOMBRE DE USUARIO
    // NO ENCONTRADO.
    public UsuariosDetailsPersonalizados(String username, String password, boolean enabled, boolean accountNonExpired,
            boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities,
            String userId) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userId = userId;
    }

    // OBTIENE EL ID DEL USUARIO.
    public String getUserId() {
        return userId;
    }

    // CONSTRUYO LOS DETALLES PERSONALIZADOS DEL USUARIO A PARTIR DE UN MODELO DE
    // USUARIO.
    public static UsuariosDetailsPersonalizados build(UsuarioModel usuario) {
        // Extraigo el nombre del rol desde el objeto RolModel y lo convierto a
        // GrantedAuthority
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getName().toString());

        // CreO la lista de autoridades (solo hay una porque es un ManyToOne, un
        // usuario tiene un solo rol)
        Collection<GrantedAuthority> authorities = Collections.singletonList(authority);

        return new UsuariosDetailsPersonalizados(
                usuario.getEmail(),
                usuario.getPassword(),
                true,
                true,
                true,
                true,
                authorities,
                usuario.getUsuarioId());
    }

}
