package first.bimester.repository;

import first.bimester.domain.entity.Staff;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class StaffRepository {
	private final EntityManager entityManager;

	public StaffRepository(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public List<Staff> findAllWithRole() {
		return entityManager
			.createQuery("select s from Staff s join fetch s.role order by s.id desc", Staff.class)
			.getResultList();
	}

	public Optional<Staff> findByIdWithRole(Long id) {
		List<Staff> staff = entityManager
			.createQuery("select s from Staff s join fetch s.role where s.id = :id", Staff.class)
			.setParameter("id", id)
			.getResultList();

		return staff.stream().findFirst();
	}

	public Staff save(Staff staff) {
		entityManager.persist(staff);
		return staff;
	}

	public Staff update(Staff staff) {
		return entityManager.merge(staff);
	}

	public void delete(Staff staff) {
		Staff managedStaff = entityManager.contains(staff) ? staff : entityManager.merge(staff);
		entityManager.remove(managedStaff);
	}
}
