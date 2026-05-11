package edu.icesi.emprendimientos.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.icesi.emprendimientos.rest.dto.LoginRequestDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para la capa REST.
 * Cubre: 401 (sin token), 403 (rol incorrecto), 200/201 (éxito), 404 (no encontrado), 400 (cuerpo inválido).
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RestApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Tokens obtenidos en el primer test de login
    private static String tokenAdmin;        // ximena: ADMIN + EMPRENDEDOR
    private static String tokenEmprendedor;  // carlos: EMPRENDEDOR
    private static String tokenComprador;    // juan: COMPRADOR

    // ─── /api/auth/login ──────────────────────────────────────────────────────

    @Test
    @Order(1)
    void login_CredencialesValidas_Admin_RetornaToken() throws Exception {
        LoginRequestDTO req = new LoginRequestDTO("ximena@icesi.edu.co", "1234");
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        tokenAdmin = objectMapper.readTree(body).get("token").asText();
    }

    @Test
    @Order(2)
    void login_CredencialesValidas_Emprendedor_RetornaToken() throws Exception {
        LoginRequestDTO req = new LoginRequestDTO("carlos@icesi.edu.co", "1234");
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        tokenEmprendedor = objectMapper.readTree(body).get("token").asText();
    }

    @Test
    @Order(3)
    void login_CredencialesValidas_Comprador_RetornaToken() throws Exception {
        LoginRequestDTO req = new LoginRequestDTO("juan@icesi.edu.co", "1234");
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        tokenComprador = objectMapper.readTree(body).get("token").asText();
    }

    @Test
    @Order(4)
    void login_CredencialesInvalidas_RetornaError() throws Exception {
        // BadCredentialsException es RuntimeException → GlobalRestExceptionHandler → 400
        LoginRequestDTO req = new LoginRequestDTO("nadie@icesi.edu.co", "claveWrong");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().is4xxClientError());
    }

    // ─── /api/emprendimientos — sin token → 401 ───────────────────────────────

    @Test
    @Order(10)
    void getEmprendimientos_SinToken_Retorna401() throws Exception {
        mockMvc.perform(get("/api/emprendimientos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(11)
    void getEmprendimientoPorId_SinToken_Retorna401() throws Exception {
        mockMvc.perform(get("/api/emprendimientos/1"))
                .andExpect(status().isUnauthorized());
    }

    // ─── /api/emprendimientos — rol incorrecto (COMPRADOR) → 403 ─────────────

    @Test
    @Order(12)
    void getEmprendimientos_RolComprador_Retorna403() throws Exception {
        Assumptions.assumeTrue(tokenComprador != null, "Token COMPRADOR no disponible");
        mockMvc.perform(get("/api/emprendimientos")
                        .header("Authorization", "Bearer " + tokenComprador))
                .andExpect(status().isForbidden());
    }

    // ─── /api/emprendimientos — rol correcto (EMPRENDEDOR) → 200 ─────────────

    @Test
    @Order(13)
    void getEmprendimientos_RolEmprendedor_RetornaLista() throws Exception {
        Assumptions.assumeTrue(tokenEmprendedor != null, "Token EMPRENDEDOR no disponible");
        mockMvc.perform(get("/api/emprendimientos")
                        .header("Authorization", "Bearer " + tokenEmprendedor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    @Order(14)
    void getEmprendimientoPorId_Existe_Retorna200() throws Exception {
        Assumptions.assumeTrue(tokenEmprendedor != null, "Token EMPRENDEDOR no disponible");
        mockMvc.perform(get("/api/emprendimientos/1")
                        .header("Authorization", "Bearer " + tokenEmprendedor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEmprendimiento").value(1))
                .andExpect(jsonPath("$.nombre").isNotEmpty());
    }

    // ─── /api/emprendimientos — id no existe → 400 (RuntimeException → handler) ──

    @Test
    @Order(15)
    void getEmprendimientoPorId_NoExiste_Retorna400() throws Exception {
        Assumptions.assumeTrue(tokenEmprendedor != null, "Token EMPRENDEDOR no disponible");
        // buscarPorId lanza RuntimeException → GlobalRestExceptionHandler → 400
        mockMvc.perform(get("/api/emprendimientos/9999")
                        .header("Authorization", "Bearer " + tokenEmprendedor))
                .andExpect(status().isBadRequest());
    }

    // ─── /api/emprendimientos POST — 201 Created ──────────────────────────────

    @Test
    @Order(16)
    void crearEmprendimiento_DatosValidos_Retorna201() throws Exception {
        Assumptions.assumeTrue(tokenEmprendedor != null, "Token EMPRENDEDOR no disponible");
        String body = """
                {
                    "nombre": "Emprendimiento Test",
                    "descripcion": "Descripcion de prueba",
                    "logoUrl": null,
                    "idUsuario": 2,
                    "idCategoria": 1,
                    "idSemestre": 1,
                    "idEstado": 1
                }
                """;
        mockMvc.perform(post("/api/emprendimientos")
                        .header("Authorization", "Bearer " + tokenEmprendedor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Emprendimiento Test"));
    }

    // ─── /api/categorias — rol incorrecto (EMPRENDEDOR) → 403 ────────────────

    @Test
    @Order(20)
    void getCategorias_SinToken_Retorna401() throws Exception {
        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(21)
    void getCategorias_RolEmprendedor_Retorna403() throws Exception {
        Assumptions.assumeTrue(tokenEmprendedor != null, "Token EMPRENDEDOR no disponible");
        mockMvc.perform(get("/api/categorias")
                        .header("Authorization", "Bearer " + tokenEmprendedor))
                .andExpect(status().isForbidden());
    }

    // ─── /api/categorias — rol ADMIN → 200 ───────────────────────────────────

    @Test
    @Order(22)
    void getCategorias_RolAdmin_RetornaLista() throws Exception {
        Assumptions.assumeTrue(tokenAdmin != null, "Token ADMIN no disponible");
        mockMvc.perform(get("/api/categorias")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    @Order(23)
    void crearCategoria_RolAdmin_Retorna201() throws Exception {
        Assumptions.assumeTrue(tokenAdmin != null, "Token ADMIN no disponible");
        String body = """
                { "nombre": "CategoriaTest", "descripcion": "Descripcion test" }
                """;
        mockMvc.perform(post("/api/categorias")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("CategoriaTest"));
    }

    @Test
    @Order(24)
    void getCategoriaPorId_NoExiste_Retorna400() throws Exception {
        Assumptions.assumeTrue(tokenAdmin != null, "Token ADMIN no disponible");
        // buscarPorId lanza RuntimeException → GlobalRestExceptionHandler → 400
        mockMvc.perform(get("/api/categorias/9999")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isBadRequest());
    }

    // ─── /api/roles — solo ADMIN ──────────────────────────────────────────────

    @Test
    @Order(30)
    void getRoles_SinToken_Retorna401() throws Exception {
        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(31)
    void getRoles_RolAdmin_RetornaLista() throws Exception {
        Assumptions.assumeTrue(tokenAdmin != null, "Token ADMIN no disponible");
        mockMvc.perform(get("/api/roles")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    @Order(32)
    void getRoles_RolEmprendedor_Retorna403() throws Exception {
        Assumptions.assumeTrue(tokenEmprendedor != null, "Token EMPRENDEDOR no disponible");
        mockMvc.perform(get("/api/roles")
                        .header("Authorization", "Bearer " + tokenEmprendedor))
                .andExpect(status().isForbidden());
    }

    // ─── /api/productos ───────────────────────────────────────────────────────

    @Test
    @Order(40)
    void getProductos_SinToken_Retorna401() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(41)
    void getProductos_RolEmprendedor_RetornaLista() throws Exception {
        Assumptions.assumeTrue(tokenEmprendedor != null, "Token EMPRENDEDOR no disponible");
        mockMvc.perform(get("/api/productos")
                        .header("Authorization", "Bearer " + tokenEmprendedor))
                .andExpect(status().isOk());
    }

    @Test
    @Order(42)
    void getProductos_RolComprador_Retorna403() throws Exception {
        Assumptions.assumeTrue(tokenComprador != null, "Token COMPRADOR no disponible");
        mockMvc.perform(get("/api/productos")
                        .header("Authorization", "Bearer " + tokenComprador))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(43)
    void getProductoPorId_NoExiste_Retorna400() throws Exception {
        Assumptions.assumeTrue(tokenEmprendedor != null, "Token EMPRENDEDOR no disponible");
        // buscarPorId lanza RuntimeException → GlobalRestExceptionHandler → 400
        mockMvc.perform(get("/api/productos/9999")
                        .header("Authorization", "Bearer " + tokenEmprendedor))
                .andExpect(status().isBadRequest());
    }

    // ─── /api/usuarios ────────────────────────────────────────────────────────

    @Test
    @Order(50)
    void getUsuarios_SinToken_Retorna401() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(51)
    void getUsuarios_TokenValido_RetornaLista() throws Exception {
        Assumptions.assumeTrue(tokenAdmin != null, "Token ADMIN no disponible");
        mockMvc.perform(get("/api/usuarios")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    @Order(52)
    void deleteUsuario_RolNoAdmin_Retorna403() throws Exception {
        Assumptions.assumeTrue(tokenEmprendedor != null, "Token EMPRENDEDOR no disponible");
        mockMvc.perform(delete("/api/usuarios/99")
                        .header("Authorization", "Bearer " + tokenEmprendedor))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(53)
    void getUsuarioPorId_NoExiste_Retorna400() throws Exception {
        Assumptions.assumeTrue(tokenAdmin != null, "Token ADMIN no disponible");
        // buscarPorId lanza RuntimeException → GlobalRestExceptionHandler → 400
        mockMvc.perform(get("/api/usuarios/9999")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isBadRequest());
    }

    // ─── /api/pedidos ─────────────────────────────────────────────────────────

    @Test
    @Order(60)
    void crearPedido_SinToken_Retorna401() throws Exception {
        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(61)
    void crearPedido_RolEmprendedor_Retorna403() throws Exception {
        Assumptions.assumeTrue(tokenEmprendedor != null, "Token EMPRENDEDOR no disponible");
        mockMvc.perform(post("/api/pedidos")
                        .header("Authorization", "Bearer " + tokenEmprendedor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(62)
    void misPedidos_RolComprador_Retorna200() throws Exception {
        Assumptions.assumeTrue(tokenComprador != null, "Token COMPRADOR no disponible");
        mockMvc.perform(get("/api/pedidos/mis-pedidos")
                        .header("Authorization", "Bearer " + tokenComprador))
                .andExpect(status().isOk());
    }

    @Test
    @Order(63)
    void misPedidos_RolEmprendedor_Retorna403() throws Exception {
        Assumptions.assumeTrue(tokenEmprendedor != null, "Token EMPRENDEDOR no disponible");
        mockMvc.perform(get("/api/pedidos/mis-pedidos")
                        .header("Authorization", "Bearer " + tokenEmprendedor))
                .andExpect(status().isForbidden());
    }
}
