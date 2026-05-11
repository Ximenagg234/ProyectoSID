package edu.icesi.emprendimientos.unit;

import edu.icesi.emprendimientos.rest.exception.GlobalRestExceptionHandler;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GlobalRestExceptionHandlerTest {

    private GlobalRestExceptionHandler handler;

    @BeforeEach
    void setup() {
        handler = new GlobalRestExceptionHandler();
    }

    // ─── EntityNotFoundException → 404 ────────────────────────────────────────

    @Test
    void handleNotFound_RetornaStatus404() {
        EntityNotFoundException ex = new EntityNotFoundException("Emprendimiento no encontrado: 99");
        ResponseEntity<Map<String, String>> response = handler.handleNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void handleNotFound_CuerpoContieneError() {
        EntityNotFoundException ex = new EntityNotFoundException("Recurso no encontrado");
        ResponseEntity<Map<String, String>> response = handler.handleNotFound(ex);

        assertNotNull(response.getBody());
        assertEquals("No encontrado", response.getBody().get("error"));
    }

    @Test
    void handleNotFound_CuerpoContieneMensaje() {
        EntityNotFoundException ex = new EntityNotFoundException("Producto 5 no existe");
        ResponseEntity<Map<String, String>> response = handler.handleNotFound(ex);

        assertNotNull(response.getBody());
        assertEquals("Producto 5 no existe", response.getBody().get("mensaje"));
    }

    // ─── MethodArgumentNotValidException → 400 ────────────────────────────────

    @Test
    void handleValidation_RetornaStatus400() throws NoSuchMethodException {
        MethodArgumentNotValidException ex = buildValidationException("nombre", "no debe ser nulo");
        ResponseEntity<Map<String, Object>> response = handler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleValidation_CuerpoContieneListaDeCampos() throws NoSuchMethodException {
        MethodArgumentNotValidException ex = buildValidationException("nombre", "no debe ser nulo");
        ResponseEntity<Map<String, Object>> response = handler.handleValidation(ex);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("campos"));
        @SuppressWarnings("unchecked")
        List<String> campos = (List<String>) response.getBody().get("campos");
        assertFalse(campos.isEmpty());
    }

    @Test
    void handleValidation_CuerpoContieneErrorValidacion() throws NoSuchMethodException {
        MethodArgumentNotValidException ex = buildValidationException("descripcion", "no debe estar vacío");
        ResponseEntity<Map<String, Object>> response = handler.handleValidation(ex);

        assertNotNull(response.getBody());
        assertEquals("Validación fallida", response.getBody().get("error"));
    }

    // ─── RuntimeException → 400 ───────────────────────────────────────────────

    @Test
    void handleRuntime_RetornaStatus400() {
        RuntimeException ex = new RuntimeException("El nombre es obligatorio");
        ResponseEntity<Map<String, String>> response = handler.handleRuntime(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleRuntime_CuerpoContieneErrorNegocio() {
        RuntimeException ex = new RuntimeException("El usuario no existe");
        ResponseEntity<Map<String, String>> response = handler.handleRuntime(ex);

        assertNotNull(response.getBody());
        assertEquals("Error de negocio", response.getBody().get("error"));
        assertEquals("El usuario no existe", response.getBody().get("mensaje"));
    }

    // ─── Exception → 500 ──────────────────────────────────────────────────────

    @Test
    void handleGeneral_RetornaStatus500() {
        Exception ex = new Exception("Error inesperado del servidor");
        ResponseEntity<Map<String, String>> response = handler.handleGeneral(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void handleGeneral_CuerpoContieneErrorInterno() {
        Exception ex = new Exception("NullPointerException en servicio");
        ResponseEntity<Map<String, String>> response = handler.handleGeneral(ex);

        assertNotNull(response.getBody());
        assertEquals("Error interno", response.getBody().get("error"));
        assertEquals("NullPointerException en servicio", response.getBody().get("mensaje"));
    }

    // ─── helper ───────────────────────────────────────────────────────────────

    private MethodArgumentNotValidException buildValidationException(String field, String message) {
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", field, message));
        return new MethodArgumentNotValidException(null, bindingResult);
    }
}
