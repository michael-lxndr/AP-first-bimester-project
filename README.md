# Restaurant Order Manager

Proyecto académico de escritorio para gestionar pedidos de restaurante con Java 21, JavaFX, JPA/Hibernate, MySQL, HikariCP, pools de hilos y pruebas unitarias.

El objetivo es simular el flujo completo de un pedido: registro, preparación, despacho, entrega y consulta por código.

## Estado Actual

El proyecto quedó centralizado bajo un solo paquete raíz:

```text
com.restaurante.pedidos
```

La arquitectura se organiza por capas simples. No se usa Spring Boot; las dependencias se crean manualmente desde Java.

## Tecnologías

```text
Java 21
Maven
JavaFX 21.0.6
Jakarta Persistence 3.1.0
Hibernate ORM 6.6.3.Final
Hibernate HikariCP 6.6.3.Final
MySQL Connector/J 9.1.0
Lombok 1.18.46
JUnit Jupiter 5.10.2
```

## Estructura De Paquetes

```text
src/main/java/com/restaurante/pedidos
├── config
│   ├── DatabaseConfig.java
│   ├── SimulationConfig.java
│   └── ThreadPoolConfig.java
├── domain
│   ├── OrderStatusCode.java
│   ├── ProductCategory.java
│   ├── RoleCode.java
│   └── entity
├── repository
│   ├── CustomerRepository.java
│   ├── RoleRepository.java
│   └── StaffRepository.java
├── service
│   └── StaffService.java
├── presentation
│   ├── MainApp.java
│   ├── JavaFxApplication.java
│   ├── StageManager.java
│   ├── ViewLoader.java
│   ├── DashboardController.java
│   ├── AdminController.java
│   ├── CookController.java
│   ├── DeliveryController.java
│   └── CustomerController.java
└── util
    ├── OrderCodeGenerator.java
    └── TimeSimulator.java
```

Recursos JavaFX:

```text
src/main/resources/com/restaurante/pedidos/presentation
├── style/application.css
└── view
    ├── admin-view.fxml
    ├── cook-view.fxml
    ├── customer-view.fxml
    ├── delivery-view.fxml
    └── main-view.fxml
```

Pruebas unitarias:

```text
src/test/java/com/restaurante/pedidos
└── util
    ├── OrderCodeGeneratorTest.java
    └── TimeSimulatorTest.java
```

## Reglas De Arquitectura

```text
presentation -> service -> repository -> domain
```

Reglas obligatorias:

```text
Los controllers JavaFX no contienen reglas de negocio.
Los services coordinan reglas, validaciones, transacciones y casos de uso.
Los repositories solo hablan con EntityManager.
El domain no depende de presentation, service ni repository.
Los pools de hilos se centralizan en ThreadPoolConfig.
Los delays simulados se centralizan en SimulationConfig y TimeSimulator.
```

Esto es importante: si cada pantalla crea sus propios hilos, delays, EntityManager o reglas, el proyecto se vuelve imposible de probar. Primero la estructura, después la velocidad. Es así de fácil.

## Configuración De Base De Datos

Archivo principal:

```text
src/main/resources/META-INF/persistence.xml
```

Base esperada:

```sql
CREATE DATABASE first_bimester_project;
```

Conexión actual:

```text
URL:      jdbc:mysql://localhost:3307/first_bimester_project
Usuario:  root
Password: root
Pool:     HikariCP mediante Hibernate
```

Si tu MySQL usa otro puerto, usuario o contraseña, modificá `persistence.xml`.

## Cómo Ejecutar

Desde la raíz del proyecto:

```bash
mvn javafx:run
```

Comando alternativo:

```bash
mvn exec:java
```

Clase principal configurada:

```text
com.restaurante.pedidos.presentation.MainApp
```

## Cómo Ejecutar Tests

```bash
mvn test
```

Los tests deben ser unitarios siempre que sea posible. Si una prueba necesita MySQL real, ya no es unitaria: documentala como prueba de integración.

## Modelo De Negocio

Estados operativos del pedido:

```text
PENDING -> IN_PREPARATION -> READY -> ON_THE_WAY -> DELIVERED
```

Roles principales:

```text
ADMINISTRATOR
COOK
COURIER
CUSTOMER
```

Responsabilidades:

```text
ADMINISTRATOR: gestiona personal, productos, clientes y pedidos.
COOK: toma pedidos pendientes y los prepara.
COURIER: toma pedidos listos, los despacha y confirma entrega.
CUSTOMER: consulta el estado del pedido por código.
```

## División Del Trabajo

El proyecto se divide entre tres personas. Cada persona trabaja una capa principal y entrega pruebas de lo que toca.

```text
Persona 1: Dominio, base de datos y repositorios.
Persona 2: Servicios, reglas de negocio, state machine, simulación y pools de hilos.
Persona 3: JavaFX, DTOs de pantalla, navegación y pruebas de presentación/utilidades.
```

Guías detalladas:

```text
docs/persona-1-dominio-persistencia.md
docs/persona-2-negocio-simulacion.md
docs/persona-3-presentacion-pruebas.md
```

## Contrato Entre Capas

Persona 1 entrega repositorios con métodos claros.

Persona 2 consume repositorios desde servicios y expone métodos simples para la UI.

Persona 3 consume servicios, no repositories directamente.

Ejemplo correcto:

```text
AdminController -> StaffService -> StaffRepository -> EntityManager
```

Ejemplo incorrecto:

```text
AdminController -> EntityManager
```

## Assets Incluidos

```text
assets/ER Diagram.png
assets/ER Diagram.puml
assets/ROM - ER Diagram.sql
assets/ROM - Test Data.sql
assets/Tema del Proyecto Primer Bimestre.pdf
```

## Convenciones

```text
Usar Java 21.
Usar Maven.
No agregar Spring Boot.
No crear paquetes nuevos sin necesidad real.
No meter reglas de negocio en FXML ni controllers.
No abrir hilos manualmente desde controllers.
No duplicar delays ni configuración de tiempos.
No mezclar DTOs de pantalla con entidades JPA si la pantalla no necesita la entidad completa.
```

## Próximo Objetivo Técnico

Completar las piezas faltantes manteniendo esta estructura:

```text
OrderRepository
ProductRepository
OrderStatusRepository
TransitionRuleRepository
OrderService
OrderStateMachine
KitchenService
DeliveryService
SimulationService
DTOs para la UI
Tests unitarios por servicio
```

No hay que crear paquetes por ansiedad. Hay que crear paquetes cuando separan responsabilidades reales. CONCEPTOS primero, código después.
