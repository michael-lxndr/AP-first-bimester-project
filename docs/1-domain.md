# Package `domain`

El paquete `domain` representa el corazón del modelo del sistema. Acá viven las entidades JPA y los códigos/enums que permiten expresar el negocio de forma consistente.

Este paquete no debe contener lógica de interfaz, lectura de teclado, ventanas JavaFX ni consultas directas a base de datos. Su responsabilidad es modelar las tablas, relaciones y conceptos del restaurante.

## Estructura

```text
domain
├── entity
│   ├── Role.java
│   ├── Staff.java
│   ├── Customer.java
│   ├── CustomerAddress.java
│   ├── Product.java
│   ├── OrderStatus.java
│   ├── OrderStatusTransitionRule.java
│   ├── CustomerOrder.java
│   ├── OrderItem.java
│   ├── OrderStatusHistory.java
│   └── Delivery.java
│
└── enums
    ├── RoleCode.java
    ├── OrderStatusCode.java
    └── ProductCategory.java
```

## `entity/Role.java`

Representa el rol de un trabajador interno del restaurante.

Ejemplos de roles:

```text
ADMINISTRATOR
COOK
COURIER
```

Qué hace:

```text
1. Guarda el identificador del rol.
2. Guarda el nombre o código del rol.
3. Permite relacionar muchos Staff con un mismo Role.
```

Cómo se usa:

```text
- Staff tiene un Role.
- OrderStatusTransitionRule usa Role para saber qué tipo de staff puede hacer una transición.
- StaffService valida si un trabajador tiene el rol requerido.
```

Relación principal:

```text
Role 1 ─── 0..* Staff
```

Es decir: un rol puede pertenecer a muchos trabajadores, pero cada trabajador tiene un solo rol.

## `entity/Staff.java`

Representa al personal del restaurante.

Puede ser:

```text
- Administrador
- Cocinero
- Repartidor
```

Qué hace:

```text
1. Guarda datos del trabajador.
2. Guarda su rol.
3. Permite saber quién registró un pedido.
4. Permite saber quién cambió un estado.
5. Permite saber qué repartidor hizo una entrega.
```

Campos importantes:

```text
staffId
role
fullName
phone
email
username
isActive
createdAt
```

Cómo se usa:

```text
- CustomerOrder.registeredByStaff guarda el administrador que registró el pedido.
- OrderStatusHistory.changedByStaff guarda quién hizo el cambio de estado.
- Delivery.courierStaff guarda qué repartidor realizó la entrega.
```

Importante:

```text
Staff solo guarda el rol.
La validación de si un Staff puede hacer una acción se hace en service.
```

## `entity/Customer.java`

Representa al cliente que realiza pedidos y consulta el seguimiento.

Qué hace:

```text
1. Guarda datos del cliente.
2. Permite asociar pedidos a un cliente.
3. Permite validar que el cliente existe antes de consultar o confirmar recepción.
4. Agrupa las direcciones reutilizables del cliente.
```

Campos importantes:

```text
customerId
fullName
phone
email
isActive
createdAt
addresses
```

Relación principal:

```text
Customer 1 ─── 0..* CustomerOrder
Customer 1 ─── 0..* CustomerAddress
```

Es decir: un cliente puede tener muchos pedidos, pero cada pedido pertenece a un solo cliente.

Cómo se usa:

```text
- OrderService usa Customer para crear pedidos.
- OrderQueryService usa Customer para consultar pedidos.
- DeliveryService valida que el cliente que confirma recepción sea dueño del pedido.
- CustomerAddressService valida y administra direcciones del cliente.
```

## `entity/CustomerAddress.java`

Representa una dirección guardada por un cliente.

Viene de la idea `Direccion` del proyecto `FoodFlow2`, pero adaptada al naming y al modelo principal.

Qué hace:

```text
1. Permite que un cliente tenga varias direcciones.
2. Marca una dirección principal para sugerirla al crear pedidos.
3. Permite desactivar direcciones sin borrar historial.
4. Sirve como dirección seleccionable para un pedido nuevo.
```

Campos importantes:

