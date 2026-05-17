# Analisis De Arquitectura

El proyecto quedó organizado por capas en español bajo:

```text
com.restaurante.pedidos
```

## Capas Actuales

```text
configuracion -> JPA, HikariCP, pools de hilos y tiempos simulados
dominio       -> entidades JPA, enums y lógica pura del dominio
repositorio   -> acceso a datos con EntityManager
servicio      -> casos de uso y reglas de negocio
presentacion  -> JavaFX, controladores, navegación y carga de vistas
```

Se eliminó el paquete `util` porque no representaba una capa. Sus clases quedaron donde corresponde:

```text
GeneradorCodigoPedido -> dominio
SimuladorTiempo       -> servicio
```

## Regla Principal

```text
presentacion -> servicio -> repositorio -> dominio
```

La dependencia va hacia adentro. La UI no debe conocer `EntityManager`; los repositorios no deben decidir reglas de negocio; el dominio no debe depender de capas externas.

## Estructura Recomendada

```text
src/main/java/com/restaurante/pedidos
├── configuracion
├── dominio
│   └── entidad
├── repositorio
├── servicio
└── presentacion
```

## Pendientes Criticos

```text
RepositorioPedido
RepositorioProducto
RepositorioEstadoPedido
RepositorioReglaTransicionEstadoPedido
ServicioPedido
MaquinaEstadosPedido
ServicioCocina
ServicioEntrega
ServicioSimulacion
PedidoDTO
EstadoPedidoDTO
validadores de negocio reutilizables
pruebas unitarias de servicios
```

## Decision Importante

No se agrega Spring Boot. Este proyecto usa Maven, JavaFX y JPA/Hibernate con configuración manual. Eso exige mantener bien la dirección de dependencias, porque no hay contenedor que tape una mala arquitectura.
