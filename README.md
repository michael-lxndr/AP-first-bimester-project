# Restaurant Order Manager

Proyecto académico para modelar y documentar la gestión completa de pedidos de restaurante: registro, preparación, despacho, entrega y confirmación de recepción.

## Fuente de verdad del modelo

La documentación de este repositorio se alinea principalmente con estos archivos:

```text
assets/ROM - ER Diagram.sql
assets/ER Diagram.puml
assets/restaurant-order-complete-flow.puml
```

Si hay una diferencia entre un documento narrativo y esos assets, manda el modelo relacional/ER.

## Flujo operativo principal

```text
PENDING → IN_PREPARATION → READY → ON_THE_WAY → DELIVERED
```

Pares código/etiqueta del catálogo de estados:

```text
PENDING         → Pendiente
IN_PREPARATION  → En preparación
READY           → Listo
ON_THE_WAY      → En camino
DELIVERED       → Entregado
```

Importante:

```text
- status_code es la clave operativa del flujo.
- status_name es la etiqueta visible para la UI.
- La confirmación del cliente NO crea un nuevo estado.
- La confirmación del cliente se guarda en deliveries.customer_confirmed_at.
```

## Roles operativos

```text
ADMINISTRATOR
COOK
COURIER
```

Responsabilidades:

```text
- ADMINISTRATOR: registra clientes, direcciones, productos y pedidos.
- COOK: mueve pedidos de PENDING a IN_PREPARATION y luego a READY.
- COURIER: mueve pedidos de READY a ON_THE_WAY y luego a DELIVERED.
- CUSTOMER: consulta seguimiento y confirma recepción, pero NO cambia estados operativos.
```

## Aclaración importante sobre identificación del staff

El esquema actual guarda `staff.username`, `staff.email`, `staff.role_id` e `is_active`, pero NO modela contraseña.

Eso significa que:

```text
- la identificación del staff en diagramas es operativa/demostrativa
- no hay autenticación completa en el modelo relacional actual
- las autorizaciones fuertes se apoyan en role_name + reglas de transición
```

## Modelo relacional vigente

Tablas principales:

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
- staff.role_id → roles.role_id
- customer_addresses.customer_id → customers.customer_id
- customer_orders.customer_id → customers.customer_id
- customer_orders.registered_by_staff_id → staff.staff_id
- customer_orders.current_status_id → order_statuses.status_id
- customer_orders.delivery_address_id → customer_addresses.address_id
- order_items.order_id → customer_orders.order_id
- order_items.product_id → products.product_id
- order_status_histories.order_id → customer_orders.order_id
- order_status_histories.from_status_id/to_status_id → order_statuses.status_id
- order_status_histories.changed_by_staff_id → staff.staff_id
- deliveries.order_id → customer_orders.order_id
- deliveries.courier_staff_id → staff.staff_id
```

## Reglas de negocio que YA están reflejadas en SQL/ER

```text
- order_code debe ser único.
- Un pedido debe tener delivery_address_id obligatorio.
- La dirección elegida debe pertenecer al cliente del pedido.
- customer_orders.delivery_address_snapshot preserva el histórico de entrega.
- city y province son obligatorias en customer_addresses.
- country es obligatorio y usa Ecuador por defecto.
- unit_price, production_cost y preparation_time_minutes tienen CHECKs.
- total_amount no puede ser negativo.
- current_status_changed_at acelera seguimiento.
- Todo cambio de estado debe insertarse en order_status_histories.
- deliveries.order_id es UNIQUE: un pedido tiene máximo una entrega.
- delivered_at exige receiver_name.
- customer_confirmed_at solo puede existir después de delivered_at.
```

## Reglas de transición por rol

La tabla `order_status_transition_rules` documenta y hace trazable quién puede mover qué estado:

```text
PENDING         → IN_PREPARATION  : COOK
IN_PREPARATION  → READY           : COOK
READY           → ON_THE_WAY      : COURIER
ON_THE_WAY      → DELIVERED       : COURIER
```

Consecuencia arquitectónica:

```text
- No conviene validar estados solo “a mano” contra strings sueltos.
- La aplicación debería consultar las reglas activas de transición.
- status_code + role_name son la combinación correcta para esa validación.
```

## Arquitectura objetivo

El proyecto se documenta con arquitectura por capas:

```text
Presentation Layer
    ↓
