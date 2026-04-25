# Package `presentation`

El paquete `presentation` contiene la interfaz del sistema.

En este proyecto existirán dos presentaciones:

```text
1. Consola: versión funcional inicial y presentable mientras se desarrolla JavaFX.
2. JavaFX: interfaz gráfica final o avanzada.
```

Regla principal:

```text
Presentation no contiene reglas de negocio.
Presentation solo muestra datos, captura entrada del usuario y llama servicios.
```

## Estructura

```text
presentation
├── console
│   ├── ConsoleApplicationRunner.java
│   ├── MainMenu.java
│   ├── AdminMenu.java
│   ├── CookMenu.java
│   ├── CourierMenu.java
│   └── CustomerMenu.java
│
└── javafx
    ├── JavaFxApplication.java
    ├── StageManager.java
    ├── ViewLoader.java
    ├── controller
    │   ├── MainController.java
    │   ├── AdminController.java
    │   ├── CookController.java
    │   ├── CourierController.java
    │   ├── CustomerController.java
    │   ├── ProductController.java
    │   └── OrderTrackingController.java
    ├── model
    │   ├── ProductTableModel.java
    │   ├── OrderTableModel.java
    │   └── OrderHistoryTableModel.java
    └── util
        ├── AlertHelper.java
        └── FormValidator.java
```

# Console presentation

La consola es la primera interfaz funcional. Sirve para probar todo el sistema mientras JavaFX se desarrolla.

## `console/ConsoleApplicationRunner.java`

Arranca la interfaz por consola cuando inicia Spring Boot.

Qué hace:

```text
1. Espera que Spring Boot cargue el contexto.
2. Obtiene los menús necesarios.
3. Inicia MainMenu.
4. Mantiene el flujo de interacción hasta que el usuario salga.
```

Qué no hace:

```text
- No crea pedidos directamente.
- No valida reglas de negocio.
- No llama repositorios.
```

## `console/MainMenu.java`

Menú principal de la consola.

Opciones esperadas:

```text
1. Administrador
2. Cocinero
3. Repartidor
4. Cliente
5. Salir
```

Qué hace:

```text
1. Muestra opciones principales.
2. Lee la opción del usuario.
3. Redirige al menú correspondiente.
4. Maneja opciones inválidas.
```

## `console/AdminMenu.java`

Menú del administrador.

Servicios que usa:

```text
CustomerService
ProductService
OrderService
OrderQueryService
```

Opciones esperadas:

```text
1. Registrar cliente.
2. Registrar producto.
3. Consultar menú.
4. Crear pedido.
5. Listar pedidos.
6. Ver detalle de pedido.
7. Volver.
```

Flujo para crear pedido:

```text
1. Pedir ID del administrador.
2. Pedir ID del cliente.
3. Pedir dirección de entrega.
4. Pedir productos y cantidades.
5. Crear CreateOrderRequest.
6. Llamar OrderService.createOrder.
7. Mostrar resultado o error.
```

## `console/CookMenu.java`

Menú del cocinero.

Servicios que usa:

```text
OrderQueryService
OrderService
```

Opciones esperadas:

```text
1. Ver pedidos PENDING.
2. Cambiar pedido a IN_PREPARATION.
3. Ver pedidos IN_PREPARATION.
4. Cambiar pedido a READY.
5. Volver.
```

Flujo para cambiar estado:

```text
1. Pedir ID del cocinero.
2. Pedir código del pedido.
3. Elegir estado destino permitido.
4. Crear ChangeOrderStatusRequest.
5. Llamar OrderService.changeOrderStatus.
6. Mostrar resultado o error.
```

## `console/CourierMenu.java`

Menú del repartidor.

Servicios que usa:

```text
OrderQueryService
DeliveryService
```

Opciones esperadas:

```text
1. Ver pedidos READY.
2. Despachar pedido.
3. Ver pedidos ON_THE_WAY.
4. Confirmar entrega.
5. Volver.
```

Flujo para despachar:

```text
1. Pedir ID del repartidor.
2. Pedir código del pedido.
3. Crear DispatchOrderRequest.
4. Llamar DeliveryService.dispatchOrder.
5. Mostrar resultado o error.
```

Flujo para confirmar entrega:

```text
1. Pedir ID del repartidor.
2. Pedir código del pedido.
3. Pedir nombre de quien recibe.
4. Crear ConfirmDeliveryRequest.
5. Llamar DeliveryService.confirmDelivery.
6. Mostrar resultado o error.
```

