/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurante.pedidos.Clases;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Javier Montaño
 */
@Entity
@Table(name = "entregas")
@NamedQueries({
    @NamedQuery(name = "Entregas.findAll", query = "SELECT e FROM Entregas e")})
public class Entregas implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "entrega_id")
    private Long entregaId;
    @Basic(optional = false)
    @Column(name = "despachado_en")
    @Temporal(TemporalType.TIMESTAMP)
    private Date despachadoEn;
    @Column(name = "entregado_en")
    @Temporal(TemporalType.TIMESTAMP)
    private Date entregadoEn;
    @Column(name = "nombre_receptor")
    private String nombreReceptor;
    @Column(name = "confirmado_por_cliente_en")
    @Temporal(TemporalType.TIMESTAMP)
    private Date confirmadoPorClienteEn;
    @Column(name = "notas_confirmacion_cliente")
    private String notasConfirmacionCliente;
    @Basic(optional = false)
    @Column(name = "estado_entrega")
    private String estadoEntrega;
    @Column(name = "notas_repartidor")
    private String notasRepartidor;
    @Basic(optional = false)
    @Column(name = "numero_intento")
    private int numeroIntento;
    @JoinColumn(name = "pedido_id", referencedColumnName = "pedido_id")
    @OneToOne(optional = false)
    private PedidosCliente pedidoId;
    @JoinColumn(name = "repartidor_personal_id", referencedColumnName = "personal_id")
    @ManyToOne(optional = false)
    private Personal repartidorPersonalId;

    public Entregas() {
    }

    public Entregas(Long entregaId) {
        this.entregaId = entregaId;
    }

    public Entregas(Long entregaId, Date despachadoEn, String estadoEntrega, int numeroIntento) {
        this.entregaId = entregaId;
        this.despachadoEn = despachadoEn;
        this.estadoEntrega = estadoEntrega;
        this.numeroIntento = numeroIntento;
    }

    public Long getEntregaId() {
        return entregaId;
    }

    public void setEntregaId(Long entregaId) {
        this.entregaId = entregaId;
    }

    public Date getDespachadoEn() {
        return despachadoEn;
    }

    public void setDespachadoEn(Date despachadoEn) {
        this.despachadoEn = despachadoEn;
    }

    public Date getEntregadoEn() {
        return entregadoEn;
    }

    public void setEntregadoEn(Date entregadoEn) {
        this.entregadoEn = entregadoEn;
    }

    public String getNombreReceptor() {
        return nombreReceptor;
    }

    public void setNombreReceptor(String nombreReceptor) {
        this.nombreReceptor = nombreReceptor;
    }

    public Date getConfirmadoPorClienteEn() {
        return confirmadoPorClienteEn;
    }

    public void setConfirmadoPorClienteEn(Date confirmadoPorClienteEn) {
        this.confirmadoPorClienteEn = confirmadoPorClienteEn;
    }

    public String getNotasConfirmacionCliente() {
        return notasConfirmacionCliente;
    }

    public void setNotasConfirmacionCliente(String notasConfirmacionCliente) {
        this.notasConfirmacionCliente = notasConfirmacionCliente;
    }

    public String getEstadoEntrega() {
        return estadoEntrega;
    }

    public void setEstadoEntrega(String estadoEntrega) {
        this.estadoEntrega = estadoEntrega;
    }

    public String getNotasRepartidor() {
        return notasRepartidor;
    }

    public void setNotasRepartidor(String notasRepartidor) {
        this.notasRepartidor = notasRepartidor;
    }

    public int getNumeroIntento() {
        return numeroIntento;
    }

    public void setNumeroIntento(int numeroIntento) {
        this.numeroIntento = numeroIntento;
    }

    public PedidosCliente getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(PedidosCliente pedidoId) {
        this.pedidoId = pedidoId;
    }

    public Personal getRepartidorPersonalId() {
        return repartidorPersonalId;
    }

    public void setRepartidorPersonalId(Personal repartidorPersonalId) {
        this.repartidorPersonalId = repartidorPersonalId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (entregaId != null ? entregaId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Entregas)) {
            return false;
        }
        Entregas other = (Entregas) object;
        if ((this.entregaId == null && other.entregaId != null) || (this.entregaId != null && !this.entregaId.equals(other.entregaId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Clases.Entregas[ entregaId=" + entregaId + " ]";
    }

}
