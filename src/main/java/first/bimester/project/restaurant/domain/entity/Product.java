package first.bimester.project.restaurant.domain.entity;

import first.bimester.project.restaurant.domain.enums.ProductCategory;
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
@Table(name = "products")
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id", nullable = false)
	private Long id;

	@Size(max = 20)
	@Column(name = "product_code", unique = true, length = 20)
	private String productCode;

	@Size(max = 120)
	@NotNull
	@Column(name = "product_name", nullable = false, length = 120)
	private String productName;

	@Size(max = 255)
	@Column(name = "description")
	private String description;

	@NotNull
	@Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
	private BigDecimal unitPrice;

	@Column(name = "production_cost", precision = 10, scale = 2)
	@Builder.Default
	private BigDecimal productionCost = BigDecimal.ZERO;

	@Enumerated(EnumType.STRING)
	@Column(name = "category", length = 30)
	private ProductCategory category;

	@ColumnDefault("15")
	@Column(name = "preparation_time_minutes")
	@Builder.Default
	private Integer preparationTimeMinutes = 15;

	@NotNull
	@ColumnDefault("1")
	@Column(name = "is_available", nullable = false)
	private Boolean isAvailable;

	@Size(max = 255)
	@Column(name = "image_url")
	private String imageUrl;

	@ColumnDefault("CURRENT_TIMESTAMP(6)")
	@Column(name = "created_at")
	private Instant createdAt;


}
