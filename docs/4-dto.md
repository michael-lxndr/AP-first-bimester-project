# Package `dto`

El paquete `dto` contiene objetos para transportar datos entre la presentación y los servicios.

Regla principal:

```text
La presentación no debería trabajar directamente con entidades JPA.
Usa request DTO para enviar datos.
Usa response DTO para recibir datos.
```

Esto evita exponer entidades completas, relaciones internas o datos que la pantalla no necesita.

## Estructura

```text
dto
├── request
│   ├── CreateCustomerRequest.java
│   ├── CreateCustomerAddressRequest.java
│   ├── CreateProductRequest.java
│   ├── CreateOrderRequest.java
│   ├── CreateOrderItemRequest.java
│   ├── ChangeOrderStatusRequest.java
│   ├── DispatchOrderRequest.java
│   ├── ConfirmDeliveryRequest.java
│   └── ConfirmReceiptRequest.java
│
└── response
    ├── ProductResponse.java
    ├── CustomerResponse.java
    ├── CustomerAddressResponse.java
    ├── OrderSummaryResponse.java
    ├── OrderDetailResponse.java
    ├── OrderItemResponse.java
    ├── OrderHistoryResponse.java
    └── DeliveryResponse.java
```

# Request DTOs

## `request/CreateCustomerRequest.java`

Datos necesarios para registrar un cliente.

Campos esperados:

```text
fullName
phone
email
```

Qué hace:

```text
1. Lleva datos desde consola o JavaFX hacia CustomerService.
2. Permite validar datos simples con Validation.
```

Validaciones esperadas:

```text
fullName: requerido
phone: requerido o validado según decisión del grupo
email: formato válido si se ingresa
```

## `request/CreateCustomerAddressRequest.java`

Datos necesarios para registrar una dirección de cliente.

Campos esperados:

```text
customerId
alias
mainStreet
secondaryStreet
houseNumber
reference
postalCode
city
province
isPrimary
```

Qué hace:

```text
1. Lleva datos desde consola o JavaFX hacia CustomerAddressService.
2. Permite registrar varias direcciones por cliente.
3. Permite indicar si la dirección será la principal.
```

Validaciones esperadas:

```text
customerId: requerido
alias: requerido
mainStreet: requerida
city/province: opcionales con valor por defecto si el negocio lo decide
```

## `request/CreateProductRequest.java`

Datos necesarios para registrar un producto.

Campos esperados:

```text
productName
description
unitPrice
productCode
category
productionCost
preparationTimeMinutes
imageUrl
```

Qué hace:

```text
1. Lleva datos del formulario o menú hacia ProductService.
2. Permite validar nombre y precio.
```

Validaciones esperadas:

```text
productName: requerido
unitPrice: mayor o igual a cero
description: opcional
category: opcional, pero debe existir en ProductCategory si se informa
preparationTimeMinutes: mayor a cero si se informa
```

## `request/CreateOrderRequest.java`

Datos necesarios para crear un pedido.

Campos esperados:

```text
customerId
registeredByStaffId
deliveryAddressId
generalNotes
discountCode
isPriority
items
```

Qué hace:

```text
1. Lleva datos de creación del pedido hacia OrderService.
2. Agrupa cliente, administrador, dirección elegida, notas, descuentos e ítems.
```

Validaciones esperadas:

```text
customerId: requerido
registeredByStaffId: requerido
deliveryAddressId: requerido cuando el cliente ya tiene direcciones registradas
items: mínimo un elemento
```

Cómo lo usa `OrderService`:

```text
1. Valida staff administrador.
2. Valida cliente.
3. Valida ítems.
4. Calcula total.
5. Crea pedido e historial.
```

## `request/CreateOrderItemRequest.java`

Representa un producto solicitado dentro de un pedido.

Campos esperados:

```text
productId
quantity
specialNote
```

Qué hace:

```text
1. Indica qué producto se pide.
2. Indica cuántas unidades se piden.
3. Permite una observación específica para ese producto.
```

Validaciones esperadas:

```text
productId: requerido
quantity: mayor a cero
specialNote: opcional, máximo recomendado 150 caracteres
```

## `request/ChangeOrderStatusRequest.java`

Datos para cambiar el estado de un pedido.

Campos esperados:

```text
orderCode
targetStatusCode
changedByStaffId
notes
```

Qué hace:

```text
1. Lleva la solicitud de cambio de estado hacia OrderService.
2. Permite saber quién intenta hacer el cambio.
3. Permite guardar observaciones en historial.
```

Ejemplo:

```text
orderCode: ORD-0001
targetStatusCode: IN_PREPARATION
changedByStaffId: 2
notes: Pedido tomado por cocina
```

## `request/DispatchOrderRequest.java`

Datos para despachar un pedido.

Campos esperados:

```text
orderCode
courierStaffId
notes
```

Qué hace:

```text
1. Lleva la solicitud de salida del pedido hacia DeliveryService.
2. Indica qué repartidor sale con el pedido.
3. Permite registrar notas en historial.
```

