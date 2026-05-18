a# 🧠 ms-restricciones | Sistema de Gestión de Canchas Deportivas Municipales

Este microservicio actúa como el **motor de reglas de negocio y control perimetral** dentro del ecosistema distribuido. Su responsabilidad principal es centralizar la lógica de negocio orientada a las políticas públicas del municipio, evaluando si un usuario cumple con los requisitos mínimos para efectuar un arriendo deportivo y determinando la aplicación de incentivos financieros dinámicos.

---

## 🛠️ Stack Tecnológico Core
* **Lenguaje:** Java 21 LTS
* **Framework:** Spring Boot 4.0.6 (o 3.x estable según entorno local)
* **Gestor de Dependencias:** Maven
* **Persistencia:** Spring Data JPA / Hibernate 7.x
* **Base de Datos:** MySQL (Puerto de red 3307 gestionado vía Docker)
* **Librerías Adicionales:** Spring Boot Starter Validation, Spring Reactive Web (WebClient), Lombok

---

## 📂 Árbol de Directorios (Simetría ms-pagos)
Siguiendo los lineamientos de la arquitectura maestro-alumno establecidos en el proyecto, el código se divide en las siguientes capas de alta cohesión:

```text
ms-restricciones/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── cl/
│   │   │       └── municipalidad/
│   │   │           └── restricciones/
│   │   │               ├── client/
│   │   │               │   └── AuthClient.java
│   │   │               ├── config/
│   │   │               │   └── WebClientConfig.java
│   │   │               ├── controller/
│   │   │               │   └── RestriccionController.java
│   │   │               ├── dto/
│   │   │               │   ├── request/
│   │   │               │   │   └── DtoValidarRestriccionRequest.java
│   │   │               │   └── response/
│   │   │               │       └── DtoValidarRestriccionResponse.java
│   │   │               ├── exception/
│   │   │               │   └── GlobalExceptionHandler.java
│   │   │               ├── model/
│   │   │               │   └── RestriccionModel.java
│   │   │               ├── repository/
│   │   │               │   └── RestriccionRepository.java
│   │   │               ├── service/
│   │   │               │   └── RestriccionService.java
│   │   │               └── RestriccionesApplication.java
│   │   └── resources/
│   │       └── application.properties
└── pom.xml
🚀 Flujo de Operación e Integración de Red
Este microservicio corre exclusivamente en el puerto 8085 y expone una interfaz de comunicación de tipo RPC/REST para ser consumida por el módulo transaccional de reservas.

Evaluar Reglas de Usuario e Incentivos
URL: POST http://localhost:8085/api/v1/restricciones/validar

Headers: Content-Type: application/json

Cuerpo de la Petición (JSON Request):

JSON
{
    "idUsuario": 1,
    "fechaReserva": "2026-05-20"
}
Cuerpo de la Respuesta Exitosa (JSON Response - 200 OK):

JSON
{
    "aprobado": true,
    "aplicaDescuento": false,
    "mensaje": "Usuario habilitado de forma exitosa. Tarifa normal (No es residente verificado)."
}
🛡️ Manejo de Excepciones y Filtros
El módulo cuenta con un interceptor perimetral @RestControllerAdvice. En caso de enviar un payload corrupto (por ejemplo, un idUsuario menor o igual a cero), la capa de validación abortará la transacción devolviendo un código de error de tipo 400 Bad Request formateado para su lectura en los logs de integración.