# Package `exception`

El paquete `exception` contiene errores personalizados del dominio.

La idea es que el sistema no falle con mensajes técnicos feos, sino con errores claros para el usuario o para la capa de presentación.

## Estructura

```text
exception
├── BusinessException.java
├── ResourceNotFoundException.java
├── InvalidOrderStatusException.java
├── InvalidRoleException.java
└── DuplicateResourceException.java
```

## `BusinessException.java`

Excepción base para reglas de negocio.

Cuándo usarla:

```text
- Cuando una acción no se puede realizar por una regla del sistema.
- Cuando el error no encaja en una excepción más específica.
```

Ejemplos:

```text
El cliente está inactivo.
El producto no está disponible.
El pedido ya fue confirmado por el cliente.
La entrega aún no fue registrada por el repartidor.
```

Quién la lanza:

```text
- CustomerService
- ProductService
- OrderService
- DeliveryService
```

## `ResourceNotFoundException.java`

Excepción para recursos inexistentes.

Cuándo usarla:

```text
- Cuando no existe un pedido con ese código.
- Cuando no existe un cliente con ese ID.
- Cuando no existe un producto con ese ID.
- Cuando no existe un staff con ese ID.
- Cuando no existe Delivery para un pedido.
```

Ejemplos:

```text
No existe un pedido con código ORD-0001.
No existe un producto con ID 5.
No existe una entrega registrada para este pedido.
```

Quién la lanza:

```text
- CustomerService
- ProductService
- StaffService
- OrderService
- OrderQueryService
- DeliveryService
```

## `InvalidOrderStatusException.java`

Excepción para estados o transiciones inválidas.

Cuándo usarla:

```text
- Cuando se intenta saltar estados.
- Cuando se intenta retroceder estados.
- Cuando se intenta entregar un pedido que no está ON_THE_WAY.
- Cuando se intenta despachar un pedido que no está READY.
```

Ejemplos:

```text
No se puede pasar de PENDING a DELIVERED.
No se puede despachar un pedido que no está READY.
No se puede confirmar entrega si el pedido no está ON_THE_WAY.
```

Quién la lanza:

```text
- StateTransitionService
- OrderService
- DeliveryService
```

## `InvalidRoleException.java`

Excepción para roles no autorizados.

Cuándo usarla:

```text
- Cuando un staff no tiene el rol requerido.
- Cuando un cocinero intenta despachar.
- Cuando un administrador intenta marcar un pedido como listo.
- Cuando un staff que no es repartidor intenta entregar.
```

Ejemplos:

```text
El staff seleccionado no tiene rol COURIER.
Solo un administrador puede registrar pedidos.
Solo un cocinero puede cambiar el pedido a IN_PREPARATION.
```

Quién la lanza:

```text
- StaffService
- OrderService
- DeliveryService
```

## `DuplicateResourceException.java`

Excepción para datos que deben ser únicos.

Cuándo usarla:

```text
- Cuando ya existe un pedido con el mismo código.
- Cuando ya existe un cliente con el mismo email.
- Cuando ya existe un staff con el mismo username.
- Cuando se intenta crear otra Delivery para el mismo pedido.
```

Ejemplos:

```text
Ya existe un pedido con código ORD-0001.
Ya existe una entrega para este pedido.
Ya existe un producto con ese nombre si se decide hacerlo único.
```

Quién la lanza:

```text
- CustomerService
- ProductService
- StaffService
- OrderService
- DeliveryService
```

## Cómo las usa Presentation

La consola o JavaFX deben capturar estas excepciones y mostrar mensajes claros.

Flujo recomendado:

```text
1. Usuario presiona botón o elige opción.
2. Presentation llama a service.
3. Service valida.
4. Si hay error, lanza excepción.
5. Presentation captura excepción.
6. Presentation muestra mensaje entendible.
```

Ejemplo visual:

```text
Error: No se puede confirmar recepción porque el pedido aún no fue entregado por el repartidor.
```

## Qué no hacer

```text
- No mostrar stacktrace al usuario final.
- No capturar excepciones y esconderlas sin mensaje.
- No devolver null cuando algo falla.
- No usar RuntimeException genérica para todo si ya existe una excepción más clara.
```
