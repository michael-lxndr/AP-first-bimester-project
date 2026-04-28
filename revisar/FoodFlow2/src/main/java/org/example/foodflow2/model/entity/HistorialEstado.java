package org.example.foodflow2.model.entity;

import org.example.foodflow2.model.enums.EstadoPedido;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_estado")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class HistorialEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    @ToString.Exclude
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior", length = 20)
    private EstadoPedido estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo", nullable = false, length = 20)
    private EstadoPedido estadoNuevo;

    @Column(name = "fecha_cambio")
    private LocalDateTime fechaCambio = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_id")
    @ToString.Exclude
    private Usuario responsable;

    @Column(length = 255)
    private String observacion;

    @Column(name = "automatico")
    private Boolean automatico = false;
}