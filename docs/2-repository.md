# Package `repository`

El paquete `repository` contiene las interfaces de acceso a datos. Estas interfaces usan Spring Data JPA para consultar y guardar información en MySQL.

Regla principal:

```text
Repository busca y guarda.
Service decide.
```

Un repositorio no debe validar reglas de negocio como roles, transiciones o permisos. Eso pertenece a `service`.

## Estructura

```text
repository
├── RoleRepository.java
├── StaffRepository.java
├── CustomerRepository.java
├── ProductRepository.java
├── OrderStatusRepository.java
├── OrderStatusTransitionRuleRepository.java
├── CustomerOrderRepository.java
├── OrderItemRepository.java
├── OrderStatusHistoryRepository.java
└── DeliveryRepository.java
```

## `RoleRepository.java`

Repositorio para `Role`.

Qué hace:

```text
1. Buscar roles por nombre o código.
2. Verificar si un rol ya existe.
3. Guardar roles iniciales.
```

Consultas esperadas:

```text
findByRoleName(...)
existsByRoleName(...)
```

Quién lo usa:

```text
- DataInitializer
- StaffService
- StateTransitionService indirectamente
```

## `StaffRepository.java`

Repositorio para `Staff`.

Qué hace:

```text
1. Buscar staff por ID.
2. Buscar staff por username.
3. Buscar staff por email.
4. Listar staff activos.
5. Buscar staff por rol.
```

Consultas esperadas:

```text
findByUsername(...)
findByEmail(...)
findByIsActiveTrue()
findByRoleRoleName(...)
```

Quién lo usa:

```text
- StaffService
- OrderService
- DeliveryService
- DataInitializer
```

Importante:

```text
Este repositorio no decide si un staff puede entregar o cambiar estados.
Solo devuelve el staff. La validación ocurre en StaffService o en servicios de negocio.
```

## `CustomerRepository.java`

Repositorio para `Customer`.

Qué hace:

```text
1. Buscar cliente por ID.
2. Buscar cliente por email.
3. Buscar cliente por teléfono.
4. Guardar clientes.
5. Verificar duplicados.
```

Consultas esperadas:

```text
findByEmail(...)
findByPhone(...)
existsByEmail(...)
```

Quién lo usa:

```text
- CustomerService
- OrderService
- OrderQueryService
- DeliveryService para confirmar recepción
```

## `ProductRepository.java`

Repositorio para `Product`.

Qué hace:

```text
1. Buscar producto por ID.
2. Listar productos disponibles.
3. Verificar existencia de producto.
4. Guardar productos.
```

Consultas esperadas:

```text
findByIsAvailableTrue()
findByProductNameContainingIgnoreCase(...)
```

Quién lo usa:

```text
- ProductService
- OrderService
- DataInitializer
```

## `OrderStatusRepository.java`

Repositorio para `OrderStatus`.

Qué hace:

```text
1. Buscar estado por código.
2. Listar estados ordenados.
3. Guardar estados iniciales.
4. Verificar si un estado existe.
```

Consultas esperadas:

```text
findByStatusCode(...)
existsByStatusCode(...)
findAllByOrderByStatusOrderAsc()
```

Quién lo usa:

```text
- OrderService
- DeliveryService
- StateTransitionService
- DataInitializer
```

## `OrderStatusTransitionRuleRepository.java`

Repositorio para `OrderStatusTransitionRule`.

Qué hace:

```text
1. Buscar si existe una transición activa.
2. Listar reglas activas.
3. Guardar reglas iniciales.
```

Consulta clave:

```text
existsByFromStatusAndToStatusAndRoleAndIsActiveTrue(...)
```

O una variante por códigos:

```text
existsByFromStatusStatusCodeAndToStatusStatusCodeAndRoleRoleNameAndIsActiveTrue(...)
```

Quién lo usa:

```text
- StateTransitionService
- DataInitializer
```

Importante:

```text
Este repositorio no cambia estados.
Solo responde si existe una regla válida.
```

## `CustomerOrderRepository.java`

Repositorio para `CustomerOrder`.

Qué hace:

```text
1. Buscar pedido por código.
2. Verificar si un código ya existe.
3. Listar pedidos por estado.
4. Listar pedidos por cliente.
5. Guardar pedidos.
```

Consultas esperadas:

```text
findByOrderCode(...)
existsByOrderCode(...)
findByCurrentStatusStatusCode(...)
findByCustomerCustomerId(...)
```

Quién lo usa:

```text
- OrderService
- OrderQueryService
- DeliveryService
```

Importante:

```text
orderCode debe ser único.
La base de datos debe tener una restricción UNIQUE.
El servicio igualmente valida antes para devolver un error claro.
```

## `OrderItemRepository.java`

Repositorio para `OrderItem`.

Qué hace:

```text
1. Buscar ítems por pedido.
2. Guardar ítems.
3. Eliminar ítems si algún caso futuro lo requiere.
```

Consultas esperadas:

```text
findByOrderOrderId(...)
```

Quién lo usa:

```text
- OrderService
- OrderQueryService
```

Importante:

```text
OrderService suele crear los ítems junto con el pedido dentro de una misma transacción.
```

## `OrderStatusHistoryRepository.java`

Repositorio para `OrderStatusHistory`.

Qué hace:

```text
1. Guardar historial de estados.
2. Consultar historial por pedido.
3. Ordenar historial por fecha.
```

Consultas esperadas:

```text
findByOrderOrderCodeOrderByChangedAtAsc(...)
findByOrderOrderIdOrderByChangedAtAsc(...)
```

Quién lo usa:

```text
- OrderService
- DeliveryService
- OrderQueryService
```

Regla relacionada:

```text
Todo cambio de estado debe tener un registro de historial.
```

## `DeliveryRepository.java`

Repositorio para `Delivery`.

Qué hace:

```text
1. Buscar entrega por pedido.
2. Verificar si un pedido ya tiene entrega.
3. Buscar entregas por repartidor.
4. Guardar despacho, entrega y confirmación del cliente.
```

Consultas esperadas:

```text
findByOrderOrderCode(...)
existsByOrderOrderId(...)
findByCourierStaffStaffId(...)
```

Quién lo usa:

```text
- DeliveryService
- OrderQueryService
```

Importante:

```text
Delivery.order debe ser UNIQUE para asegurar que un pedido tenga máximo una entrega.
```