Service Layer
    ↓
Repository Layer
    ↓
Database Layer
```

Responsabilidad por capa:

```text
- Presentation: captura datos y muestra resultados.
- Service: valida reglas, calcula montos y coordina transacciones.
- Repository: consulta y persiste entidades.
- Database: aplica constraints, relaciones, índices y defaults.
```

Regla de oro:

```text
La presentación no contiene reglas de negocio.
Los repositorios no deciden negocio.
Los servicios deciden negocio.
```

## Stack verificado del proyecto

Tomado de `build.gradle.kts`:

```text
Java 25
Spring Boot 4.0.6
Gradle Kotlin DSL
Spring Data JPA
Spring Validation
MySQL Connector/J
Lombok
JavaFX 25.0.3
```

Además, `bootRun` está configurado para levantar:

```text
first.bimester.project.restaurant.presentation.javafx.JavaFxLauncher
```

## Qué describe la documentación de `docs/`

Los archivos dentro de `docs/` describen la arquitectura objetivo por paquete, alineada al modelo ER:

```text
docs/1-domain.md
docs/2-repository.md
docs/3-service.md
docs/4-dto.md
docs/5-mapper.md
docs/6-exception.md
docs/7-presentation.md
```

## Flujos principales esperados

### 1. Crear pedido

```text
1. Validar que el staff sea ADMINISTRATOR.
2. Buscar o registrar el cliente.
3. Buscar o registrar una dirección del cliente.
4. Validar que la dirección pertenezca al cliente y esté activa.
5. Generar deliveryAddressSnapshot.
6. Validar que exista al menos un ítem.
7. Validar productos disponibles.
8. Calcular subtotal, impuestos, descuento, recargo y total.
9. Asignar estado inicial PENDING.
10. Insertar el pedido, sus ítems y el historial inicial en una transacción.
```

### 2. Cambiar estado en cocina

```text
1. Buscar el pedido por orderCode.
2. Validar staff COOK.
3. Buscar estado destino por status_code.
4. Validar transición en order_status_transition_rules.
5. Actualizar current_status_id y current_status_changed_at.
6. Insertar OrderStatusHistory.
```

### 3. Despachar pedido

```text
1. Buscar el pedido por orderCode.
2. Validar staff COURIER.
3. Verificar que no exista delivery previa.
4. Validar transición READY → ON_THE_WAY.
5. Crear Delivery con dispatched_at.
6. Actualizar estado e historial en una transacción.
```

### 4. Confirmar entrega

```text
1. Buscar el pedido y su Delivery.
2. Validar transición ON_THE_WAY → DELIVERED.
3. Registrar delivered_at y receiver_name.
4. Actualizar estado e historial en una transacción.
```

### 5. Confirmar recepción por cliente

```text
1. Verificar que el pedido pertenezca al cliente.
2. Verificar que el pedido esté DELIVERED.
3. Verificar que exista Delivery y que delivered_at no sea null.
4. Verificar que customer_confirmed_at siga vacío.
5. Registrar customer_confirmed_at y customer_confirmation_notes.
6. NO crear nuevo estado ni nuevo historial.
```

## Diagramas incluidos

```text
assets/ER Diagram.puml                         → diagrama entidad-relación
assets/ROM - ER Diagram.sql                    → script SQL de referencia
assets/restaurant-order-complete-flow.puml     → secuencia completa por rol
```

## Idea clave del negocio

La dirección editable del cliente y la dirección histórica del pedido NO son la misma cosa.

```text
customer_addresses        → dato vivo del cliente
delivery_address_snapshot → evidencia histórica del pedido
```

Si mezclás ambas, rompés trazabilidad. Y eso, hermano, en un sistema real es un bug silencioso de los feos.
