# Package `presentation`

El paquete `presentation` contiene la interfaz del sistema.

En el entorno actual la presentación es JavaFX.

Importante:

```text
- No hay Spring Boot.
- No hay ApplicationContext de Spring.
- No hay inyección automática con @Autowired.
- No hay consola implementada actualmente.
```

La presentación debe mostrar datos, capturar acciones del usuario y delegar el trabajo a servicios. No debe contener reglas de negocio.

## Estructura actual

Código Java:

```text
presentation/javafx
├── JavaFxApplication.java
├── JavaFxLauncher.java
├── StageManager.java
├── ViewLoader.java
└── controller
    └── MainController.java
```

Recursos:

```text
src/main/resources/first/bimester/presentation/javafx
├── style
│   └── application.css
└── view
    └── main-view.fxml
```

## `JavaFxLauncher.java`

Clase puente para arrancar la aplicación JavaFX.

Qué hace:

```text
1. Expone un main estándar.
2. Llama a JavaFxApplication.main(args).
3. Evita problemas al ejecutar directamente una clase que extiende Application.
```

Clase configurada en Maven:

```text
first.bimester.presentation.javafx.JavaFxLauncher
```

## `JavaFxApplication.java`

Clase principal de JavaFX. Extiende `javafx.application.Application`.

Qué hace actualmente:

```text
1. Recibe el primaryStage en start(...).
2. Crea un StageManager.
3. Registra el primaryStage.
4. Muestra la vista principal.
5. Finaliza JavaFX con Platform.exit() al cerrar.
```

Qué no hace:

```text
- No levanta Spring Boot.
- No crea un ApplicationContext.
- No inyecta services automáticamente.
```

## `StageManager.java`

Responsable de administrar la ventana principal.

Qué hace:

```text
1. Define la ruta del FXML principal.
2. Define la ruta del CSS principal.
3. Carga la vista con ViewLoader.
4. Crea la Scene.
5. Aplica estilos.
6. Configura título, tamaño y posición de la ventana.
```

Vista actual:

```text
/first/bimester/presentation/javafx/view/main-view.fxml
```

CSS actual:

```text
/first/bimester/presentation/javafx/style/application.css
```

## `ViewLoader.java`

Responsable de cargar archivos FXML.

Qué hace:

```text
1. Busca el recurso FXML en el classpath.
2. Crea un FXMLLoader.
3. Ejecuta loader.load().
4. Lanza IllegalStateException si el archivo no existe o no puede cargarse.
```

Actualmente usa el comportamiento estándar de `FXMLLoader`, por lo que el controller se crea desde el `fx:controller` definido en el FXML.

## `controller/MainController.java`

Controller de la vista principal.

Qué hace actualmente:

```text
1. Recibe acciones de botones desde main-view.fxml.
2. Muestra qué rol fue seleccionado.
3. Permite cerrar la aplicación.
```

Acciones actuales:

```text
openAdminView()
openCookView()
openCourierView()
openCustomerView()
exitApplication()
```

Esta clase todavía no ejecuta casos de uso reales. Es una base para conectar futuras pantallas.

## `main-view.fxml`

Vista principal de la aplicación.

Contiene:

```text
- Título del sistema.
- Subtítulo "Proyecto JavaFX + JPA".
- Botones para Administrador, Cocinero, Repartidor y Cliente.
- Label de estado.
- Botón para salir.
```

Controller asociado:

```text
first.bimester.presentation.javafx.controller.MainController
```

## Cómo conectar servicios sin Spring Boot

Como el proyecto no usa Spring, las dependencias deben conectarse manualmente.

La idea recomendada es crear una clase de arranque propia, por ejemplo:

```text
ProjectBootstrap
o
AppBootstrap
```

Esa clase podría encargarse de crear:

```text
1. EntityManagerFactory.
2. EntityManager.
3. Repositories.
4. Services.
5. Controllers o factories para controllers.
```

Si un controller necesita servicios, hay dos caminos razonables:

```text
1. Mantener controllers simples y que StageManager les pase dependencias.
2. Configurar FXMLLoader con una controllerFactory propia.
```

La segunda opción escala mejor cuando hay varias pantallas, porque evita crear lógica de construcción dentro de cada controller.

## Regla principal para controllers

```text
Controller valida entrada simple de pantalla.
Service valida reglas de negocio.
Repository consulta y guarda datos.
```

Ejemplo:

```text
Correcto:
MainController -> OrderService -> CustomerOrderRepository -> EntityManager

Incorrecto:
MainController -> EntityManager -> reglas de negocio dentro del botón
```

Si una acción de botón empieza a calcular totales, validar roles o decidir transiciones, esa lógica debe moverse a `service`.

## Pantallas esperadas

A futuro, la presentación JavaFX puede dividirse por rol:

```text
AdminView / AdminController
CookView / CookController
CourierView / CourierController
CustomerView / CustomerController
ProductView / ProductController
OrderTrackingView / OrderTrackingController
```

Cada pantalla debe ser una capa fina sobre los servicios.

## Flujos esperados por rol

### Administrador

```text
1. Registrar cliente.
2. Registrar dirección de cliente.
3. Registrar producto.
4. Crear pedido.
5. Consultar pedidos.
```

### Cocinero

```text
1. Ver pedidos pendientes.
2. Cambiar pedido a IN_PREPARATION.
3. Cambiar pedido a READY.
```

### Repartidor

```text
1. Ver pedidos listos.
2. Despachar pedido.
3. Confirmar entrega.
```

### Cliente

```text
1. Consultar pedido por código.
2. Ver historial del pedido.
3. Confirmar recepción.
```

## Errores comunes a evitar

```text
- Documentar Spring Boot cuando el proyecto no lo usa.
- Usar @Autowired en controllers JavaFX.
- Poner EntityManager directamente en métodos de botones.
- Cargar FXML con rutas absolutas del sistema operativo.
- Mezclar CSS, lógica de negocio y acceso a datos en el controller.
```

## Resumen

La presentación actual es JavaFX puro:

```text
JavaFxLauncher -> JavaFxApplication -> StageManager -> ViewLoader -> FXML -> Controller
```

Esa cadena es suficiente para el proyecto académico y evita la complejidad de Spring Boot cuando el entorno pedido es JavaFX + JPA + Maven.
