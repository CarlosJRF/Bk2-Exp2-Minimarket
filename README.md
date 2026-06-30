Minimarket Plus - Backend API REST 🛒🛡️

Este proyecto es el sistema backend para la gestión de "Minimarket Plus". Está desarrollado con Spring Boot y se enfoca fuertemente en la seguridad de los accesos, la integridad de los datos y la calidad del código, garantizada mediante una suite exhaustiva de pruebas unitarias con cobertura superior al 70%.

🚀 Tecnologías y Herramientas

Java 21

Spring Boot 3 (Web, Data JPA)

Spring Security (Autenticación y Autorización basada en Roles)

Jakarta Bean Validation (Integridad de datos)

JUnit 5 & Mockito (Pruebas Unitarias)

JaCoCo (Análisis de Cobertura de Código)

Docker (Contenerización del entorno de pruebas)

Maven (Gestión de dependencias)

🏗️ Estructura del Proyecto

El código sigue una arquitectura de capas estándar para microservicios, promoviendo la separación de responsabilidades:

com.minimarket.entity: Modelos de base de datos (Producto, Venta, Usuario, etc.) blindados con Jakarta Validation (@NotNull, @Min, @NotBlank) para evitar el ingreso de datos corruptos.

com.minimarket.repository: Interfaces de Spring Data JPA para la persistencia.

com.minimarket.service: Contiene la lógica de negocio pura. Maneja las excepciones lógicas y transacciones.

com.minimarket.controller: Capa REST expuesta. Protegida con @PreAuthorize para restringir accesos según los roles (ADMIN, CAJERO, CLIENTE).

com.minimarket.security: Configuraciones de Spring Security, filtros y el AuthenticationManager para el manejo de credenciales (AuthController).

🧪 Arquitectura de Pruebas Unitarias (Testing Suite)

La calidad del sistema está respaldada por una suite de pruebas de múltiples niveles:

Pruebas de Entidades (EntidadesValidationTest): Validamos que las restricciones de negocio de Jakarta se disparen correctamente (ej. evitar precios negativos o carritos vacíos).

Pruebas de Servicios (@ExtendWith(MockitoExtension.class)): Validamos la lógica interna y el comportamiento de las clases de servicio, simulando la base de datos (Mocks) para pruebas ultrarrápidas y sin estado.

Pruebas de Controladores (@WebMvcTest): Evaluamos las respuestas HTTP, el manejo de errores (404 Not Found) y los filtros de seguridad (401 Unauthorized / 403 Forbidden) inyectando roles dinámicos con @WithMockUser.

🐳 Ejecución de Pruebas y Reportes con Docker (Recomendado)

El proyecto incluye un Dockerfile optimizado que permite ejecutar toda la suite de pruebas en cualquier computadora sin necesidad de instalar Java o Maven.

Importante: Para no perder el reporte HTML de JaCoCo al destruirse el contenedor, es obligatorio ejecutar la imagen mapeando un volumen local hacia la carpeta /app/target.

Paso 1: Construir la imagen de Docker

Abre tu terminal en la raíz del proyecto (donde está este archivo) y ejecuta:

docker build -t minimarket-tests .


Paso 2: Ejecutar los tests extrayendo los reportes

Dependiendo de tu sistema operativo, usa el siguiente comando:

En Linux, macOS o Git Bash (Windows):

docker run --rm -v $(pwd)/target:/app/target minimarket-tests


En PowerShell (Windows):

docker run --rm -v ${PWD}/target:/app/target minimarket-tests


Paso 3: Revisar los resultados

Una vez que el contenedor termine y muestre el BUILD SUCCESS, abre el siguiente archivo en tu navegador web para ver el análisis de cobertura interactivo:
👉 target/site/jacoco/index.html

💻 Ejecución Local (Sin Docker)

Si tienes Java 21 y Maven instalados en tu entorno local, puedes compilar, testear y generar el reporte directamente con:

mvn clean test jacoco:report


📝 Próximas Mejoras (Roadmap)

Implementación de filtros para manejo de sesión Stateless con JWT (JSON Web Tokens).

Centralización de excepciones globales mediante @ControllerAdvice.