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
@Table(name = "customer_addresses")
public class CustomerAddress {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "address_id", nullable = false)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	@Size(max = 50)
	@NotNull
	@Column(name = "alias", nullable = false, length = 50)
	private String alias;

	@Size(max = 150)
	@NotNull
	@Column(name = "main_street", nullable = false, length = 150)
	private String mainStreet;

	@Size(max = 150)
	@Column(name = "secondary_street", length = 150)
	private String secondaryStreet;

	@Size(max = 10)
	@Column(name = "house_number", length = 10)
	private String houseNumber;

	@Size(max = 255)
	@Column(name = "reference")
	private String reference;

	@Size(max = 10)
	@Column(name = "postal_code", length = 10)
	private String postalCode;

	@Size(max = 50)
	@Builder.Default
	@Column(name = "city", length = 50)
	private String city = "Quito";

	@Size(max = 50)
	@Builder.Default
	@Column(name = "province", length = 50)
	private String province = "Pichincha";

	@NotNull
	@Builder.Default
	@ColumnDefault("0")
	@Column(name = "is_primary", nullable = false)
	private Boolean isPrimary = false;

	@NotNull
	@Builder.Default
	@ColumnDefault("1")
	@Column(name = "is_active", nullable = false)
	private Boolean isActive = true;
}
