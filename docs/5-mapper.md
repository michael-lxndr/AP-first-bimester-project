# Package `mapper`

El paquete `mapper` convierte entidades del dominio en DTOs de respuesta.

Regla principal:

```text
Los servicios deciden.
Los mappers transforman.
```

Un mapper no debe consultar base de datos ni validar reglas de negocio. Solo recibe entidades ya cargadas y arma respuestas legibles para presentación.

## Estructura

```text
mapper
├── CustomerMapper.java
├── ProductMapper.java
├── OrderMapper.java
└── DeliveryMapper.java
```

## `CustomerMapper.java`

Convierte `Customer` en `CustomerResponse`.

Qué hace:

```text
1. Recibe una entidad Customer.
2. Extrae datos visibles.
3. Construye CustomerResponse.
```

Transformación esperada:

```text
Customer.customerId  → CustomerResponse.customerId
Customer.fullName    → CustomerResponse.fullName
Customer.phone       → CustomerResponse.phone
Customer.email       → CustomerResponse.email
Customer.isActive    → CustomerResponse.isActive
Customer.createdAt   → CustomerResponse.createdAt
```

Quién lo usa:

```text
- CustomerService
- OrderMapper
```

## `ProductMapper.java`

Convierte `Product` en `ProductResponse`.

Qué hace:

```text
1. Recibe una entidad Product.
2. Extrae datos de menú.
3. Construye ProductResponse.
```

Transformación esperada:

```text
Product.productId    → ProductResponse.productId
Product.productName  → ProductResponse.productName
Product.description  → ProductResponse.description
Product.unitPrice    → ProductResponse.unitPrice
Product.isAvailable  → ProductResponse.isAvailable
```

Quién lo usa:

```text
- ProductService
- Pantallas de menú
```

## `OrderMapper.java`

Convierte pedidos, ítems e historial en respuestas.

Qué hace:

```text
1. Convierte CustomerOrder en OrderSummaryResponse.
2. Convierte CustomerOrder en OrderDetailResponse.
3. Convierte OrderItem en OrderItemResponse.
4. Convierte OrderStatusHistory en OrderHistoryResponse.
```

### Para `OrderSummaryResponse`

Transformación esperada:

```text
CustomerOrder.orderCode                  → orderCode
CustomerOrder.customer.fullName          → customerName
CustomerOrder.currentStatus.statusCode   → currentStatusCode
CustomerOrder.currentStatus.statusName   → currentStatusName
CustomerOrder.totalAmount                → totalAmount
CustomerOrder.createdAt                  → createdAt
CustomerOrder.currentStatusChangedAt     → currentStatusChangedAt
```

### Para `OrderDetailResponse`

Debe incluir:

```text
1. Datos principales del pedido.
2. Datos del cliente.
3. Lista de ítems.
4. Historial.
5. Datos de entrega si existe.
```

Importante:

```text
OrderMapper puede apoyarse en CustomerMapper y DeliveryMapper.
No debería buscar datos en repositorios.
Si necesita ítems o historial, el service se los debe pasar ya cargados.
```

Quién lo usa:

```text
- OrderService
- OrderQueryService
- DeliveryService
```

## `DeliveryMapper.java`

Convierte `Delivery` en `DeliveryResponse`.

Qué hace:

```text
1. Recibe una entidad Delivery.
2. Extrae datos del pedido, repartidor y fechas.
3. Calcula si el cliente confirmó recepción.
4. Construye DeliveryResponse.
```

Transformación esperada:

```text
Delivery.deliveryId                  → deliveryId
Delivery.order.orderCode             → orderCode
Delivery.courierStaff.fullName       → courierStaffName
Delivery.dispatchedAt                → dispatchedAt
Delivery.deliveredAt                 → deliveredAt
Delivery.receiverName                → receiverName
Delivery.customerConfirmedAt         → customerConfirmedAt
Delivery.customerConfirmationNotes   → customerConfirmationNotes
Delivery.customerConfirmedAt != null → isCustomerConfirmed
```

Quién lo usa:

```text
- DeliveryService
- OrderMapper
- OrderQueryService
```

## Buenas prácticas

```text
- No usar entidades directamente en JavaFX o consola.
- No devolver entidades desde servicios hacia presentación.
- No meter validaciones de negocio en mapper.
- No consultar repositorios desde mapper.
- Mantener los DTOs orientados a lo que la pantalla necesita mostrar.
```
