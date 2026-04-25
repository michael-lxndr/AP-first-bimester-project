# Restaurant Order Manager

Proyecto académico para gestionar pedidos de restaurante desde su registro hasta la entrega y confirmación de recepción por parte del cliente.

El sistema se basa en arquitectura por capas:

```text
Presentation Layer
    ↓
Service Layer
    ↓
Repository Layer
    ↓
Database Layer
```

La idea central es simple: la presentación muestra pantallas o menús, los servicios aplican reglas de negocio, los repositorios acceden a la base de datos y las entidades representan las tablas.

## Requerimiento base

El sistema permite:

```text
- Registrar clientes.
- Registrar productos.
- Crear pedidos con uno o más productos.
- Calcular el total del pedido.
- Controlar el flujo de estados.
- Asignar responsabilidades por rol.
- Registrar historial de cambios.
- Despachar pedidos.
- Confirmar entrega por parte del repartidor.
- Confirmar recepción por parte del cliente.
- Consultar estado actual e historial por código de pedido.
```

Flujo operativo principal:

```text
PENDING → IN_PREPARATION → READY → ON_THE_WAY → DELIVERED
```

Además del estado `DELIVERED`, el sistema guarda una confirmación adicional del cliente en `Delivery.customerConfirmedAt`. Esa confirmación no cambia el estado operativo del pedido; solo registra que el cliente confirmó la recepción.

## Stack propuesto

```text
Java 25
Spring Boot 4
Gradle Kotlin DSL
Spring Data JPA
MySQL Driver
Lombok
Validation
JavaFX
```

La consola se usa como presentación inicial mientras se desarrolla la interfaz gráfica. JavaFX será la presentación final o demostrable cuando esté conectada correctamente a los servicios.

## Nombre del proyecto

```text
Project name: FirstBimesterProject
Artifact: restaurant-order-manager
Base package: first.bimester.project.restaurant
```

## Estructura del proyecto