```text
addressId
customer
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

Relación principal:

```text
Customer 1 ─── 0..* CustomerAddress
CustomerAddress 1 ─── 0..* CustomerOrder
```

Regla clave:

```text
Un pedido solo puede usar una dirección que pertenezca al mismo cliente del pedido.
```

Importante:

```text
CustomerAddress puede cambiar con el tiempo. Por eso CustomerOrder también guarda deliveryAddressSnapshot.
El snapshot preserva la dirección usada originalmente aunque el cliente edite o desactive la dirección después.
```

## `entity/Product.java`

Representa un producto del menú.

Ejemplos:

```text
Hamburger
Pizza
Soda
French Fries
```

Qué hace:

```text
1. Guarda nombre, descripción y precio actual.
2. Indica si el producto está disponible.
3. Permite crear ítems de pedido a partir de productos existentes.
4. Clasifica el producto por categoría.
5. Guarda datos operativos como costo interno, imagen y tiempo de preparación.
```

Campos importantes:

```text
productId
productCode
productName
description
unitPrice
productionCost
category
preparationTimeMinutes
isAvailable
imageUrl
createdAt
```

Relación principal:

```text
Product 1 ─── 0..* OrderItem
```

Es decir: un producto puede aparecer en muchos ítems de pedido.

Importante:

```text
El precio actual del Product no debe modificar pedidos antiguos.
Por eso OrderItem guarda unitPrice y productNameSnapshot.
productionCost es información interna: sirve para análisis, no necesariamente para mostrar al cliente.
```

## `entity/OrderStatus.java`

Representa los estados posibles del pedido.

Estados esperados:

```text
PENDING
IN_PREPARATION
READY
ON_THE_WAY
DELIVERED
```

Qué hace:

```text
1. Guarda el catálogo de estados.
2. Permite saber el estado actual de un pedido.
3. Permite registrar historial usando estados origen y destino.
4. Permite ordenar visualmente el flujo mediante statusOrder.
5. Permite marcar si un estado es final usando isFinal.
```

Campos importantes:

```text
statusId
statusCode
statusName
statusOrder
isFinal
```

Cómo se usa:

```text
- CustomerOrder.currentStatus indica el estado actual.
- OrderStatusHistory.fromStatus y toStatus registran cambios.
- OrderStatusTransitionRule define transiciones permitidas.
```

## `entity/OrderStatusTransitionRule.java`

Representa una regla de transición de estado autorizada por rol.

Ejemplos:

```text
PENDING        → IN_PREPARATION : COOK
IN_PREPARATION → READY          : COOK
READY          → ON_THE_WAY     : COURIER
ON_THE_WAY     → DELIVERED      : COURIER
```

Qué hace:

```text
1. Guarda estado origen.
2. Guarda estado destino.
3. Guarda el rol autorizado para hacer ese cambio.
4. Permite activar o desactivar reglas.
```

Campos importantes:

```text
transitionRuleId
fromStatus
toStatus
role
isActive
```

Cómo se usa:

```text
StateTransitionService consulta esta entidad para saber si un cambio de estado está permitido.
```

Importante:

```text
Esta tabla aplica a Staff.
El cliente no usa esta tabla porque confirmar recepción no cambia el estado operativo del pedido.
```

## `entity/CustomerOrder.java`

Representa la cabecera del pedido.

Qué hace:

```text
1. Guarda el código único del pedido.
2. Relaciona el pedido con el cliente.
3. Relaciona el pedido con el staff que lo registró.
4. Guarda el estado actual.
5. Relaciona la dirección de entrega elegida.
6. Guarda un snapshot textual de la dirección.
7. Guarda subtotal, impuestos, descuentos, recargos y total.
8. Guarda prioridad, observaciones y fecha estimada.
9. Guarda fechas de creación y cambio de estado.
```

Campos importantes:

```text
orderId
orderCode
customer
registeredByStaff
currentStatus
deliveryAddress
deliveryAddressSnapshot
subtotalAmount
taxAmount
discountAmount
addressSurchargeAmount
totalAmount
discountCode
isPriority
generalNotes
createdAt
estimatedDeliveryAt
currentStatusChangedAt
```

Relaciones:

```text
CustomerOrder 1 ─── 1 Customer
CustomerOrder 1 ─── 0..1 CustomerAddress deliveryAddress
CustomerOrder 1 ─── 1 Staff registeredByStaff
CustomerOrder 1 ─── 1 OrderStatus currentStatus
CustomerOrder 1 ─── 1..* OrderItem
CustomerOrder 1 ─── 0..* OrderStatusHistory
CustomerOrder 1 ─── 0..1 Delivery
```

Reglas relacionadas:

```text
- orderCode debe ser único.
- Un pedido debe tener al menos un OrderItem.
- Si se informa deliveryAddress, debe pertenecer al Customer del pedido.
- deliveryAddressSnapshot debe llenarse al crear el pedido.
- totalAmount debe ser consistente con subtotalAmount, taxAmount, discountAmount y addressSurchargeAmount.
- Al crear un pedido, su estado inicial debe ser PENDING.
- Al crear un pedido, se debe registrar el primer historial.
```

Estas reglas se validan en `OrderService`.

## `entity/OrderItem.java`

Representa un producto dentro de un pedido.

Qué hace:

```text
1. Relaciona un pedido con un producto.
2. Guarda cantidad.
3. Guarda nombre del producto en el momento de compra.
4. Guarda precio unitario en el momento de compra.
5. Guarda subtotal de la línea.
6. Guarda una nota especial por producto.
7. Permite marcar si el ítem ya está listo en cocina.
```

Campos importantes:

```text
orderItemId
order
product
quantity
productNameSnapshot
unitPrice
lineTotal
specialNote
isReady
```

Relaciones:

```text
CustomerOrder 1 ─── 1..* OrderItem
Product 1 ─── 0..* OrderItem
```

Importante:

```text
productNameSnapshot y unitPrice preservan historia.
Si el producto cambia de nombre o precio después, el pedido antiguo mantiene los datos originales.
specialNote pertenece al pedido, no al producto del catálogo.
isReady sirve para seguimiento interno de preparación, no reemplaza el estado global del pedido.
```

## `entity/OrderStatusHistory.java`

Representa el historial de estados del pedido.

Qué hace:

```text
1. Guarda cada cambio de estado.
2. Guarda estado origen.
3. Guarda estado destino.
4. Guarda quién hizo el cambio.
5. Guarda cuándo ocurrió.
6. Guarda notas opcionales.
```

Campos importantes:

```text
historyId
order
fromStatus
toStatus
changedByStaff
changedAt
notes
```

Cómo se usa:

```text
- OrderService crea registros de historial al crear y cambiar pedidos.
- DeliveryService crea historial al despachar y confirmar entrega.
- OrderQueryService consulta historial para el cliente.
```

Regla clave:

```text
Todo cambio de estado debe registrarse en OrderStatusHistory.
```

## `entity/Delivery.java`

Representa la entrega física del pedido.

Qué hace:

```text
1. Relaciona una entrega con un pedido.
2. Relaciona una entrega con el repartidor.
3. Guarda fecha/hora de salida.
4. Guarda fecha/hora de entrega.
5. Guarda nombre de quien recibe.
6. Guarda confirmación de recepción por parte del cliente.
```

Campos importantes:

```text
deliveryId
order
courierStaff
dispatchedAt
deliveredAt
receiverName
customerConfirmedAt
customerConfirmationNotes
```

Relaciones:

```text
CustomerOrder 1 ─── 0..1 Delivery
Staff 1 ─── 0..* Delivery
```

Reglas relacionadas:

```text
- Un pedido puede tener máximo una Delivery.
- Delivery.order debe ser UNIQUE.
- courierStaff debe existir.
- courierStaff debe tener rol COURIER.
- El pedido debe estar READY para ser despachado.
- El pedido debe estar ON_THE_WAY para confirmar entrega.
- El cliente solo puede confirmar recepción si el pedido está DELIVERED.
```

Las reglas de negocio se validan en `DeliveryService`.

## `enums/RoleCode.java`

Define los códigos de roles usados por el sistema.

Valores esperados:

```text
ADMINISTRATOR
COOK
COURIER
```

Qué hace:

```text
1. Evita strings mágicos.
2. Centraliza los nombres de roles.
3. Reduce errores por escribir mal un rol.
```

Cómo se usa:

```text
StaffService compara el rol del staff contra RoleCode.
DataInitializer usa RoleCode para cargar roles iniciales.
```

## `enums/OrderStatusCode.java`

Define los códigos de estados usados por el sistema.

Valores esperados:

```text
PENDING
IN_PREPARATION
READY
ON_THE_WAY
DELIVERED
```

Qué hace:

```text
1. Evita strings mágicos.
2. Centraliza los estados válidos.
3. Ayuda a buscar estados en OrderStatusRepository.
```

Cómo se usa:

```text
OrderService busca PENDING al crear pedidos.
DeliveryService busca ON_THE_WAY y DELIVERED.
OrderQueryService lista pedidos por estado.
DataInitializer carga estados iniciales.
```

## `enums/ProductCategory.java`

Define categorías para clasificar el menú.

Valores esperados:

```text
STARTER
MAIN_COURSE
DRINK
DESSERT
COMBO
```

Qué hace:

```text
1. Evita escribir categorías como texto libre.
2. Permite filtrar productos por tipo.
3. Ayuda a organizar pantallas de menú y reportes.
```

Equivalencia con `FoodFlow2`:

```text
ENTRADA       → STARTER
PLATO_FUERTE  → MAIN_COURSE
BEBIDA        → DRINK
POSTRE        → DESSERT
COMBO         → COMBO
```
