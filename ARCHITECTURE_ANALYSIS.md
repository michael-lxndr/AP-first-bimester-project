# Analisis De Arquitectura

El proyecto fue refactorizado a una estructura por capas bajo el paquete raiz:

```text
com.restaurante.pedidos
```

## Capas Actuales

```text
config        -> configuracion transversal: JPA, HikariCP, pools y delays
domain        -> entidades JPA y enums del negocio
repository    -> acceso a datos con EntityManager
service       -> casos de uso y reglas de negocio
presentation  -> JavaFX, controllers, navegacion y carga de vistas
util          -> utilidades reutilizables sin estado de UI ni base de datos
```

## Estado Actual

```text
Domain: implementado con entidades JPA y enums.
Repository: parcial; existen CustomerRepository, RoleRepository y StaffRepository.
Service: parcial; existe StaffService.
Presentation: parcial; existe estructura JavaFX centralizada.
Config: centralizado con DatabaseConfig, ThreadPoolConfig y SimulationConfig.
Tests: base inicial para utilidades.
```

## Regla Principal

```text
presentation -> service -> repository -> domain
```

La dependencia va hacia adentro. La UI no debe conocer `EntityManager`, los repositories no deben decidir reglas de negocio y el domain no debe depender de ninguna capa externa.

## Estructura Recomendada

```text
src/main/java/com/restaurante/pedidos
├── config
│   ├── DatabaseConfig.java
│   ├── SimulationConfig.java
│   └── ThreadPoolConfig.java
├── domain
│   ├── entity
│   ├── dto
│   └── validator
├── repository
├── service
├── presentation
│   └── component
└── util
```

## Pendientes Criticos

```text
OrderRepository
ProductRepository
OrderStatusRepository
TransitionRuleRepository
OrderService
OrderStateMachine
KitchenService
DeliveryService
SimulationService
OrderDTO
OrderStatusDTO
validadores de negocio reutilizables
tests unitarios de services
```

## Decision Importante

No se agrega Spring Boot. Este proyecto usa Maven, JavaFX y JPA/Hibernate con configuracion manual. Eso obliga a ser prolijos con la direccion de dependencias, porque no hay contenedor que tape una mala arquitectura.
