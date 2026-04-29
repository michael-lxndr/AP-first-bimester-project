package first.bimester.project.restaurant.domain.entity;

import first.bimester.project.restaurant.domain.enums.RoleCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "role_id", nullable = false)
	private Long id;

	@Size(max = 30)
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "role_name", nullable = false, length = 30)
	private RoleCode roleCode;


}
