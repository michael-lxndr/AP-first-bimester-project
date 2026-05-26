package com.restaurante.pedidos.dominio;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Prueba de persistencia básica de entidades de dominio.
 * Requiere:
 *   - MySQL corriendo en localhost:3306
 *   - BD proyecto_primer_bimestre creada
 *   - Entidades migradas a paquete dominio.entidad (pendiente)
 *
 * Se habilita cuando Persona 1 complete la migración de entidades.
 */
@DisplayName("DominioTest - Persistencia de entidades (requiere BD)")
class DominioTest {

    @Test
    @Disabled("Requiere BD activa y migración de entidades a dominio.entidad - pendiente Persona 1")
    @DisplayName("Entidades básicas persisten sin Lombok")
    void entidadesBasicasPersistenSinLombok() {
        // Este test se habilitará cuando existan:
        // - com.restaurante.pedidos.dominio.entidad.Cliente
        // - com.restaurante.pedidos.dominio.entidad.EstadoPedido
        // - com.restaurante.pedidos.dominio.entidad.Rol
        // - com.restaurante.pedidos.configuracion.ConfiguracionBaseDatos
    }
}
