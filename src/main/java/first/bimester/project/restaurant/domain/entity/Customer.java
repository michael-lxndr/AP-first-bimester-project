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
@Table(name = "customers")
public class Customer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "customer_id", nullable = false)
	private Long id;

	@Size(max = 120)
	@NotNull
	@Column(name = "full_name", nullable = false, length = 120)
	private String fullName;

	@Size(max = 20)
	@Column(name = "phone", length = 20)
	private String phone;

	@Size(max = 120)
	@Column(name = "email", length = 120)
	private String email;

	@NotNull
	@ColumnDefault("1")
	@Column(name = "is_active", nullable = false)
	private Boolean isActive;

	@NotNull
	@ColumnDefault("CURRENT_TIMESTAMP(6)")
	@Column(name = "created_at", nullable = false)
	private Instant createdAt;


}