## `console/CustomerMenu.java`

Menú del cliente.

Servicios que usa:

```text
OrderQueryService
DeliveryService
```

Opciones esperadas:

```text
1. Consultar pedido por código.
2. Ver historial.
3. Confirmar recepción.
4. Volver.
```

Flujo para consultar pedido:

```text
1. Pedir código del pedido.
2. Llamar OrderQueryService.trackOrder.
3. Mostrar estado actual.
4. Mostrar historial.
5. Mostrar datos de entrega si existen.
```

Flujo para confirmar recepción:

```text
1. Pedir ID del cliente.
2. Pedir código del pedido.
3. Crear ConfirmReceiptRequest.
4. Llamar DeliveryService.confirmCustomerReceipt.
5. Mostrar resultado o error.
```

# JavaFX presentation

JavaFX será la interfaz gráfica. Debe conectarse a los mismos servicios que usa la consola.

La clave es esta:

```text
Consola y JavaFX usan los mismos services.
No se duplica lógica de negocio.
```

## `javafx/JavaFxApplication.java`

Punto de entrada visual de JavaFX.

Qué hace:

```text
1. Inicia JavaFX.
2. Levanta Spring Boot por debajo.
3. Conecta JavaFX con el ApplicationContext de Spring.
4. Muestra la ventana principal.
5. Cierra Spring correctamente al salir.
```

Qué no hace:

```text
- No registra pedidos.
- No valida estados.
- No accede a repositorios.
```

## `javafx/StageManager.java`

Administra ventanas y escenas.

Qué hace:

```text
1. Abrir la pantalla principal.
2. Cambiar entre pantallas.
3. Volver al menú principal.
4. Definir títulos de ventanas.
5. Centralizar navegación.
```

Ejemplo conceptual:

```text
MainController llama StageManager.showAdminView()
```

## `javafx/ViewLoader.java`

Carga archivos FXML.

Qué hace:

```text
1. Buscar el archivo FXML.
2. Cargar la vista.
3. Crear o pedir el controller desde Spring.
4. Devolver la vista lista para mostrar.
```

Importante:

```text
Los controllers de JavaFX deben poder recibir services de Spring.
No deben crear services con new.
```

# JavaFX controllers

## `javafx/controller/MainController.java`

Controlador de la pantalla principal.

Qué hace:

```text
1. Mostrar opciones de rol.
2. Navegar a administrador, cocinero, repartidor o cliente.
3. Cerrar la aplicación si el usuario lo solicita.
```

## `javafx/controller/AdminController.java`

Controlador de la pantalla del administrador.

Servicios que usa:

```text
CustomerService
ProductService
OrderService
OrderQueryService
```

Qué hace:

```text
1. Leer formularios de cliente.
2. Leer formularios de producto.
3. Leer formularios de pedido.
4. Crear request DTOs.
5. Llamar servicios.
6. Mostrar respuestas en tablas o mensajes.
```

Qué no hace:

```text
- No calcula total.
- No valida rol de administrador por sí mismo.
- No crea entidades directamente.
```

## `javafx/controller/CookController.java`

Controlador de la pantalla del cocinero.

Servicios que usa:

```text
OrderQueryService
OrderService
```

Qué hace:

```text
1. Mostrar pedidos PENDING.
2. Mostrar pedidos IN_PREPARATION.
3. Permitir cambio a IN_PREPARATION.
4. Permitir cambio a READY.
5. Actualizar tablas después de cada cambio.
```

## `javafx/controller/CourierController.java`

Controlador de la pantalla del repartidor.

Servicios que usa:

```text
OrderQueryService
DeliveryService
```

Qué hace:

```text
1. Mostrar pedidos READY.
2. Permitir despachar pedido.
3. Mostrar pedidos ON_THE_WAY.
4. Permitir confirmar entrega.
5. Pedir receiverName.
```

## `javafx/controller/CustomerController.java`

Controlador de la pantalla del cliente.

Servicios que usa:

```text
OrderQueryService
DeliveryService
```

Qué hace:

```text
1. Pedir código del pedido.
2. Mostrar estado actual.
3. Mostrar historial.
4. Mostrar datos de entrega.
5. Permitir confirmar recepción si corresponde.
```

Regla visual:

