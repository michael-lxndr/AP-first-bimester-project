package org.example.foodflow2.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "direcciones")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Direccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String alias;

    @Column(name = "calle_principal", nullable = false, length = 150)
    private String callePrincipal;

    @Column(name = "calle_secundaria", length = 150)
    private String calleSecundaria;

    @Column(name = "numero_casa", length = 10)
    private String numeroCasa;

    @Column(length = 255)
    private String referencia;

    @Column(length = 10)
    private String codigoPostal;

    @Column(length = 50)
    private String ciudad = "Quito";

    @Column(length = 50)
    private String provincia = "Pichincha";

    @Column(name = "principal")
    private Boolean principal = false;

    @Column(name = "activa")
    private Boolean activa = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @ToString.Exclude
    private Cliente cliente;
}