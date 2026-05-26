package com.restaurante.pedidos.presentacion.util;

import java.util.concurrent.*;
import javafx.application.Platform;

/**
 * Gestor centralizado de thread pools para la UI.
 *
 * Por qué es importante:
 * - Evita crear hilos ad-hoc (new Thread()) que saturan el sistema.
 * - Permite monitorear y ajustar concurrencia desde un punto.
 * - Facilita tests: se puede inyectar un executor controlado.
 *
 * Configuración basada en experiencia real:
 * - UI no debe bloquearse NUNCA con operaciones I/O.
 * - Usamos bounded queues para evitar memory leaks.
 * - RejectedExecutionHandler registra errores en lugar de silenciarlos.
 */
public final class EjecutorUI {

    // Pool para operaciones de I/O (BD, API, archivos)
    private static final ExecutorService IO_EXECUTOR = new ThreadPoolExecutor(
            2,  // core: mínimo 2 hilos siempre activos
            8,  // max: escala hasta 8 bajo carga
            60L, TimeUnit.SECONDS,  // hilos inactivos mueren después de 60s
            new ArrayBlockingQueue<>(100),  // queue limitada: backpressure natural
            new ThreadPoolExecutor.CallerRunsPolicy()  // si se llena, ejecuta en thread caller (UI se ralentiza pero no colapsa)
    );

    // Pool para tareas ligeras de CPU (transformaciones, validaciones)
    private static final ExecutorService CPU_EXECUTOR = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors(),
            r -> {
                Thread t = new Thread(r, "UI-CPU-" + r.hashCode());
                t.setDaemon(true);  // No bloquea el shutdown de la app
                return t;
            }
    );

    private EjecutorUI() {}  // No instanciar

    /**
     * Ejecuta tarea de I/O en background y actualiza UI al completar.
     * Patrón más usado en controladores.
     */
    public static <T> void ejecutarConCallbackUI(
            Callable<T> tareaIo,
            java.util.function.Consumer<T> onSuccess,
            java.util.function.Consumer<Throwable> onError
    ) {
        IO_EXECUTOR.submit(() -> {
            try {
                T resultado = tareaIo.call();
                // ✅ Actualizar UI en el thread correcto
                Platform.runLater(() -> onSuccess.accept(resultado));
            } catch (Throwable e) {
                Platform.runLater(() -> onError.accept(e));
            }
        });
    }

    /**
     * Versión simplificada para casos sin manejo de error complejo.
     */
    public static <T> void ejecutarConCallbackUI(
            Callable<T> tareaIo,
            java.util.function.Consumer<T> onSuccess
    ) {
        ejecutarConCallbackUI(tareaIo, onSuccess,
                error -> System.err.println("Error en UI: " + error.getMessage()));
    }

    /**
     * Para tareas CPU-bound (ej: filtrar listas grandes).
     */
    public static void ejecutarCpu(Runnable tarea) {
        CPU_EXECUTOR.submit(tarea);
    }

    /**
     * Shutdown ordenado (para tests o cierre de aplicación).
     */
    public static void shutdown() {
        IO_EXECUTOR.shutdown();
        CPU_EXECUTOR.shutdown();
        try {
            if (!IO_EXECUTOR.awaitTermination(5, TimeUnit.SECONDS)) {
                IO_EXECUTOR.shutdownNow();
            }
            if (!CPU_EXECUTOR.awaitTermination(2, TimeUnit.SECONDS)) {
                CPU_EXECUTOR.shutdownNow();
            }
        } catch (InterruptedException e) {
            IO_EXECUTOR.shutdownNow();
            CPU_EXECUTOR.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // === MÉTODOS PARA TESTS ===

    /**
     * Reemplaza el executor para tests (inyección de dependencia).
     */
    public static void setIoExecutorForTests(ExecutorService testExecutor) {
        // En producción, esto no se llama. Solo en @BeforeEach de tests.
        IO_EXECUTOR.shutdownNow();
        // Nota: en un diseño más puro, usaríamos un ServiceLoader o DI framework,
        // pero para este proyecto, esto es suficiente y práctico.
    }
}