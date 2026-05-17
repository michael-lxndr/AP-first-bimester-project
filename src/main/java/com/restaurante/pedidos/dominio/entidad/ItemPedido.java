package com.restaurante.pedidos.dominio.entidad;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "items_pedido")
public class ItemPedido {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "item_pedido_id", nullable = false)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "pedido_id", nullable = false)
	private PedidoCliente pedido;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "producto_id", nullable = false)
	private Producto producto;

	@NotNull
	@Column(name = "cantidad", nullable = false)
	private Integer cantidad;

	@Size(max = 120)
	@NotNull
	@Column(name = "snapshot_nombre_producto", nullable = false, length = 120)
	private String snapshotNombreProducto;

	@NotNull
	@Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
	private BigDecimal precioUnitario;

	@NotNull
	@Column(name = "total_linea", nullable = false, precision = 10, scale = 2)
	private BigDecimal totalLinea;

	@Size(max = 150)
	@Column(name = "nota_especial", length = 150)
	private String notaEspecial;

	@NotNull
	@ColumnDefault("0")
	@Column(name = "listo", nullable = false)
	private Boolean listo;
}
