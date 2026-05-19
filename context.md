# Code Context

## Files Retrieved
1. `C:\Users\Michael\.config\opencode\skills\cognitive-doc-design\SKILL.md` (lines 1-65) - output structure guidance for concise, reviewable notes.
2. `C:\Users\Michael\.config\opencode\skills\work-unit-commits\SKILL.md` (lines 1-74) - work-unit/review-load guidance; no commits requested.
3. `docs/persona-1-dominio-persistencia.md` (lines 1-42) - Persona 1 scope, required repository classes/methods, repository rules.
4. `pom.xml` (lines 1-124) - Java 21/Maven dependencies and Surefire test plugin.
5. `README.md` (lines 1-239) - architecture rules, DB connection, test command, pending technical work.
6. `assets/scripts/ER_Script.sql` (lines 1-456) - authoritative table/column names, FK relationships, seed roles/states/transitions/products.
7. `src/main/resources/META-INF/persistence.xml` (lines 1-39) - JPA persistence unit, HikariCP settings, entity registrations.
8. `src/main/java/com/restaurante/pedidos/configuracion/ConfiguracionBaseDatos.java` (lines 1-30) - EntityManagerFactory lifecycle and persistence-unit lookup.
9. `src/main/java/com/restaurante/pedidos/dominio/CategoriaProducto.java` (lines 1-9) - product category enum mapped by `Producto`.
10. `src/main/java/com/restaurante/pedidos/dominio/CodigoEstadoPedido.java` (lines 1-9) - order state enum mapped by `EstadoPedido`.
11. `src/main/java/com/restaurante/pedidos/dominio/CodigoRol.java` (lines 1-7) - role enum mapped by `Rol`.
12. `src/main/java/com/restaurante/pedidos/dominio/GeneradorCodigoPedido.java` (lines 1-27) - existing domain generator for `PedidoCliente.codigoPedido`.
13. `src/main/java/com/restaurante/pedidos/dominio/entidad/*.java` (key lines listed below) - all mapped JPA entities for Persona 1.
14. `src/main/java/com/restaurante/pedidos/repositorio/RepositorioCliente.java` (lines 1-22) - existing repository style.
15. `src/main/java/com/restaurante/pedidos/repositorio/RepositorioPersonal.java` (lines 1-46) - existing repository methods; expected signature mismatch.
16. `src/main/java/com/restaurante/pedidos/repositorio/RepositorioRol.java` (lines 1-35) - existing enum lookup repository style.
17. `src/test/java/com/restaurante/pedidos/dominio/DominioTest.java` (lines 1-269) - DB/integration-style smoke test using `ConfiguracionBaseDatos` and all entities.
18. `src/test/java/com/restaurante/pedidos/dominio/GeneradorCodigoPedidoTest.java` (lines 1-12) - unit test for code generator.
19. `src/test/java/com/restaurante/pedidos/servicio/SimuladorTiempoTest.java` (lines 1-31) - unrelated service unit tests; confirms `mvn test` scope.

## Key Code

### Persona 1 requirements
`docs/persona-1-dominio-persistencia.md` requires creating these repositories (lines 18-22):

```text
RepositorioPedido
RepositorioProducto
RepositorioEstadoPedido
RepositorioReglaTransicionEstadoPedido
RepositorioEntrega si hace falta para confirmar entregas
```

Minimum methods (lines 27-33):

```text
RepositorioPedido.buscarPorCodigo(String codigo)
RepositorioPedido.buscarPorEstado(CodigoEstadoPedido estado)
RepositorioPedido.buscarPendientesParaCocinero()
RepositorioPedido.guardar(PedidoCliente pedido)
RepositorioPersonal.buscarPorRol(CodigoRol rol)
RepositorioPersonal.buscarRepartidoresDisponibles()
RepositorioReglaTransicionEstadoPedido.buscarTransicionesValidas(CodigoRol rol, CodigoEstadoPedido estadoActual)
```

