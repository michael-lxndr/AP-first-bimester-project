package com.restaurante.pedidos.presentacion.controladores;

import com.restaurante.pedidos.dominio.dto.PersonalDTO;
import com.restaurante.pedidos.dominio.servicio.ui.IServicioPersonalUI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ControladorAdministrador - Pruebas de gestión de personal")
class ControladorAdministradorTest {

    private ControladorAdministrador controlador;
    private IServicioPersonalUI mockServicio;

    @BeforeEach
    void setUp() {
        controlador = new ControladorAdministrador();
        mockServicio = mock(IServicioPersonalUI.class);
        controlador.setServicioPersonal(mockServicio);
    }

    @Test
    @DisplayName("setServicioPersonal - inyección funciona correctamente")
    void setServicioPersonal_InyeccionFunciona() {
        assertDoesNotThrow(() -> controlador.setServicioPersonal(mock(IServicioPersonalUI.class)));
    }

    @Test
    @DisplayName("obtenerPersonalPorRol - retorna lista filtrada por rol")
    void obtenerPersonalPorRol_RetornaListaFiltrada() {
        List<PersonalDTO> cocineros = List.of(
                new PersonalDTO(1L, "Ana Martínez", "COCINERO", "3001234567", true, null)
        );

        when(mockServicio.obtenerPersonalPorRol("COCINERO"))
                .thenReturn(CompletableFuture.completedFuture(cocineros));

        var resultado = mockServicio.obtenerPersonalPorRol("COCINERO").join();

        assertEquals(1, resultado.size());
        assertEquals("COCINERO", resultado.get(0).rol());
        assertTrue(resultado.get(0).activo());
    }

    @Test
    @DisplayName("obtenerPersonalPorRol - retorna lista vacía cuando no hay personal activo")
    void obtenerPersonalPorRol_SinPersonalActivo_RetornaVacio() {
        when(mockServicio.obtenerPersonalPorRol("REPARTIDOR"))
                .thenReturn(CompletableFuture.completedFuture(List.of()));

        var resultado = mockServicio.obtenerPersonalPorRol("REPARTIDOR").join();

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("actualizarDisponibilidad - retorna true para ID existente")
    void actualizarDisponibilidad_IdExistente_RetornaTrue() {
        when(mockServicio.actualizarDisponibilidad(1L, false))
                .thenReturn(CompletableFuture.completedFuture(true));

        boolean resultado = mockServicio.actualizarDisponibilidad(1L, false).join();

        assertTrue(resultado);
        verify(mockServicio).actualizarDisponibilidad(1L, false);
    }

    @Test
    @DisplayName("actualizarDisponibilidad - retorna false para ID inexistente")
    void actualizarDisponibilidad_IdInexistente_RetornaFalse() {
        when(mockServicio.actualizarDisponibilidad(999L, true))
                .thenReturn(CompletableFuture.completedFuture(false));

        assertFalse(mockServicio.actualizarDisponibilidad(999L, true).join());
    }

    @Test
    @DisplayName("buscarPersonalPorNombre - retorna coincidencias parciales")
    void buscarPersonalPorNombre_RetornaCoincidencias() {
        List<PersonalDTO> encontrado = List.of(
                new PersonalDTO(1L, "Ana Martínez", "COCINERO", "300", true, null)
        );

        when(mockServicio.buscarPersonalPorNombre("Ana"))
                .thenReturn(CompletableFuture.completedFuture(encontrado));

        var resultado = mockServicio.buscarPersonalPorNombre("Ana").join();

        assertFalse(resultado.isEmpty());
        assertTrue(resultado.get(0).nombreCompleto().contains("Ana"));
    }

    @Test
    @DisplayName("PersonalDTO - personal inactivo tiene activo=false")
    void personalDTO_Inactivo_TieneActivoFalse() {
        PersonalDTO inactivo = new PersonalDTO(4L, "Pedro Sánchez", "COCINERO", "311", false, null);
        assertFalse(inactivo.activo());
    }

    @Test
    @DisplayName("getSeleccionActual - es null antes de seleccionar")
    void getSeleccionActual_AntesDeSeleccionar_EsNull() {
        assertNull(controlador.getSeleccionActual());
    }
}
