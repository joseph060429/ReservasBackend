package com.proyecto.reservas.reservas.Controllers.PendingOwner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.proyecto.reservas.reservas.DTO.DTOPendingOwner.FormuForOwnerDetailsDTO;
import com.proyecto.reservas.reservas.Security.Annotations.SecuredByRole;
import com.proyecto.reservas.reservas.Security.Jwt.JwtUtils;
import com.proyecto.reservas.reservas.Services.OwnerPendingService.OwnerPendingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/pendingOwner")
@SecuredByRole({ "ROLE_PENDING_OWNER", "ROLE_ADMIN" })
public class PendingOwnerController {

    @Autowired
    private OwnerPendingService ownerPendingService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/enviarFormulario")
    public ResponseEntity<?> crearFormu(@RequestHeader("Authorization") String token, @Valid @ModelAttribute FormuForOwnerDetailsDTO formuForOwnerDetails) {
        // HttpServletRequest request) {
        // try {
        // MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)
        // request;
        // MultipartFile file = multipartRequest.getFile("img");
        // System.out.println("IMAGEN RECIBIDA: " + file);

        // Validar y crear el producto
        return ownerPendingService.crearFormuOwnerDetails(token, jwtUtils, formuForOwnerDetails, null);

    }

}
