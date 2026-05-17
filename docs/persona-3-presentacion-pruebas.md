# Persona 3: Presentación Y Pruebas

Tu responsabilidad es que la aplicación se pueda usar sin contaminar la UI con reglas de negocio.

## Paquetes A Tocar

```text
src/main/java/com/restaurante/pedidos/presentacion
src/main/resources/com/restaurante/pedidos/presentacion
src/main/java/com/restaurante/pedidos/dominio/dto cuando se creen DTOs de UI
src/test/java/com/restaurante/pedidos
```

## Qué Debés Crear

```text
PedidoDTO
EstadoPedidoDTO
componentes reutilizables si una UI se repite
pantallas completas para administrador, cocinero, repartidor y cliente
pruebas unitarias de utilidades de dominio, servicios simples, DTOs y validadores
```

Pantallas esperadas:

```text
ControladorPanelPrincipal: navegación principal.
ControladorAdministrador: gestión de personal, productos y pedidos.
ControladorCocinero: cola de preparación.
ControladorRepartidor: cola de entregas.
ControladorCliente: consulta por código.
```

## Reglas

```text
No uses EntityManager en controladores.
No uses repositorios en controladores.
No crees hilos desde controladores.
No dupliques lógica de estados en botones.
No muestres entidades completas si un DTO alcanza.
```

## Entregable

La UI debe navegar y llamar servicios sin conocer base de datos, transacciones ni pools de hilos.
