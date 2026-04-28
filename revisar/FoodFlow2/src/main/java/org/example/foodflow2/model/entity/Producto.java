package org.example.foodflow2.model.entity;

import org.example.foodflow2.model.enums.CategoriaProducto;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "productos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20, unique = true)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "costo_produccion", precision = 10, scale = 2)
    private BigDecimal costoProduccion = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoriaProducto categoria;

    @Column(name = "tiempo_preparacion_min")
    private Integer tiempoPreparacionMin = 15;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "imagen_url", length = 255)
    private String imagenUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}