package org.example.foodflow2.model.entity;

import org.example.foodflow2.model.enums.RolUsuario;
import org.example.foodflow2.model.enums.TipoVehiculo;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "repartidores")
@PrimaryKeyJoinColumn(name = "id")
@Getter @Setter
@NoArgsConstructor
public class Repartidor extends Usuario {

    @Column(name = "disponible")
    private Boolean disponible = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_vehiculo", length = 20)
    private TipoVehiculo tipoVehiculo = TipoVehiculo.MOTO;

    @Column(name = "placa_vehiculo", length = 10)
    private String placaVehiculo;

    @Builder
    public Repartidor(String nombre, String apellido, String cedula,
                      String telefono, String email, String username,
                      String password, TipoVehiculo tipoVehiculo, String placaVehiculo) {
        super(null, nombre, apellido, cedula, telefono, email, username,
                password, RolUsuario.REPARTIDOR , true, null);
        this.tipoVehiculo = tipoVehiculo;
        this.placaVehiculo = placaVehiculo;
    }
}