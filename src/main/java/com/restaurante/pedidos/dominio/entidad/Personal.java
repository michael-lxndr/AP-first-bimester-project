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
@Table(name = "personal")
public class Personal {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "personal_id", nullable = false)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "rol_id", nullable = false)
	private Rol rol;

	@Size(max = 120)
	@NotNull
	@Column(name = "nombre_completo", nullable = false, length = 120)
	private String nombreCompleto;

	@Size(max = 20)
	@Column(name = "telefono", length = 20)
	private String telefono;

	@Size(max = 120)
	@NotNull
	@Column(name = "correo_electronico", nullable = false, length = 120)
	private String correoElectronico;

	@Size(max = 50)
	@NotNull
	@Column(name = "nombre_usuario", nullable = false, length = 50)
	private String nombreUsuario;

	@NotNull
	@ColumnDefault("1")
	@Column(name = "activo", nullable = false)
	private Boolean activo;

	@NotNull
	@ColumnDefault("CURRENT_TIMESTAMP(6)")
	@Column(name = "creado_en", nullable = false)
	private Instant creadoEn;
}
