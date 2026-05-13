package first.bimester.demo;

import first.bimester.domain.entity.Customer;
import first.bimester.repository.CustomerRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.time.Instant;

public class JpaInsertDemo {
	public static void main(String[] args) {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("firstBimesterPU");
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();

		try (entityManagerFactory; entityManager) {
			transaction.begin();

			Customer customer = Customer.builder()
				.fullName("Cliente Demo JPA")
				.phone("0999999999")
				.email("cliente.demo.jpa@example.com")
				.isActive(true)
				.createdAt(Instant.now())
				.build();

			CustomerRepository customerRepository = new CustomerRepository(entityManager);
			customerRepository.save(customer);

			transaction.commit();
			System.out.println("Cliente insertado con ID: " + customer.getId());
		} catch (RuntimeException exception) {
			if (transaction.isActive()) {
				transaction.rollback();
			}

			throw exception;
		}
	}
}
