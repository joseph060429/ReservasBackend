package com.proyecto.reservas.reservas.Enum;

public enum ERol {

    ADMIN, //Los dueños de la página
    OWNER, //Los dueños de las pistas
    USER, // Los usuarios que reservan
    PENDING_OWNER, //Para los usuarios que quieren ser owner al registrarse, o para actualizar de USER OWNER
    INVITED // Solo pueden ver
    

}