```text
restaurant-order-manager
│
├── build.gradle.kts
├── settings.gradle.kts
│
├── src
│   ├── main
│   │   ├── java
│   │   │   └── first
│   │   │       └── bimester
│   │   │           └── project
│   │   │               └── restaurant
│   │   │                   │
│   │   │                   ├── FirstBimesterProjectApplication.java
│   │   │                   │
│   │   │                   ├── config
│   │   │                   │   ├── DataInitializer.java
│   │   │                   │   └── JavaFxConfig.java
│   │   │                   │
│   │   │                   ├── domain
│   │   │                   │   ├── entity
│   │   │                   │   │   ├── Role.java
│   │   │                   │   │   ├── Staff.java
│   │   │                   │   │   ├── Customer.java
│   │   │                   │   │   ├── Product.java
│   │   │                   │   │   ├── OrderStatus.java
│   │   │                   │   │   ├── OrderStatusTransitionRule.java
│   │   │                   │   │   ├── CustomerOrder.java
│   │   │                   │   │   ├── OrderItem.java
│   │   │                   │   │   ├── OrderStatusHistory.java
│   │   │                   │   │   └── Delivery.java
│   │   │                   │   │
│   │   │                   │   └── enums
│   │   │                   │       ├── RoleCode.java
│   │   │                   │       └── OrderStatusCode.java
│   │   │                   │
│   │   │                   ├── repository
│   │   │                   │   ├── RoleRepository.java
│   │   │                   │   ├── StaffRepository.java
│   │   │                   │   ├── CustomerRepository.java
│   │   │                   │   ├── ProductRepository.java
│   │   │                   │   ├── OrderStatusRepository.java
│   │   │                   │   ├── OrderStatusTransitionRuleRepository.java
│   │   │                   │   ├── CustomerOrderRepository.java
│   │   │                   │   ├── OrderItemRepository.java
│   │   │                   │   ├── OrderStatusHistoryRepository.java
│   │   │                   │   └── DeliveryRepository.java
│   │   │                   │
│   │   │                   ├── service
│   │   │                   │   ├── CustomerService.java
│   │   │                   │   ├── ProductService.java
│   │   │                   │   ├── StaffService.java
│   │   │                   │   ├── OrderService.java
│   │   │                   │   ├── OrderQueryService.java
│   │   │                   │   ├── DeliveryService.java
│   │   │                   │   └── StateTransitionService.java
│   │   │                   │
│   │   │                   ├── dto
│   │   │                   │   ├── request
│   │   │                   │   │   ├── CreateCustomerRequest.java
│   │   │                   │   │   ├── CreateProductRequest.java
│   │   │                   │   │   ├── CreateOrderRequest.java
│   │   │                   │   │   ├── CreateOrderItemRequest.java
│   │   │                   │   │   ├── ChangeOrderStatusRequest.java
│   │   │                   │   │   ├── DispatchOrderRequest.java
│   │   │                   │   │   ├── ConfirmDeliveryRequest.java
│   │   │                   │   │   └── ConfirmReceiptRequest.java
│   │   │                   │   │
│   │   │                   │   └── response
│   │   │                   │       ├── ProductResponse.java
│   │   │                   │       ├── CustomerResponse.java
│   │   │                   │       ├── OrderSummaryResponse.java
│   │   │                   │       ├── OrderDetailResponse.java
│   │   │                   │       ├── OrderItemResponse.java
│   │   │                   │       ├── OrderHistoryResponse.java
│   │   │                   │       └── DeliveryResponse.java
│   │   │                   │
│   │   │                   ├── mapper
│   │   │                   │   ├── CustomerMapper.java
│   │   │                   │   ├── ProductMapper.java
│   │   │                   │   ├── OrderMapper.java
│   │   │                   │   └── DeliveryMapper.java
│   │   │                   │
│   │   │                   ├── exception
│   │   │                   │   ├── BusinessException.java
│   │   │                   │   ├── ResourceNotFoundException.java
│   │   │                   │   ├── InvalidOrderStatusException.java
│   │   │                   │   ├── InvalidRoleException.java
│   │   │                   │   └── DuplicateResourceException.java
│   │   │                   │
│   │   │                   └── presentation
│   │   │                       ├── console
│   │   │                       │   ├── ConsoleApplicationRunner.java
│   │   │                       │   ├── MainMenu.java
│   │   │                       │   ├── AdminMenu.java
│   │   │                       │   ├── CookMenu.java
│   │   │                       │   ├── CourierMenu.java
│   │   │                       │   └── CustomerMenu.java
│   │   │                       │
│   │   │                       └── javafx
│   │   │                           ├── JavaFxApplication.java
│   │   │                           ├── StageManager.java
│   │   │                           ├── ViewLoader.java
│   │   │                           ├── controller
│   │   │                           │   ├── MainController.java
│   │   │                           │   ├── AdminController.java
│   │   │                           │   ├── CookController.java
│   │   │                           │   ├── CourierController.java
│   │   │                           │   ├── CustomerController.java
│   │   │                           │   ├── ProductController.java
│   │   │                           │   └── OrderTrackingController.java
│   │   │                           ├── model
│   │   │                           │   ├── ProductTableModel.java
│   │   │                           │   ├── OrderTableModel.java
│   │   │                           │   └── OrderHistoryTableModel.java
│   │   │                           └── util
│   │   │                               ├── AlertHelper.java
│   │   │                               └── FormValidator.java
│   │   │
│   │   └── resources
│   │       ├── application.yaml
│   │       └── first
│   │           └── bimester
│   │               └── project
│   │                   └── restaurant
│   │                       └── presentation
│   │                           └── javafx
│   │                               ├── view
│   │                               │   ├── main-view.fxml
│   │                               │   ├── admin-view.fxml
│   │                               │   ├── cook-view.fxml
│   │                               │   ├── courier-view.fxml
│   │                               │   ├── customer-view.fxml
│   │                               │   ├── product-view.fxml
│   │                               │   └── order-tracking-view.fxml
│   │                               └── style
│   │                                   └── application.css
│   │
│   └── test
│       └── java
│           └── first
│               └── bimester
│                   └── project
│                       └── restaurant
│                           ├── service
│                           │   ├── OrderServiceTest.java
│                           │   ├── DeliveryServiceTest.java
│                           │   └── StateTransitionServiceTest.java
│                           └── repository
│                               └── CustomerOrderRepositoryTest.java
```

## Documentación por paquete

```text
- docs/domain.md
- docs/repository.md
- docs/service.md
- docs/dto.md
- docs/mapper.md
- docs/exception.md
- docs/presentation.md
```

## Responsabilidad de cada capa

### Presentation

Muestra la aplicación al usuario.

```text
- Consola: interfaz funcional inicial.
- JavaFX: interfaz gráfica final.
```

No decide reglas de negocio. Solo recibe datos, muestra información y llama servicios.

### Service

Contiene las reglas del restaurante.

```text
- Validar roles.
- Validar flujo de estados.
- Calcular totales.
- Crear historial.
- Crear entregas.
- Confirmar entregas.
- Confirmar recepción por cliente.
```

