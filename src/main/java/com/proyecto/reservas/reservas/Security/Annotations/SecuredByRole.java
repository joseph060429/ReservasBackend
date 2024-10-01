package com.proyecto.reservas.reservas.Security.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//Anotación `@Target` define dónde puedo aplicar `SecuredByRole`.
// En este caso, digo que mi anotación puede aplicarse a MÉTODOS (`ElementType.METHOD`) y CLASES o INTERFACES (`ElementType.TYPE`).
// Esto significa que puedo usar `@SecuredByRole` en un método individual o en toda una clase.
@Target({ ElementType.METHOD, ElementType.TYPE })

// `@Retention`, especifico cuánto tiempo estará disponible mi anotación.
// Al usar `RetentionPolicy.RUNTIME`, indico que la anotación estará disponible
// en tiempo de ejecución,
// lo cual es esencial para que los aspectos de seguridad puedan verificar los
// roles cuando se ejecute el programa.
@Retention(RetentionPolicy.RUNTIME)

// Aquí declaro mi anotación personalizada con `@interface`, que indica a Java
// que estoy creando una nueva anotación.
// El nombre de la anotación es `SecuredByRole`. Es pública para que pueda ser
// utilizada en cualquier parte del proyecto.
public @interface SecuredByRole {

    // Defino un atributo llamado `value` que es de tipo `String[]` (un array de
    // cadenas de texto).
    // Esto significa que puedo pasar uno o más roles como un array de strings
    // cuando use la anotación.
    // Por ejemplo: @SecuredByRole({"ROLE_USER", "ROLE_ADMIN"}).
    // Al definirlo así, indico que cada vez que se use la anotación se deben
    // especificar uno o más roles.
    String[] value();
}
