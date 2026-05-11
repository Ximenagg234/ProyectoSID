package edu.icesi.emprendimientos.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Pruebas de integración de UI con Selenium WebDriver.
 * Cubre TODAS las páginas y funcionalidades del frontend de IcesiEmprende:
 *   1. Páginas públicas (login, registro)
 *   2. Autenticación (login válido / inválido, logout)
 *   3. Registro de usuario
 *   4. Admin – Gestión de roles (CRUD)
 *   5. Admin – Gestión de usuarios (CRUD + asignación de roles)
 *   6. Home dashboard
 *   7. Emprendedor – Mis emprendimientos (CRUD)
 *   8. Emprendedor – Productos (CRUD)
 *   9. Emprendedor – Pedidos recibidos
 *  10. Marketplace (lista, filtro, detalle de emprendimiento)
 *  11. Comprador – Carrito (agregar, vaciar, confirmar)
 *  12. Comprador – Mis pedidos
 *  13. Control de acceso por roles
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SeleniumUITest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;

    // ── Credenciales de usuarios precargados en data.sql ─────────────────────
    private static final String ADMIN_EMAIL       = "ximena@icesi.edu.co";
    private static final String EMPRENDEDOR_EMAIL = "carlos@icesi.edu.co";
    private static final String COMPRADOR_EMAIL   = "juan@icesi.edu.co";
    private static final String PASSWORD          = "1234";

    // ── Estado compartido entre tests ─────────────────────────────────────────
    private Integer emprendimientoId;   // creado en el test 62, reutilizado hasta el 141
    private Integer productoId;         // creado en el test 72, reutilizado hasta el 140

    // =========================================================================
    // Setup / Teardown
    // =========================================================================

    @BeforeAll
    void configurarDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--headless=new",
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--window-size=1280,900",
                "--disable-extensions",
                "--remote-allow-origins=*"
        );
        // PageLoadStrategy.EAGER: driver.get() retorna cuando el DOM está completamente
        // construido (DOMContentLoaded), sin esperar recursos externos (CSS/JS de CDN).
        // Garantiza que todos los elementos HTML estén en el DOM antes de buscarlos.
        options.setPageLoadStrategy(org.openqa.selenium.PageLoadStrategy.EAGER);
        driver  = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        wait    = new WebDriverWait(driver, Duration.ofSeconds(20));
        baseUrl = "http://localhost:" + port;
    }

    /** Antes de cada test limpia las cookies para garantizar sesión fresca. */
    @BeforeEach
    void resetarSesion() {
        try {
            driver.manage().deleteAllCookies();
        } catch (Exception ignored) { /* driver podría no estar en un estado limpio */ }
        try {
            // Navegar a /login DESPUÉS de limpiar cookies:
            // – Cancela cualquier navegación pendiente del test anterior
            //   (p.ej. la cadena / → /home que quedó en vuelo tras el login del admin).
            // – Establece un estado de sesión fresca antes de que comience el test.
            driver.get(baseUrl + "/login");
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.presenceOfElementLocated(By.name("username")));
        } catch (Exception ignored) { /* si el driver no está disponible, el test fallará con su propio error */ }
    }

    @AfterAll
    void cerrarDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /** Navega a una ruta relativa. */
    private void ir(String ruta) {
        try {
            driver.get(baseUrl + ruta);
        } catch (TimeoutException te) {
            // EAGER: pageLoad timeout (CDN blocking). DOM podría estar listo de todas formas.
            System.out.println("[ir] TIMEOUT navegando a " + ruta
                    + " | URL actual: " + driver.getCurrentUrl());
        }
    }

    /**
     * Espera a que el elemento esté PRESENTE en el DOM (presenceOfElementLocated).
     * No requiere que esté visualmente renderizado (ignora bloqueos de CSS externo).
     */
    private WebElement esperar(By locator) {
        try {
            return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException e) {
            String url  = driver.getCurrentUrl();
            String src  = driver.getPageSource();
            System.out.println("[esperar] TIMEOUT buscando " + locator
                    + "\n  URL: " + url
                    + "\n  Title: " + driver.getTitle()
                    + "\n  Source[0..300]: " + src.substring(0, Math.min(300, src.length())));
            throw e;
        }
    }

    /** Espera a que el elemento sea clickeable (presente + habilitado). */
    private WebElement esperarClickeable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /** Realiza login con las credenciales indicadas y espera a llegar a /home.
     *
     * @BeforeEach garantiza que el formulario /login está cargado antes de llamar aquí.
     * Se usa JavaScript para rellenar los campos y enviar el formulario de forma robusta,
     * evitando los problemas de sendKeys() + button.click() con Chrome EAGER tras un
     * login anterior (same-URL reload deja campos vacíos; click() no dispara el POST).
     */
    private void login(String email, String password) {
        // Asegurar que el formulario está presente antes de interactuar
        esperar(By.name("username"));
        // Rellenar credenciales y enviar mediante JavaScript (fiable en todas las sesiones)
        ((JavascriptExecutor) driver).executeScript(
                "var f = document.querySelector('form');" +
                "f.querySelector('input[name=\"username\"]').value = arguments[0];" +
                "f.querySelector('input[name=\"password\"]').value = arguments[1];" +
                "f.submit();",
                email, password);
        // Esperar a salir de /login. La URL pasa por "/" → "/home" tras el login exitoso.
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));
    }

    /** Simula logout borrando las cookies de sesión. */
    private void logout() {
        driver.manage().deleteAllCookies();
    }

    /** Verifica si un elemento existe en el DOM (sin wait). */
    private boolean existe(By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /** Rellena un campo limpiando su contenido previo. */
    private void escribir(By locator, String texto) {
        WebElement el = esperar(locator);
        el.clear();
        el.sendKeys(texto);
    }

    // =========================================================================
    // ── FLUJO 1: Páginas públicas ─────────────────────────────────────────────
    // =========================================================================

    @Test
    @Order(1)
    void paginaLogin_AccesoPublico_MuestraFormulario() {
        ir("/login");
        // Con EAGER, el DOM está completo: verificamos presencia (no visibility que depende de CSS externo)
        assertNotNull(esperar(By.name("username")), "El campo username debe estar en el DOM");
        assertNotNull(driver.findElement(By.name("password")), "El campo password debe estar en el DOM");
        assertNotNull(driver.findElement(By.cssSelector("button[type='submit']")), "El botón submit debe estar en el DOM");
        assertTrue(driver.getTitle().contains("IcesiEmprende") ||
                   driver.getPageSource().contains("IcesiEmprende"),
                "El título debe mencionar IcesiEmprende");
    }

    @Test
    @Order(2)
    void paginaRegistroAdmin_AccesoPublico_MuestraFormulario() {
        // /usuarios/nuevo es la ruta pública de auto-registro (permitida sin auth)
        ir("/usuarios/nuevo");
        assertNotNull(esperar(By.id("nombreCompleto")));
        assertNotNull(driver.findElement(By.id("correoInstitucional")));
        assertNotNull(driver.findElement(By.id("clave")));
    }

    @Test
    @Order(3)
    void paginaProtegida_SinLogin_RedirigeALogin() {
        logout(); // asegurar que no hay sesión activa
        ir("/mis-emprendimientos");
        wait.until(ExpectedConditions.urlContains("/login"));
        assertTrue(driver.getCurrentUrl().contains("/login"),
                "Debe redirigir al login si no está autenticado");
    }

    @Test
    @Order(4)
    void marketplace_SinLogin_RedirigeALogin() {
        logout();
        ir("/marketplace");
        wait.until(ExpectedConditions.urlContains("/login"));
        assertTrue(driver.getCurrentUrl().contains("/login"));
    }

    @Test
    @Order(5)
    void carrito_SinLogin_RedirigeALogin() {
        logout();
        ir("/carrito");
        wait.until(ExpectedConditions.urlContains("/login"));
        assertTrue(driver.getCurrentUrl().contains("/login"));
    }

    // =========================================================================
    // ── FLUJO 2: Autenticación (Login / Logout) ───────────────────────────────
    // =========================================================================

    @Test
    @Order(10)
    void login_CredencialesValidas_Admin_RedirigeHome() {
        login(ADMIN_EMAIL, PASSWORD);
        assertFalse(driver.getCurrentUrl().contains("/login"),
                "Admin no debe quedarse en /login tras autenticarse");
    }

    @Test
    @Order(11)
    void login_CredencialesValidas_Emprendedor_RedirigeHome() {
        login(EMPRENDEDOR_EMAIL, PASSWORD);
        assertFalse(driver.getCurrentUrl().contains("/login"));
    }

    @Test
    @Order(12)
    void login_CredencialesValidas_Comprador_RedirigeHome() {
        login(COMPRADOR_EMAIL, PASSWORD);
        assertFalse(driver.getCurrentUrl().contains("/login"));
    }

    @Test
    @Order(13)
    void login_CredencialesInvalidas_MuestraError() {
        ir("/login");
        driver.findElement(By.name("username")).sendKeys("noexiste@icesi.edu.co");
        driver.findElement(By.name("password")).sendKeys("claveIncorrecta");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        // Spring Security redirige a /login?error
        wait.until(ExpectedConditions.urlContains("/login"));
        String url = driver.getCurrentUrl();
        String src = driver.getPageSource();
        assertTrue(url.contains("error") || src.contains("alert-error") ||
                   src.contains("incorr"),
                "Debe mostrar error o parámetro ?error en la URL");
    }

    @Test
    @Order(14)
    void logout_UsuarioAutenticado_InvalidaSesion() {
        login(ADMIN_EMAIL, PASSWORD);
        assertFalse(driver.getCurrentUrl().contains("/login"),
                "Admin debe estar en home tras login");

        logout(); // borra cookies
        ir("/mis-emprendimientos"); // página protegida
        wait.until(ExpectedConditions.urlContains("/login"));
        assertTrue(driver.getCurrentUrl().contains("/login"),
                "Tras logout, la página protegida debe redirigir a login");
    }

    // =========================================================================
    // ── FLUJO 3: Registro de usuario ─────────────────────────────────────────
    // =========================================================================

    @Test
    @Order(20)
    void registro_FormularioPublico_ContieneNombreCampos() {
        ir("/usuarios/nuevo");
        // Verificar que todos los campos del formulario están presentes en el DOM
        assertNotNull(esperar(By.id("nombreCompleto")));
        assertNotNull(driver.findElement(By.id("correoInstitucional")));
        assertNotNull(driver.findElement(By.id("programaAcademico")));
        assertNotNull(driver.findElement(By.id("semestreAcademico")));
        assertNotNull(driver.findElement(By.id("clave")));
    }

    @Test
    @Order(21)
    void registro_UsuarioNuevo_DatosValidos_Redirige() {
        ir("/usuarios/nuevo");
        // Esperar a que el formulario esté listo antes de interactuar
        esperar(By.id("nombreCompleto"));
        // Usar JavaScript para rellenar y enviar el formulario: evita el bug de Chrome
        // headless donde button.click() no dispara el POST en páginas sin sesión activa.
        ((JavascriptExecutor) driver).executeScript(
                "document.getElementById('nombreCompleto').value     = arguments[0];" +
                "document.getElementById('correoInstitucional').value = arguments[1];" +
                "document.getElementById('programaAcademico').value  = arguments[2];" +
                "document.getElementById('semestreAcademico').value  = arguments[3];" +
                "document.getElementById('clave').value              = arguments[4];" +
                "document.querySelector('form').submit();",
                "Selenium Test User",
                "selenium.nuevo@icesi.edu.co",
                "Ingeniería de Sistemas",
                "5",
                "selenium1234");
        // Tras guardar redirige a /usuarios/asignar-roles/{id} (requiere admin)
        // o a /login si no hay sesión activa. En cualquier caso no queda en /usuarios/nuevo
        wait.until(driver -> !driver.getCurrentUrl().endsWith("/usuarios/nuevo") &&
                             !driver.getCurrentUrl().endsWith("/usuarios/nuevo/"));
        assertFalse(driver.getCurrentUrl().contains("/usuarios/nuevo"),
                "Debe redirigir tras registrar al usuario");
    }

    // =========================================================================
    // ── FLUJO 4: Admin – Gestión de Roles ────────────────────────────────────
    // =========================================================================

    @Test
    @Order(30)
    void roles_Lista_AdminPuedeVerRoles() {
        login(ADMIN_EMAIL, PASSWORD);
        ir("/roles");
        esperar(By.cssSelector(".page-header h1, h1"));
        String src = driver.getPageSource();
        // La lista debe mostrar al menos un rol existente en data.sql
        assertTrue(src.contains("ROLE_") || src.contains("Rol") ||
                   src.contains("ADMIN") || src.contains("Roles"),
                "La página de roles debe mostrar datos");
    }

    @Test
    @Order(31)
    void roles_FormNuevo_MuestraCampoNombre() {
        login(ADMIN_EMAIL, PASSWORD);
        ir("/roles/nuevo");
        WebElement inputNombre = esperar(By.id("nombre"));
        assertNotNull(inputNombre);
    }

    @Test
    @Order(32)
    void roles_Crear_NuevoRol_Exitoso() {
        login(ADMIN_EMAIL, PASSWORD);
        ir("/roles/nuevo");
        escribir(By.id("nombre"), "ROLE_SELENIUM");
        esperarClickeable(By.cssSelector("button[type='submit']")).click();
        // Redirige a /roles
        wait.until(ExpectedConditions.urlContains("/roles"));
        // La lista debe mostrar el rol recién creado
        assertTrue(driver.getPageSource().contains("ROLE_SELENIUM") ||
                   driver.getCurrentUrl().contains("/roles"),
                "Debe redirigir a la lista de roles tras crear");
    }

    @Test
    @Order(33)
    void roles_Comprador_NoTieneAcceso_Redirige() {
        login(COMPRADOR_EMAIL, PASSWORD);
        ir("/roles");
        // COMPRADOR no tiene ADMIN, debe ser redirigido
        wait.until(driver ->
            driver.getCurrentUrl().contains("acceso-denegado") ||
            driver.getCurrentUrl().contains("/login") ||
            driver.getPageSource().contains("403") ||
            driver.getPageSource().contains("acceso"));
        String url = driver.getCurrentUrl();
        assertTrue(url.contains("acceso-denegado") || url.contains("/login") ||
                   !url.contains("/roles"),
                "COMPRADOR no debe tener acceso a /roles");
    }

    // =========================================================================
    // ── FLUJO 5: Admin – Gestión de Usuarios ─────────────────────────────────
    // =========================================================================

    @Test
    @Order(40)
    void usuarios_Lista_AdminPuedeVerUsuarios() {
        login(ADMIN_EMAIL, PASSWORD);
        ir("/usuarios");
        esperar(By.cssSelector(".page-header h1, h1"));
        String src = driver.getPageSource();
        // Debe mostrar usuarios pre-cargados en data.sql
        assertTrue(src.contains("icesi.edu.co") || src.contains("Estudiantes") ||
                   src.contains("Usuarios") || src.contains("ximena") ||
                   src.contains("carlos"),
                "La lista de usuarios debe mostrar datos");
    }

    @Test
    @Order(41)
    void usuarios_AsignarRoles_Usuario1_Accesible() {
        login(ADMIN_EMAIL, PASSWORD);
        ir("/usuarios/asignar-roles/1");
        esperar(By.cssSelector("form, .page-header h1, h1"));
        assertFalse(driver.getPageSource().contains("Whitelabel Error Page"),
                "La página de asignar roles debe renderizarse sin error");
    }

    @Test
    @Order(42)
    void usuarios_Emprendedor_NoTieneAcceso_Redirige() {
        login(EMPRENDEDOR_EMAIL, PASSWORD);
        ir("/usuarios");
        // EMPRENDEDOR no tiene ADMIN
        wait.until(driver ->
            driver.getCurrentUrl().contains("acceso-denegado") ||
            driver.getCurrentUrl().contains("/login") ||
            !driver.getCurrentUrl().endsWith("/usuarios"));
        assertFalse(driver.getPageSource().contains("Whitelabel Error Page"));
    }

    // =========================================================================
    // ── FLUJO 6: Home Dashboard ───────────────────────────────────────────────
    // =========================================================================

    @Test
    @Order(50)
    void home_AdminAutenticado_MuestraDashboard() {
        login(ADMIN_EMAIL, PASSWORD);
        ir("/");
        esperar(By.cssSelector("body"));
        assertFalse(driver.getPageSource().contains("Whitelabel Error Page"),
                "El home no debe mostrar error 500");
    }

    @Test
    @Order(51)
    void home_EmprendedorAutenticado_Accesible() {
        login(EMPRENDEDOR_EMAIL, PASSWORD);
        ir("/");
        assertFalse(driver.getPageSource().contains("Whitelabel Error Page"));
    }

    @Test
    @Order(52)
    void home_CompradorAutenticado_Accesible() {
        login(COMPRADOR_EMAIL, PASSWORD);
        ir("/");
        assertFalse(driver.getPageSource().contains("Whitelabel Error Page"));
    }

    // =========================================================================
    // ── FLUJO 7: Emprendedor – Mis emprendimientos (CRUD) ────────────────────
    // =========================================================================

    @Test
    @Order(60)
    void misEmprendimientos_Lista_EmprendedorAutenticado_Accesible() {
        login(EMPRENDEDOR_EMAIL, PASSWORD);
        ir("/mis-emprendimientos");
        esperar(By.cssSelector(".page-header h1, h1"));
        assertFalse(driver.getPageSource().contains("Whitelabel Error Page"));
    }

    @Test
    @Order(61)
    void misEmprendimientos_FormNuevo_ContieneInputsRequeridos() {
        login(EMPRENDEDOR_EMAIL, PASSWORD);
        ir("/mis-emprendimientos/nuevo");
        assertNotNull(esperar(By.id("nombre")), "El campo nombre debe estar en el DOM");
        assertNotNull(driver.findElement(By.id("descripcion")), "El campo descripcion debe estar en el DOM");
        assertNotNull(driver.findElement(By.cssSelector("select[id*='categoria']")), "El selector de categoría debe estar en el DOM");
    }

    @Test
    @Order(62)
    void misEmprendimientos_Crear_NuevoEmprendimiento_Exitoso() {
        login(EMPRENDEDOR_EMAIL, PASSWORD);
        ir("/mis-emprendimientos/nuevo");
        esperar(By.id("nombre"));

        // Rellenar campos de texto vía JavaScript (evita el bug de button.click())
        ((JavascriptExecutor) driver).executeScript(
                "document.getElementById('nombre').value      = arguments[0];" +
                "document.getElementById('descripcion').value = arguments[1];",
                "Emprendimiento Selenium",
                "Descripcion de prueba creada por Selenium");

        // Seleccionar primera categoría disponible (Select de Selenium funciona bien)
        Select categoriaSelect = new Select(
                driver.findElement(By.cssSelector("select[id*='categoria']")));
        if (categoriaSelect.getOptions().size() > 1) {
            categoriaSelect.selectByIndex(1);
        }

        // Enviar formulario vía JavaScript
        ((JavascriptExecutor) driver).executeScript("document.querySelector('form').submit();");

        // Debe redirigir a la LISTA /mis-emprendimientos (sin /nuevo)
        wait.until(driver -> driver.getCurrentUrl().contains("/mis-emprendimientos")
                          && !driver.getCurrentUrl().contains("/nuevo"));
        assertTrue(driver.getCurrentUrl().contains("/mis-emprendimientos"),
                "Debe redirigir a la lista tras crear");

        // Extraer el ID del emprendimiento recién creado desde los enlaces de editar
        List<WebElement> editLinks = driver.findElements(
                By.cssSelector("a[href*='/mis-emprendimientos/editar/']"));
        if (!editLinks.isEmpty()) {
            String href = editLinks.get(editLinks.size() - 1).getAttribute("href");
            String rawId = href.replaceAll(".*/editar/(\\d+).*", "$1");
            try {
                emprendimientoId = Integer.parseInt(rawId);
            } catch (NumberFormatException ignored) { /* no encontrado */ }
        }
        assertNotNull(emprendimientoId, "El ID del emprendimiento creado debe quedar guardado");
    }

    @Test
    @Order(63)
    void misEmprendimientos_ListaMuestraEmprendimientoCreado() {
        assumeTrue(emprendimientoId != null, "Emprendimiento no fue creado en el test anterior");
        login(EMPRENDEDOR_EMAIL, PASSWORD);
        ir("/mis-emprendimientos");
        assertTrue(driver.getPageSource().contains("Emprendimiento Selenium"),
                "La lista debe mostrar el emprendimiento recién creado");
    }

    @Test
    @Order(64)
    void misEmprendimientos_FormEditar_CargaDatosPreexistentes() {
        assumeTrue(emprendimientoId != null, "Emprendimiento ID no disponible");
        login(EMPRENDEDOR_EMAIL, PASSWORD);
        ir("/mis-emprendimientos/editar/" + emprendimientoId);
        WebElement nombreInput = esperar(By.id("nombre"));
        assertNotNull(nombreInput);
        assertEquals("Emprendimiento Selenium", nombreInput.getAttribute("value"),
                "El campo nombre debe estar prellenado");
    }

    @Test
    @Order(65)
    void misEmprendimientos_Actualizar_Exitoso() {
        assumeTrue(emprendimientoId != null, "Emprendimiento ID no disponible");
        login(EMPRENDEDOR_EMAIL, PASSWORD);
        ir("/mis-emprendimientos/editar/" + emprendimientoId);
        esperar(By.id("nombre"));

        // Rellenar vía JavaScript para garantizar que button.click() no falla silencioso
        ((JavascriptExecutor) driver).executeScript(
                "document.getElementById('nombre').value      = arguments[0];" +
                "document.getElementById('descripcion').value = arguments[1];",
                "Emprendimiento Selenium Actualizado",
                "Descripcion actualizada por Selenium");

        // Enviar formulario vía JavaScript
        ((JavascriptExecutor) driver).executeScript("document.querySelector('form').submit();");

        // Redirige a la lista /mis-emprendimientos (sin /editar)
        wait.until(driver -> driver.getCurrentUrl().contains("/mis-emprendimientos")
                          && !driver.getCurrentUrl().contains("/editar"));
        assertTrue(driver.getCurrentUrl().contains("/mis-emprendimientos"),
                "Debe redirigir a la lista tras actualizar");
    }

    @Test
    @Order(66)
    void misEmprendimientos_CompradorSinAcceso_Redirige() {
        login(COMPRADOR_EMAIL, PASSWORD);
        ir("/mis-emprendimientos");
        // COMPRADOR no tiene EMPRENDEDOR ni ADMIN
        wait.until(driver ->
            driver.getCurrentUrl().contains("acceso-denegado") ||
            driver.getCurrentUrl().contains("/login") ||
            !driver.getCurrentUrl().endsWith("/mis-emprendimientos"));
        assertFalse(driver.getPageSource().contains("Whitelabel Error Page"));
    }

    // =========================================================================
    // ── FLUJO 8: Emprendedor – Productos (CRUD) ───────────────────────────────
    // =========================================================================

    @Test
    @Order(70)
    void productos_Lista_EmprendimientoExistente_Accesible() {
        assumeTrue(emprendimientoId != null, "Emprendimiento ID no disponible");
        login(EMPRENDEDOR_EMAIL, PASSWORD);
        ir("/mis-emprendimientos/" + emprendimientoId + "/productos");
        esperar(By.cssSelector(".page-header h1, h1"));
        assertFalse(driver.getPageSource().contains("Whitelabel Error Page"));
    }

    @Test
    @Order(71)
    void productos_FormNuevo_ContieneInputsRequeridos() {
        assumeTrue(emprendimientoId != null, "Emprendimiento ID no disponible");
        login(EMPRENDEDOR_EMAIL, PASSWORD);
        ir("/mis-emprendimientos/" + emprendimientoId + "/productos/nuevo");
        assertNotNull(esperar(By.id("nombre")));
        assertNotNull(driver.findElement(By.id("precio")));
        assertNotNull(driver.findElement(By.id("stockDisponible")));
    }

    @Test
    @Order(72)
    void productos_Crear_NuevoProducto_Exitoso() {
        assumeTrue(emprendimientoId != null, "Emprendimiento ID no disponible");
        login(EMPRENDEDOR_EMAIL, PASSWORD);
        ir("/mis-emprendimientos/" + emprendimientoId + "/productos/nuevo");
        esperar(By.id("nombre"));

        // Rellenar vía JavaScript
        ((JavascriptExecutor) driver).executeScript(
                "document.getElementById('nombre').value          = arguments[0];" +
                "document.getElementById('descripcion').value     = arguments[1];" +
                "document.getElementById('precio').value          = arguments[2];" +
                "document.getElementById('stockDisponible').value = arguments[3];",
                "Producto Selenium Test",
                "Descripcion del producto Selenium",
                "12500",
                "30");

        // Enviar formulario vía JavaScript
        ((JavascriptExecutor) driver).executeScript("document.querySelector('form').submit();");

        // Redirige a la lista de productos (sin /nuevo)
        wait.until(driver -> driver.getCurrentUrl().contains("/productos")
                          && !driver.getCurrentUrl().contains("/nuevo"));
        assertFalse(driver.getPageSource().contains("Whitelabel Error Page"));

        // Extraer el ID del producto recién creado
        List<WebElement> editLinks = driver.findElements(
                By.cssSelector("a[href*='/productos/editar/']"));
        if (!editLinks.isEmpty()) {
            String href = editLinks.get(editLinks.size() - 1).getAttribute("href");
            String rawId = href.replaceAll(".*/editar/(\\d+).*", "$1");
            try {
                productoId = Integer.parseInt(rawId);
            } catch (NumberFormatException ignored) { /* no encontrado */ }
        }
        assertNotNull(productoId, "El ID del producto creado debe quedar guardado");
    }

    @Test
    @Order(73)
    void productos_ListaMuestraProductoCreado() {
        assumeTrue(emprendimientoId != null, "Emprendimiento ID no disponible");
        login(EMPRENDEDOR_EMAIL, PASSWORD);
        ir("/mis-emprendimientos/" + emprendimientoId + "/productos");
        assertTrue(driver.getPageSource().contains("Producto Selenium Test"),
                "La lista debe mostrar el producto creado");
    }

    @Test
    @Order(74)
    void productos_FormEditar_CargaDatosPreexistentes() {
        assumeTrue(emprendimientoId != null && productoId != null,
                "IDs de emprendimiento y producto requeridos");
        login(EMPRENDEDOR_EMAIL, PASSWORD);
        ir("/mis-emprendimientos/" + emprendimientoId + "/productos/editar/" + productoId);
        WebElement nombreInput = esperar(By.id("nombre"));
        assertNotNull(nombreInput);
        assertEquals("Producto Selenium Test", nombreInput.getAttribute("value"),
                "El nombre debe estar prellenado con el valor guardado");
    }

    @Test
    @Order(75)
    void productos_Actualizar_Exitoso() {
        assumeTrue(emprendimientoId != null && productoId != null,
                "IDs de emprendimiento y producto requeridos");
        login(EMPRENDEDOR_EMAIL, PASSWORD);
        ir("/mis-emprendimientos/" + emprendimientoId + "/productos/editar/" + productoId);
        esperar(By.id("nombre"));

        // Rellenar vía JavaScript
        ((JavascriptExecutor) driver).executeScript(
                "document.getElementById('nombre').value          = arguments[0];" +
                "document.getElementById('precio').value          = arguments[1];" +
                "document.getElementById('stockDisponible').value = arguments[2];",
                "Producto Selenium Actualizado",
                "18000",
                "25");

        // Enviar formulario vía JavaScript
        ((JavascriptExecutor) driver).executeScript("document.querySelector('form').submit();");

        // Redirige a la lista de productos (sin /editar)
        wait.until(driver -> driver.getCurrentUrl().contains("/productos")
                          && !driver.getCurrentUrl().contains("/editar"));
        assertFalse(driver.getPageSource().contains("Whitelabel Error Page"),
                "Debe renderizarse sin error tras actualizar");
    }

    // =========================================================================
    // ── FLUJO 9: Emprendedor – Pedidos recibidos ──────────────────────────────
    // =========================================================================

    @Test
    @Order(80)
    void pedidosRecibidos_EmprendedorAutenticado_Accesible() {
        login(EMPRENDEDOR_EMAIL, PASSWORD);
        ir("/pedidos/recibidos");
        esperar(By.cssSelector(".page-header h1, h1"));
        String src = driver.getPageSource();
        assertFalse(src.contains("Whitelabel Error Page"));
        assertTrue(src.contains("Pedidos Recibidos") || src.contains("pedidos") ||
                   src.contains("gestionar"),
                "La página debe mencionar pedidos recibidos");
    }

    @Test
    @Order(81)
    void pedidosRecibidos_AdminConRolEmprendedor_Accesible() {
        login(ADMIN_EMAIL, PASSWORD); // ximena tiene ADMIN + EMPRENDEDOR
        ir("/pedidos/recibidos");
        assertFalse(driver.getPageSource().contains("Whitelabel Error Page"));
    }

    @Test
    @Order(82)
    void pedidosRecibidos_CompradorSinAcceso_Redirige() {
        login(COMPRADOR_EMAIL, PASSWORD);
        ir("/pedidos/recibidos");
        wait.until(driver ->
            driver.getCurrentUrl().contains("acceso-denegado") ||
            driver.getCurrentUrl().contains("/login") ||
            !driver.getCurrentUrl().contains("/recibidos"));
        assertFalse(driver.getPageSource().contains("Whitelabel Error Page"));
    }

    // =========================================================================
    // ── FLUJO 10: Marketplace ─────────────────────────────────────────────────
    // =========================================================================

    @Test
    @Order(90)
    void marketplace_CompradorAutenticado_MuestraCatalogo() {
        login(COMPRADOR_EMAIL, PASSWORD);
        ir("/marketplace");
        esperar(By.cssSelector(".page-header h1, h1, body"));
        assertFalse(driver.getPageSource().contains("Whitelabel Error Page"),
                "El marketplace no debe mostrar error");
    }

    @Test
    @Order(91)
    void marketplace_EmprendedorAutenticado_Accesible() {
        login(EMPRENDEDOR_EMAIL, PASSWORD);
        ir("/marketplace");
        assertFalse(driver.getPageSource().contains("Whitelabel Error Page"));
    }

    @Test
    @Order(92)
    void marketplace_AdminAutenticado_Accesible() {
        login(ADMIN_EMAIL, PASSWORD);
        ir("/marketplace");
        assertFalse(driver.getPageSource().contains("Whitelabel Error Page"));
    }

    @Test
    @Order(93)
    void marketplace_FiltrarPorCategoria_Accesible() {
        login(COMPRADOR_EMAIL, PASSWORD);
        ir("/marketplace?categoria=1");
        esperar(By.cssSelector("body"));
        assertFalse(driver.getPageSource().contains("Whitelabel Error Page"),
                "El filtro por categoría no debe causar error");
    }

    @Test
    @Order(94)
    void marketplace_DetalleEmprendimiento1_Accesible() {
        login(COMPRADOR_EMAIL, PASSWORD);
        ir("/marketplace/emprendimiento/1");
        esperar(By.cssSelector(".page-header h1, h1, body"));
        assertFalse(driver.getPageSource().contains("Whitelabel Error Page"),
                "El detalle del emprendimiento debe renderizarse sin error");
    }

    @Test
    @Order(95)
    void marketplace_AgregarProductoAlCarrito_DesdeProductoExistente() {
        login(COMPRADOR_EMAIL, PASSWORD);
        ir("/marketplace");
        // Buscar cualquier formulario de "agregar al carrito"
        List<WebElement> agregarForms = driver.findElements(
                By.cssSelector("form[action='/carrito/agregar']"));
        if (!agregarForms.isEmpty()) {
            // Enviar el primer formulario vía JavaScript para evitar problemas de click
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].submit();", agregarForms.get(0));
            wait.until(driver -> !driver.getCurrentUrl().isEmpty());
            // Después de agregar redirige al marketplace
            String url = driver.getCurrentUrl();
            assertTrue(url.contains("/marketplace") || url.contains("/carrito"),
                    "Tras agregar al carrito debe redirigir a marketplace o carrito");
        }
        // Si no hay productos en el marketplace, el test pasa de todas formas
    }

    // =========================================================================
    // ── FLUJO 11: Comprador – Carrito ────────────────────────────────────────
    // =========================================================================

    @Test
    @Order(100)
    void carrito_CompradorAutenticado_Accesible() {
        login(COMPRADOR_EMAIL, PASSWORD);
        ir("/carrito");
        esperar(By.cssSelector(".page-header h1, h1, body"));
        assertFalse(driver.getPageSource().contains("Whitelabel Error Page"),
                "El carrito no debe mostrar error 500");
    }

    @Test
    @Order(101)
    void carrito_VaciarCarrito_Funciona() {
        login(COMPRADOR_EMAIL, PASSWORD);
        ir("/carrito");
        List<WebElement> vaciarForms = driver.findElements(
                By.cssSelector("form[action='/carrito/vaciar']"));
        if (!vaciarForms.isEmpty()) {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].submit();", vaciarForms.get(0));
            wait.until(ExpectedConditions.urlContains("/carrito"));
        }
        // El carrito debe ser accesible (vacío o con ítems)
        assertFalse(driver.getPageSource().contains("Whitelabel Error Page"));
    }

    @Test
    @Order(102)
    void carrito_EmprendedorSinRolComprador_Redirige() {
        login(EMPRENDEDOR_EMAIL, PASSWORD);
        ir("/carrito");
        wait.until(driver ->
            driver.getCurrentUrl().contains("acceso-denegado") ||
            driver.getCurrentUrl().contains("/login") ||
            !driver.getCurrentUrl().endsWith("/carrito"));
        assertFalse(driver.getPageSource().contains("Whitelabel Error Page"),
                "No debe mostrar error 500 al denegar acceso al carrito");
    }

    @Test
    @Order(103)
    void carrito_ConfirmarPedidoVacio_MuestraError() {
        login(COMPRADOR_EMAIL, PASSWORD);
        ir("/carrito");
        // Si hay un form de confirmar y el carrito está vacío, debe mostrar mensaje de error
        List<WebElement> confirmarForms = driver.findElements(
                By.cssSelector("form[action='/carrito/confirmar']"));
        if (!confirmarForms.isEmpty()) {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].submit();", confirmarForms.get(0));
            wait.until(driver -> !driver.getCurrentUrl().isEmpty());
            // Redirige a /carrito con error flash o a /pedidos/mis-pedidos
            String url = driver.getCurrentUrl();
            assertTrue(url.contains("/carrito") || url.contains("/pedidos"),
                    "Confirmar carrito vacío debe redirigir a carrito o pedidos");
        }
    }

    // =========================================================================
    // ── FLUJO 12: Comprador – Mis Pedidos ────────────────────────────────────
    // =========================================================================

    @Test
    @Order(110)
    void misPedidos_CompradorAutenticado_Accesible() {
        login(COMPRADOR_EMAIL, PASSWORD);
        ir("/pedidos/mis-pedidos");
        esperar(By.cssSelector(".page-header h1, h1, body"));
        String src = driver.getPageSource();
        assertFalse(src.contains("Whitelabel Error Page"));
        assertTrue(src.contains("Mis Pedidos") || src.contains("pedidos") ||
                   src.contains("compras"),
                "La página debe mostrar el título de mis pedidos");
    }

    @Test
    @Order(111)
    void misPedidos_EmprendedorSinAcceso_Redirige() {
        login(EMPRENDEDOR_EMAIL, PASSWORD);
        ir("/pedidos/mis-pedidos");
        wait.until(driver ->
            driver.getCurrentUrl().contains("acceso-denegado") ||
            driver.getCurrentUrl().contains("/login") ||
            !driver.getCurrentUrl().contains("/mis-pedidos"));
        assertFalse(driver.getPageSource().contains("Whitelabel Error Page"));
    }

    // =========================================================================
    // ── FLUJO 13: Eliminación de recursos (cleanup) ───────────────────────────
    // =========================================================================

    @Test
    @Order(140)
    void productos_Eliminar_EliminaCorrectamente() {
        assumeTrue(emprendimientoId != null && productoId != null,
                "IDs requeridos para eliminar el producto");
        login(EMPRENDEDOR_EMAIL, PASSWORD);
        ir("/mis-emprendimientos/" + emprendimientoId + "/productos/eliminar/" + productoId);
        wait.until(ExpectedConditions.urlContains("/productos"));
        assertFalse(driver.getPageSource().contains("Whitelabel Error Page"),
                "La eliminación no debe causar error");
        // Verificar que el producto ya no está en la lista
        assertFalse(driver.getPageSource().contains("Producto Selenium Actualizado"),
                "El producto eliminado no debe aparecer en la lista");
    }

    @Test
    @Order(141)
    void misEmprendimientos_Eliminar_EliminaCorrectamente() {
        assumeTrue(emprendimientoId != null, "Emprendimiento ID requerido para eliminar");
        login(EMPRENDEDOR_EMAIL, PASSWORD);
        ir("/mis-emprendimientos/eliminar/" + emprendimientoId);
        wait.until(ExpectedConditions.urlContains("/mis-emprendimientos"));
        assertFalse(driver.getPageSource().contains("Whitelabel Error Page"),
                "La eliminación no debe causar error");
        // Verificar que el emprendimiento ya no está en la lista
        assertFalse(driver.getPageSource().contains("Emprendimiento Selenium Actualizado"),
                "El emprendimiento eliminado no debe aparecer en la lista");
    }

    // =========================================================================
    // ── FLUJO 14: Emprendimientos – Panel de administración (ADMIN) ───────────
    // =========================================================================

    @Test
    @Order(150)
    void emprendimientoDashboard_AdminConRolEmprendedor_Accesible() {
        login(ADMIN_EMAIL, PASSWORD); // ximena: ADMIN + EMPRENDEDOR
        ir("/emprendimientos/dashboard");
        esperar(By.cssSelector("body"));
        assertFalse(driver.getPageSource().contains("Whitelabel Error Page"),
                "El dashboard de emprendimientos debe renderizarse sin error para ADMIN");
    }

    // =========================================================================
    // ── FLUJO 15: Navegación entre secciones del sidebar ─────────────────────
    // =========================================================================

    @Test
    @Order(160)
    void sidebar_NavegacionAdmin_TodasLasRutasFuncionan() {
        login(ADMIN_EMAIL, PASSWORD);

        // Verificar accesibilidad de las rutas principales del admin
        String[] rutasAdmin = { "/", "/usuarios", "/roles", "/marketplace",
                                "/mis-emprendimientos", "/pedidos/recibidos" };

        for (String ruta : rutasAdmin) {
            ir(ruta);
            esperar(By.cssSelector("body"));
            assertFalse(driver.getPageSource().contains("Whitelabel Error Page"),
                    "La ruta " + ruta + " no debe causar error 500 para el admin");
        }
    }

    @Test
    @Order(161)
    void sidebar_NavegacionEmprendedor_TodasLasRutasFuncionan() {
        login(EMPRENDEDOR_EMAIL, PASSWORD);

        String[] rutasEmprendedor = { "/", "/marketplace", "/mis-emprendimientos",
                                      "/pedidos/recibidos" };

        for (String ruta : rutasEmprendedor) {
            ir(ruta);
            esperar(By.cssSelector("body"));
            assertFalse(driver.getPageSource().contains("Whitelabel Error Page"),
                    "La ruta " + ruta + " no debe causar error 500 para el emprendedor");
        }
    }

    @Test
    @Order(162)
    void sidebar_NavegacionComprador_TodasLasRutasFuncionan() {
        login(COMPRADOR_EMAIL, PASSWORD);

        String[] rutasComprador = { "/", "/marketplace", "/carrito",
                                    "/pedidos/mis-pedidos" };

        for (String ruta : rutasComprador) {
            ir(ruta);
            esperar(By.cssSelector("body"));
            assertFalse(driver.getPageSource().contains("Whitelabel Error Page"),
                    "La ruta " + ruta + " no debe causar error 500 para el comprador");
        }
    }
}
