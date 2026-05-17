package com.restaurante.pedidos.dominio.entidad;

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
@Table(name = "clientes")
public class Cliente {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cliente_id", nullable = false)
	private Long id;

	@Size(max = 120)
	@NotNull
	@Column(name = "nombre_completo", nullable = false, length = 120)
	private String nombreCompleto;

	@Size(max = 20)
	@Column(name = "telefono", length = 20)
	private String telefono;

	@Size(max = 120)
	@Column(name = "correo_electronico", length = 120)
	private String correoElectronico;

	@NotNull
	@ColumnDefault("1")
	@Column(name = "activo", nullable = false)
	private Boolean activo;

	@NotNull
	@ColumnDefault("CURRENT_TIMESTAMP(6)")
	@Column(name = "creado_en", nullable = false)
	private Instant creadoEn;
}
