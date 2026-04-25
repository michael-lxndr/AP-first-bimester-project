# Package `service`

El paquete `service` contiene las reglas de negocio. Esta es la capa más importante del proyecto.

Regla principal:

```text
La presentación no decide negocio.
Los repositorios no deciden negocio.
Los servicios deciden negocio.
```

Acá se valida quién puede hacer qué, en qué momento, bajo qué estado y con qué datos.

## Estructura

```text
service
├── CustomerService.java
├── ProductService.java
├── StaffService.java
├── OrderService.java
├── OrderQueryService.java
├── DeliveryService.java
└── StateTransitionService.java
```

## `CustomerService.java`

Servicio para clientes.

Qué hace:

```text
1. Registrar clientes.
2. Buscar clientes existentes.
3. Validar que un cliente exista.
4. Validar que un cliente esté activo.
5. Evitar duplicados si se usa email o teléfono como dato único.
```

Flujo recomendado para registrar cliente:

```text
registerCustomer(request)

1. Validar que fullName no esté vacío.
2. Validar teléfono si es requerido.
3. Validar email si fue ingresado.
4. Verificar que el email no esté duplicado.
5. Crear Customer.
6. Guardar con CustomerRepository.
7. Convertir a CustomerResponse.
8. Devolver respuesta.
```

Quién lo usa:

```text
- AdminMenu/AdminController
- OrderService
- OrderQueryService
```

## `ProductService.java`

Servicio para productos del menú.

Qué hace:

```text
1. Crear productos.
2. Consultar menú.
3. Listar productos disponibles.
4. Activar o desactivar productos.
5. Validar precio y nombre.
```

Flujo recomendado para crear producto:

```text
createProduct(request)

1. Validar productName.
2. Validar unitPrice mayor o igual a cero.
3. Crear Product.
4. Marcar isAvailable como true.
5. Guardar con ProductRepository.
6. Convertir a ProductResponse.
7. Devolver respuesta.
```

Quién lo usa:

```text
- AdminMenu/AdminController
- ProductController
- OrderService
```

## `StaffService.java`

Servicio para personal del restaurante.

Qué hace:

```text
1. Buscar staff por ID.
2. Validar que el staff exista.
3. Validar que el staff esté activo.
4. Validar que tenga un rol específico.
```

Método clave conceptual:

```text
validateStaffRole(staffId, requiredRole)
```

Flujo:

```text
1. Buscar Staff por ID.
2. Si no existe, lanzar ResourceNotFoundException.
3. Si está inactivo, lanzar BusinessException.
4. Comparar su Role con requiredRole.
5. Si no coincide, lanzar InvalidRoleException.
6. Devolver Staff válido.
```

Quién lo usa:

```text
- OrderService para validar ADMINISTRATOR.
- OrderService para validar COOK en cambios de cocina.
- DeliveryService para validar COURIER.
- StateTransitionService puede usar el rol del staff ya cargado.
```

## `StateTransitionService.java`

Servicio para validar cambios de estado.

Qué hace:

```text
1. Recibir estado actual.
2. Recibir estado destino.
3. Recibir rol del staff.
4. Consultar si existe una regla activa en OrderStatusTransitionRule.
5. Aprobar o rechazar la transición.
```

Flujo recomendado:

```text
validateTransition(currentStatus, targetStatus, staffRole)

1. Verificar que currentStatus no sea null.
2. Verificar que targetStatus no sea null.
3. Verificar que staffRole no sea null.
4. Consultar OrderStatusTransitionRuleRepository.
5. Si no existe regla activa, lanzar InvalidOrderStatusException.
6. Si existe, permitir el cambio.
```

Ejemplo:

```text
Current: READY
Target: ON_THE_WAY
Role: COURIER
Result: allowed
```

Ejemplo inválido:

```text
Current: PENDING
Target: DELIVERED
Role: COURIER
Result: rejected
```

Quién lo usa:

```text
- OrderService
- DeliveryService
```

## `OrderService.java`

Servicio principal de pedidos.

Qué hace:

```text
1. Crear pedidos.
2. Validar administrador.
3. Validar cliente.
4. Validar productos.
5. Calcular subtotales.
6. Calcular total.
7. Asignar estado inicial.
8. Registrar historial inicial.
9. Cambiar estados generales del pedido.
10. Registrar historial en cada cambio.
```

Flujo recomendado para crear pedido:

