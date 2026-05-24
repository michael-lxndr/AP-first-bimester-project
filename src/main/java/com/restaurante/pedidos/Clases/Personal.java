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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "personal")
@NamedQueries({
    @NamedQuery(name = "Personal.findAll", query = "SELECT p FROM Personal p")})
public class Personal implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "personal_id")
    private Long personalId;
    @Basic(optional = false)
    @Column(name = "nombre_completo")
    private String nombreCompleto;
    @Column(name = "telefono")
    private String telefono;
    @Basic(optional = false)
    @Column(name = "correo_electronico")
    private String correoElectronico;
    @Basic(optional = false)
    @Column(name = "nombre_usuario")
    private String nombreUsuario;
    @Basic(optional = false)
    @Column(name = "activo")
    private boolean activo;
    @Basic(optional = false)
    @Column(name = "creado_en")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creadoEn;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cambiadoPorPersonalId")
    private Collection<HistorialEstadosPedido> historialEstadosPedidoCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "repartidorPersonalId")
    private Collection<Entregas> entregasCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "registradoPorPersonalId")
    private Collection<PedidosCliente> pedidosClienteCollection;
    @JoinColumn(name = "rol_id", referencedColumnName = "rol_id")
    @ManyToOne(optional = false)
    private Roles rolId;

    public Personal() {
    }

    public Personal(Long personalId) {
        this.personalId = personalId;
    }

    public Personal(Long personalId, String nombreCompleto, String correoElectronico, String nombreUsuario, boolean activo, Date creadoEn) {
        this.personalId = personalId;
        this.nombreCompleto = nombreCompleto;
        this.correoElectronico = correoElectronico;
        this.nombreUsuario = nombreUsuario;
        this.activo = activo;
        this.creadoEn = creadoEn;
    }

    public Long getPersonalId() {
        return personalId;
    }

    public void setPersonalId(Long personalId) {
        this.personalId = personalId;
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

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
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

    public Collection<HistorialEstadosPedido> getHistorialEstadosPedidoCollection() {
        return historialEstadosPedidoCollection;
    }

    public void setHistorialEstadosPedidoCollection(Collection<HistorialEstadosPedido> historialEstadosPedidoCollection) {
        this.historialEstadosPedidoCollection = historialEstadosPedidoCollection;
    }

    public Collection<Entregas> getEntregasCollection() {
        return entregasCollection;
    }

    public void setEntregasCollection(Collection<Entregas> entregasCollection) {
        this.entregasCollection = entregasCollection;
    }

    public Collection<PedidosCliente> getPedidosClienteCollection() {
        return pedidosClienteCollection;
    }

    public void setPedidosClienteCollection(Collection<PedidosCliente> pedidosClienteCollection) {
        this.pedidosClienteCollection = pedidosClienteCollection;
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
        hash += (personalId != null ? personalId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Personal)) {
            return false;
        }
        Personal other = (Personal) object;
        if ((this.personalId == null && other.personalId != null) || (this.personalId != null && !this.personalId.equals(other.personalId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Clases.Personal[ personalId=" + personalId + " ]";
    }

}
