/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurante.pedidos.Clases;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Javier Montaño
 */
@Entity
@Table(name = "clientes")
@NamedQueries({
    @NamedQuery(name = "Clientes.findAll", query = "SELECT c FROM Clientes c")})
public class Clientes implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "cliente_id")
    private Long clienteId;
    @Basic(optional = false)
    @Column(name = "nombre_completo")
    private String nombreCompleto;
    @Column(name = "telefono")
    private String telefono;
    @Column(name = "correo_electronico")
    private String correoElectronico;
    @Basic(optional = false)
    @Column(name = "activo")
    private boolean activo;
    @Basic(optional = false)
    @Column(name = "creado_en")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creadoEn;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "clienteId")
    private Collection<PedidosCliente> pedidosClienteCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "clienteId")
    private Collection<DireccionesCliente> direccionesClienteCollection;

    public Clientes() {
    }

    public Clientes(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Clientes(Long clienteId, String nombreCompleto, boolean activo, Date creadoEn) {
        this.clienteId = clienteId;
        this.nombreCompleto = nombreCompleto;
        this.activo = activo;
        this.creadoEn = creadoEn;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public boolean getActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Date getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Date creadoEn) {
        this.creadoEn = creadoEn;
    }

    public Collection<PedidosCliente> getPedidosClienteCollection() {
        return pedidosClienteCollection;
    }

    public void setPedidosClienteCollection(Collection<PedidosCliente> pedidosClienteCollection) {
        this.pedidosClienteCollection = pedidosClienteCollection;
    }

    public Collection<DireccionesCliente> getDireccionesClienteCollection() {
        return direccionesClienteCollection;
    }

    public void setDireccionesClienteCollection(Collection<DireccionesCliente> direccionesClienteCollection) {
        this.direccionesClienteCollection = direccionesClienteCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (clienteId != null ? clienteId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Clientes)) {
            return false;
        }
        Clientes other = (Clientes) object;
        if ((this.clienteId == null && other.clienteId != null) || (this.clienteId != null && !this.clienteId.equals(other.clienteId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Clases.Clientes[ clienteId=" + clienteId + " ]";
    }

}
