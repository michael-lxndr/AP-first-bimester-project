package com.restaurante.pedidos.dominio.entidad;

import com.restaurante.pedidos.dominio.CodigoEstadoPedido;
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
@Table(name = "estados_pedido")
public class EstadoPedido {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "estado_id", nullable = false)
	private Long id;

	@Size(max = 30)
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "codigo_estado", nullable = false, length = 30)
	private CodigoEstadoPedido codigoEstado;

	@Size(max = 50)
	@NotNull
	@Column(name = "nombre_estado", nullable = false, length = 50)
	private String nombreEstado;

	@NotNull
	@Column(name = "orden_estado", nullable = false)
	private Integer ordenEstado;

	@NotNull
	@ColumnDefault("0")
	@Column(name = "finalizado", nullable = false)
	private Boolean finalizado;
}