Rules (lines 37-39): repositories must not contain business rules, must not expose `EntityManager`, and must not rename DB tables without checking `assets/scripts/ER_Script.sql`.

### Existing domain/entity model
Enums match SQL seed values:

```java
// src/main/java/com/restaurante/pedidos/dominio/CodigoEstadoPedido.java:3-8
PENDIENTE, EN_PREPARACION, LISTO, EN_CAMINO, ENTREGADO

// src/main/java/com/restaurante/pedidos/dominio/CodigoRol.java:3-6
ADMINISTRADOR, COCINERO, REPARTIDOR
```

Important JPA mappings:

- `PedidoCliente` maps `pedidos_cliente` and has lazy required links to `Cliente`, `Personal`, `EstadoPedido`, `DireccionCliente` (`PedidoCliente.java:18-46`).
- `EstadoPedido` maps `estados_pedido`; `codigoEstado` uses `@Enumerated(EnumType.STRING)` (`EstadoPedido.java:16-25`).
- `ReglaTransicionEstadoPedido` maps `reglas_transicion_estado_pedido`; links origin state, destination state, and role (`ReglaTransicionEstadoPedido.java:14-34`).
- `Entrega` maps `entregas`; one-to-one `PedidoCliente`, many-to-one `Personal repartidor` (`Entrega.java:17-31`).
- `Producto` maps `productos`; `categoria` uses `@Enumerated(EnumType.STRING)` (`Producto.java:19-49`).
- `Rol` maps `roles`; `codigoRol` uses `@Enumerated(EnumType.STRING)` (`Rol.java:15-25`).
- `Personal` maps `personal`; lazy required `rol` link and `activo` flag (`Personal.java:17-52`).

### Existing repositories and conventions
All existing repositories are constructor-injected with an `EntityManager`; they do not create/close it themselves.

```java
// RepositorioCliente.java:8-21
public class RepositorioCliente {
    private final EntityManager administradorDeEntidad;
    public void guardar(Cliente cliente) { administradorDeEntidad.persist(cliente); }
    public Optional<Cliente> buscarPorId(Long id) { ... }
}
```

`RepositorioRol` shows enum lookup + `NoResultException` to `Optional.empty()` convention (`RepositorioRol.java:17-30`):

```java
createQuery("SELECT r FROM Rol r WHERE r.codigoRol = :codigoRol", Rol.class)
    .setParameter("codigoRol", codigoRol)
    .getSingleResult();
```

`RepositorioPersonal` currently has CRUD and `buscarPorIdConRol`, but `buscarPorRol()` has no `CodigoRol` parameter and returns all staff with role (`RepositorioPersonal.java:17-21`):

```java
public List<Personal> buscarPorRol() {
    return administradorDeEntidad
        .createQuery("SELECT p FROM Personal p JOIN FETCH p.rol ORDER BY p.id DESC", Personal.class)
        .getResultList();
}
```

This does **not** satisfy the persona doc method `buscarPorRol(CodigoRol rol)`.

### JPA/Hikari/EntityManager setup
`persistence.xml` declares persistence unit `firstBimesterPU` (`src/main/resources/META-INF/persistence.xml:7`) and HikariCP via Hibernate (`lines 23-31`):

```xml
<property name="hibernate.connection.provider_class" value="org.hibernate.hikaricp.internal.HikariCPConnectionProvider"/>
<property name="hibernate.hikari.jdbcUrl" value="jdbc:mysql://localhost:3307/proyecto_primer_bimestre?..."/>
<property name="hibernate.hikari.username" value="root"/>
<property name="hibernate.hikari.password" value="root"/>
```

It uses `hibernate.hbm2ddl.auto=update` and SQL logging enabled (`persistence.xml:33-35`).

**Blocker:** `ConfiguracionBaseDatos` calls `Persistence.createEntityManagerFactory("primerBimestrePU")` (`ConfiguracionBaseDatos.java:19`), but `persistence.xml` defines `firstBimesterPU`. This mismatch will break any runtime/test path that creates an `EntityManagerFactory` through `ConfiguracionBaseDatos`.

