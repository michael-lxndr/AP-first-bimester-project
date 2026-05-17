# Persona 2: Negocio, Estados Y Simulación

Tu responsabilidad es la lógica de negocio: crear pedidos, validar cambios de estado, asignar cocineros, asignar repartidores y simular tiempos.

## Paquetes A Tocar

```text
src/main/java/com/restaurante/pedidos/servicio
src/main/java/com/restaurante/pedidos/configuracion/ConfiguracionHilos.java
src/main/java/com/restaurante/pedidos/configuracion/ConfiguracionSimulacion.java
```

## Qué Debés Crear

```text
ServicioPedido
MaquinaEstadosPedido
ServicioCocina
ServicioEntrega
ServicioSimulacion
ServicioGestionPersonal si ServicioPersonal queda chico
```

Responsabilidades mínimas:

```text
ServicioPedido: crear pedido y consultar estado por código.
MaquinaEstadosPedido: validar transiciones usando RepositorioReglaTransicionEstadoPedido.
ServicioCocina: mover PENDING -> IN_PREPARATION -> READY.
ServicioEntrega: mover READY -> ON_THE_WAY -> DELIVERED.
ServicioSimulacion: orquestar demoras usando ConfiguracionHilos y SimuladorTiempo.
```

## Reglas

```text
No llames JavaFX desde servicios.
No crees hilos manualmente.
No uses demoras hardcodeadas.
No cambies estados sin pasar por MaquinaEstadosPedido.
```

## Entregable

Persona 3 debe poder llamar métodos simples desde controladores, sin conocer repositorios ni transacciones.
