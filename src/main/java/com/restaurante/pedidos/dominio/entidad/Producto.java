package com.restaurante.pedidos.dominio.entidad;

import com.restaurante.pedidos.dominio.CategoriaProducto;
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
@Table(name = "productos")
public class Producto {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "producto_id", nullable = false)
	private Long id;

	@Size(max = 20)
	@Column(name = "codigo_producto", length = 20)
	private String codigoProducto;

	@Size(max = 120)
	@NotNull
	@Column(name = "nombre_producto", nullable = false, length = 120)
	private String nombreProducto;

	@Size(max = 255)
	@Column(name = "descripcion")
	private String descripcion;

	@NotNull
	@Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
	private BigDecimal precioUnitario;

	@ColumnDefault("0.00")
	@Column(name = "costo_produccion", precision = 10, scale = 2)
	private BigDecimal costoProduccion;

	@Size(max = 30)
	@Enumerated(EnumType.STRING)
	@Column(name = "categoria", length = 30)
	private CategoriaProducto categoria;

	@ColumnDefault("15")
	@Column(name = "tiempo_preparacion_minutos")
	private Integer tiempoPreparacionMinutos;

	@NotNull
	@ColumnDefault("1")
	@Column(name = "disponible", nullable = false)
	private Boolean disponible;

	@Size(max = 255)
	@Column(name = "url_imagen")
	private String urlImagen;

	@ColumnDefault("CURRENT_TIMESTAMP(6)")
	@Column(name = "creado_en")
	private Instant creadoEn;
}