### SQL schema facts to preserve
`assets/scripts/ER_Script.sql` is the naming source:

- Tables: `roles` (line 25), `personal` (39), `clientes` (67), `direcciones_cliente` (85), `productos` (117), `estados_pedido` (150), `reglas_transicion_estado_pedido` (170), `pedidos_cliente` (203), `items_pedido` (263), `historial_estados_pedido` (298), `entregas` (338).
- Seed roles: `ADMINISTRADOR`, `COCINERO`, `REPARTIDOR` (`ER_Script.sql:382-385`).
- Seed states: `PENDIENTE`, `EN_PREPARACION`, `LISTO`, `EN_CAMINO`, `ENTREGADO` (`ER_Script.sql:389-394`).
- Seed transition rules: COCINERO handles `PENDIENTE -> EN_PREPARACION` and `EN_PREPARACION -> LISTO`; REPARTIDOR handles `LISTO -> EN_CAMINO` and `EN_CAMINO -> ENTREGADO` (`ER_Script.sql:396-430`).

### Tests observed; not executed
`README.md:181-185` says test command is:

```bash
mvn test
```

Do not run yet per scout instructions. Existing `DominioTest` is DB/integration-like: it creates an `EntityManager` through `ConfiguracionBaseDatos`, begins a transaction, persists all mapped entity types, flushes, commits, closes, and calls `ConfiguracionBaseDatos.cerrar()` (`DominioTest.java:16-269`). Because of the persistence-unit mismatch, this test path is likely affected.

## Architecture

- Dependency rule from `README.md:71-80`: `presentacion -> servicio -> repositorio -> dominio`.
- Repositories sit below services and should only encapsulate JPA/JPQL/`EntityManager` access.
- Transaction ownership appears outside repositories: existing repositories call `persist`, `merge`, `remove`, or queries only; they do not begin/commit transactions. `DominioTest` manages transactions directly (`DominioTest.java:16-269`). Future services likely should own transactions/case-use orchestration.
- Entity relationships are mostly lazy, so repository methods needed by services/UI should use `JOIN FETCH` only when callers need associated data inside the transaction. Avoid leaking lazy-loading problems upward.
- `ConfiguracionBaseDatos` centralizes `EntityManagerFactory` creation and `EntityManager` creation/closing; repositories receive `EntityManager` from caller.

## Missing Repositories / Methods

### Missing classes
Create in `src/main/java/com/restaurante/pedidos/repositorio`:

1. `RepositorioPedido`
2. `RepositorioProducto`
3. `RepositorioEstadoPedido`
4. `RepositorioReglaTransicionEstadoPedido`
5. Optional/likely `RepositorioEntrega` for delivery confirmation/availability checks.

### Missing or incorrect methods
1. `RepositorioPedido.buscarPorCodigo(String codigo)` - should return `Optional<PedidoCliente>`; likely query by `codigoPedido`.
2. `RepositorioPedido.buscarPorEstado(CodigoEstadoPedido estado)` - query via `pedido.estadoActual.codigoEstado`.
3. `RepositorioPedido.buscarPendientesParaCocinero()` - likely orders with `estadoActual.codigoEstado = PENDIENTE`, maybe order by `prioritario DESC, creadoEn ASC`; ordering is a business/product decision if not documented.
4. `RepositorioPedido.guardar(PedidoCliente pedido)` - should return `PedidoCliente` for consistency with `RepositorioRol.guardar` and `RepositorioPersonal.guardar`; doc only says `guardar(PedidoCliente pedido)`.
5. `RepositorioPersonal.buscarPorRol(CodigoRol rol)` - current `buscarPorRol()` lacks parameter and does not filter (`RepositorioPersonal.java:17-21`).
6. `RepositorioPersonal.buscarRepartidoresDisponibles()` - no current implementation; availability definition is not documented.
7. `RepositorioReglaTransicionEstadoPedido.buscarTransicionesValidas(CodigoRol rol, CodigoEstadoPedido estadoActual)` - query active rules by role code and origin state code, likely `JOIN FETCH` destination state.

