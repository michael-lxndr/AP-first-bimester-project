# Persona 1: Dominio Y Persistencia

Tu responsabilidad es que el modelo y el acceso a datos estén sólidos.

## Paquetes A Tocar

```text
src/main/java/com/restaurante/pedidos/dominio
src/main/java/com/restaurante/pedidos/dominio/entidad
src/main/java/com/restaurante/pedidos/repositorio
src/main/java/com/restaurante/pedidos/configuracion/ConfiguracionBaseDatos.java
src/main/resources/META-INF/persistence.xml
```

## Qué Debés Crear

```text
RepositorioPedido
RepositorioProducto
RepositorioEstadoPedido
RepositorioReglaTransicionEstadoPedido
RepositorioEntrega si hace falta para confirmar entregas
```

Métodos mínimos esperados:

```text
RepositorioPedido.buscarPorCodigo(String codigo)
RepositorioPedido.buscarPorEstado(CodigoEstadoPedido estado)
RepositorioPedido.buscarPendientesParaCocinero()
RepositorioPedido.guardar(PedidoCliente pedido)
RepositorioPersonal.buscarPorRol(CodigoRol rol)
RepositorioPersonal.buscarRepartidoresDisponibles()
RepositorioReglaTransicionEstadoPedido.buscarTransicionesValidas(CodigoRol rol, CodigoEstadoPedido estadoActual)
```

## Reglas

```text
No pongas reglas de negocio en repositorios.
No devuelvas EntityManager fuera del repositorio.
No cambies nombres de tablas sin revisar assets/ROM - ER Diagram.sql.
```

## Entregable

Persona 2 debe poder usar tus repositorios sin saber nada de SQL, HikariCP ni EntityManager.
