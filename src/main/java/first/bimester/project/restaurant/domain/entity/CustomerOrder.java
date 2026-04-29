package first.bimester.project.restaurant.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "customer_orders")
public class CustomerOrder {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id", nullable = false)
	private Long id;

	@Size(max = 30)
	@NotNull
	@Column(name = "order_code", nullable = false, length = 30)
	private String orderCode;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "registered_by_staff_id", nullable = false)
	private Staff registeredByStaff;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "current_status_id", nullable = false)
	private OrderStatus currentStatus;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "delivery_address_id")
	private CustomerAddress deliveryAddress;

	@Size(max = 255)
	@Column(name = "delivery_address_snapshot")
	private String deliveryAddressSnapshot;

	@Builder.Default
	@ColumnDefault("0.00")
	@Column(name = "subtotal_amount", precision = 10, scale = 2)
	private BigDecimal subtotalAmount = BigDecimal.ZERO;

	@Builder.Default
	@ColumnDefault("0.00")
	@Column(name = "tax_amount", precision = 10, scale = 2)
	private BigDecimal taxAmount = BigDecimal.ZERO;

	@Builder.Default
	@ColumnDefault("0.00")
	@Column(name = "discount_amount", precision = 10, scale = 2)
	private BigDecimal discountAmount = BigDecimal.ZERO;

	@Builder.Default
	@ColumnDefault("0.00")
	@Column(name = "address_surcharge_amount", precision = 10, scale = 2)
	private BigDecimal addressSurchargeAmount = BigDecimal.ZERO;

	@NotNull
	@ColumnDefault("0.00")
	@Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
	private BigDecimal totalAmount;

	@Size(max = 20)
	@Column(name = "discount_code", length = 20)
	private String discountCode;

	@NotNull
	@Builder.Default
	@ColumnDefault("0")
	@Column(name = "is_priority", nullable = false)
	private Boolean isPriority = false;

	@Size(max = 255)
	@Column(name = "general_notes")
	private String generalNotes;

	@NotNull
	@ColumnDefault("CURRENT_TIMESTAMP(6)")
	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "estimated_delivery_at")
	private Instant estimatedDeliveryAt;

	@NotNull
	@ColumnDefault("CURRENT_TIMESTAMP(6)")
	@Column(name = "current_status_changed_at", nullable = false)
	private Instant currentStatusChangedAt;


}
