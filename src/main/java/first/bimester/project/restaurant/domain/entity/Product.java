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
@Table(name = "products")
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id", nullable = false)
	private Long id;

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

	@NotNull
	@ColumnDefault("1")
	@Column(name = "is_available", nullable = false)
	private Boolean isAvailable;


}
