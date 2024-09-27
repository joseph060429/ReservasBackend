package com.proyecto.reservas.reservas.ExceptionesGlobals;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import jakarta.persistence.RollbackException;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // MÉTODO QUE MANEJA LAS EXCEPECIONES DE LOS CAMPOS
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    // MÉTODO QUE MANEJA LAS EXCEPCIONES DE VALIDACION DE LAS ENTIDADES
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleConstraintViolationExceptions(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations()
                .forEach(violation -> errors.put(violation.getPropertyPath().toString(), violation.getMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    //MÉTODO QUE MANEJA LAS EXCEPCIONES DE rollback Y PROPAGA LAS ConstraintViolationException SI EXISTEN 
    @ExceptionHandler(RollbackException.class)
    public ResponseEntity<Map<String, String>> handleRollbackException(RollbackException ex) {
        if (ex.getCause() instanceof ConstraintViolationException) {
            ConstraintViolationException constraintViolationException = (ConstraintViolationException) ex.getCause();
            Map<String, String> errors = new HashMap<>();
            constraintViolationException.getConstraintViolations()
                    .forEach(violation -> errors.put(violation.getPropertyPath().toString(), violation.getMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error interno del servidor"));
    }
}
