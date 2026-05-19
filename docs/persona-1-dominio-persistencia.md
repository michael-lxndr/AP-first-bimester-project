# Persona 1: Dominio y Persistencia

Queda listo el modelo persistente y los repositorios para que la capa de servicio use entidades sin conocer SQL, HikariCP ni `EntityManager`.

## Alcance implementado

```text
src/main/java/com/restaurante/pedidos/dominio
src/main/java/com/restaurante/pedidos/dominio/entidad
src/main/java/com/restaurante/pedidos/repositorio
src/main/java/com/restaurante/pedidos/configuracion/ConfiguracionBaseDatos.java
src/main/resources/META-INF/persistence.xml
```

## Repositorios disponibles

| Repositorio                              | Responsabilidad                                                           |
|------------------------------------------|---------------------------------------------------------------------------|
| `RepositorioPedido`                      | Guardar, actualizar y buscar pedidos por código, estado o cola de cocina. |
| `RepositorioProducto`                    | Buscar productos por id/código y listar productos disponibles.            |
| `RepositorioEstadoPedido`                | Buscar estados por código y listar el flujo ordenado de estados.          |
| `RepositorioReglaTransicionEstadoPedido` | Buscar transiciones activas permitidas para un rol y estado actual.       |
| `RepositorioEntrega`                     | Guardar/actualizar entregas y buscar la entrega asociada a un pedido.     |
| `RepositorioPersonal`                    | Buscar personal por rol y repartidores disponibles.                       |
| `RepositorioRol`                         | Buscar roles por código.                                                  |
| `RepositorioCliente`                     | Guardar y buscar clientes por id.                                         |

## Métodos mínimos esperados

- [x] `RepositorioPedido.buscarPorCodigo(String codigo)`
- [x] `RepositorioPedido.buscarPorEstado(CodigoEstadoPedido estado)`
- [x] `RepositorioPedido.buscarPendientesParaCocinero()`
- [x] `RepositorioPedido.guardar(PedidoCliente pedido)`
- [x] `RepositorioPersonal.buscarPorRol(CodigoRol rol)`
- [x] `RepositorioPersonal.buscarRepartidoresDisponibles()`
- [x] `RepositorioReglaTransicionEstadoPedido.buscarTransicionesValidas(CodigoRol rol, CodigoEstadoPedido estadoActual)`

## Convenciones de persistencia

### EntityManager

Los repositorios reciben él `EntityManager` por constructor:

```java
new RepositorioPedido(administradorDeEntidad)
```

El repositorio **no crea**, **no cierra** y **no expone** él `EntityManager`. Eso mantiene separada la infraestructura de la capa de servicio.

### Transacciones

Los repositorios no hacen `begin`, `commit` ni `rollback`. La transacción pertenece a la capa que orquesta el caso de uso, normalmente servicio o prueba de integración.

Ejemplo esperado:

```java
EntityManager em = ConfiguracionBaseDatos.crearAdministradorDeEntidad();
em.getTransaction().begin();

RepositorioPedido repositorioPedido = new RepositorioPedido(em);
repositorioPedido.guardar(pedido);

em.getTransaction().commit();
em.close();
```

### JPQL

Las consultas usan nombres de entidades y campos Java, no nombres de tablas o columnas SQL. Ejemplo:

```java
SELECT p FROM PedidoCliente p WHERE p.codigoPedido = :codigo
```

No se debe consultar directamente `pedidos_cliente.codigo_pedido` desde el repositorio.

### Optional y listas

- Las búsquedas de un solo resultado devuelven `Optional<T>`.
- Las búsquedas de varios resultados devuelven `List<T>`.
- Si no hay datos, se devuelve `Optional.empty()` o una lista vacía.

## Detalle por clase

### `RepositorioPedido`

| Método                                       | Cómo funciona                                                                                                                                                  |
|----------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `buscarPorCodigo(String codigo)`             | Busca un pedido por `codigoPedido` y trae el estado actual con `JOIN FETCH`. Devuelve `Optional<PedidoCliente>`.                                               |
| `buscarPorEstado(CodigoEstadoPedido estado)` | Filtra por `pedido.estadoActual.codigoEstado` y ordena por `creadoEn ASC`.                                                                                     |
| `buscarPendientesParaCocinero()`             | Filtra pedidos en `PENDIENTE` y ordena por `prioritario DESC, creadoEn ASC`. Así salen primero los prioritarios y, dentro de cada grupo, el primero que llegó. |
| `guardar(PedidoCliente pedido)`              | Persiste un pedido nuevo con `persist` y devuelve la misma entidad.                                                                                            |
| `actualizar(PedidoCliente pedido)`           | Actualiza una entidad administrada o separada con `merge`.                                                                                                     |
| `buscarPorId(Long id)`                       | Busca por clave primaria usando `EntityManager.find`.                                                                                                          |

### `RepositorioProducto`

