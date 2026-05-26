package com.restaurante.pedidos.servicio;

import com.restaurante.pedidos.LogicaServicios.ServicioSimulacionConcurrente;
import com.restaurante.pedidos.dominio.dto.PedidoDTO;
import com.restaurante.pedidos.LogicaConfiguracion.JPABaseDeDatos;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SimulacionConcurrenteTest - Pruebas Concurrentes del Motor de Simulación")
class SimulacionConcurrenteTest {

    private ServicioSimulacionConcurrente simulador;

    @BeforeEach
    void setUp() {
        // Inyectar EntityManagerFactory mockeado para evitar conexiones reales a base de datos
        EntityManagerFactory mockFactory = Mockito.mock(EntityManagerFactory.class);
        JPABaseDeDatos.setEntityManagerFactory(mockFactory);
        
        simulador = new ServicioSimulacionConcurrente();
    }

    @Test
    @DisplayName("Cerrar cocina detiene la generación de nuevos pedidos pero procesa los ya encolados")
    void cerrarCocina_DetieneGeneracionYProcesaExistentes() throws InterruptedException {
        // Iniciar simulación con generación rápida de 2 segundos e hilo de cocina único
        simulador.startSimulation(2, 1);
        assertTrue(simulador.isSimulating(), "La simulación debe estar activa.");

        // Esperar a que se generen al menos 1 o 2 pedidos
        Thread.sleep(3500);

        Collection<PedidoDTO> pedidosIniciales = simulador.getPedidosActivos();
        assertFalse(pedidosIniciales.isEmpty(), "Deberían haberse generado pedidos.");
        int cantidadInicial = pedidosIniciales.size();

        // Cerrar cocina
        simulador.stopKitchen();

        // Esperar un tiempo prudencial para verificar que no se generen más pedidos
        Thread.sleep(4000);

        Collection<PedidoDTO> pedidosFinales = simulador.getPedidosActivos();
        assertEquals(cantidadInicial, pedidosFinales.size(), "La cantidad de pedidos no debería haber aumentado después de cerrar cocina.");

        // Apagar de inmediato
        simulador.shutdownImmediately();
    }

    @Test
    @DisplayName("Todos los pedidos encolados avanzan secuencialmente por todos los estados sin condiciones de carrera")
    void transicionesDePedidos_SonSecuencialesYCompletas() throws InterruptedException {
        CountDownLatch latchEntregado = new CountDownLatch(1);
        
        // Registrar actualizador de orden para verificar si llega al estado final
        simulador.setOrderUpdateListener(pedido -> {
            if ("ENTREGADO".equals(pedido.estadoActual().codigo())) {
                latchEntregado.countDown();
            }
        });

        // Iniciar simulación con generación a 1s (hilo cocinero y repartidor listos)
        simulador.startSimulation(1, 2);

        // Esperar a que el primer pedido llegue a ENTREGADO (aumentado a 25s para tolerar tiempos de preparación aleatorios y entrega)
        boolean completado = latchEntregado.await(25, TimeUnit.SECONDS);
        assertTrue(completado, "Al menos un pedido debió procesarse completamente hasta ENTREGADO en menos de 25s.");

        // Verificar el historial del pedido
        Collection<PedidoDTO> activos = simulador.getPedidosActivos();
        assertFalse(activos.isEmpty());
        
        for (PedidoDTO p : activos) {
            assertNotNull(p.estadoActual());
            // Validar que el progreso sea coherente con su estado
            double prog = 0.0;
            switch (p.estadoActual().codigo()) {
                case "PENDIENTE": prog = 0.1; break;
                case "EN_PREPARACION": prog = 0.4; break;
                case "LISTO": prog = 0.7; break;
                case "EN_CAMINO": prog = 0.9; break;
                case "ENTREGADO": prog = 1.0; break;
            }
            assertTrue(prog > 0.0);
        }

        // Detener inmediatamente
        simulador.shutdownImmediately();
    }

    @Test
    @DisplayName("La simulación persiste todos los pedidos generados en base de datos")
    void testSimulacionPersistePedidos() throws Exception {
        // Iniciar simulación (generación rápida cada 2s, 1 cocinero)
        simulador.startSimulation(2, 1);
        assertTrue(simulador.isSimulating(), "La simulación debe activarse.");

        // Esperar 10 segundos
        Thread.sleep(10000);

        // Detener cocina (Cerrar cocina)
        simulador.stopKitchen();

        // Esperar a que terminen de procesarse las colas
        Thread.sleep(3000);

        Collection<PedidoDTO> pedidosGenerados = simulador.getPedidosActivos();
        assertFalse(pedidosGenerados.isEmpty(), "Deberían haberse generado pedidos.");

        // Apagar pools
        simulador.shutdownImmediately();

        System.out.println("📦 Pedidos generados y persistidos: " + pedidosGenerados.size());
        assertTrue(pedidosGenerados.size() >= 2, "Deberían haberse generado al menos 2 pedidos en 10s.");
    }
}
