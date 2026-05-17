# Gestor De Pedidos Del Restaurante

Proyecto académico de escritorio para gestionar pedidos de restaurante con Java 21, JavaFX, JPA/Hibernate, MySQL, HikariCP, pools de hilos y pruebas unitarias.

## Paquete Raíz

```text
com.restaurante.pedidos
```

## Capas Del Proyecto

La estructura queda reducida a paquetes necesarios:

```text
src/main/java/com/restaurante/pedidos
├── configuracion
│   ├── ConfiguracionBaseDatos.java
│   ├── ConfiguracionHilos.java
│   └── ConfiguracionSimulacion.java
├── dominio
│   ├── CategoriaProducto.java
│   ├── CodigoEstadoPedido.java
│   ├── CodigoRol.java
│   ├── GeneradorCodigoPedido.java
│   └── entidad
├── repositorio
│   ├── RepositorioCliente.java
│   ├── RepositorioPersonal.java
│   └── RepositorioRol.java
├── servicio
│   ├── ServicioPersonal.java
│   └── SimuladorTiempo.java
└── presentacion
    ├── AplicacionPrincipal.java
    ├── AplicacionJavaFx.java
    ├── GestorEscenas.java
    ├── CargadorVistas.java
    ├── ControladorPanelPrincipal.java
    ├── ControladorAdministrador.java
    ├── ControladorCocinero.java
    ├── ControladorRepartidor.java
    └── ControladorCliente.java
```

No existe paquete `util`. Las clases que antes estaban ahí fueron movidas a una capa concreta:

```text
GeneradorCodigoPedido -> dominio
SimuladorTiempo       -> servicio
```

## Recursos JavaFX

```text
src/main/resources/com/restaurante/pedidos/presentacion
├── estilo/aplicacion.css
└── vista
    ├── vista-administrador.fxml
    ├── vista-cocinero.fxml
    ├── vista-cliente.fxml
    ├── vista-principal.fxml
    └── vista-repartidor.fxml
```

## Regla De Dependencias

```text
presentacion -> servicio -> repositorio -> dominio
```

Reglas obligatorias:

```text
Los controladores JavaFX no contienen reglas de negocio.
Los servicios coordinan reglas, validaciones, transacciones y casos de uso.
Los repositorios solo hablan con EntityManager.
El dominio no depende de presentación, servicio ni repositorio.
Los pools de hilos se centralizan en ConfiguracionHilos.
Los tiempos simulados se centralizan en ConfiguracionSimulacion y SimuladorTiempo.
```

## Base De Datos

Archivo principal:

```text
src/main/resources/META-INF/persistence.xml
```

Base esperada:

```sql
CREATE DATABASE first_bimester_project;
```

Conexión actual:

```text
URL:      jdbc:mysql://localhost:3307/first_bimester_project
Usuario:  root
Password: root
Pool:     HikariCP mediante Hibernate
```

## Ejecución

```bash
mvn javafx:run
```

Comando alternativo:

```bash
mvn exec:java
```

Clase principal configurada:

```text
com.restaurante.pedidos.presentacion.AplicacionPrincipal
```

## Pruebas

```bash
mvn test
```

Las pruebas unitarias viven en:

```text
src/test/java/com/restaurante/pedidos
├── dominio/GeneradorCodigoPedidoTest.java
└── servicio/SimuladorTiempoTest.java
```

## Modelo De Negocio

Estados operativos del pedido:

```text
PENDING -> IN_PREPARATION -> READY -> ON_THE_WAY -> DELIVERED
```

Roles principales:

```text
ADMINISTRATOR
COOK
COURIER
CUSTOMER
```

## División Del Equipo

```text
Persona 1: dominio, entidades, base de datos y repositorios.
Persona 2: servicios, reglas de negocio, máquina de estados, simulación y pools de hilos.
Persona 3: presentación JavaFX, navegación, pantallas, DTOs de vista y pruebas.
```

Guías:

```text
docs/persona-1-dominio-persistencia.md
docs/persona-2-negocio-simulacion.md
docs/persona-3-presentacion-pruebas.md
```

## Pendientes Técnicos

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
validadores de negocio
```

No creen paquetes nuevos por ansiedad. Primero definan responsabilidad. Después código. Esa es la diferencia entre ordenar un proyecto y simplemente mover carpetas.
