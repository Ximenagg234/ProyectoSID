package edu.icesi.emprendimientos.integration;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para la capa MVC (Thymeleaf + Spring Security form login).
 * Cubre los 5 flujos principales del frontend.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MvcFrontendTest {

    @Autowired
    private MockMvc mockMvc;

    // =========================================================================
    // Flujo 1: Autenticación (Login / Logout)
    // =========================================================================

    @Test
    @Order(1)
    void paginaLogin_AccesoPublico_RetornaOk() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void login_CredencialesValidas_RedirigeHome() throws Exception {
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "ximena@icesi.edu.co")
                        .param("password", "1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @Order(3)
    void login_CredencialesInvalidas_RedirigeLoginConError() throws Exception {
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "noexiste@icesi.edu.co")
                        .param("password", "wrongpass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/login*"));
    }

    @Test
    @Order(4)
    void paginaProtegida_SinAutenticar_RedirigeLogin() throws Exception {
        mockMvc.perform(get("/emprendimientos"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    // =========================================================================
    // Flujo 2: Registro de nuevo usuario
    // =========================================================================

    @Test
    @Order(10)
    void formularioNuevoUsuario_AccesoPublico_RetornaOk() throws Exception {
        mockMvc.perform(get("/usuarios/nuevo"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(11)
    void registrarUsuario_DatosValidos_RedirigeLogin() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("nombreCompleto", "Test User")
                        .param("correoInstitucional", "testmvc@icesi.edu.co")
                        .param("programaAcademico", "Ingenieria de Sistemas")
                        .param("semestreAcademico", "3")
                        .param("clave", "test1234"))
                .andExpect(status().is3xxRedirection());
    }

    // =========================================================================
    // Flujo 3: Gestión de emprendimientos (rol EMPRENDEDOR)
    // =========================================================================

    @Test
    @Order(20)
    @WithMockUser(username = "carlos@icesi.edu.co", roles = {"EMPRENDEDOR"})
    void misEmprendimientos_RolEmprendedor_RetornaOk() throws Exception {
        mockMvc.perform(get("/mis-emprendimientos"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(21)
    @WithMockUser(username = "carlos@icesi.edu.co", roles = {"EMPRENDEDOR"})
    void misProductos_RolEmprendedor_RetornaOk() throws Exception {
        mockMvc.perform(get("/mis-emprendimientos"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(22)
    @WithMockUser(username = "juan@icesi.edu.co", roles = {"COMPRADOR"})
    void emprendimientos_RolComprador_RedirigeAccesoDenegado() throws Exception {
        // /emprendimientos/** requiere EMPRENDEDOR o ADMIN; COMPRADOR debe redirigir
        mockMvc.perform(get("/emprendimientos/dashboard"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @Order(23)
    @WithMockUser(username = "ximena@icesi.edu.co", roles = {"ADMIN", "EMPRENDEDOR"})
    void generarReporteEmprendimientos_RolAdmin_RetornaOk() throws Exception {
        mockMvc.perform(get("/emprendimientos/reporte"))
                .andExpect(status().isOk());
    }

    // =========================================================================
    // Flujo 4: Marketplace (todos los usuarios autenticados)
    // =========================================================================

    @Test
    @Order(30)
    @WithMockUser(username = "juan@icesi.edu.co", roles = {"COMPRADOR"})
    void marketplace_RolComprador_RetornaOk() throws Exception {
        mockMvc.perform(get("/marketplace"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(31)
    @WithMockUser(username = "carlos@icesi.edu.co", roles = {"EMPRENDEDOR"})
    void marketplace_RolEmprendedor_RetornaOk() throws Exception {
        mockMvc.perform(get("/marketplace"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(32)
    void marketplace_SinAutenticar_RedirigeLogin() throws Exception {
        mockMvc.perform(get("/marketplace"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    // =========================================================================
    // Flujo 5: Carrito y pedidos (rol COMPRADOR)
    // =========================================================================

    @Test
    @Order(40)
    @WithMockUser(username = "juan@icesi.edu.co", roles = {"COMPRADOR"})
    void carrito_RolComprador_RetornaOk() throws Exception {
        mockMvc.perform(get("/carrito"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(41)
    @WithMockUser(username = "carlos@icesi.edu.co", roles = {"EMPRENDEDOR"})
    void carrito_RolEmprendedor_RedirigeAccesoDenegado() throws Exception {
        mockMvc.perform(get("/carrito"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @Order(42)
    @WithMockUser(username = "juan@icesi.edu.co", roles = {"COMPRADOR"})
    void misPedidos_RolComprador_RetornaOk() throws Exception {
        mockMvc.perform(get("/pedidos/mis-pedidos"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(43)
    @WithMockUser(username = "carlos@icesi.edu.co", roles = {"EMPRENDEDOR"})
    void pedidosRecibidos_RolEmprendedor_RetornaOk() throws Exception {
        mockMvc.perform(get("/pedidos/recibidos"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(44)
    @WithMockUser(username = "juan@icesi.edu.co", roles = {"COMPRADOR"})
    void pedidosRecibidos_RolComprador_RedirigeAccesoDenegado() throws Exception {
        mockMvc.perform(get("/pedidos/recibidos"))
                .andExpect(status().is3xxRedirection());
    }

    // =========================================================================
    // Flujo 6: Administración (rol ADMIN)
    // =========================================================================

    @Test
    @Order(50)
    @WithMockUser(username = "ximena@icesi.edu.co", roles = {"ADMIN"})
    void listarRoles_RolAdmin_RetornaOk() throws Exception {
        mockMvc.perform(get("/roles"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(51)
    @WithMockUser(username = "carlos@icesi.edu.co", roles = {"EMPRENDEDOR"})
    void listarRoles_RolEmprendedor_RedirigeAccesoDenegado() throws Exception {
        // Roles requiere ADMIN; EMPRENDEDOR debe ser redirigido a acceso-denegado
        mockMvc.perform(get("/roles"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @Order(52)
    @WithMockUser(username = "ximena@icesi.edu.co", roles = {"ADMIN"})
    void listarUsuarios_RolAdmin_RetornaOk() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk());
    }
}
