package edu.icesi.emprendimientos.unit;

import edu.icesi.emprendimientos.rest.security.JwtService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET =
            "universidadicesiuniversidadicesiuniversidadicesiemprendimientos2026";
    private static final int EXPIRATION_MINUTES = 1440;

    @BeforeEach
    void setup() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expirationMinutes", EXPIRATION_MINUTES);
    }

    // ─── generateToken ────────────────────────────────────────────────────────

    @Test
    void generateToken_UsuarioValido_RetornaTokenNoNulo() {
        UserDetails user = buildUser("test@icesi.edu.co", "ROLE_ADMIN");
        String token = jwtService.generateToken(user);
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void generateToken_UsuarioValido_TokenContieneTresParts() {
        UserDetails user = buildUser("test@icesi.edu.co", "ROLE_EMPRENDEDOR");
        String token = jwtService.generateToken(user);
        // JWT = header.payload.signature
        assertEquals(3, token.split("\\.").length);
    }

    // ─── extractAllClaims ─────────────────────────────────────────────────────

    @Test
    void extractAllClaims_TokenValido_RetornaEmailCorrecto() {
        UserDetails user = buildUser("ximena@icesi.edu.co", "ROLE_ADMIN");
        String token = jwtService.generateToken(user);

        Claims claims = jwtService.extractAllClaims(token);
        assertEquals("ximena@icesi.edu.co", claims.get("email", String.class));
    }

    @Test
    void extractAllClaims_TokenValido_RetornaAuthoritiesCorrectas() {
        UserDetails user = buildUser("carlos@icesi.edu.co", "ROLE_EMPRENDEDOR");
        String token = jwtService.generateToken(user);

        Claims claims = jwtService.extractAllClaims(token);
        @SuppressWarnings("unchecked")
        List<String> authorities = claims.get("authorities", List.class);

        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertEquals("ROLE_EMPRENDEDOR", authorities.get(0));
    }

    @Test
    void extractAllClaims_TokenValido_FechaExpiracionFutura() {
        UserDetails user = buildUser("juan@icesi.edu.co", "ROLE_COMPRADOR");
        String token = jwtService.generateToken(user);

        Claims claims = jwtService.extractAllClaims(token);
        assertTrue(claims.getExpiration().after(new java.util.Date()));
    }

    @Test
    void extractAllClaims_TokenInvalido_LanzaExcepcion() {
        assertThrows(Exception.class,
                () -> jwtService.extractAllClaims("esto.no.es.un.jwt.valido"));
    }

    @Test
    void generateToken_VariosRoles_TodosPresentes() {
        UserDetails user = new User("admin@icesi.edu.co", "pass",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"),
                        new SimpleGrantedAuthority("ROLE_EMPRENDEDOR")));
        String token = jwtService.generateToken(user);

        Claims claims = jwtService.extractAllClaims(token);
        @SuppressWarnings("unchecked")
        List<String> authorities = claims.get("authorities", List.class);

        assertNotNull(authorities);
        assertEquals(2, authorities.size());
        assertTrue(authorities.contains("ROLE_ADMIN"));
        assertTrue(authorities.contains("ROLE_EMPRENDEDOR"));
    }

    // ─── helper ───────────────────────────────────────────────────────────────

    private UserDetails buildUser(String email, String role) {
        return new User(email, "password",
                List.of(new SimpleGrantedAuthority(role)));
    }
}
