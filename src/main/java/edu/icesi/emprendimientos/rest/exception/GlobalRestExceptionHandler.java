package edu.icesi.emprendimientos.rest.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice(basePackages = "edu.icesi.emprendimientos.rest.controller")
public class GlobalRestExceptionHandler {

    /** 404 — recurso no encontrado */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "No encontrado", "mensaje", e.getMessage()));
    }

    /** 401 — credenciales inválidas */
    @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
    public ResponseEntity<Map<String, String>> handleUnauthorized(Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "No autorizado", "mensaje", "Credenciales inválidas"));
    }

    /** 403 — acceso denegado por rol insuficiente (@PreAuthorize) */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Acceso denegado", "mensaje", "No tienes permisos para esta acción"));
    }

    /** 400 — errores de validación de campos (@Valid) */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Validación fallida", "campos", errors));
    }

    /** 400 — errores de negocio (validaciones manuales en servicios) */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Error de negocio", "mensaje", e.getMessage()));
    }

    /** 500 — errores inesperados */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneral(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno", "mensaje", e.getMessage()));
    }
}
