/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurante.pedidos.Clases;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;


@Entity
@Table(name = "estados_pedido")
@NamedQueries({
	@NamedQuery(name = "EstadosPedido.findAll", query = "SELECT e FROM EstadosPedido e")})
public class EstadosPedido implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "estado_id")
	private Long estadoId;
	@Basic(optional = false)
	@Column(name = "codigo_estado")
	private String codigoEstado;
	@Basic(optional = false)
	@Column(name = "nombre_estado")
	private String nombreEstado;
	@Basic(optional = false)
	@Column(name = "orden_estado")
	private int ordenEstado;
	@Basic(optional = false)
	@Column(name = "finalizado")
	private boolean finalizado;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "estadoDestinoId")
	private Collection<HistorialEstadosPedido> historialEstadosPedidoCollection;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "estadoOrigenId")
	private Collection<HistorialEstadosPedido> historialEstadosPedidoCollection1;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "estadoActualId")
	private Collection<PedidosCliente> pedidosClienteCollection;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "estadoDestinoId")
	private Collection<ReglasTransicionEstadoPedido> reglasTransicionEstadoPedidoCollection;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "estadoOrigenId")
	private Collection<ReglasTransicionEstadoPedido> reglasTransicionEstadoPedidoCollection1;

	public EstadosPedido() {
	}

	public EstadosPedido(Long estadoId) {
		this.estadoId = estadoId;
	}

	public EstadosPedido(Long estadoId, String codigoEstado, String nombreEstado, int ordenEstado, boolean finalizado) {
		this.estadoId = estadoId;
		this.codigoEstado = codigoEstado;
		this.nombreEstado = nombreEstado;
		this.ordenEstado = ordenEstado;
		this.finalizado = finalizado;
	}

	public Long getEstadoId() {
		return estadoId;
	}

	public void setEstadoId(Long estadoId) {
		this.estadoId = estadoId;
	}

	public String getCodigoEstado() {
		return codigoEstado;
	}

	public void setCodigoEstado(String codigoEstado) {
		this.codigoEstado = codigoEstado;
	}

	public String getNombreEstado() {
		return nombreEstado;
	}

	public void setNombreEstado(String nombreEstado) {
		this.nombreEstado = nombreEstado;
	}

	public int getOrdenEstado() {
		return ordenEstado;
	}

	public void setOrdenEstado(int ordenEstado) {
		this.ordenEstado = ordenEstado;
	}

	public boolean getFinalizado() {
		return finalizado;
	}

	public void setFinalizado(boolean finalizado) {
		this.finalizado = finalizado;
	}

	public Collection<HistorialEstadosPedido> getHistorialEstadosPedidoCollection() {
		return historialEstadosPedidoCollection;
	}

	public void setHistorialEstadosPedidoCollection(Collection<HistorialEstadosPedido> historialEstadosPedidoCollection) {
		this.historialEstadosPedidoCollection = historialEstadosPedidoCollection;
	}

	public Collection<HistorialEstadosPedido> getHistorialEstadosPedidoCollection1() {
		return historialEstadosPedidoCollection1;
	}

	public void setHistorialEstadosPedidoCollection1(Collection<HistorialEstadosPedido> historialEstadosPedidoCollection1) {
		this.historialEstadosPedidoCollection1 = historialEstadosPedidoCollection1;
	}

	public Collection<PedidosCliente> getPedidosClienteCollection() {
		return pedidosClienteCollection;
	}

	public void setPedidosClienteCollection(Collection<PedidosCliente> pedidosClienteCollection) {
		this.pedidosClienteCollection = pedidosClienteCollection;
	}

	public Collection<ReglasTransicionEstadoPedido> getReglasTransicionEstadoPedidoCollection() {
		return reglasTransicionEstadoPedidoCollection;
	}

	public void setReglasTransicionEstadoPedidoCollection(Collection<ReglasTransicionEstadoPedido> reglasTransicionEstadoPedidoCollection) {
		this.reglasTransicionEstadoPedidoCollection = reglasTransicionEstadoPedidoCollection;
	}

	public Collection<ReglasTransicionEstadoPedido> getReglasTransicionEstadoPedidoCollection1() {
		return reglasTransicionEstadoPedidoCollection1;
	}

	public void setReglasTransicionEstadoPedidoCollection1(Collection<ReglasTransicionEstadoPedido> reglasTransicionEstadoPedidoCollection1) {
		this.reglasTransicionEstadoPedidoCollection1 = reglasTransicionEstadoPedidoCollection1;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (estadoId != null ? estadoId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof EstadosPedido other)) {
			return false;
		}
		return (this.estadoId != null || other.estadoId == null) && (this.estadoId == null || this.estadoId.equals(other.estadoId));
	}

	@Override
	public String toString() {
		return "Clases.EstadosPedido[ estadoId=" + estadoId + " ]";
	}

}
