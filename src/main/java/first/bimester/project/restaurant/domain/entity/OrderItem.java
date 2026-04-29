package first.bimester.project.restaurant.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "order_items")
public class OrderItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_item_id", nullable = false)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "order_id", nullable = false)
	private CustomerOrder order;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@NotNull
	@Column(name = "quantity", nullable = false)
	private Integer quantity;

	@Size(max = 120)
	@NotNull
	@Column(name = "product_name_snapshot", nullable = false, length = 120)
	private String productNameSnapshot;

	@NotNull
	@Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
	private BigDecimal unitPrice;

	@NotNull
	@Column(name = "line_total", nullable = false, precision = 10, scale = 2)
	private BigDecimal lineTotal;

	@Size(max = 150)
	@Column(name = "special_note", length = 150)
	private String specialNote;

	@NotNull
	@Builder.Default
	@ColumnDefault("0")
	@Column(name = "is_ready", nullable = false)
	private Boolean isReady = false;


}
