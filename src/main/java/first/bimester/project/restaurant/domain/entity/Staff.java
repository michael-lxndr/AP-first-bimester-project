package first.bimester.project.restaurant.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "staff")
public class Staff {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "staff_id", nullable = false)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "role_id", nullable = false)
	private Role role;

	@Size(max = 120)
	@NotNull
	@Column(name = "full_name", nullable = false, length = 120)
	private String fullName;

	@Size(max = 20)
	@Column(name = "phone", length = 20)
	private String phone;

	@Size(max = 120)
	@NotNull
	@Column(name = "email", nullable = false, length = 120)
	private String email;

	@Size(max = 50)
	@NotNull
	@Column(name = "username", nullable = false, length = 50)
	private String username;

	@NotNull
	@ColumnDefault("1")
	@Column(name = "is_active", nullable = false)
	private Boolean isActive;

	@NotNull
	@ColumnDefault("CURRENT_TIMESTAMP(6)")
	@Column(name = "created_at", nullable = false)
	private Instant createdAt;


}
