package com.restaurante.pedidos.dominio.entidad;

import com.restaurante.pedidos.dominio.CodigoRol;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "roles")
public class Rol {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "role_id", nullable = false)
	private Long id;

	@Size(max = 30)
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "role_name", nullable = false, length = 30)
	private CodigoRol codigoRol;


}
