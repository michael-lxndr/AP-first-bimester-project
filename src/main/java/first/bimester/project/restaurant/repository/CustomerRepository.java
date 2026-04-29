package first.bimester.project.restaurant.repository;

import first.bimester.project.restaurant.domain.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