Useful optional methods for Persona 2:

- `RepositorioProducto.buscarDisponibles()` and/or `buscarPorCodigo(String codigo)`.
- `RepositorioEstadoPedido.buscarPorCodigo(CodigoEstadoPedido codigo)`.
- `RepositorioEntrega.guardar(Entrega entrega)`, `buscarPorPedido(Long pedidoId)` or `buscarPorPedido(PedidoCliente pedido)` depending service style.

## Likely Implementation Plan

1. **Fix configuration blocker first**: align `ConfiguracionBaseDatos` persistence unit name with `persistence.xml` (`firstBimesterPU`) or vice versa. Prefer changing code to `firstBimesterPU` because XML and README already use English-ish naming and XML is the actual JPA unit declaration.
2. **Normalize `RepositorioPersonal`**:
   - Replace/add `buscarPorRol(CodigoRol rol)` using `JOIN FETCH p.rol WHERE p.rol.codigoRol = :rol ORDER BY p.id DESC`.
   - Add `buscarRepartidoresDisponibles()` using `CodigoRol.REPARTIDOR` + `p.activo = true`; if delivery occupancy matters, add a `NOT EXISTS` against `Entrega` with non-final states, but confirm semantics first.
3. **Add lookup repositories**:
   - `RepositorioEstadoPedido.buscarPorCodigo(CodigoEstadoPedido codigo)` returns `Optional<EstadoPedido>`.
   - `RepositorioProducto` with at least `guardar`, `buscarPorId`, and likely `buscarDisponibles`/`buscarPorCodigo`.
4. **Add `RepositorioPedido`**:
   - `guardar(PedidoCliente)` with `persist` for new instances; consider `merge` only if updates are needed by services later.
   - `buscarPorCodigo`, `buscarPorEstado`, `buscarPendientesParaCocinero`.
   - Use JPQL over entity field names, not table/column names.
5. **Add transition repository**:
   - Query `ReglaTransicionEstadoPedido` with joins on `rol.codigoRol` and `estadoOrigen.codigoEstado`, `activa = true`.
   - Fetch destination state so Persona 2 can decide next states without touching JPA internals.
6. **Add `RepositorioEntrega` only if Persona 2 needs it now**:
   - Needed if confirming deliveries or computing courier availability from active deliveries.
7. **Add/adjust tests later**:
   - Use unit/integration tests carefully because current DB config points to local MySQL. The command to run after implementation is `mvn test`.

## Documentation Gaps

- `docs/persona-1-dominio-persistencia.md` marks `RepositorioPersonal.buscarPorRol(CodigoRol rol)` as done, but code has `buscarPorRol()` without a parameter and no filtering.
- `README.md` business model lists English states/roles (`PENDING`, `COOK`, etc.) while code/SQL use Spanish enum values (`PENDIENTE`, `COCINERO`, etc.). This can confuse Persona 2/3.
- No documented transaction boundary convention beyond existing patterns; repositories currently do not manage transactions.
- No documented definition for `buscarRepartidoresDisponibles()` availability: active courier only, no active delivery, or both?
- No documented ordering for `buscarPendientesParaCocinero()`; priority and creation time are likely but not specified.
- `persistence.xml` uses `hbm2ddl.auto=update` while `ER_Script.sql` is authoritative; clarify whether developers should run SQL manually or rely on Hibernate update.

## Start Here

Open `src/main/java/com/restaurante/pedidos/configuracion/ConfiguracionBaseDatos.java` first. The persistence-unit mismatch at line 19 blocks repository/runtime verification before any repository work can be trusted.

Then open `docs/persona-1-dominio-persistencia.md` and `src/main/java/com/restaurante/pedidos/repositorio/RepositorioPersonal.java` to align the required API with the existing repository style.

## Supervisor coordination

No supervisor decision requested. Scout was completed without running Maven/tests and without modifying source files. `context.md` is the only written artifact. Engram memory tools were not available in this child session, so no memory save was performed.
