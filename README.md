# IcesiEmprende 🚀

**Plataforma de Gestión de Emprendimientos Universitarios — Universidad Icesi**

Sistema web para que estudiantes registren emprendimientos, publiquen productos, gestionen usuarios con roles y permisos, y accedan a métricas de desempeño.

---

## Tecnologías

- Java 17
- Spring Boot 3.x
- Spring Security 6
- Spring Data JPA + Hibernate
- Thymeleaf 3
- H2 (base de datos en memoria)
- Bootstrap 5 + Bootstrap Icons
- JUnit 5 + Mockito
- Maven

---

## Requisitos previos

| Herramienta | Versión mínima |
|---|---|
| Java JDK | 17 |
| Maven | incluido (Maven Wrapper) |
| Navegador | Chrome, Firefox, Edge |
| IDE (opcional) | IntelliJ IDEA |

---

## Clonar el repositorio

```bash
git clone <url-del-repositorio>
cd proyecto-final-mergemasters
```

---

## Ejecutar la aplicación

### Opción 1 — Terminal (Windows)

```bash
mvnw.cmd spring-boot:run
```

### Opción 2 — Terminal (Linux / Mac)

```bash
./mvnw spring-boot:run
```

### Opción 3 — IntelliJ IDEA

1. Abrir el proyecto en IntelliJ IDEA
2. Esperar a que Maven descargue las dependencias
3. Ubicar la clase `EmprendimientosApplication.java`
4. Click en el botón ▶ **Run**

---

## Acceder a la aplicación

Una vez iniciada, abrir en el navegador:

```
http://localhost:8081
```

Serás redirigido automáticamente al login.

---

## Credenciales de prueba

| Usuario | Correo | Contraseña | Rol |
|---|---|---|---|
| Ximena Gomez | `ximena@icesi.edu.co` | `1234` | **ADMIN** — acceso completo |
| Carlos Perez | `carlos@icesi.edu.co` | `1234` | **CLIENTE** — acceso limitado |

> El usuario ADMIN puede ver y gestionar Estudiantes, Roles y Permisos.
> El usuario CLIENTE no tiene acceso a la sección de Administración.

---

## Funcionalidades disponibles

### Autenticación
- Iniciar sesión con correo institucional y contraseña
- Cerrar sesión

### Estudiantes (requiere login)
- Listar estudiantes registrados con métricas de resumen
- Registrar nuevo estudiante
- Asignar roles a un estudiante
- Eliminar estudiante

### Roles (requiere rol ADMIN)
- Listar roles del sistema
- Crear nuevo rol
- Asignar permisos a un rol
- Eliminar rol

### Productos (requiere login)
- Listar productos y servicios del catálogo
- Publicar nuevo producto (requiere permiso `CREAR_PRODUCTO`)
- Eliminar producto (requiere permiso `ELIMINAR_PRODUCTO`)

---

## Base de datos H2

La aplicación usa H2, una base de datos en memoria que se crea automáticamente al iniciar y se pre-carga con datos de prueba desde `data.sql`.

Para inspeccionar las tablas y datos:

1. Con la aplicación corriendo, ir a:
   ```
   http://localhost:8081/h2-console
   ```

2. Ingresar las siguientes credenciales:

   | Campo | Valor |
      |---|---|
   | JDBC URL | `jdbc:h2:mem:testdb` |
   | User Name | `sa` |
   | Password | *(dejar vacío)* |

3. Click en **Connect**

> ⚠️ La base de datos se reinicia cada vez que se reinicia la aplicación. Todos los datos creados durante la sesión se perderán.

---

## Ejecutar pruebas unitarias

### Terminal (Windows)

```bash
mvnw.cmd test
```

### Terminal (Linux / Mac)

```bash
./mvnw test
```

### IntelliJ IDEA

1. Click derecho sobre la carpeta `src/test/java`
2. Seleccionar **Run 'All Tests'**

Las pruebas cubren los servicios principales:

| Clase de test | Servicio cubierto |
|---|---|
| `UsuarioServiceTest` | Crear, listar, buscar, actualizar, eliminar, asignar/quitar rol |
| `RolServiceTest` | Crear, listar, buscar, actualizar, eliminar, asignar/quitar permiso |
| `PermissionServiceTest` | CRUD de permisos |
| `ProductoServiceTest` | CRUD de productos |
| `EmprendimientoServiceTest` | CRUD de emprendimientos |
| `CategoriaServiceTest` | CRUD de categorías |
| `EstadoServiceTest` | CRUD de estados |
| `PedidoServiceTest` | CRUD de pedidos |
| `DetallePedidoServiceTest` | CRUD de detalles de pedido |
| `ImagenProductoServiceTest` | CRUD de imágenes |

---

## Reporte de cobertura (JaCoCo)

Para generar y visualizar el reporte de cobertura:

### 1. Generar el reporte

```bash
mvnw.cmd clean test
```

### 2. Abrir el reporte en el navegador

```
target/site/jacoco/index.html
```

El reporte muestra:

- **Instructions** — porcentaje de instrucciones ejecutadas por los tests
- **Branches** — cobertura de estructuras condicionales (`if`, `switch`, etc.)
- **Methods** — métodos cubiertos
- **Lines** — líneas de código ejecutadas

---

## Estructura del proyecto

```
src/
├── main/
│   ├── java/edu/icesi/emprendimientos/
│   │   ├── controller/       # Controladores MVC
│   │   ├── entity/           # Entidades JPA
│   │   ├── repository/       # Repositorios Spring Data
│   │   ├── security/         # Configuración Spring Security
│   │   └── service/          # Servicios e implementaciones
│   └── resources/
│       ├── templates/        # Vistas Thymeleaf
│       │   ├── auth/         # Login
│       │   ├── usuarios/     # Vistas de estudiantes
│       │   ├── roles/        # Vistas de roles
│       │   ├── productos/    # Vistas de productos
│       │   └── layout.html   # Layout base
│       ├── application.properties
│       └── data.sql          # Datos iniciales
└── test/
    └── java/edu/icesi/emprendimientos/unit/
        └── *ServiceTest.java # Pruebas unitarias
```

---

## Notas importantes

- La aplicación corre en el puerto **8081** (no el 8080 por defecto).
- Al registrar un nuevo usuario, la contraseña se encripta automáticamente con **BCrypt**.
- Los datos del `data.sql` incluyen usuarios de prueba con contraseñas ya hasheadas.
- Los IDs de los datos seed empiezan en valores bajos (1, 2, 3...), los nuevos registros creados desde la app empezarán desde el **100** para evitar conflictos.

---

## Autores

Proyecto desarrollado por el equipo **MergeMasters** — Universidad Icesi
Computación en Internet II · 2026-1