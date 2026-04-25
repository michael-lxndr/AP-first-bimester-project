package first.bimester.project.restaurant.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "order_statuses")
public class OrderStatus {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "status_id", nullable = false)
	private Long id;

	@Size(max = 30)
	@NotNull
	@Column(name = "status_code", nullable = false, length = 30)
	private String statusCode;

	@Size(max = 50)
	@NotNull
	@Column(name = "status_name", nullable = false, length = 50)
	private String statusName;

	@NotNull
	@Column(name = "status_order", nullable = false)
	private Integer statusOrder;

	@NotNull
	@ColumnDefault("0")
	@Column(name = "is_final", nullable = false)
	private Boolean isFinal;


}
