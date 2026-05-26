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
├── persistencia
│   ├── controlador
│   │   ├── ClientesJpaController.java
│   │   ├── PedidosClienteJpaController.java
│   │   └── ...
│   └── excepcion
├── repositorio
│   └── consultas JPQL internas de apoyo
├── servicio
│   ├── FachadaServiciosPedido.java
│   ├── MaquinaEstadosPedido.java
│   ├── ServicioPedido.java
│   ├── ServicioCocina.java
│   ├── ServicioEntrega.java
│   ├── ServicioPersonal.java
│   ├── ServicioSimulacion.java
│   └── SimuladorTiempo.java
└── presentacion
    ├── AplicacionPrincipal.java
    ├── AplicacionConsola.java
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
presentacion -> servicio -> persistencia.controlador -> dominio
```

Reglas obligatorias:

```text
Los controladores JavaFX no contienen reglas de negocio.
Los servicios coordinan reglas, validaciones, transacciones y casos de uso.
Los controladores JPA concentran operaciones de persistencia estilo NetBeans.
Los repositorios, si se usan, son apoyo interno de consultas JPQL y no API de negocio.
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
CREATE DATABASE proyecto_primer_bimestre;
```

Conexión actual:

```text
URL:      jdbc:mysql://localhost:3306/proyecto_primer_bimestre
Usuario:  root
Password: root
Pool:     HikariCP mediante Hibernate
```

## Ejecución

Al iniciar el programa se abren cuatro ventanas al mismo tiempo:

```text
Administrador
Cocinero
Repartidor
Cliente
```

Clase principal configurada:

```text
com.restaurante.pedidos.presentacion.AplicacionPrincipal
```

### Desde Terminal

```bash
mvn javafx:run
```

Comando alternativo:

```bash
mvn exec:java
```

### Desde NetBeans

1. Abrí NetBeans.
2. Seleccioná `File > Open Project`.
3. Elegí la carpeta raíz del proyecto: `FirstBimesterProject`.
4. Verificá que NetBeans lo detecte como proyecto Maven.
5. Click derecho sobre el proyecto.
6. Ejecutá `Run` si toma el goal Maven configurado.
7. Si `Run` no levanta JavaFX, abrí la terminal integrada y ejecutá:

```bash
mvn javafx:run
```

También podés configurar la clase principal manualmente:

```text
com.restaurante.pedidos.presentacion.AplicacionPrincipal
```

### Desde IntelliJ IDEA

1. Abrí IntelliJ IDEA.
2. Seleccioná `File > Open`.
3. Elegí el archivo `pom.xml` o la carpeta raíz del proyecto.
4. Confirmá `Open as Project` para importarlo como Maven.
5. Esperá a que Maven descargue dependencias.
6. En la ventana Maven, ejecutá:

```text
Plugins > javafx > javafx:run
```

Alternativa desde la terminal integrada:

```bash
mvn javafx:run
```

Si creás una configuración de ejecución manual, usá esta clase principal:

```text
com.restaurante.pedidos.presentacion.AplicacionPrincipal
```

La opción recomendada es correrlo con Maven (`mvn javafx:run`) porque JavaFX necesita cargar módulos y dependencias correctamente. Si lo corrés como una clase Java común y el IDE no configura JavaFX, puede fallar aunque el código esté bien.

## Pruebas

```bash
mvn test
```

Las pruebas viven en:

```text
src/test/java/com/restaurante/pedidos
├── configuracion
├── dominio/GeneradorCodigoPedidoTest.java
├── persistencia
├── presentacion
├── repositorio
└── servicio/SimuladorTiempoTest.java
```

## Modelo De Negocio

Estados operativos del pedido (enum real en código/BD):

```text
PENDIENTE -> EN_PREPARACION -> LISTO -> EN_CAMINO -> ENTREGADO
```

Roles principales:

```text
ADMINISTRADOR
COCINERO
REPARTIDOR
```

## División Del Equipo

```text
Persona 1: dominio, entidades, base de datos y controladores JPA.
Persona 2: servicios, reglas de negocio, máquina de estados, simulación y pools de hilos.
Persona 3: presentación JavaFX, navegación, pantallas, DTOs de vista y pruebas.
```

Guías:

```text
docs/persona-1-dominio-persistencia.md
docs/persona-2-negocio-simulacion.md
docs/persona-3-presentacion-pruebas.md
```

## Ejecución IDE + Maven (refactor Persona 2)

- Unidad de persistencia única del proyecto: `primerBimestrePU`.
- IntelliJ: verificar `.idea/jpa.xml` apuntando a `primerBimestrePU`.
- NetBeans: `nbactions.xml` ejecuta `com.restaurante.pedidos.presentacion.AplicacionPrincipal` con Maven.
- Maven CLI:

```bash
mvn test
mvn javafx:run
```

## Pendientes Técnicos

```text
limpieza final de controladores JavaFX por caso de uso
PedidoDTO
EstadoPedidoDTO
validadores de negocio
```

No creen paquetes nuevos por ansiedad. Primero definan responsabilidad. Después código. Esa es la diferencia entre ordenar un proyecto y simplemente mover carpetas.
