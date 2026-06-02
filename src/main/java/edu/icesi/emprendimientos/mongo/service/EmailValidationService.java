package edu.icesi.emprendimientos.mongo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Servicio de validación de correos institucionales.
 * Solo se permiten correos con dominio @icesi.edu.co o @u.icesi.edu.co.
 *
 * Esta validación opera en dos capas:
 * 1. Aplicación (este servicio): valida antes de guardar en PostgreSQL
 * 2. Base de datos (trigger PostgreSQL): valida a nivel de BD como segunda línea de defensa
 */
@Service
public class EmailValidationService {

    private final List<String> dominiosPermitidos;

    public EmailValidationService(
            @Value("${app.email.dominios-permitidos:@icesi.edu.co,@u.icesi.edu.co}")
            String dominiosConfig) {
        this.dominiosPermitidos = Arrays.asList(dominiosConfig.split(","));
    }

    /**
     * Verifica que el correo tenga un dominio institucional permitido.
     * @throws IllegalArgumentException si el dominio no está permitido
     */
    public void validar(String correo) {
        if (correo == null || correo.isBlank()) {
            throw new IllegalArgumentException(
                    "El correo institucional es obligatorio.");
        }

        String correoLower = correo.toLowerCase().trim();
        boolean valido = dominiosPermitidos.stream()
                .anyMatch(dominio -> correoLower.endsWith(dominio.trim().toLowerCase()));

        if (!valido) {
            throw new IllegalArgumentException(
                    "El correo '" + correo + "' no es un correo institucional válido. " +
                    "Solo se permiten correos con dominio: " +
                    String.join(" o ", dominiosPermitidos) + ".");
        }
    }

    /**
     * @return true si el correo tiene dominio institucional válido
     */
    public boolean esValido(String correo) {
        if (correo == null || correo.isBlank()) return false;
        String correoLower = correo.toLowerCase().trim();
        return dominiosPermitidos.stream()
                .anyMatch(d -> correoLower.endsWith(d.trim().toLowerCase()));
    }
}
