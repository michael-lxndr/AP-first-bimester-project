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
@Table(name = "deliveries")
public class Delivery {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "delivery_id", nullable = false)
	private Long id;

	@NotNull
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "order_id", nullable = false)
	private CustomerOrder order;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "courier_staff_id", nullable = false)
	private Staff courierStaff;

	@NotNull
	@ColumnDefault("CURRENT_TIMESTAMP(6)")
	@Column(name = "dispatched_at", nullable = false)
	private Instant dispatchedAt;

	@Column(name = "delivered_at")
	private Instant deliveredAt;

	@Size(max = 120)
	@Column(name = "receiver_name", length = 120)
	private String receiverName;

	@Column(name = "customer_confirmed_at")
	private Instant customerConfirmedAt;

	@Size(max = 255)
	@Column(name = "customer_confirmation_notes")
	private String customerConfirmationNotes;


}
