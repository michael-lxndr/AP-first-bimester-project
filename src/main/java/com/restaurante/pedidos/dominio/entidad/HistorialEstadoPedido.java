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
@Table(name = "order_status_histories")
public class HistorialEstadoPedido {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "history_id", nullable = false)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "order_id", nullable = false)
	private PedidoCliente order;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@ColumnDefault("1")
	@JoinColumn(name = "from_status_id", nullable = false)
	private EstadoPedido fromStatus;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "to_status_id", nullable = false)
	private EstadoPedido toStatus;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "changed_by_staff_id", nullable = false)
	private Personal changedByPersonal;

	@NotNull
	@ColumnDefault("CURRENT_TIMESTAMP(6)")
	@Column(name = "changed_at", nullable = false)
	private Instant changedAt;

	@Size(max = 255)
	@Column(name = "notes")
	private String notes;


}
