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
@Table(name = "entregas")
public class Entrega {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "entrega_id", nullable = false)
	private Long id;

	@NotNull
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "pedido_id", nullable = false)
	private PedidoCliente pedido;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "repartidor_personal_id", nullable = false)
	private Personal repartidor;

	@NotNull
	@ColumnDefault("CURRENT_TIMESTAMP(6)")
	@Column(name = "despachado_en", nullable = false)
	private Instant despachadoEn;

	@Column(name = "entregado_en")
	private Instant entregadoEn;

	@Size(max = 120)
	@Column(name = "nombre_receptor", length = 120)
	private String nombreReceptor;

	@Column(name = "confirmado_por_cliente_en")
	private Instant confirmadoPorClienteEn;

	@Size(max = 255)
	@Column(name = "notas_confirmacion_cliente")
	private String notasConfirmacionCliente;

	@Size(max = 30)
	@NotNull
	@ColumnDefault("'DESPACHADO'")
	@Column(name = "estado_entrega", nullable = false, length = 30)
	private String estadoEntrega;

	@Size(max = 500)
	@Column(name = "notas_repartidor", length = 500)
	private String notasRepartidor;

	@NotNull
	@ColumnDefault("1")
	@Column(name = "numero_intento", nullable = false)
	private Integer numeroIntento;
}
