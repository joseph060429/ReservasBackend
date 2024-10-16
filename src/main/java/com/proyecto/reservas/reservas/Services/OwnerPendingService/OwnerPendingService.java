package com.proyecto.reservas.reservas.Services.OwnerPendingService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.proyecto.reservas.reservas.DTO.DTOPendingOwner.FormuForOwnerDetailsDTO;
import com.proyecto.reservas.reservas.Security.Jwt.JwtUtils;

public interface OwnerPendingService {

    // METODO PARA CREAR EL FORMULARIO CUANDO QUIERES SER OWNER
    ResponseEntity<?> crearFormuOwnerDetails(String token, JwtUtils jwtUtils, FormuForOwnerDetailsDTO formuForOwnerDetails, MultipartFile file);
    
} 
