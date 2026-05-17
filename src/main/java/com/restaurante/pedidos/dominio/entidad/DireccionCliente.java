package com.restaurante.pedidos.dominio.entidad;

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
@Table(name = "direcciones_cliente")
public class DireccionCliente {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "direccion_id", nullable = false)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "cliente_id", nullable = false)
	private Cliente cliente;

	@Size(max = 50)
	@NotNull
	@Column(name = "alias", nullable = false, length = 50)
	private String alias;

	@Size(max = 150)
	@NotNull
	@Column(name = "calle_principal", nullable = false, length = 150)
	private String callePrincipal;

	@Size(max = 150)
	@Column(name = "calle_secundaria", length = 150)
	private String calleSecundaria;

	@Size(max = 10)
	@Column(name = "numero_casa", length = 10)
	private String numeroCasa;

	@Size(max = 255)
	@Column(name = "referencia")
	private String referencia;

	@Size(max = 10)
	@Column(name = "codigo_postal", length = 10)
	private String codigoPostal;

	@Size(max = 50)
	@NotNull
	@Column(name = "ciudad", nullable = false, length = 50)
	private String ciudad;

	@Size(max = 50)
	@NotNull
	@Column(name = "provincia", nullable = false, length = 50)
	private String provincia;

	@Size(max = 50)
	@NotNull
	@ColumnDefault("'Ecuador'")
	@Column(name = "pais", nullable = false, length = 50)
	private String pais;

	@NotNull
	@ColumnDefault("0")
	@Column(name = "principal", nullable = false)
	private Boolean principal;

	@NotNull
	@ColumnDefault("1")
	@Column(name = "activa", nullable = false)
	private Boolean activa;
}
