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
├── CustomerAddressService.java
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

## `CustomerAddressService.java`

Servicio para administrar direcciones de clientes.

Qué hace:

```text
1. Registrar direcciones para un cliente.
2. Listar direcciones activas.
3. Marcar una dirección como principal.
4. Desactivar direcciones sin borrar historial.
5. Validar que una dirección pertenece al cliente antes de crear un pedido.
```

Flujo recomendado para registrar dirección:

```text
createAddress(request)

1. Buscar Customer por ID.
2. Validar que el cliente exista y esté activo.
3. Validar alias y mainStreet.
4. Si request.isPrimary es true, desmarcar otras direcciones principales del cliente.
5. Crear CustomerAddress.
6. Guardar con CustomerAddressRepository.
7. Devolver CustomerAddressResponse.
```

Método clave para pedidos:

```text
validateAddressForCustomer(customerId, addressId)

1. Buscar dirección activa por addressId.
2. Verificar que address.customer.id coincida con customerId.
3. Si no coincide, lanzar BusinessException.
4. Devolver CustomerAddress válida.
```

Por qué importa:

```text
Sin esta validación, un cliente podría usar por error o malicia una dirección de otro cliente.
Eso no se resuelve en repository; es una regla de negocio.
```

## `ProductService.java`

Servicio para productos del menú.

Qué hace:

```text
1. Crear productos.
2. Consultar menú.
3. Listar productos disponibles.
4. Filtrar por categoría.
5. Activar o desactivar productos.
6. Validar precio, nombre, código y tiempo de preparación.
```

Flujo recomendado para crear producto:

```text
createProduct(request)

1. Validar productName.
2. Validar unitPrice mayor o igual a cero.
3. Validar productCode si se usa como único.
4. Validar category si se informa.
5. Validar preparationTimeMinutes mayor a cero si se informa.
6. Crear Product.
7. Marcar isAvailable como true.
8. Guardar con ProductRepository.
9. Convertir a ProductResponse.
10. Devolver respuesta.
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
5. Validar deliveryAddressId con CustomerAddressService.
6. Generar deliveryAddressSnapshot.
7. Validar que el pedido tenga al menos un producto.
8. Buscar cada Product por ID.
9. Validar que cada Product esté disponible.
10. Calcular lineTotal de cada OrderItem.
11. Calcular subtotalAmount.
12. Calcular taxAmount, discountAmount y addressSurchargeAmount si aplican.
13. Calcular totalAmount.
14. Buscar estado inicial PENDING.
15. Crear CustomerOrder.
16. Crear OrderItem por cada producto, incluyendo specialNote si existe.
17. Crear primer OrderStatusHistory.
18. Guardar todo dentro de una transacción.
19. Devolver OrderDetailResponse.
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

## Pasos de integración de las mejoras de FoodFlow2

```text
1. Implementar CustomerAddressService antes de cambiar pantallas.
2. Cambiar CreateOrderRequest para recibir deliveryAddressId y no solo texto libre.
3. En OrderService.createOrder, validar dirección y generar snapshot antes de guardar.
4. Extender ProductService para manejar ProductCategory y preparationTimeMinutes.
5. Extender cálculo de totales: subtotalAmount, taxAmount, discountAmount, addressSurchargeAmount y totalAmount.
6. Usar OrderItem.specialNote para notas por producto.
7. Usar OrderItem.isReady solo para cocina; no reemplaza OrderStatus.
```

Tradeoff importante:

```text
FoodFlow2 usa herencia de Usuario para Cliente/Cocinero/Repartidor/Administrador.
Este proyecto mantiene Staff + Role porque las reglas de transición ya dependen de roles trazables.
Copiar esa herencia acá duplicaría conceptos y haría más confuso el control de permisos.
```
