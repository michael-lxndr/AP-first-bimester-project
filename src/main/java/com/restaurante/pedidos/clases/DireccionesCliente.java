/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurante.pedidos.clases;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;


@Entity
@Table(name = "direcciones_cliente")
@NamedQueries({
	@NamedQuery(name = "DireccionesCliente.findAll", query = "SELECT d FROM DireccionesCliente d")})
public class DireccionesCliente implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "direccion_id")
	private Long direccionId;
	@Basic(optional = false)
	@Column(name = "alias")
	private String alias;
	@Basic(optional = false)
	@Column(name = "calle_principal")
	private String callePrincipal;
	@Column(name = "calle_secundaria")
	private String calleSecundaria;
	@Column(name = "numero_casa")
	private String numeroCasa;
	@Column(name = "referencia")
	private String referencia;
	@Column(name = "codigo_postal")
	private String codigoPostal;
	@Basic(optional = false)
	@Column(name = "ciudad")
	private String ciudad;
	@Basic(optional = false)
	@Column(name = "provincia")
	private String provincia;
	@Basic(optional = false)
	@Column(name = "pais")
	private String pais;
	@Basic(optional = false)
	@Column(name = "principal")
	private boolean principal;
	@Basic(optional = false)
	@Column(name = "activa")
	private boolean activa;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "direccionEntregaId")
	private Collection<PedidosCliente> pedidosClienteCollection;
	@JoinColumn(name = "cliente_id", referencedColumnName = "cliente_id")
	@ManyToOne(optional = false)
	private Clientes clienteId;

	public DireccionesCliente() {
	}

	public DireccionesCliente(Long direccionId) {
		this.direccionId = direccionId;
	}

	public DireccionesCliente(Long direccionId, String alias, String callePrincipal, String ciudad, String provincia, String pais, boolean principal, boolean activa) {
		this.direccionId = direccionId;
		this.alias = alias;
		this.callePrincipal = callePrincipal;
		this.ciudad = ciudad;
		this.provincia = provincia;
		this.pais = pais;
		this.principal = principal;
		this.activa = activa;
	}

	public Long getDireccionId() {
		return direccionId;
	}

	public void setDireccionId(Long direccionId) {
		this.direccionId = direccionId;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getCallePrincipal() {
		return callePrincipal;
	}

	public void setCallePrincipal(String callePrincipal) {
		this.callePrincipal = callePrincipal;
	}

	public String getCalleSecundaria() {
		return calleSecundaria;
	}

	public void setCalleSecundaria(String calleSecundaria) {
		this.calleSecundaria = calleSecundaria;
	}

	public String getNumeroCasa() {
		return numeroCasa;
	}

	public void setNumeroCasa(String numeroCasa) {
		this.numeroCasa = numeroCasa;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public String getCodigoPostal() {
		return codigoPostal;
	}

	public void setCodigoPostal(String codigoPostal) {
		this.codigoPostal = codigoPostal;
	}

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public boolean getPrincipal() {
		return principal;
	}

	public void setPrincipal(boolean principal) {
		this.principal = principal;
	}

	public boolean getActiva() {
		return activa;
	}

	public void setActiva(boolean activa) {
		this.activa = activa;
	}

	public Collection<PedidosCliente> getPedidosClienteCollection() {
		return pedidosClienteCollection;
	}

	public void setPedidosClienteCollection(Collection<PedidosCliente> pedidosClienteCollection) {
		this.pedidosClienteCollection = pedidosClienteCollection;
	}

	public Clientes getClienteId() {
		return clienteId;
	}

	public void setClienteId(Clientes clienteId) {
		this.clienteId = clienteId;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (direccionId != null ? direccionId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof DireccionesCliente other)) {
			return false;
		}
		return (this.direccionId != null || other.direccionId == null) && (this.direccionId == null || this.direccionId.equals(other.direccionId));
	}

	@Override
	public String toString() {
		return "Clases.DireccionesCliente[ direccionId=" + direccionId + " ]";
	}

}
