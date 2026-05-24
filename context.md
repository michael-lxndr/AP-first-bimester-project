# Code Context

## Estado Actual

El proyecto ya esta refactorizado hacia la version aceptada usando el paquete raiz `com.restaurante.pedidos`, entidades JPA sin Lombok y controladores JPA estilo NetBeans en `src/main/java/com/restaurante/pedidos/persistencia/controlador`.

La suite actual pasa con:

```bash
mvn test
```

Resultado verificado: 17 tests, 0 failures, 0 errors.

## Persistencia

La unidad de persistencia correcta y unica es:

```xml
<persistence-unit name="primerBimestrePU" transaction-type="RESOURCE_LOCAL">
```

Archivos relevantes:

- `src/main/resources/META-INF/persistence.xml` - declara `primerBimestrePU`, entidades JPA y conexion MySQL en `localhost:3306`.
- `src/main/java/com/restaurante/pedidos/configuracion/ConfiguracionBaseDatos.java` - centraliza `EntityManagerFactory` y crea `EntityManager`.
- `.idea/jpa.xml` - debe apuntar tambien a `primerBimestrePU`.
- `nbactions.xml` - configura ejecucion Maven/NetBeans.

La base esperada es `proyecto_primer_bimestre` con usuario `root` y password `root`.

## Arquitectura

Flujo principal:

```text
presentacion -> servicio -> persistencia.controlador -> dominio.entidad -> BD
```

Los repositorios antiguos permanecen como apoyo interno de consultas JPQL para los controladores JPA, pero ya no son la API principal que debe consumir Persona 2.

## Version Aceptada

La referencia externa esta en:

```text
versionAceptada/RestaurantDelivery
```

No se copio literalmente el arbol de paquetes `Clases`, `Logica` y `LogicaServicios`. La decision vigente es conservar el paquete raiz del proyecto y adaptar el estilo aceptado a esta estructura.

## Persona 1

Documentacion actualizada:

```text
docs/persona-1-dominio-persistencia.md
```

Responsabilidad actual: entidades, `persistence.xml`, `ConfiguracionBaseDatos`, excepciones de persistencia y controladores JPA estilo NetBeans.

Controladores clave:

- `ClientesJpaController`
- `PedidosClienteJpaController`
- `ProductosJpaController`
- `EstadosPedidoJpaController`
- `ReglasTransicionEstadoPedidoJpaController`
- `EntregasJpaController`
- `PersonalJpaController`
- `RolesJpaController`

## Persona 2

Documentacion actualizada:

```text
docs/persona-2-negocio-simulacion.md
```

Servicios implementados:

- `ServicioPedido`
- `MaquinaEstadosPedido`
- `ServicioCocina`
- `ServicioEntrega`
- `ServicioSimulacion`
- `ServicioPersonal`
- `FachadaServiciosPedido`

Regla importante: los estados y roles se resuelven por enums (`CodigoEstadoPedido`, `CodigoRol`), no por ids hardcodeados.

Flujo de estados:

```text
PENDIENTE -> EN_PREPARACION -> LISTO -> EN_CAMINO -> ENTREGADO
```

## Presentacion

JavaFX se mantiene. Los controladores de presentacion deben llamar servicios o `FachadaServiciosPedido`, no persistencia directa.

Tambien existe `AplicacionConsola` como demo eficiente del flujo sin JavaFX.

## Pruebas Relevantes

- `src/test/java/com/restaurante/pedidos/configuracion/*` - bootstrap/IDE/persistence unit.
- `src/test/java/com/restaurante/pedidos/persistencia/*` - controladores JPA.
- `src/test/java/com/restaurante/pedidos/presentacion/AplicacionConsolaTest.java` - demo consola.
- `src/test/java/com/restaurante/pedidos/servicio/MaquinaEstadosPedidoTest.java` - reglas de transicion.
- `src/test/java/com/restaurante/pedidos/servicio/ServicioEscenariosIntegracionTest.java` - escenarios Persona 2 con BD.

## Reglas Vigentes

```text
No usar Lombok.
No usar ids hardcodeados para estados o roles.
No poner reglas de negocio en controladores JPA.
No llamar JavaFX desde servicios.
No crear hilos manualmente fuera de ConfiguracionHilos.
No cambiar nombres de tablas sin revisar assets/scripts/ER_Script.sql.
```
