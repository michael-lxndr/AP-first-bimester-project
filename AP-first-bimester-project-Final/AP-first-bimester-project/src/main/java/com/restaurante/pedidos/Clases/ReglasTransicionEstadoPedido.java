/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurante.pedidos.Clases;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "reglas_transicion_estado_pedido")
@NamedQueries({
	@NamedQuery(name = "ReglasTransicionEstadoPedido.findAll", query = "SELECT r FROM ReglasTransicionEstadoPedido r")})
public class ReglasTransicionEstadoPedido implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "regla_transicion_id")
	private Long reglaTransicionId;
	@Basic(optional = false)
	@Column(name = "activa")
	private boolean activa;
	@JoinColumn(name = "estado_destino_id", referencedColumnName = "estado_id")
	@ManyToOne(optional = false)
	private EstadosPedido estadoDestinoId;
	@JoinColumn(name = "estado_origen_id", referencedColumnName = "estado_id")
	@ManyToOne(optional = false)
	private EstadosPedido estadoOrigenId;
	@JoinColumn(name = "rol_id", referencedColumnName = "rol_id")
	@ManyToOne(optional = false)
	private Roles rolId;

	public ReglasTransicionEstadoPedido() {
	}

	public ReglasTransicionEstadoPedido(Long reglaTransicionId) {
		this.reglaTransicionId = reglaTransicionId;
	}

	public ReglasTransicionEstadoPedido(Long reglaTransicionId, boolean activa) {
		this.reglaTransicionId = reglaTransicionId;
		this.activa = activa;
	}

	public Long getReglaTransicionId() {
		return reglaTransicionId;
	}

	public void setReglaTransicionId(Long reglaTransicionId) {
		this.reglaTransicionId = reglaTransicionId;
	}

	public boolean getActiva() {
		return activa;
	}

	public void setActiva(boolean activa) {
		this.activa = activa;
	}

	public EstadosPedido getEstadoDestinoId() {
		return estadoDestinoId;
	}

	public void setEstadoDestinoId(EstadosPedido estadoDestinoId) {
		this.estadoDestinoId = estadoDestinoId;
	}

	public EstadosPedido getEstadoOrigenId() {
		return estadoOrigenId;
	}

	public void setEstadoOrigenId(EstadosPedido estadoOrigenId) {
		this.estadoOrigenId = estadoOrigenId;
	}

	public Roles getRolId() {
		return rolId;
	}

	public void setRolId(Roles rolId) {
		this.rolId = rolId;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (reglaTransicionId != null ? reglaTransicionId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof ReglasTransicionEstadoPedido other)) {
			return false;
		}
		return (this.reglaTransicionId != null || other.reglaTransicionId == null) && (this.reglaTransicionId == null || this.reglaTransicionId.equals(other.reglaTransicionId));
	}

	@Override
	public String toString() {
		return "Clases.ReglasTransicionEstadoPedido[ reglaTransicionId=" + reglaTransicionId + " ]";
	}

}
