# Persona 2: Negocio, Estados Y Simulación

Tu responsabilidad es la lógica de negocio. Acá vive el cerebro del sistema: crear pedidos, validar cambios de estado, asignar cocineros, asignar repartidores y simular tiempos.

## Paquetes A Tocar

```text
src/main/java/com/restaurante/pedidos/service
src/main/java/com/restaurante/pedidos/config/ThreadPoolConfig.java
src/main/java/com/restaurante/pedidos/config/SimulationConfig.java
src/main/java/com/restaurante/pedidos/util/TimeSimulator.java
```

## Qué Debés Crear

```text
OrderService
OrderStateMachine
KitchenService
DeliveryService
SimulationService
StaffManagementService si StaffService queda chico
```

Responsabilidades mínimas:

```text
OrderService: crear pedido y consultar estado por código.
OrderStateMachine: validar transición usando TransitionRuleRepository.
KitchenService: asignar pedido a cocinero y mover PENDING -> IN_PREPARATION -> READY.
DeliveryService: asignar repartidor y mover READY -> ON_THE_WAY -> DELIVERED.
SimulationService: orquestar delays usando ThreadPoolConfig y TimeSimulator.
```

## Reglas

```text
No llames JavaFX desde services.
No crees new Thread() manualmente.
No uses delays hardcodeados en services.
No cambies estados sin pasar por OrderStateMachine.
No mezcles transacciones de varios casos de uso sin necesidad.
```

## Tests Esperados

Tests unitarios obligatorios para:

```text
OrderStateMachine
TimeSimulator
OrderCodeGenerator si se usa al crear pedidos
validaciones de OrderService
```

Los tests no deben depender de JavaFX. Si un test necesita abrir una ventana, NO es test unitario de negocio.

## Entregable

Al terminar, Persona 3 debe poder llamar métodos simples desde controllers, por ejemplo:

```text
orderService.createOrder(...)
orderService.findStatusByCode(code)
kitchenService.startPreparation(orderId, cookId)
deliveryService.markDelivered(orderId, courierId)
```