Reglas que activa:

```text
- El pedido debe estar READY.
- El staff debe ser COURIER.
- El pedido no debe tener Delivery previa.
```

## `request/ConfirmDeliveryRequest.java`

Datos para que el repartidor confirme la entrega.

Campos esperados:

```text
orderCode
courierStaffId
receiverName
notes
```

Qué hace:

```text
1. Lleva la confirmación de entrega hacia DeliveryService.
2. Guarda el nombre de quien recibió.
3. Permite registrar notas en historial.
```

Reglas que activa:

```text
- El pedido debe estar ON_THE_WAY.
- Debe existir Delivery.
- receiverName debe estar presente.
- El estado cambia a DELIVERED.
```

## `request/ConfirmReceiptRequest.java`

Datos para que el cliente confirme que recibió el pedido.

Campos esperados:

```text
orderCode
customerId
notes
```

Qué hace:

```text
1. Lleva la confirmación de recepción hacia DeliveryService.
2. Permite validar que el cliente sea dueño del pedido.
3. Permite guardar notas opcionales de recepción.
```

Reglas que activa:

```text
- El pedido debe pertenecer al cliente.
- El pedido debe estar DELIVERED.
- Delivery.deliveredAt no debe ser null.
- Delivery.customerConfirmedAt debe ser null.
```

# Response DTOs

## `response/ProductResponse.java`

Datos que se muestran sobre un producto.

Campos esperados:

```text
productId
productCode
productName
description
unitPrice
category
preparationTimeMinutes
isAvailable
imageUrl
```

Quién lo usa:

```text
- ProductService
- AdminMenu/AdminController
- ProductController
- Pantallas de menú
```

## `response/CustomerResponse.java`

Datos que se muestran sobre un cliente.

Campos esperados:

```text
customerId
fullName
phone
email
isActive
createdAt
addresses
```

Quién lo usa:

```text
- CustomerService
- OrderDetailResponse
- Pantallas de administración
```

## `response/CustomerAddressResponse.java`

Datos visibles de una dirección de cliente.

Campos esperados:

```text
addressId
customerId
alias
mainStreet
secondaryStreet
houseNumber
reference
postalCode
city
province
isPrimary
isActive
```

Quién lo usa:

```text
- CustomerAddressService
- CustomerResponse si se decide incluir direcciones
- Pantallas de creación de pedidos
```

## `response/OrderSummaryResponse.java`

Resumen de un pedido para listados.

Campos esperados:

```text
orderCode
customerName
currentStatusCode
currentStatusName
totalAmount
createdAt
currentStatusChangedAt
```

Qué hace:

```text
1. Muestra información corta para tablas.
2. Evita cargar detalles innecesarios.
```

Quién lo usa:

```text
- Listado de pedidos pendientes.
- Listado de pedidos en preparación.
- Listado de pedidos listos.
- Listado de pedidos en camino.
```

## `response/OrderDetailResponse.java`

Detalle completo de un pedido.

Campos esperados:

```text
orderCode
customer
deliveryAddressId
deliveryAddressSnapshot
currentStatusCode
currentStatusName
subtotalAmount
taxAmount
discountAmount
addressSurchargeAmount
totalAmount
discountCode
isPriority
generalNotes
estimatedDeliveryAt
createdAt
currentStatusChangedAt
items
history
delivery
```

Qué hace:

```text
1. Muestra la información completa del pedido.
2. Sirve para consulta del cliente.
3. Sirve para vista administrativa.
```

## `response/OrderItemResponse.java`

Datos visibles de un ítem del pedido.

Campos esperados:

```text
productId
productNameSnapshot
quantity
unitPrice
lineTotal
specialNote
isReady
```

Qué hace:

```text
1. Muestra qué productos forman el pedido.
2. Muestra cantidad y subtotal.
3. Usa snapshot para respetar datos históricos.
```

## `response/OrderHistoryResponse.java`

Datos visibles del historial de estados.

Campos esperados:

```text
fromStatusCode
fromStatusName
toStatusCode
toStatusName
changedByStaffName
changedAt
notes
```

Qué hace:

```text
1. Permite mostrar línea de tiempo del pedido.
2. Permite explicar quién hizo cada cambio y cuándo.
```

Quién lo usa:

```text
- CustomerMenu/CustomerController
- OrderTrackingController
- AdminController
```

## `response/DeliveryResponse.java`

Datos visibles de la entrega.

Campos esperados:

```text
deliveryId
orderCode
courierStaffName
dispatchedAt
deliveredAt
receiverName
customerConfirmedAt
customerConfirmationNotes
isCustomerConfirmed
```

Qué hace:

```text
1. Muestra información de despacho.
2. Muestra información de entrega.
3. Muestra si el cliente confirmó la recepción.
```

Cómo se calcula `isCustomerConfirmed`:

```text
customerConfirmedAt != null
```
