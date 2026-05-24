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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Javier Montaño
 */
@Entity
@Table(name = "historial_estados_pedido")
@NamedQueries({
    @NamedQuery(name = "HistorialEstadosPedido.findAll", query = "SELECT h FROM HistorialEstadosPedido h")})
public class HistorialEstadosPedido implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "historial_id")
    private Long historialId;
    @Basic(optional = false)
    @Column(name = "cambiado_en")
    @Temporal(TemporalType.TIMESTAMP)
    private Date cambiadoEn;
    @Column(name = "notas")
    private String notas;
    @JoinColumn(name = "estado_destino_id", referencedColumnName = "estado_id")
    @ManyToOne(optional = false)
    private EstadosPedido estadoDestinoId;
    @JoinColumn(name = "estado_origen_id", referencedColumnName = "estado_id")
    @ManyToOne(optional = false)
    private EstadosPedido estadoOrigenId;
    @JoinColumn(name = "pedido_id", referencedColumnName = "pedido_id")
    @ManyToOne(optional = false)
    private PedidosCliente pedidoId;
    @JoinColumn(name = "cambiado_por_personal_id", referencedColumnName = "personal_id")
    @ManyToOne(optional = false)
    private Personal cambiadoPorPersonalId;

    public HistorialEstadosPedido() {
    }

    public HistorialEstadosPedido(Long historialId) {
        this.historialId = historialId;
    }

    public HistorialEstadosPedido(Long historialId, Date cambiadoEn) {
        this.historialId = historialId;
        this.cambiadoEn = cambiadoEn;
    }

    public Long getHistorialId() {
        return historialId;
    }

    public void setHistorialId(Long historialId) {
        this.historialId = historialId;
    }

    public Date getCambiadoEn() {
        return cambiadoEn;
    }

    public void setCambiadoEn(Date cambiadoEn) {
        this.cambiadoEn = cambiadoEn;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
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

    public PedidosCliente getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(PedidosCliente pedidoId) {
        this.pedidoId = pedidoId;
    }

    public Personal getCambiadoPorPersonalId() {
        return cambiadoPorPersonalId;
    }

    public void setCambiadoPorPersonalId(Personal cambiadoPorPersonalId) {
        this.cambiadoPorPersonalId = cambiadoPorPersonalId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (historialId != null ? historialId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof HistorialEstadosPedido)) {
            return false;
        }
        HistorialEstadosPedido other = (HistorialEstadosPedido) object;
        if ((this.historialId == null && other.historialId != null) || (this.historialId != null && !this.historialId.equals(other.historialId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Clases.HistorialEstadosPedido[ historialId=" + historialId + " ]";
    }

}
