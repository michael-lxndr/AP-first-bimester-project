/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurante.pedidos.Clases;

import java.io.Serializable;
import java.util.Collection;
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


@Entity
@Table(name = "roles")
@NamedQueries({
    @NamedQuery(name = "Roles.findAll", query = "SELECT r FROM Roles r")})
public class Roles implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "rol_id")
    private Long rolId;
    @Basic(optional = false)
    @Column(name = "codigo_rol")
    private String codigoRol;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rolId")
    private Collection<Personal> personalCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rolId")
    private Collection<ReglasTransicionEstadoPedido> reglasTransicionEstadoPedidoCollection;

    public Roles() {
    }

    public Roles(Long rolId) {
        this.rolId = rolId;
    }

    public Roles(Long rolId, String codigoRol) {
        this.rolId = rolId;
        this.codigoRol = codigoRol;
    }

    public Long getRolId() {
        return rolId;
    }

    public void setRolId(Long rolId) {
        this.rolId = rolId;
    }

    public String getCodigoRol() {
        return codigoRol;
    }

    public void setCodigoRol(String codigoRol) {
        this.codigoRol = codigoRol;
    }

    public Collection<Personal> getPersonalCollection() {
        return personalCollection;
    }

    public void setPersonalCollection(Collection<Personal> personalCollection) {
        this.personalCollection = personalCollection;
    }

    public Collection<ReglasTransicionEstadoPedido> getReglasTransicionEstadoPedidoCollection() {
        return reglasTransicionEstadoPedidoCollection;
    }

    public void setReglasTransicionEstadoPedidoCollection(Collection<ReglasTransicionEstadoPedido> reglasTransicionEstadoPedidoCollection) {
        this.reglasTransicionEstadoPedidoCollection = reglasTransicionEstadoPedidoCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (rolId != null ? rolId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Roles)) {
            return false;
        }
        Roles other = (Roles) object;
        if ((this.rolId == null && other.rolId != null) || (this.rolId != null && !this.rolId.equals(other.rolId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Clases.Roles[ rolId=" + rolId + " ]";
    }

}
