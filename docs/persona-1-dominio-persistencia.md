# Persona 1: Dominio Y Persistencia

La persistencia queda alineada con la version aceptada: las entidades JPA viven en `dominio.entidad` y la frontera que usan los servicios son controladores JPA estilo NetBeans en `persistencia.controlador`.

## Alcance Implementado

```text
src/main/java/com/restaurante/pedidos/dominio
src/main/java/com/restaurante/pedidos/dominio/entidad
src/main/java/com/restaurante/pedidos/persistencia/controlador
src/main/java/com/restaurante/pedidos/persistencia/excepcion
src/main/java/com/restaurante/pedidos/configuracion/ConfiguracionBaseDatos.java
src/main/resources/META-INF/persistence.xml
```

## Controladores JPA

| Controlador | Responsabilidad |
|-------------|-----------------|
| `ClientesJpaController` | Crear y buscar clientes. |
| `DireccionesClienteJpaController` | Crear y buscar direcciones de cliente. |
| `PedidosClienteJpaController` | Crear, editar y buscar pedidos por id, codigo, estado o cola de cocina. |
| `ProductosJpaController` | Crear, editar y consultar productos disponibles por codigo o categoria. |
| `EstadosPedidoJpaController` | Buscar estados por codigo y listar el flujo ordenado. |
| `ReglasTransicionEstadoPedidoJpaController` | Buscar transiciones activas permitidas para un rol y estado actual. |
| `EntregasJpaController` | Crear, editar y buscar entregas por pedido. |
| `PersonalJpaController` | Crear, editar, borrar y buscar personal por rol o disponibilidad. |
| `RolesJpaController` | Crear y buscar roles por codigo. |

## Metodos Minimos

- [x] `PedidosClienteJpaController.findByCodigo(String codigo)`
- [x] `PedidosClienteJpaController.findByEstado(CodigoEstadoPedido estado)`
- [x] `PedidosClienteJpaController.findPendientesParaCocinero()`
- [x] `PedidosClienteJpaController.create(PedidoCliente pedido)`
- [x] `PersonalJpaController.findByRol(CodigoRol rol)`
- [x] `PersonalJpaController.findRepartidoresDisponibles()`
- [x] `ReglasTransicionEstadoPedidoJpaController.findTransicionesValidas(CodigoRol rol, CodigoEstadoPedido estadoActual)`

## Convenciones

### Unidad De Persistencia

El proyecto usa una sola unidad de persistencia:

```xml
<persistence-unit name="primerBimestrePU" transaction-type="RESOURCE_LOCAL">
```

`ConfiguracionBaseDatos` centraliza el `EntityManagerFactory` y expone dos entradas:

```java
ConfiguracionBaseDatos.obtenerFabricaAdministradorDeEntidades();
ConfiguracionBaseDatos.crearAdministradorDeEntidad();
```

### Transacciones

Los controladores JPA abren, confirman y revierten transacciones en operaciones de escritura. La capa de presentacion no maneja `EntityManager` ni transacciones.

Ejemplo esperado:

```java
var fabrica = ConfiguracionBaseDatos.obtenerFabricaAdministradorDeEntidades();
var pedidos = new PedidosClienteJpaController(fabrica);

pedidos.create(pedido);
```

### Repositorios

Los repositorios antiguos no son la API principal de Persona 2. Quedan como soporte interno para reutilizar consultas JPQL ya probadas dentro de los controladores JPA.

Regla practica:

```text
Servicio -> *JpaController -> Repositorio interno opcional -> EntityManager -> BD
```

No agregues reglas de negocio en repositorios ni en controladores JPA. Las reglas viven en `servicio`.

### Consultas Y Codigos

Las busquedas de estados y roles deben usar enums persistidos (`CodigoEstadoPedido`, `CodigoRol`), no ids hardcodeados.

Correcto:

```java
findByCodigo(CodigoEstadoPedido.PENDIENTE)
findByRol(CodigoRol.REPARTIDOR)
```

Incorrecto:

```java
find(1L)
find(3L)
```

## Base De Datos

La fuente de verdad para nombres de tablas, columnas y datos semilla es:

```text
assets/scripts/ER_Script.sql
```

La conexion actual esta configurada en:

```text
src/main/resources/META-INF/persistence.xml
```

## Reglas

```text
No pongas reglas de negocio en controladores JPA.
No expongas EntityManager a presentacion.
No uses ids hardcodeados para estados o roles.
No cambies nombres de tablas sin revisar assets/scripts/ER_Script.sql.
```

## Entregable

Persona 2 puede crear pedidos, cambiar estados y consultar datos usando controladores JPA estilo NetBeans, sin conocer SQL, HikariCP ni detalles de `EntityManager`.
