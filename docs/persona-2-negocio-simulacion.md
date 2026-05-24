# Persona 2: Negocio, Estados Y Simulacion

La capa de negocio queda sobre la persistencia aceptada: servicios coordinan casos de uso, validan transiciones con la maquina de estados y usan controladores JPA estilo NetBeans.

## Alcance Implementado

```text
src/main/java/com/restaurante/pedidos/servicio/ServicioPedido.java
src/main/java/com/restaurante/pedidos/servicio/MaquinaEstadosPedido.java
src/main/java/com/restaurante/pedidos/servicio/ServicioCocina.java
src/main/java/com/restaurante/pedidos/servicio/ServicioEntrega.java
src/main/java/com/restaurante/pedidos/servicio/ServicioSimulacion.java
src/main/java/com/restaurante/pedidos/servicio/ServicioPersonal.java
src/main/java/com/restaurante/pedidos/servicio/FachadaServiciosPedido.java
src/main/java/com/restaurante/pedidos/configuracion/ConfiguracionHilos.java
src/main/java/com/restaurante/pedidos/configuracion/ConfiguracionSimulacion.java
src/main/java/com/restaurante/pedidos/presentacion/AplicacionConsola.java
```

## Responsabilidades

| Servicio | Responsabilidad |
|----------|-----------------|
| `ServicioPedido` | Crear pedidos con estado inicial `PENDIENTE` y consultar estado por codigo. |
| `MaquinaEstadosPedido` | Validar si una transicion esta permitida para el rol actor y el estado actual. |
| `ServicioCocina` | Mover pedidos de `PENDIENTE` a `EN_PREPARACION` y luego a `LISTO`. |
| `ServicioEntrega` | Mover pedidos de `LISTO` a `EN_CAMINO` y luego a `ENTREGADO`. |
| `ServicioSimulacion` | Orquestar la simulacion asincrona usando `ConfiguracionHilos` y `SimuladorTiempo`. |
| `ServicioPersonal` | Consultar personal por rol y repartidores disponibles. |
| `FachadaServiciosPedido` | Dar una entrada simple para JavaFX y consola sin exponer persistencia. |

## Metodos Minimos

- [x] `ServicioPedido.crearPedido(PedidoCliente pedido)`
- [x] `ServicioPedido.consultarEstadoPorCodigo(String codigo)`
- [x] `MaquinaEstadosPedido.esTransicionValida(...)`
- [x] `ServicioCocina.iniciarPreparacion(String codigoPedido, CodigoRol actor)`
- [x] `ServicioCocina.marcarListo(String codigoPedido, CodigoRol actor)`
- [x] `ServicioEntrega.iniciarEntrega(String codigoPedido, CodigoRol actor)`
- [x] `ServicioEntrega.confirmarEntrega(String codigoPedido, CodigoRol actor)`
- [x] `ServicioSimulacion.simularPedido(String codigoPedido)`

## Flujo De Estados

```text
PENDIENTE -> EN_PREPARACION -> LISTO -> EN_CAMINO -> ENTREGADO
```

Roles autorizados por el flujo semilla:

| Rol | Transiciones |
|-----|--------------|
| `COCINERO` | `PENDIENTE -> EN_PREPARACION`, `EN_PREPARACION -> LISTO` |
| `REPARTIDOR` | `LISTO -> EN_CAMINO`, `EN_CAMINO -> ENTREGADO` |

La maquina de estados no inventa permisos. Usa las reglas persistidas que entrega `ReglasTransicionEstadoPedidoJpaController`.

## Simulacion

`ServicioSimulacion` devuelve un `CompletableFuture<List<String>>` con los estados recorridos. No crea hilos manualmente: usa el executor centralizado en `ConfiguracionHilos`.

Las demoras salen de `SimuladorTiempo` y de la configuracion de simulacion. No deben hardcodearse dentro de los servicios.

## Consola

`AplicacionConsola` es una demo de uso del sistema. Debe llamar servicios o fachada, no controladores JPA directamente.

La consola existe para mostrar el flujo completo sin JavaFX:

```text
crear pedido -> consultar estado -> simular cocina/entrega -> consultar resultado
```

## Reglas

```text
No llames JavaFX desde servicios.
No crees hilos manualmente.
No uses demoras hardcodeadas.
No cambies estados sin pasar por MaquinaEstadosPedido.
No uses ids de estados o roles; usa CodigoEstadoPedido y CodigoRol.
```

## Entregable

Persona 3 puede llamar metodos simples desde JavaFX o consola usando `FachadaServiciosPedido`, sin conocer controladores JPA, repositorios, transacciones ni SQL.
