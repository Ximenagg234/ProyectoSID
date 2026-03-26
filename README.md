# Ejecución y Pruebas del Proyecto

## Requisitos Previos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

- Java 17 o superior
- IntelliJ IDEA (recomendado)
- Maven Wrapper (incluido en el proyecto)

---

# Ejecución de la Aplicación

### Opción 1: Desde IntelliJ

1. Abrir el proyecto en IntelliJ IDEA
2. Ubicar la clase principal: EmprendimientosApplication
3. Ejecutar la aplicación (Run)

---

### Opción 2: Desde terminal

Ejecutar el siguiente comando en la raíz del proyecto:

```bash
mvnw.cmd spring-boot:run

# Base de Datos 

## Acceso a la Base de Datos (H2)

Una vez la aplicación esté en ejecución:

1. Abrir en el navegador:
   http://localhost:8081/h2-console

2. Ingresar los siguientes datos:

   - JDBC URL:
     jdbc:h2:mem:testdb

   - User:
     sa

   - Password:
     (dejar vacío)

3. Presionar **Connect** para visualizar las tablas y datos cargados.

---

## Ejecución de Pruebas Unitarias

### Opción 1: Desde IntelliJ IDEA

1. Ubicar la carpeta:
   src/test/java

2. Click derecho → **Run 'All Tests'**

---

### Opción 2: Desde terminal

Ejecutar el siguiente comando en la raíz del proyecto:

```bash
mvnw.cmd test

# Generación y Visualización del Reporte de Cobertura (JaCoCo)

### Generar reporte

Ejecutar el siguiente comando en la raíz del proyecto:

```bash
mvnw.cmd clean test

Abrir el siguiente archivo en el navegador para ver el reporte:

target/site/jacoco/index.html

El reporte incluye:

1. Instructions: porcentaje de código ejecutado
2. Branches: cobertura de estructuras condicionales (if, etc.)
3. Methods: métodos cubiertos por pruebas
