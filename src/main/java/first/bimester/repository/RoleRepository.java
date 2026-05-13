package first.bimester.repository;

import first.bimester.domain.entity.Role;
import first.bimester.domain.enums.RoleCode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.Optional;

public class RoleRepository {
	private final EntityManager entityManager;

	public RoleRepository(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public Optional<Role> findByCode(RoleCode roleCode) {
		try {
			Role role = entityManager
				.createQuery("select r from Role r where r.roleCode = :roleCode", Role.class)
				.setParameter("roleCode", roleCode)
				.getSingleResult();

			return Optional.of(role);
		} catch (NoResultException exception) {
			return Optional.empty();
		}
	}

	public Role save(Role role) {
		entityManager.persist(role);
		return role;
	}
}
