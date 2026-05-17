# Persona 3: JavaFX, DTOs Y Pruebas

Tu responsabilidad es que la aplicación se pueda usar sin contaminar la UI con reglas de negocio. La pantalla muestra, captura eventos y llama services. Nada más.

## Paquetes A Tocar

```text
src/main/java/com/restaurante/pedidos/presentation
src/main/resources/com/restaurante/pedidos/presentation
src/main/java/com/restaurante/pedidos/domain/dto cuando se creen DTOs de UI
src/test/java/com/restaurante/pedidos
```

## Qué Debés Crear

```text
OrderDTO
OrderStatusDTO
componentes reutilizables si una UI se repite
pantallas completas para admin, cocinero, delivery y cliente
tests unitarios de utilidades y mapeos simples
```

Pantallas esperadas:

```text
DashboardController: navegación principal.
AdminController: gestión de personal, productos y pedidos.
CookController: cola de preparación.
DeliveryController: cola de entregas.
CustomerController: consulta por código.
```

## Reglas

```text
No uses EntityManager en controllers.
No uses repositories en controllers.
No crees hilos desde controllers.
No dupliques lógica de estados en botones.
No muestres entidades completas si un DTO alcanza.
```

## Tests Esperados

Priorizá tests simples y útiles:

```text
OrderCodeGeneratorTest
TimeSimulatorTest
tests de mappers DTO cuando existan
tests de validadores si se crean reglas de formulario reutilizables
```

JavaFX se prueba con cuidado. Si no hay framework de UI testing configurado, no inventes pruebas frágiles de ventanas. Mejor testear servicios, DTOs, mappers y validadores.

## Entregable

Al terminar, la UI debe poder navegar y llamar services sin conocer detalles de base de datos, transacciones ni pools de hilos.
