package com.proyecto.reservas.reservas.Security.Aspects;

import com.proyecto.reservas.reservas.Security.Annotations.SecuredByRole;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Defino la clase `SecurityAspect` como un aspecto mediante la anotación `@Aspect`.
// lo que significa que esta clase va a contener "cross-cutting concerns" o funcionalidades que deben
// aplicarse de manera transversal en varios puntos del programa, como es el caso de la seguridad.
// La anotación `@Component` permite que esta clase sea gestionada por el contenedor de Spring, es decir,
// la convierte en un bean de Spring que puede ser inyectado en otras partes del código si es necesario.
@Aspect
@Component
public class SecurityAspect {

    // Aquí declaro un logger utilizando
    // `LoggerFactory.getLogger(SecurityAspect.class)` para obtener
    // una instancia del logger asociada a esta clase. Con esto puedo registrar
    // información relevante,
    // como los accesos a métodos o advertencias cuando hay intentos de acceso no
    // autorizado.
    // Uso `private static final` porque el logger debe ser único para esta clase y
    // no debe modificarse.
    private static final Logger logger = LoggerFactory.getLogger(SecurityAspect.class);

    // Utilizo la anotación `@Around`, que es parte de AspectJ. Esto me permite
    // interceptar y rodear
    // la ejecución de métodos anotados con `@SecuredByRole`, o cualquier método
    // dentro de una clase
    // anotada con `@SecuredByRole`. Esto significa que cada vez que se ejecute un
    // método o una clase
    // con esta anotación, se ejecutará el código dentro de este método `checkRole`.
    @Around("@annotation(securedByRole) || @within(securedByRole)")

    // El método `checkRole` es el que se va a ejecutar cuando se detecte un método
    // o clase anotada con `@SecuredByRole`.
    // Recibo dos parámetros:
    // - `ProceedingJoinPoint joinPoint`: Me permite obtener información sobre el
    // método interceptado y controlarlo,
    // por ejemplo, para decidir si continúo su ejecución.
    // - `SecuredByRole securedByRole`: Este es un objeto que contiene la
    // información de la anotación, específicamente
    // los roles permitidos que se han definido en la anotación `@SecuredByRole` en
    // el método o clase.
    public Object checkRole(ProceedingJoinPoint joinPoint, SecuredByRole securedByRole) throws Throwable {

        // Aquí obtengo el valor de los roles permitidos a través de
        // `securedByRole.value()`.
        // `value()` me devuelve un array de `String[]` que contiene los roles
        // permitidos,
        // tal como se definieron en la anotación `@SecuredByRole`. Por ejemplo:
        // {"ROLE_USER", "ROLE_ADMIN"}.
        String[] requiredRoles = securedByRole.value();

        // Utilizo `SecurityContextHolder` para obtener la información de autenticación
        // actual.
        // Esto me da el `Authentication` del usuario que está haciendo la solicitud.
        // Con `authentication.getAuthorities()`, más adelante, puedo acceder a los
        // roles o permisos que tiene este usuario.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Log del acceso
        // Registro información sobre el acceso mediante el logger.
        // `joinPoint.getSignature()` me permite obtener el nombre del método
        // interceptado,
        // y `requiredRoles` me dice cuáles son los roles que se requieren para acceder
        // a ese método.
        // Esto es útil para monitorear o depurar el acceso a métodos sensibles.
        logger.info("Acceso a método: {} con roles permitidos: {}", joinPoint.getSignature(), requiredRoles);

        // Primero, me aseguro de que la autenticación no sea nula, es decir, que el
        // usuario esté autenticado.
        // Luego, utilizo `authentication.getAuthorities()` para obtener los roles o
        // permisos que tiene el usuario.
        // Utilizo `stream()` para iterar sobre estos roles y con `anyMatch()` verifico
        // si alguno de los roles
        // del usuario coincide con los roles permitidos definidos en `requiredRoles`
        // (los que están en la anotación).
        // Para hacer la comparación, convierto `requiredRoles` en una lista usando
        // `java.util.Arrays.asList()`.
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> java.util.Arrays.asList(requiredRoles)
                        .contains(grantedAuthority.getAuthority()))) {
            // Si el usuario tiene al menos uno de los roles permitidos, llamo a
            // `joinPoint.proceed()` para permitir
            // que el método interceptado continúe con su ejecución. Esto significa que el
            // control de seguridad ha sido
            // superado y el método puede ejecutarse normalmente.
            return joinPoint.proceed();
        } else {
            // Registro una advertencia en el logger indicando que el acceso fue denegado.
            // Proporciono el nombre del método (`joinPoint.getSignature()`) y los roles que
            // eran requeridos (`requiredRoles`).
            logger.warn("Acceso denegado a: {}. Roles requeridos: {}", joinPoint.getSignature(), requiredRoles);
            // Lanzo una excepción de seguridad (`SecurityException`) para evitar que el
            // método se ejecute, la expcion la manejo de forma global en mi clase GlobalExceptionsHandler
            // Esto detiene la ejecución del método y le indica al sistema que el usuario no
            // tiene los permisos necesarios.
            // Esta excepción puede ser capturada y gestionada en otro lugar de la
            // aplicación para devolver
            // una respuesta adecuada al cliente, como un mensaje de error o un código de
            // estado HTTP 403 (Forbidden).
            throw new SecurityException("No tienes permiso para acceder aquí");
        }
    }

}