```text
El botón Confirmar recepción solo debería habilitarse si el pedido está DELIVERED y customerConfirmedAt está vacío.
```

Pero el service igual debe validar esa regla.

## `javafx/controller/ProductController.java`

Controlador específico para productos.

Servicios que usa:

```text
ProductService
```

Qué hace:

```text
1. Registrar productos.
2. Listar menú.
3. Mostrar productos disponibles.
4. Cambiar disponibilidad si se implementa.
```

## `javafx/controller/OrderTrackingController.java`

Controlador para seguimiento de pedido.

Servicios que usa:

```text
OrderQueryService
DeliveryService opcionalmente
```

Qué hace:

```text
1. Buscar pedido por código.
2. Mostrar estado actual.
3. Mostrar historial en tabla.
4. Mostrar entrega.
5. Mostrar si el cliente ya confirmó recepción.
```

# JavaFX table models

## `javafx/model/ProductTableModel.java`

Modelo para mostrar productos en tabla JavaFX.

Qué hace:

```text
1. Adaptar ProductResponse a columnas visibles.
2. Exponer nombre, precio y disponibilidad.
```

## `javafx/model/OrderTableModel.java`

Modelo para mostrar pedidos en tabla JavaFX.

Qué hace:

```text
1. Adaptar OrderSummaryResponse a columnas visibles.
2. Mostrar código, cliente, estado, total y fecha.
```

## `javafx/model/OrderHistoryTableModel.java`

Modelo para mostrar historial en tabla JavaFX.

Qué hace:

```text
1. Adaptar OrderHistoryResponse a columnas visibles.
2. Mostrar estado anterior, estado nuevo, responsable, fecha y notas.
```

# JavaFX utilities

## `javafx/util/AlertHelper.java`

Utilidad para mensajes visuales.

Qué hace:

```text
1. Mostrar mensajes de éxito.
2. Mostrar errores.
3. Mostrar confirmaciones.
4. Evitar repetir código de alerts en todos los controllers.
```

## `javafx/util/FormValidator.java`

Utilidad para validaciones simples de pantalla.

Qué hace:

```text
1. Verificar campos vacíos.
2. Verificar números básicos.
3. Verificar selección de filas en tablas.
4. Mostrar mensajes antes de llamar al service.
```

Importante:

```text
FormValidator no reemplaza validaciones de service.
Solo mejora la experiencia del usuario.
```

# FXML y CSS

Los archivos FXML viven en `src/main/resources`.

```text
resources/first/bimester/project/restaurant/presentation/javafx/view
├── main-view.fxml
├── admin-view.fxml
├── cook-view.fxml
├── courier-view.fxml
├── customer-view.fxml
├── product-view.fxml
└── order-tracking-view.fxml
```

## `main-view.fxml`

Pantalla principal.

Debe mostrar:

```text
- Botón Administrador.
- Botón Cocinero.
- Botón Repartidor.
- Botón Cliente.
- Botón Salir.
```

## `admin-view.fxml`

Pantalla del administrador.

Debe permitir:

```text
- Registrar cliente.
- Registrar producto.
- Crear pedido.
- Ver pedidos.
```

## `cook-view.fxml`

Pantalla del cocinero.

Debe permitir:

```text
- Ver pedidos pendientes.
- Cambiar a IN_PREPARATION.
- Ver pedidos en preparación.
- Cambiar a READY.
```

## `courier-view.fxml`

Pantalla del repartidor.

Debe permitir:

```text
- Ver pedidos listos.
- Despachar pedido.
- Ver pedidos en camino.
- Confirmar entrega.
- Registrar receiverName.
```

## `customer-view.fxml`

Pantalla del cliente.

Debe permitir:

```text
- Ingresar código de pedido.
- Ver estado actual.
- Ver historial.
- Confirmar recepción.
```

## `product-view.fxml`

Pantalla de productos.

Debe permitir:

```text
- Registrar producto.
- Listar productos.
- Ver disponibilidad.
```

## `order-tracking-view.fxml`

Pantalla de seguimiento.

Debe mostrar:

```text
- Código del pedido.
- Estado actual.
- Historial.
- Datos de entrega.
- Confirmación del cliente.
```

## `application.css`

Archivo de estilos.

Qué hace:

```text
1. Define colores.
2. Define espaciados.
3. Define estilos de botones.
4. Define estilos de tablas.
5. Mejora presentación visual.
```