```text
createOrder(request)

1. Buscar staff que registra el pedido.
2. Validar que el staff tenga rol ADMINISTRATOR.
3. Buscar cliente.
4. Validar que el cliente exista y esté activo.
5. Validar que el pedido tenga al menos un producto.
6. Buscar cada Product por ID.
7. Validar que cada Product esté disponible.
8. Calcular lineTotal de cada OrderItem.
9. Calcular totalAmount.
10. Buscar estado inicial PENDING.
11. Crear CustomerOrder.
12. Crear OrderItem por cada producto.
13. Crear primer OrderStatusHistory.
14. Guardar todo dentro de una transacción.
15. Devolver OrderDetailResponse.
```

Flujo recomendado para cambiar estado:

```text
changeOrderStatus(request)

1. Buscar pedido por orderCode.
2. Buscar staff que hace el cambio.
3. Validar que el staff esté activo.
4. Buscar targetStatus por código.
5. Obtener currentStatus desde el pedido.
6. Validar transición con StateTransitionService.
7. Actualizar currentStatus.
8. Actualizar currentStatusChangedAt.
9. Crear OrderStatusHistory.
10. Guardar todo dentro de una transacción.
11. Devolver OrderDetailResponse.
```

Importante:

```text
Crear pedido y crear historial debe ser transaccional.
Cambiar estado y crear historial debe ser transaccional.
Si una parte falla, se revierte todo.
```

## `OrderQueryService.java`

Servicio de consultas de pedidos.

Se separa de `OrderService` para mantener más claro qué modifica y qué consulta.

Qué hace:

```text
1. Consultar pedido por código.
2. Listar pedidos.
3. Listar pedidos por estado.
4. Listar pedidos por cliente.
5. Consultar historial del pedido.
6. Consultar estado actual para seguimiento.
```

Flujo recomendado para seguimiento del cliente:

```text
trackOrder(orderCode)

1. Buscar CustomerOrder por orderCode.
2. Si no existe, lanzar ResourceNotFoundException.
3. Buscar OrderItem del pedido.
4. Buscar OrderStatusHistory ordenado por fecha.
5. Buscar Delivery si existe.
6. Armar OrderDetailResponse.
7. Devolver respuesta.
```

Quién lo usa:

```text
- CustomerMenu/CustomerController
- AdminMenu/AdminController
- CookMenu/CookController
- CourierMenu/CourierController
```

## `DeliveryService.java`

Servicio para despacho, entrega y recepción.

Qué hace:

```text
1. Despachar pedido.
2. Confirmar entrega por repartidor.
3. Confirmar recepción por cliente.
4. Validar repartidor.
5. Validar estados necesarios.
6. Registrar fechas.
7. Registrar historial cuando cambia el estado operativo.
```

### Flujo para despachar pedido

```text
dispatchOrder(request)

1. Buscar pedido por orderCode.
2. Verificar que currentStatus sea READY.
3. Buscar staff repartidor.
4. Validar que tenga rol COURIER.
5. Verificar que no exista Delivery previa para ese pedido.
6. Crear Delivery.
7. Registrar dispatchedAt.
8. Cambiar estado del pedido a ON_THE_WAY.
9. Registrar OrderStatusHistory.
10. Guardar todo dentro de una transacción.
11. Devolver DeliveryResponse u OrderDetailResponse.
```

### Flujo para confirmar entrega por repartidor

```text
confirmDelivery(request)

1. Buscar pedido por orderCode.
2. Verificar que currentStatus sea ON_THE_WAY.
3. Buscar Delivery del pedido.
4. Validar que el courierStaff sea el repartidor correcto o tenga rol COURIER.
5. Validar receiverName.
6. Registrar deliveredAt.
7. Registrar receiverName.
8. Cambiar estado del pedido a DELIVERED.
9. Registrar OrderStatusHistory.
10. Guardar todo dentro de una transacción.
11. Devolver DeliveryResponse u OrderDetailResponse.
```

### Flujo para confirmar recepción por cliente

```text
confirmCustomerReceipt(request)

1. Buscar pedido por orderCode.
2. Buscar cliente por customerId.
3. Verificar que el pedido pertenezca al cliente.
4. Verificar que currentStatus sea DELIVERED.
5. Buscar Delivery del pedido.
6. Verificar que deliveredAt no sea null.
7. Verificar que customerConfirmedAt sea null.
8. Registrar customerConfirmedAt.
9. Registrar customerConfirmationNotes si existen.
10. Guardar Delivery.
11. Devolver DeliveryResponse u OrderDetailResponse.
```

Importante:

```text
La confirmación del cliente no crea OrderStatusHistory porque no cambia el estado operativo.
El estado ya es DELIVERED.
La confirmación se guarda en Delivery.
```