| Método                                                       | Cómo funciona                                                              |
|--------------------------------------------------------------|----------------------------------------------------------------------------|
| `buscarPorId(Long id)`                                       | Busca un producto por clave primaria.                                      |
| `buscarPorCodigo(String codigo)`                             | Busca por `codigoProducto`.                                                |
| `buscarDisponibles()`                                        | Lista productos con `disponible = true`, ordenados por categoría y nombre. |
| `buscarDisponiblesPorCategoria(CategoriaProducto categoria)` | Lista productos disponibles de una categoría específica.                   |
| `guardar(Producto producto)`                                 | Persiste un producto nuevo.                                                |
| `actualizar(Producto producto)`                              | Actualiza un producto con `merge`.                                         |

### `RepositorioEstadoPedido`

| Método                                       | Cómo funciona                                           |
|----------------------------------------------|---------------------------------------------------------|
| `buscarPorId(Long id)`                       | Busca un estado por clave primaria.                     |
| `buscarPorCodigo(CodigoEstadoPedido codigo)` | Busca el estado persistido asociado al enum de dominio. |
| `listarOrdenados()`                          | Lista los estados por `ordenEstado ASC`.                |
| `guardar(EstadoPedido estadoPedido)`         | Persiste un estado nuevo.                               |

### `RepositorioReglaTransicionEstadoPedido`

| Método                                                                      | Cómo funciona                                                                                                                              |
|-----------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------|
| `buscarPorId(Long id)`                                                      | Busca una regla por clave primaria.                                                                                                        |
| `buscarTransicionesValidas(CodigoRol rol, CodigoEstadoPedido estadoActual)` | Filtra reglas activas por rol permitido y estado de origen. Usa `JOIN FETCH` para traer origen, destino y rol dentro de la misma consulta. |
| `guardar(ReglaTransicionEstadoPedido regla)`                                | Persiste una regla nueva.                                                                                                                  |

Este repositorio no decide si el usuario puede cambiar el estado; solo devuelve las reglas persistidas que coinciden con los filtros.

### `RepositorioEntrega`

| Método                             | Cómo funciona                                                                    |
|------------------------------------|----------------------------------------------------------------------------------|
| `buscarPorId(Long id)`             | Busca una entrega por clave primaria.                                            |
| `buscarPorPedidoId(Long pedidoId)` | Busca la entrega asociada a un pedido y trae pedido/repartidor con `JOIN FETCH`. |
| `guardar(Entrega entrega)`         | Persiste una entrega nueva.                                                      |
| `actualizar(Entrega entrega)`      | Actualiza una entrega con `merge`.                                               |

### `RepositorioPersonal`

| Método                            | Cómo funciona                                                                                                                                                                                       |
|-----------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `buscarPorRol()`                  | Método heredado de compatibilidad: lista personal con su rol, sin filtrar.                                                                                                                          |
| `buscarPorRol(CodigoRol rol)`     | Filtra por `rol.codigoRol`. Este es el método que debe usar la capa de servicio cuando necesita un rol específico.                                                                                  |
| `buscarRepartidoresDisponibles()` | Devuelve personal con rol `REPARTIDOR`, `activo = true` y sin entregas activas. Se considera activa una entrega cuyo pedido está `EN_CAMINO` o cuya `estadoEntrega` sea `DESPACHADO`/`EN_TRANSITO`. |
| `buscarPorIdConRol(Long id)`      | Busca personal por id y trae el rol con `JOIN FETCH`.                                                                                                                                               |
| `guardar`, `actualizar`, `borrar` | Operaciones básicas de persistencia; la transacción la maneja la capa superior.                                                                                                                     |

## Configuración de base de datos

El persistence unit del proyecto está en español:

```xml
<persistence-unit name="primerBimestrePU" transaction-type="RESOURCE_LOCAL">
```

`ConfiguracionBaseDatos` usa ese mismo nombre para crear la fábrica de entidades. La fábrica se guarda como singleton estático y su creación/cierre están sincronizados para evitar inicializaciones duplicadas si varias partes del programa la piden al mismo tiempo.

## Hilos y persistencia

No conviene implementar hilos dentro de los repositorios. En JPA, `EntityManager` **no es thread-safe**: no debe compartirse entre hilos.

La convención segura es:

1. Cada hilo o tarea crea su propio `EntityManager`.
2. Ese hilo abre/cierra su propia transacción si va a escribir.
3. El repositorio sé instancia con el `EntityManager` de ese hilo.
4. Nunca se guarda un repositorio como singleton si contiene un `EntityManager`.

La parte compartible entre hilos es él `EntityManagerFactory`, que ya queda centralizado en `ConfiguracionBaseDatos`.

## Reglas

```text
No pongas reglas de negocio en repositorios.
No devuelvas EntityManager fuera del repositorio.
No cambies nombres de tablas sin revisar: assets/scripts/ER_Script.sql.
```

## Entregable

Persona 2 puede usar estos repositorios desde servicios sin conocer SQL, HikariCP ni `EntityManager`. Persona 2 sigue siendo responsable de coordinar casos de uso, transacciones y reglas de negocio.
