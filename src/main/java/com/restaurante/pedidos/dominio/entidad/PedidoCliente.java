package com.restaurante.pedidos.dominio.entidad;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "pedidos_cliente")
public class PedidoCliente {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pedido_id", nullable = false)
	private Long id;

	@Size(max = 30)
	@NotNull
	@Column(name = "codigo_pedido", nullable = false, length = 30)
	private String codigoPedido;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "cliente_id", nullable = false)
	private Cliente cliente;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "registrado_por_personal_id", nullable = false)
	private Personal registradoPorPersonal;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "estado_actual_id", nullable = false)
	private EstadoPedido estadoActual;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "direccion_entrega_id", nullable = false)
	private DireccionCliente direccionEntrega;

	@Size(max = 500)
	@NotNull
	@Column(name = "snapshot_direccion_entrega", nullable = false, length = 500)
	private String snapshotDireccionEntrega;

	@Size(max = 500)
	@Column(name = "instrucciones_entrega", length = 500)
	private String instruccionesEntrega;

	@ColumnDefault("0.00")
	@Column(name = "subtotal", precision = 10, scale = 2)
	private BigDecimal subtotal;

	@ColumnDefault("0.00")
	@Column(name = "impuesto", precision = 10, scale = 2)
	private BigDecimal impuesto;

	@ColumnDefault("0.00")
	@Column(name = "descuento", precision = 10, scale = 2)
	private BigDecimal descuento;

	@ColumnDefault("0.00")
	@Column(name = "recargo_direccion", precision = 10, scale = 2)
	private BigDecimal recargoDireccion;

	@NotNull
	@ColumnDefault("0.00")
	@Column(name = "total", nullable = false, precision = 10, scale = 2)
	private BigDecimal total;

	@Size(max = 20)
	@Column(name = "codigo_descuento", length = 20)
	private String codigoDescuento;

	@NotNull
	@ColumnDefault("0")
	@Column(name = "prioritario", nullable = false)
	private Boolean prioritario;

	@Size(max = 255)
	@Column(name = "notas_generales")
	private String notasGenerales;

	@NotNull
	@ColumnDefault("CURRENT_TIMESTAMP(6)")
	@Column(name = "creado_en", nullable = false)
	private Instant creadoEn;

	@Column(name = "entrega_estimada_en")
	private Instant entregaEstimadaEn;

	@NotNull
	@ColumnDefault("CURRENT_TIMESTAMP(6)")
	@Column(name = "estado_actual_cambiado_en", nullable = false)
	private Instant estadoActualCambiadoEn;
}
