/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurante.pedidos.Clases;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name = "pedidos_cliente")
@NamedQueries({
    @NamedQuery(name = "PedidosCliente.findAll", query = "SELECT p FROM PedidosCliente p")})
public class PedidosCliente implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "pedido_id")
    private Long pedidoId;
    @Basic(optional = false)
    @Column(name = "codigo_pedido")
    private String codigoPedido;
    @Basic(optional = false)
    @Column(name = "snapshot_direccion_entrega")
    private String snapshotDireccionEntrega;
    @Column(name = "instrucciones_entrega")
    private String instruccionesEntrega;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "subtotal")
    private BigDecimal subtotal;
    @Column(name = "impuesto")
    private BigDecimal impuesto;
    @Column(name = "descuento")
    private BigDecimal descuento;
    @Column(name = "recargo_direccion")
    private BigDecimal recargoDireccion;
    @Basic(optional = false)
    @Column(name = "total")
    private BigDecimal total;
    @Column(name = "codigo_descuento")
    private String codigoDescuento;
    @Basic(optional = false)
    @Column(name = "prioritario")
    private boolean prioritario;
    @Column(name = "notas_generales")
    private String notasGenerales;
    @Basic(optional = false)
    @Column(name = "creado_en")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creadoEn;
    @Column(name = "entrega_estimada_en")
    @Temporal(TemporalType.TIMESTAMP)
    private Date entregaEstimadaEn;
    @Basic(optional = false)
    @Column(name = "estado_actual_cambiado_en")
    @Temporal(TemporalType.TIMESTAMP)
    private Date estadoActualCambiadoEn;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pedidoId")
    private Collection<HistorialEstadosPedido> historialEstadosPedidoCollection;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "pedidoId")
    private Entregas entregas;
    @JoinColumn(name = "cliente_id", referencedColumnName = "cliente_id")
    @ManyToOne(optional = false)
    private Clientes clienteId;
    @JoinColumn(name = "direccion_entrega_id", referencedColumnName = "direccion_id")
    @ManyToOne(optional = false)
    private DireccionesCliente direccionEntregaId;
    @JoinColumn(name = "estado_actual_id", referencedColumnName = "estado_id")
    @ManyToOne(optional = false)
    private EstadosPedido estadoActualId;
    @JoinColumn(name = "registrado_por_personal_id", referencedColumnName = "personal_id")
    @ManyToOne(optional = false)
    private Personal registradoPorPersonalId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pedidoId")
    private Collection<ItemsPedido> itemsPedidoCollection;

    public PedidosCliente() {
    }

    public PedidosCliente(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public PedidosCliente(Long pedidoId, String codigoPedido, String snapshotDireccionEntrega, BigDecimal total, boolean prioritario, Date creadoEn, Date estadoActualCambiadoEn) {
        this.pedidoId = pedidoId;
        this.codigoPedido = codigoPedido;
        this.snapshotDireccionEntrega = snapshotDireccionEntrega;
        this.total = total;
        this.prioritario = prioritario;
        this.creadoEn = creadoEn;
        this.estadoActualCambiadoEn = estadoActualCambiadoEn;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public String getCodigoPedido() {
        return codigoPedido;
    }

    public void setCodigoPedido(String codigoPedido) {
        this.codigoPedido = codigoPedido;
    }

    public String getSnapshotDireccionEntrega() {
        return snapshotDireccionEntrega;
    }

    public void setSnapshotDireccionEntrega(String snapshotDireccionEntrega) {
        this.snapshotDireccionEntrega = snapshotDireccionEntrega;
    }

    public String getInstruccionesEntrega() {
        return instruccionesEntrega;
    }

    public void setInstruccionesEntrega(String instruccionesEntrega) {
        this.instruccionesEntrega = instruccionesEntrega;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(BigDecimal impuesto) {
        this.impuesto = impuesto;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getRecargoDireccion() {
        return recargoDireccion;
    }

    public void setRecargoDireccion(BigDecimal recargoDireccion) {
        this.recargoDireccion = recargoDireccion;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getCodigoDescuento() {
        return codigoDescuento;
    }

    public void setCodigoDescuento(String codigoDescuento) {
        this.codigoDescuento = codigoDescuento;
    }

    public boolean getPrioritario() {
        return prioritario;
    }

    public void setPrioritario(boolean prioritario) {
        this.prioritario = prioritario;
    }

    public String getNotasGenerales() {
        return notasGenerales;
    }

    public void setNotasGenerales(String notasGenerales) {
        this.notasGenerales = notasGenerales;
    }

    public Date getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Date creadoEn) {
        this.creadoEn = creadoEn;
    }

    public Date getEntregaEstimadaEn() {
        return entregaEstimadaEn;
    }

    public void setEntregaEstimadaEn(Date entregaEstimadaEn) {
        this.entregaEstimadaEn = entregaEstimadaEn;
    }

    public Date getEstadoActualCambiadoEn() {
        return estadoActualCambiadoEn;
    }

    public void setEstadoActualCambiadoEn(Date estadoActualCambiadoEn) {
        this.estadoActualCambiadoEn = estadoActualCambiadoEn;
    }

    public Collection<HistorialEstadosPedido> getHistorialEstadosPedidoCollection() {
        return historialEstadosPedidoCollection;
    }

    public void setHistorialEstadosPedidoCollection(Collection<HistorialEstadosPedido> historialEstadosPedidoCollection) {
        this.historialEstadosPedidoCollection = historialEstadosPedidoCollection;
    }

    public Entregas getEntregas() {
        return entregas;
    }

    public void setEntregas(Entregas entregas) {
        this.entregas = entregas;
    }

    public Clientes getClienteId() {
        return clienteId;
    }

    public void setClienteId(Clientes clienteId) {
        this.clienteId = clienteId;
    }

    public DireccionesCliente getDireccionEntregaId() {
        return direccionEntregaId;
    }

    public void setDireccionEntregaId(DireccionesCliente direccionEntregaId) {
        this.direccionEntregaId = direccionEntregaId;
    }

    public EstadosPedido getEstadoActualId() {
        return estadoActualId;
    }

    public void setEstadoActualId(EstadosPedido estadoActualId) {
        this.estadoActualId = estadoActualId;
    }

    public Personal getRegistradoPorPersonalId() {
        return registradoPorPersonalId;
    }

    public void setRegistradoPorPersonalId(Personal registradoPorPersonalId) {
        this.registradoPorPersonalId = registradoPorPersonalId;
    }

    public Collection<ItemsPedido> getItemsPedidoCollection() {
        return itemsPedidoCollection;
    }

    public void setItemsPedidoCollection(Collection<ItemsPedido> itemsPedidoCollection) {
        this.itemsPedidoCollection = itemsPedidoCollection;
    }

    @PrePersist
    public void prePersist() {
    if (estadoActualCambiadoEn == null) {
        estadoActualCambiadoEn = new Date();
    }
}

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (pedidoId != null ? pedidoId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PedidosCliente)) {
            return false;
        }
        PedidosCliente other = (PedidosCliente) object;
        if ((this.pedidoId == null && other.pedidoId != null) || (this.pedidoId != null && !this.pedidoId.equals(other.pedidoId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Clases.PedidosCliente[ pedidoId=" + pedidoId + " ]";
    }

}
