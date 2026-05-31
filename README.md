# Emprende ICESI

**Plataforma de emprendimientos universitarios — Universidad Icesi**

Sistema web completo que conecta estudiantes emprendedores con compradores dentro de la comunidad universitaria. Permite publicar productos, gestionar pedidos, calificar emprendimientos y administrar usuarios con roles diferenciados.

---

## Tabla de contenidos

- [Tecnologias](#tecnologias)
- [Arquitectura](#arquitectura)
- [Requisitos previos](#requisitos-previos)
- [Ejecucion en desarrollo](#ejecucion-en-desarrollo)
- [Despliegue con Docker](#despliegue-con-docker)
- [Despliegue con Tomcat](#despliegue-con-tomcat)
- [Usuarios de prueba](#usuarios-de-prueba)
- [Guia de uso de la aplicacion](#guia-de-uso-de-la-aplicacion)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Pruebas unitarias](#pruebas-unitarias)
- [Autores](#autores)

---

## Tecnologias

### Backend
| Tecnologia | Version |
|---|---|
| Java | 17 |
| Spring Boot | 3.5 |
| Spring Security + JWT | 6.x |
| Spring Data JPA + Hibernate | 6.x |
| Spring WebSocket + STOMP | 6.x |
| PostgreSQL (Docker) / H2 (desarrollo) | 15 / 2.x |
| OpenPDF | 1.3.30 |
| MapStruct | 1.5 |
| JUnit 5 + Mockito | 5.x |
| Maven | 3.9 |

### Frontend
| Tecnologia | Version |
|---|---|
| React | 19 |
| TypeScript | 6 |
| Vite | 8 |
| Redux Toolkit + Redux Persist | 2.x |
| React Router DOM | 7 |
| Axios | 1.x |
| Tailwind CSS | 4 |
| STOMP.js + SockJS | 7.x |

### Infraestructura
| Herramienta | Uso |
|---|---|
| Docker + Docker Compose | Contenedores de DB, backend y frontend |
| Nginx | Servidor del frontend en produccion |
| Apache Tomcat 10 | Servidor alternativo para el WAR |

---

## Arquitectura

El proyecto sigue una arquitectura de tres capas desacopladas:

```
Navegador (React SPA)
        |
        | HTTP / WebSocket
        v
   API REST (Spring Boot :8081)
        |
        | JPA / Hibernate
        v
   Base de datos (H2 en memoria / PostgreSQL)
```

El frontend es una Single Page Application (SPA) que se comunica exclusivamente con el backend a traves de una API REST protegida con JWT. La navegacion entre vistas ocurre en el cliente sin recargas de pagina. Las notificaciones en tiempo real se entregan via WebSocket con el protocolo STOMP.

En produccion con Docker, Nginx sirve el frontend compilado y actua como proxy inverso hacia el backend y el WebSocket.

---

## Requisitos previos

### Para desarrollo local
| Herramienta | Version minima |
|---|---|
| Java JDK | 17 |
| Node.js | 18 |
| npm | 9 |
| Maven | incluido via Maven Wrapper |

### Para Docker
| Herramienta | Version minima |
|---|---|
| Docker Desktop | 24 |
| Docker Compose | 2.x |

---

## Ejecucion en desarrollo

### 1. Clonar el repositorio

```bash
git clone https://github.com/Computacion-2/proyecto-final-mergemasters.git
cd proyecto-final-mergemasters
```

### 2. Iniciar el backend

```bash
# Windows
./mvnw spring-boot:run

# Linux / Mac
./mvnw spring-boot:run
```

El backend arranca en `http://localhost:8081`. Al iniciar, Hibernate crea automaticamente todas las tablas y el archivo `data.sql` inserta los datos de prueba.

### 3. Iniciar el frontend

```bash
cd frontend
npm install
npm run dev
```

El frontend arranca en `http://localhost:5173` y se conecta automaticamente al backend en el puerto 8081.

### 4. Abrir la aplicacion

```
http://localhost:5173
```

---

## Despliegue con Docker

Este metodo levanta los tres servicios (base de datos PostgreSQL, backend y frontend) con un solo comando.

### Requisitos
- Docker Desktop abierto y con el motor corriendo.

### Comandos

```bash
# Primera vez o cuando hay cambios en el codigo
docker-compose up --build

# Veces posteriores (ya compilado)
docker-compose up -d

# Detener todos los contenedores
docker-compose down

# Detener y eliminar volumenes (resetea la base de datos)
docker-compose down -v
```

### Acceso

Una vez levantados los contenedores, abrir:

```
http://localhost
```

### Servicios expuestos

| Servicio | Puerto | Descripcion |
|---|---|---|
| Frontend (Nginx) | 80 | Interfaz de usuario |
| Backend (Spring Boot) | 8081 | API REST |
| Base de datos (PostgreSQL) | 5432 | Solo acceso interno |

---

## Despliegue con Tomcat

La aplicacion fue desplegada en el equipo **205m03** de la Universidad Icesi usando Apache Tomcat 10.

### Acceder a la aplicacion desplegada

```
http://10.147.20.63:8080/emprendimientos-0.0.1-SNAPSHOT/
```

### Iniciar Tomcat en el servidor (si no esta corriendo)

Conectarse al servidor por SSH:

```bash
ssh swarch@10.147.20.63
```

Iniciar Tomcat:

```bash
~/tomcat/bin/startup.sh
```

Detener Tomcat:

```bash
~/tomcat/bin/shutdown.sh
```

---

## Usuarios de prueba

Todos los usuarios tienen la contrasena `1234`.

| Nombre | Correo | Rol(es) | Descripcion |
|---|---|---|---|
| Ximena Gomez | ximena@icesi.edu.co | ADMIN, EMPRENDEDOR | Acceso completo a todas las funcionalidades |
| Carlos Perez | carlos@icesi.edu.co | EMPRENDEDOR | Gestiona sus emprendimientos y productos |
| Ana Martinez | ana@icesi.edu.co | EMPRENDEDOR | Gestiona sus emprendimientos y productos |
| Sofia Ramirez | sofia@icesi.edu.co | EMPRENDEDOR | Gestiona sus emprendimientos y productos |
| Miguel Torres | miguel@icesi.edu.co | EMPRENDEDOR, COMPRADOR | Puede vender y comprar |
| Juan Restrepo | juan@icesi.edu.co | COMPRADOR | Realiza pedidos y califica emprendimientos |
| Laura Sanchez | laura@icesi.edu.co | COMPRADOR | Realiza pedidos y califica emprendimientos |
| David Gomez | david@icesi.edu.co | COMPRADOR | Realiza pedidos y califica emprendimientos |

---

## Guia de uso de la aplicacion

### Inicio de sesion

Al ingresar a la aplicacion se muestra la pantalla de login. Ingresar el correo institucional y la contrasena. El sistema redirige automaticamente al panel principal segun el rol del usuario.

Para registrarse como nuevo usuario, hacer clic en "Registrate aqui" en la pantalla de login.

---

### Panel principal

El menu lateral izquierdo muestra las opciones disponibles segun el rol del usuario autenticado. Las secciones son:

- **Principal**: Inicio con emprendimientos destacados
- **Marketplace**: Catalogo de productos
- **Mi Negocio**: Solo visible para EMPRENDEDOR y ADMIN
- **Administracion**: Solo visible para ADMIN

---

### Marketplace

El Marketplace muestra todos los productos activos de todos los emprendimientos en una cuadricula.

- **Buscar**: Escribir en la barra de busqueda filtra por nombre de producto, nombre del emprendimiento o descripcion.
- **Filtrar por categoria**: Seleccionar una categoria en el menu desplegable para ver solo los productos de esa categoria.
- **Ver fotos**: Hacer clic en la imagen de un producto abre una galeria con todas las fotos del producto. Se puede navegar con las flechas o con las teclas de direccion del teclado.
- **Agregar al carrito**: El boton "Agregar al carrito" esta disponible para todos los usuarios autenticados.
- **Ver emprendimiento**: Hacer clic en el nombre del vendedor dentro de la tarjeta del producto lleva a la pagina del emprendimiento.

---

### Carrito de compras

Accesible desde "Mi Carrito" en el menu lateral.

- Muestra todos los productos agregados, agrupados por emprendimiento.
- Se puede modificar la cantidad de cada producto o eliminarlo del carrito.
- Si hay productos de varios emprendimientos, se informa que se crearan pedidos separados (uno por emprendimiento).
- Al confirmar la compra, se generan los pedidos y el carrito se vacia.

---

### Mis Compras

Accesible desde "Mis Compras" en el menu lateral. Muestra todos los pedidos realizados por el usuario.

- Cada pedido muestra el estado actual: PENDIENTE, CONFIRMADO, PREPARANDO, ENTREGADO o CANCELADO.
- Los pedidos con estado ENTREGADO muestran el boton "Calificar emprendimiento".
- Al hacer clic en calificar, se abre un modal donde se selecciona una puntuacion de 1 a 5 estrellas y se puede escribir un comentario opcional.
- Una vez calificado, el pedido muestra el indicador "Ya calificaste este pedido".

---

### Detalle de emprendimiento

Al hacer clic en el nombre de un emprendimiento (desde el Marketplace o desde Mi Negocio), se muestra:

- Logo, nombre, categoria, descripcion, semestre y estado del emprendimiento.
- Cuadricula con todos los productos activos del emprendimiento.
- Seccion "Resenas y calificaciones" al final de la pagina con el promedio de estrellas y todos los comentarios de compradores.

---

### Mi Negocio (EMPRENDEDOR)

#### Mis Emprendimientos

Lista todos los emprendimientos del usuario. Desde aqui se puede:

- Crear un nuevo emprendimiento con nombre, descripcion, categoria y logo.
- Editar un emprendimiento existente.
- Acceder a la gestion de productos de cada emprendimiento.

#### Productos de un emprendimiento

- Lista todos los productos del emprendimiento con su estado, precio y stock.
- Crear nuevo producto con nombre, descripcion, precio, stock e imagenes.
- Editar o activar/desactivar productos existentes.
- Subir multiples imagenes por producto.

#### Pedidos Recibidos

Muestra todos los pedidos que han hecho los compradores a los emprendimientos del usuario.

- Cada pedido muestra el comprador, los productos, el total y el estado actual.
- El emprendedor puede cambiar el estado del pedido a traves de los botones de accion: CONFIRMAR, PREPARANDO, ENTREGADO o CANCELAR.

#### Metricas

Panel con estadisticas de rendimiento de los emprendimientos:

- Total de pedidos, ingresos totales, unidades vendidas y ticket promedio.
- Grafica de barras con los productos mas vendidos.
- Grafica de dona con la distribucion de pedidos por estado.
- Tabla detallada de ventas por producto con porcentaje de participacion.
- Boton "Exportar PDF" que descarga un reporte completo en formato PDF con todas las metricas.

#### Notificaciones en tiempo real

El icono de campana en el menu lateral muestra las notificaciones de nuevos pedidos en tiempo real via WebSocket. El contador rojo indica la cantidad de notificaciones no leidas. Al hacer clic se despliega el panel con el historial de notificaciones y la opcion de marcarlas todas como leidas.

---

### Administracion (ADMIN)

#### Usuarios

Panel completo de gestion de usuarios con las siguientes funcionalidades:

- Lista todos los usuarios registrados con su foto de perfil, nombre, correo, programa academico y roles actuales.
- Panel de estadisticas en la parte superior con el conteo por tipo de rol.
- Los roles de cada usuario se muestran como etiquetas de colores. Hacer clic en la "x" de un rol lo elimina del usuario inmediatamente.
- El boton "+" al lado de los roles abre un menu desplegable para asignar un rol adicional al usuario.
- Boton de eliminar usuario con confirmacion.

#### Categorias

- Listar, crear, editar y eliminar categorias de emprendimientos.

#### Emprendimientos

- Listar todos los emprendimientos del sistema con sus datos y estado.
- Cambiar el estado de cualquier emprendimiento (ACTIVO / INACTIVO).

---

### Perfil de usuario

Accesible desde "Mi Perfil" en la parte inferior del menu lateral.

- Muestra los datos del usuario: nombre, correo, programa y semestre academico.
- Permite editar el nombre, programa y semestre.
- Permite subir o cambiar la foto de perfil. La nueva foto aparece inmediatamente en el icono del menu lateral.

---

## Estructura del proyecto

```
proyecto-final-mergemasters/
|
|-- src/
|   |-- main/
|   |   |-- java/edu/icesi/emprendimientos/
|   |   |   |-- config/           # Configuracion CORS y WebSocket
|   |   |   |-- controller/       # Controladores MVC (Thymeleaf)
|   |   |   |-- entity/           # Entidades JPA
|   |   |   |-- repository/       # Repositorios Spring Data
|   |   |   |-- rest/
|   |   |   |   |-- controller/   # Controladores REST API
|   |   |   |   |-- dto/          # DTOs de request y response
|   |   |   |   |-- mapper/       # Mappers MapStruct
|   |   |   |   |-- security/     # Filtro JWT y servicio JWT
|   |   |   |-- security/         # Configuracion Spring Security
|   |   |   |-- service/          # Interfaces e implementaciones de servicio
|   |   |-- resources/
|   |       |-- application.properties          # Configuracion desarrollo (H2)
|   |       |-- application-docker.properties   # Configuracion Docker (PostgreSQL)
|   |       |-- data.sql                        # Datos iniciales para H2
|   |       |-- data-postgres.sql               # Datos iniciales para PostgreSQL
|   |-- test/
|       |-- java/edu/icesi/emprendimientos/unit/
|           |-- *ServiceTest.java               # Pruebas unitarias por servicio
|
|-- frontend/
|   |-- src/
|   |   |-- api/          # Clientes Axios por recurso
|   |   |-- components/   # Componentes reutilizables
|   |   |-- hooks/        # Hooks personalizados (useAuth, useWebSocket)
|   |   |-- pages/        # Componentes de pagina por ruta
|   |   |-- store/        # Redux slices (auth, cart, notificaciones)
|   |   |-- types/        # Tipos e interfaces TypeScript
|   |-- Dockerfile        # Build de produccion con Nginx
|   |-- nginx.conf        # Configuracion Nginx con proxy al backend
|
|-- Dockerfile            # Build del backend Spring Boot
|-- docker-compose.yml    # Orquestacion de los tres servicios
|-- pom.xml
```

---

## Pruebas unitarias

Las pruebas cubren la capa de servicios del backend usando JUnit 5 y Mockito.

### Ejecutar pruebas

```bash
# Windows
./mvnw test

# Linux / Mac
./mvnw test
```

### Cobertura con JaCoCo

```bash
./mvnw clean test
```

El reporte de cobertura se genera en:
```
target/site/jacoco/index.html
```

### Clases de prueba

| Clase | Servicio cubierto |
|---|---|
| UsuarioServiceTest | Crear, listar, buscar, actualizar, eliminar, asignar y quitar rol |
| RolServiceTest | Crear, listar, buscar, actualizar, eliminar, asignar y quitar permiso |
| PermissionServiceTest | CRUD de permisos |
| ProductoServiceTest | CRUD de productos |
| EmprendimientoServiceTest | CRUD de emprendimientos |
| CategoriaServiceTest | CRUD de categorias |
| EstadoServiceTest | CRUD de estados |
| PedidoServiceTest | Crear y listar pedidos |
| DetallePedidoServiceTest | CRUD de detalles de pedido |
| ImagenProductoServiceTest | CRUD de imagenes de producto |
| CalificacionServiceTest | Listar, promediar y verificar calificaciones |

---

## Autores

Proyecto desarrollado por el equipo **MergeMasters** compuesto por Ximena Gomez y Natalia Delgado

Universidad Icesi — Computacion en Internet II — 2026-1
