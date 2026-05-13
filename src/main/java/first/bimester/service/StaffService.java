package first.bimester.service;

import first.bimester.config.JpaUtil;
import first.bimester.domain.entity.Role;
import first.bimester.domain.entity.Staff;
import first.bimester.domain.enums.RoleCode;
import first.bimester.repository.RoleRepository;
import first.bimester.repository.StaffRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.Instant;
import java.util.List;

public class StaffService {
	public List<Staff> findAll() {
		try (EntityManager entityManager = JpaUtil.createEntityManager()) {
			return new StaffRepository(entityManager).findAllWithRole();
		}
	}

	public Staff create(RoleCode roleCode, String fullName, String phone, String email, String username, boolean active) {
		return executeInTransaction(entityManager -> {
			Role role = getOrCreateRole(entityManager, roleCode);

			Staff staff = Staff.builder()
				.role(role)
				.fullName(fullName)
				.phone(phone)
				.email(email)
				.username(username)
				.isActive(active)
				.createdAt(Instant.now())
				.build();

			return new StaffRepository(entityManager).save(staff);
		});
	}

	public Staff update(Long staffId, RoleCode roleCode, String fullName, String phone, String email, String username, boolean active) {
		return executeInTransaction(entityManager -> {
			StaffRepository staffRepository = new StaffRepository(entityManager);
			Staff staff = staffRepository.findByIdWithRole(staffId)
				.orElseThrow(() -> new IllegalArgumentException("No existe personal con ID " + staffId));
			Role role = getOrCreateRole(entityManager, roleCode);

			staff.setRole(role);
			staff.setFullName(fullName);
			staff.setPhone(phone);
			staff.setEmail(email);
			staff.setUsername(username);
			staff.setIsActive(active);

			return staffRepository.update(staff);
		});
	}

	public void setActive(Long staffId, boolean active) {
		executeInTransaction(entityManager -> {
			StaffRepository staffRepository = new StaffRepository(entityManager);
			Staff staff = staffRepository.findByIdWithRole(staffId)
				.orElseThrow(() -> new IllegalArgumentException("No existe personal con ID " + staffId));

			staff.setIsActive(active);
			staffRepository.update(staff);
			return null;
		});
	}

	public void delete(Long staffId) {
		executeInTransaction(entityManager -> {
			StaffRepository staffRepository = new StaffRepository(entityManager);
			Staff staff = staffRepository.findByIdWithRole(staffId)
				.orElseThrow(() -> new IllegalArgumentException("No existe personal con ID " + staffId));

			staffRepository.delete(staff);
			return null;
		});
	}

	private Role getOrCreateRole(EntityManager entityManager, RoleCode roleCode) {
		RoleRepository roleRepository = new RoleRepository(entityManager);

		return roleRepository.findByCode(roleCode)
			.orElseGet(() -> roleRepository.save(Role.builder().roleCode(roleCode).build()));
	}

	private <T> T executeInTransaction(TransactionWork<T> work) {
		try (EntityManager entityManager = JpaUtil.createEntityManager()) {
			EntityTransaction transaction = entityManager.getTransaction();
			try {
				transaction.begin();
				T result = work.execute(entityManager);
				transaction.commit();
				return result;
			} catch (RuntimeException exception) {
				if (transaction.isActive()) {
					transaction.rollback();
				}

				throw exception;
			}
		}
	}

	@FunctionalInterface
	private interface TransactionWork<T> {
		T execute(EntityManager entityManager);
	}
}
