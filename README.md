# Restaurant Order Manager

Proyecto académico de escritorio para gestionar pedidos de restaurante: registro de clientes, direcciones, productos, pedidos, cambios de estado, despacho y confirmación de entrega.

## Estado del proyecto

Este repositorio está configurado como una aplicación Java de escritorio con JavaFX, JPA/Hibernate y MySQL.

Importante para el entorno de trabajo actual:

```text
- No usa Spring Boot.
- No usa Spring Data JPA.
- No usa Gradle.
- Usa Maven como gestor del proyecto.
- Usa Java 21.
```

La persistencia se maneja con Jakarta Persistence + Hibernate mediante `EntityManager` y `persistence.xml`.

## Tecnologías verificadas

Tomado de `pom.xml` y de la estructura actual del proyecto:

```text
Java 21
Maven
JavaFX 21.0.6
Jakarta Persistence 3.1.0
Hibernate ORM 6.6.3.Final
Jakarta Validation API 3.1.0
MySQL Connector/J 9.1.0
Lombok 1.18.46
JUnit Jupiter API 5.10.2
```

## Requisitos para ejecutar

```text
JDK 21
Maven 3.9 o superior
MySQL 8 o compatible
IDE recomendado: NetBeans, IntelliJ IDEA o VS Code con soporte Java/Maven
```

Si se usa Lombok desde el IDE, hay que activar annotation processing. Si no, el proyecto puede compilar por Maven pero el IDE puede marcar falsos errores.

## Configuración de base de datos

La conexión está definida en:

```text
src/main/resources/META-INF/persistence.xml
```

Configuración actual:

```text
URL:      jdbc:mysql://localhost:3307/first_bimester_project
Usuario:  root
Password: root
```

Antes de ejecutar, crear la base de datos:

```sql
CREATE DATABASE first_bimester_project;
```

Si tu MySQL usa otro puerto, usuario o contraseña, actualizá `persistence.xml`.

## Cómo ejecutar

Desde la raíz del proyecto:

```bash
mvn javafx:run
```

También está configurado `exec-maven-plugin` para levantar:

```text
first.bimester.presentation.javafx.JavaFxLauncher
```

Comando alternativo:

```bash
mvn exec:java
```

## Estructura principal

```text
src/main/java/first/bimester
├── Main.java
├── demo
│   └── JpaInsertDemo.java
├── domain
│   ├── entity
│   └── enums
├── repository
│   └── CustomerRepository.java
└── presentation
    └── javafx
        ├── JavaFxApplication.java
        ├── JavaFxLauncher.java
        ├── StageManager.java
        ├── ViewLoader.java
        └── controller
            └── MainController.java
```

Recursos JavaFX y configuración JPA:

```text
src/main/resources
├── META-INF/persistence.xml
└── first/bimester/presentation/javafx
    ├── style/application.css
    └── view/main-view.fxml
```

## Arquitectura del proyecto

La arquitectura esperada se organiza por capas simples:

```text
Presentation Layer  -> JavaFX
Service Layer       -> reglas de negocio
Repository Layer    -> acceso a datos con EntityManager
Domain Layer        -> entidades JPA y enums
Database Layer      -> MySQL
```

Regla de oro:

```text
La presentación no contiene reglas de negocio.
Los repositorios no deciden reglas de negocio.
Los servicios coordinan reglas, validaciones y transacciones.
```

En este entorno sin Spring Boot, las dependencias se crean de forma explícita. Si una pantalla necesita un servicio, el proyecto debe construirlo manualmente o mediante una clase bootstrap propia, no mediante `@Autowired` ni `ApplicationContext`.

## Modelo de negocio

El sistema modela el flujo de un pedido de restaurante.

Estados operativos:

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
ADMINISTRATOR: registra clientes, direcciones, productos y pedidos.
COOK: mueve pedidos de PENDING a IN_PREPARATION y luego a READY.
COURIER: mueve pedidos de READY a ON_THE_WAY y luego a DELIVERED.
CUSTOMER: consulta seguimiento y confirma recepción; no cambia estados operativos.
```

La confirmación del cliente no crea un nuevo estado. Se registra como dato de entrega en `deliveries.customer_confirmed_at`.

## Modelo relacional de referencia

Tablas principales del modelo:

```text
roles
staff
customers
customer_addresses
products
order_statuses
order_status_transition_rules
customer_orders
order_items
order_status_histories
deliveries
```

Relaciones clave:

```text
staff.role_id -> roles.role_id
customer_addresses.customer_id -> customers.customer_id
customer_orders.customer_id -> customers.customer_id
customer_orders.registered_by_staff_id -> staff.staff_id
customer_orders.current_status_id -> order_statuses.status_id
customer_orders.delivery_address_id -> customer_addresses.address_id
order_items.order_id -> customer_orders.order_id
order_items.product_id -> products.product_id
order_status_histories.order_id -> customer_orders.order_id
deliveries.order_id -> customer_orders.order_id
deliveries.courier_staff_id -> staff.staff_id
```

## Assets incluidos

```text
assets/ER Diagram.png              -> imagen del diagrama entidad-relación
assets/ER Diagram.puml             -> fuente PlantUML del diagrama ER
assets/ROM - ER Diagram.sql        -> script SQL de referencia
assets/ROM - Test Data.sql         -> datos de prueba
assets/Tema del Proyecto Primer Bimestre.pdf -> enunciado académico
```

## Documentación por capa

```text
docs/1-domain.md       -> entidades y enums del dominio
docs/2-repository.md   -> repositorios con EntityManager, sin Spring Data
docs/3-service.md      -> reglas de negocio esperadas
docs/4-dto.md          -> objetos de entrada/salida esperados
docs/5-mapper.md       -> conversión entre entidades y DTOs
docs/6-exception.md    -> excepciones de negocio esperadas
docs/7-presentation.md -> presentación JavaFX sin Spring Boot
```

## Convenciones importantes

```text
- Mantener Java 21 para que todos trabajen con el mismo JDK.
- Usar Maven; no documentar comandos de Gradle.
- No agregar Spring Boot salvo que el profesor cambie explícitamente el requerimiento.
- Usar EntityManager en repositories.
- Mantener las reglas de negocio fuera de los controllers JavaFX.
- Mantener las entidades alineadas con el modelo ER y el SQL de assets/.
```

## Idea clave

La dirección editable del cliente y la dirección histórica del pedido no son lo mismo:

```text
customer_addresses        -> dato vivo del cliente
delivery_address_snapshot -> evidencia histórica del pedido
```

Mezclar esos conceptos rompe trazabilidad. Por eso el pedido debe conservar el snapshot de la dirección usada al momento de crearse.
