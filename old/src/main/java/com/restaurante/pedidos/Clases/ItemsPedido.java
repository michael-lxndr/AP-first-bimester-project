/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurante.pedidos.clases;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;


@Entity
@Table(name = "items_pedido")
@NamedQueries({
	@NamedQuery(name = "ItemsPedido.findAll", query = "SELECT i FROM ItemsPedido i")})
public class ItemsPedido implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "item_pedido_id")
	private Long itemPedidoId;
	@Basic(optional = false)
	@Column(name = "cantidad")
	private int cantidad;
	@Basic(optional = false)
	@Column(name = "snapshot_nombre_producto")
	private String snapshotNombreProducto;
	// @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
	@Basic(optional = false)
	@Column(name = "precio_unitario")
	private BigDecimal precioUnitario;
	@Basic(optional = false)
	@Column(name = "total_linea")
	private BigDecimal totalLinea;
	@Column(name = "nota_especial")
	private String notaEspecial;
	@Basic(optional = false)
	@Column(name = "listo")
	private boolean listo;
	@JoinColumn(name = "pedido_id", referencedColumnName = "pedido_id")
	@ManyToOne(optional = false)
	private PedidosCliente pedidoId;
	@JoinColumn(name = "producto_id", referencedColumnName = "producto_id")
	@ManyToOne(optional = false)
	private Productos productoId;

	public ItemsPedido() {
	}

	public ItemsPedido(Long itemPedidoId) {
		this.itemPedidoId = itemPedidoId;
	}

	public ItemsPedido(Long itemPedidoId, int cantidad, String snapshotNombreProducto, BigDecimal precioUnitario, BigDecimal totalLinea, boolean listo) {
		this.itemPedidoId = itemPedidoId;
		this.cantidad = cantidad;
		this.snapshotNombreProducto = snapshotNombreProducto;
		this.precioUnitario = precioUnitario;
		this.totalLinea = totalLinea;
		this.listo = listo;
	}

	public Long getItemPedidoId() {
		return itemPedidoId;
	}

	public void setItemPedidoId(Long itemPedidoId) {
		this.itemPedidoId = itemPedidoId;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public String getSnapshotNombreProducto() {
		return snapshotNombreProducto;
	}

	public void setSnapshotNombreProducto(String snapshotNombreProducto) {
		this.snapshotNombreProducto = snapshotNombreProducto;
	}

	public BigDecimal getPrecioUnitario() {
		return precioUnitario;
	}

	public void setPrecioUnitario(BigDecimal precioUnitario) {
		this.precioUnitario = precioUnitario;
	}

	public BigDecimal getTotalLinea() {
		return totalLinea;
	}

	public void setTotalLinea(BigDecimal totalLinea) {
		this.totalLinea = totalLinea;
	}

	public String getNotaEspecial() {
		return notaEspecial;
	}

	public void setNotaEspecial(String notaEspecial) {
		this.notaEspecial = notaEspecial;
	}

	public boolean getListo() {
		return listo;
	}

	public void setListo(boolean listo) {
		this.listo = listo;
	}

	public PedidosCliente getPedidoId() {
		return pedidoId;
	}

	public void setPedidoId(PedidosCliente pedidoId) {
		this.pedidoId = pedidoId;
	}

	public Productos getProductoId() {
		return productoId;
	}

	public void setProductoId(Productos productoId) {
		this.productoId = productoId;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (itemPedidoId != null ? itemPedidoId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof ItemsPedido other)) {
			return false;
		}
		return (this.itemPedidoId != null || other.itemPedidoId == null) && (this.itemPedidoId == null || this.itemPedidoId.equals(other.itemPedidoId));
	}

	@Override
	public String toString() {
		return "Clases.ItemsPedido[ itemPedidoId=" + itemPedidoId + " ]";
	}

}
