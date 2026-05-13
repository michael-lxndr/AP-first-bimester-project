# Package `repository`

El paquete `repository` contiene las clases responsables de acceder a la base de datos.

En este proyecto los repositorios usan Jakarta Persistence directamente mediante `EntityManager`.

Importante para el entorno actual:

```text
- No son interfaces de Spring Data JPA.
- No extienden JpaRepository.
- No usan @Repository.
- No dependen de Spring Boot.
```

Regla principal:

```text
Repository busca y guarda.
Service decide.
```

Un repositorio no debe validar roles, permisos, transiciones de estado ni reglas de negocio. Eso pertenece a la capa `service`.

## Estructura actual

```text
repository
└── CustomerRepository.java
```

## `CustomerRepository.java`

Repositorio manual para `Customer`.

Implementación actual:

```java
public class CustomerRepository {
    private final EntityManager entityManager;

    public CustomerRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void save(Customer customer) {
        entityManager.persist(customer);
    }

    public Optional<Customer> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Customer.class, id));
    }
}
```

Qué hace:

```text
1. Recibe un EntityManager desde afuera.
2. Persiste clientes con persist(...).
3. Busca clientes por ID con entityManager.find(...).
4. Devuelve Optional para representar que el cliente puede no existir.
```

Qué no hace:

```text
- No abre la conexión.
- No crea el EntityManagerFactory.
- No maneja reglas de negocio.
- No decide transacciones por sí mismo.
```

## Manejo de transacciones

Como el proyecto no usa Spring Boot, no existe `@Transactional`.

Las transacciones deben manejarse explícitamente desde una capa superior, normalmente desde `service` o desde una clase bootstrap/demo mientras el proyecto está en desarrollo.

Ejemplo conceptual:

```java
EntityTransaction transaction = entityManager.getTransaction();

try {
    transaction.begin();
    customerRepository.save(customer);
    transaction.commit();
} catch (RuntimeException exception) {
    if (transaction.isActive()) {
        transaction.rollback();
    }
    throw exception;
}
```

La idea es simple: el repositorio ejecuta operaciones de persistencia, pero la unidad de trabajo completa la coordina otra capa.

## Repositorios esperados

A medida que el proyecto crezca, se pueden crear repositorios equivalentes para las demás entidades:

```text
RoleRepository.java
StaffRepository.java
CustomerAddressRepository.java
ProductRepository.java
OrderStatusRepository.java
OrderStatusTransitionRuleRepository.java
CustomerOrderRepository.java
OrderItemRepository.java
OrderStatusHistoryRepository.java
DeliveryRepository.java
```

Todos deberían seguir el mismo criterio:

```text
1. Recibir EntityManager por constructor.
2. Usar entityManager.find(...) para búsquedas por ID.
3. Usar JPQL o Criteria API para consultas específicas.
4. Devolver Optional cuando el resultado puede no existir.
5. No mezclar lógica de negocio con acceso a datos.
```

## Consultas esperadas por entidad

### `CustomerRepository`

```text
save(Customer customer)
findById(Long id)
findByEmail(String email)
findByPhone(String phone)
existsByEmail(String email)
```

### `CustomerAddressRepository`

```text
findById(Long id)
findActiveByCustomerId(Long customerId)
findPrimaryActiveByCustomerId(Long customerId)
existsActiveByIdAndCustomerId(Long addressId, Long customerId)
save(CustomerAddress address)
```

### `ProductRepository`

```text
findById(Long id)
findAvailable()
findByProductCode(String productCode)
findByCategory(ProductCategory category)
save(Product product)
```

### `CustomerOrderRepository`

```text
findById(Long id)
findByOrderCode(String orderCode)
findByCurrentStatusCode(OrderStatusCode statusCode)
save(CustomerOrder order)
```

### `OrderStatusTransitionRuleRepository`

```text
existsActiveTransition(fromStatus, toStatus, role)
findActiveTransition(fromStatus, toStatus, role)
```

## Naming recomendado

Como no usamos Spring Data JPA, los nombres de métodos no generan consultas automáticamente.

Eso significa que un método como:

```java
findByEmail(String email)
```

debe tener una implementación real con JPQL, por ejemplo:

```java
return entityManager
    .createQuery("SELECT c FROM Customer c WHERE c.email = :email", Customer.class)
    .setParameter("email", email)
    .getResultStream()
    .findFirst();
```

Esa diferencia es importante: en Spring Data el framework implementa el método; en este proyecto lo implementamos nosotros.

## Errores comunes a evitar

```text
- Copiar ejemplos de Spring Data y dejar interfaces sin implementación.
- Usar @Autowired en repositorios.
- Usar @Transactional esperando que funcione sin Spring.
- Abrir un EntityManager nuevo en cada método sin cerrar recursos.
- Poner validaciones de negocio dentro del repository.
```

## Resumen

El repositorio es una capa fina sobre JPA:

```text
Service -> Repository -> EntityManager -> MySQL
```

Si el repositorio empieza a decidir reglas de negocio, la arquitectura se ensucia. Y después cuesta muchísimo mantenerla.
