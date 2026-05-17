# Persona 1: Dominio Y Persistencia

Tu responsabilidad es que el modelo y el acceso a datos estén sólidos. Si esta capa falla, todo lo demás trabaja sobre arena.

## Paquetes A Tocar

```text
src/main/java/com/restaurante/pedidos/domain
src/main/java/com/restaurante/pedidos/domain/entity
src/main/java/com/restaurante/pedidos/repository
src/main/java/com/restaurante/pedidos/config/DatabaseConfig.java
src/main/resources/META-INF/persistence.xml
```

## Qué Debés Crear

```text
OrderRepository
ProductRepository
OrderStatusRepository
TransitionRuleRepository
DeliveryRepository si hace falta para confirmar entregas
```

Métodos mínimos esperados:

```text
OrderRepository.findByCode(String code)
OrderRepository.findByStatus(OrderStatusCode status)
OrderRepository.findPendingForCook()
OrderRepository.save(CustomerOrder order)
StaffRepository.findByRole(RoleCode roleCode)
StaffRepository.findAvailableCouriers()
TransitionRuleRepository.findValidTransitions(RoleCode role, OrderStatusCode currentStatus)
```

## Reglas

```text
No pongas reglas de negocio en repositories.
No devuelvas EntityManager fuera del repository.
No uses consultas SQL sueltas si JPQL alcanza.
No cambies nombres de tablas sin revisar assets/ROM - ER Diagram.sql.
```

## Tests Esperados

Los repositories suelen requerir base de datos, así que separá mentalmente:

```text
Unitario: lógica pura sin MySQL.
Integración: repository contra MySQL o base temporal.
```

Para esta entrega, priorizá que los métodos estén claros y que no rompan la capa de servicios.

## Entregable

Al terminar, Persona 2 debe poder usar tus repositories sin saber nada de SQL, HikariCP ni EntityManager.
