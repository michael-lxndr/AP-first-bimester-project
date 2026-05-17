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
@Table(name = "historial_estados_pedido")
public class HistorialEstadoPedido {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "historial_id", nullable = false)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "pedido_id", nullable = false)
	private PedidoCliente pedido;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@ColumnDefault("1")
	@JoinColumn(name = "estado_origen_id", nullable = false)
	private EstadoPedido estadoOrigen;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "estado_destino_id", nullable = false)
	private EstadoPedido estadoDestino;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "cambiado_por_personal_id", nullable = false)
	private Personal cambiadoPorPersonal;

	@NotNull
	@ColumnDefault("CURRENT_TIMESTAMP(6)")
	@Column(name = "cambiado_en", nullable = false)
	private Instant cambiadoEn;

	@Size(max = 255)
	@Column(name = "notas")
	private String notas;
}