### Repository

Accede a MySQL mediante Spring Data JPA.

```text
- Buscar entidades.
- Guardar entidades.
- Consultar pedidos por código, estado, cliente o repartidor.
```

No toma decisiones de negocio.

### Domain

Representa el modelo del sistema.

```text
- Entidades JPA.
- Enums/códigos del dominio.
- Relaciones del diagrama ER.
```

### DTO

Transporta datos entre presentación y servicios.

```text
- Request: datos que entran.
- Response: datos que salen.
```

### Mapper

Convierte entidades en respuestas.

```text
Entity → Response DTO
```

### Exception

Define errores claros de negocio.

```text
- Recurso no encontrado.
- Rol inválido.
- Estado inválido.
- Recurso duplicado.
- Error general de negocio.
```

## Flujos principales

### Crear pedido

```text
AdminMenu/AdminController
    ↓
OrderService.createOrder
    ↓
CustomerRepository
StaffRepository
ProductRepository
OrderStatusRepository
CustomerOrderRepository
OrderItemRepository
OrderStatusHistoryRepository
    ↓
MySQL
```

Pasos:

```text
1. Validar que el staff sea ADMINISTRATOR.
2. Validar cliente.
3. Validar que exista al menos un producto.
4. Validar productos disponibles.
5. Calcular subtotales.
6. Calcular total.
7. Crear CustomerOrder con estado PENDING.
8. Crear OrderItem por cada producto.
9. Crear primer OrderStatusHistory.
10. Guardar todo en una transacción.
```

### Cambiar estado del pedido

```text
CookMenu/CookController
    ↓
OrderService.changeOrderStatus
    ↓
StateTransitionService
    ↓
OrderStatusTransitionRuleRepository
```

Pasos:

```text
1. Buscar pedido por código.
2. Buscar staff que solicita el cambio.
3. Buscar estado destino.
4. Validar transición mediante OrderStatusTransitionRule.
5. Actualizar currentStatus.
6. Actualizar currentStatusChangedAt.
7. Insertar OrderStatusHistory.
8. Guardar todo en una transacción.
```

### Despachar pedido

```text
CourierMenu/CourierController
    ↓
DeliveryService.dispatchOrder
```

Pasos:

```text
1. Buscar pedido por código.
2. Verificar que esté READY.
3. Validar que el staff sea COURIER.
4. Verificar que el pedido no tenga Delivery previa.
5. Crear Delivery.
6. Registrar dispatchedAt.
7. Cambiar estado a ON_THE_WAY.
8. Registrar historial.
9. Guardar todo en una transacción.
```

### Confirmar entrega por repartidor

```text
CourierMenu/CourierController
    ↓
DeliveryService.confirmDelivery
```

Pasos:

```text
1. Buscar pedido por código.
2. Verificar que esté ON_THE_WAY.
3. Buscar Delivery del pedido.
4. Validar receiverName.
5. Registrar deliveredAt.
6. Registrar receiverName.
7. Cambiar estado a DELIVERED.
8. Registrar historial.
9. Guardar todo en una transacción.
```

### Confirmar recepción por cliente

```text
CustomerMenu/CustomerController
    ↓
DeliveryService.confirmCustomerReceipt
```

Pasos:

```text
1. Buscar pedido por código.
2. Verificar que el pedido pertenezca al cliente.
3. Verificar que el pedido esté DELIVERED.
4. Verificar que existe Delivery.
5. Verificar que deliveredAt no sea null.
6. Verificar que customerConfirmedAt sea null.
7. Registrar customerConfirmedAt.
8. Registrar customerConfirmationNotes si existen.
9. Devolver detalle actualizado.
```

## Reglas de negocio clave

```text
- Cada pedido tiene un código único.
- Un pedido debe tener al menos un producto.
- El administrador registra pedidos.
- El cocinero cambia PENDING → IN_PREPARATION → READY.
- El repartidor cambia READY → ON_THE_WAY → DELIVERED.
- No se puede entregar un pedido si no está ON_THE_WAY.
- Todo cambio de estado debe registrarse en historial.
- El cliente puede consultar estado e historial.
- El cliente solo puede confirmar recepción cuando el repartidor ya confirmó entrega.
```

## Regla de oro del proyecto

```text
La presentación no contiene reglas de negocio.
Las reglas del restaurante viven en service.
Los repositorios solo buscan y guardan datos.
Las entidades representan tablas y relaciones.
```
