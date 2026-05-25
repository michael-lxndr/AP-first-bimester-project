package com.restaurante.pedidos.LogicaServicios;

import com.restaurante.pedidos.Clases.*;
import com.restaurante.pedidos.Clases.Enums.CodigoEstadoPedido;
import com.restaurante.pedidos.Logica.*;
import com.restaurante.pedidos.LogicaConfiguracion.JPABaseDeDatos;
import com.restaurante.pedidos.dominio.dto.PedidoDTO;
import javax.persistence.EntityManagerFactory;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Motor principal de simulación multihilo basado en el patrón Productor-Consumidor.
 * Diseñado para desacoplar operaciones de base de datos pesadas de la UI mediante
 * hilos de cocina y reparto en segundo plano, con fallbacks robustos en memoria.
 */
public class ServicioSimulacionConcurrente {

    // Interfaces para eventos en tiempo real
    public interface LogListener {
        void onLog(String mensaje);
    }

    public interface OrderUpdateListener {
        void onOrderUpdated(PedidoDTO pedido);
    }

    private static final String[] CLIENTES_MOCK = {
        "Alejandro Cazar", "Sofía Endara", "Daniel Noboa", "Camila Vallejo", "Gabriel Boric",
        "Luisa González", "Andrés Arauz", "Guillermo Lasso", "Rafael Correa", "Jaime Nebot"
    };

    private static final String[] DIRECCIONES_MOCK = {
        "Av. Amazonas N21-120 y Robles", "Av. de los Shyris 1450 y Naciones Unidas",
        "Calle Guayaquil S3-24 y Espejo", "Av. 12 de Octubre y Patria", "Calle Foch E4-12 y Reina Victoria",
        "Av. Francisco de Orellana 230", "Av. República de El Salvador 890", "Calle Chile y Benalcázar"
    };

    private final List<Productos> productosMock = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1000);

    // Colas concurrentes seguras
    private final BlockingQueue<PedidosCliente> colaPendientes = new LinkedBlockingQueue<>();
    private final BlockingQueue<PedidosCliente> colaListos = new LinkedBlockingQueue<>();
    
    // Control de Hilos
    private Thread producerThread;
    private ExecutorService poolCocineros;
    private ExecutorService poolRepartidores;
    
    private volatile boolean produciendo = false;
    private volatile boolean simulando = false;

    // Listeners y Estado Activo
    private LogListener logListener;
    private OrderUpdateListener orderUpdateListener;
    private final Map<String, PedidosCliente> pedidosActivos = new ConcurrentHashMap<>();

    // Controladores JPA (con fallbacks si fallan)
    private final PedidosClienteJpaController pedidosController;
    private final EstadosPedidoJpaController estadosController;
    private final ProductosJpaController productosController;
    private final ClientesJpaController clientesController;
    private final DireccionesClienteJpaController direccionesController;
    private final PersonalJpaController personalController;
    private final RolesJpaController rolesController;

    private List<Productos> dbProductos = new ArrayList<>();
    private List<Clientes> dbClientes = new ArrayList<>();
    private List<Personal> dbPersonal = new ArrayList<>();
    private List<DireccionesCliente> dbDirecciones = new ArrayList<>();
    private EstadosPedido[] dbEstados = new EstadosPedido[6]; // 1L a 5L

    // Servicio de negocio para persistencia y auditoría
    private final com.restaurante.pedidos.negocio.servicios.PedidoService pedidoService;

    public ServicioSimulacionConcurrente() {
        EntityManagerFactory emf = JPABaseDeDatos.getEntityManagerFactory();
        this.pedidosController = new PedidosClienteJpaController(emf);
        this.estadosController = new EstadosPedidoJpaController(emf);
        this.productosController = new ProductosJpaController(emf);
        this.clientesController = new ClientesJpaController(emf);
        this.direccionesController = new DireccionesClienteJpaController(emf);
        this.personalController = new PersonalJpaController(emf);
        this.rolesController = new RolesJpaController(emf);
        this.pedidoService = new com.restaurante.pedidos.negocio.servicios.PedidoService();

        inicializarMockProductos();
    }

    public void setLogListener(LogListener listener) {
        this.logListener = listener;
    }

    public void setOrderUpdateListener(OrderUpdateListener listener) {
        this.orderUpdateListener = listener;
    }

    public boolean isSimulating() {
        return simulando;
    }

    public Collection<PedidoDTO> getPedidosActivos() {
        List<PedidoDTO> dtos = new ArrayList<>();
        for (PedidosCliente pc : pedidosActivos.values()) {
            dtos.add(PedidoDTO.fromEntity(pc));
        }
        // Ordenar por ID para consistencia visual
        dtos.sort(Comparator.comparing(PedidoDTO::id));
        return dtos;
    }

    private void registrarLog(String mensaje) {
        String logLine = String.format("[%s] %s", new java.text.SimpleDateFormat("HH:mm:ss").format(new Date()), mensaje);
        System.out.println(logLine);
        if (logListener != null) {
            logListener.onLog(logLine);
        }
    }

    private void notificarActualizacion(PedidosCliente pedido) {
        if (orderUpdateListener != null) {
            orderUpdateListener.onOrderUpdated(PedidoDTO.fromEntity(pedido));
        }
    }

    private void inicializarMockProductos() {
        productosMock.add(crearMockProducto(1L, "PROD-HAMB", "Hamburguesa Suprema", "Hamburguesa con doble queso", 8.50, 4));
        productosMock.add(crearMockProducto(2L, "PROD-PAPA", "Papas Fritas Gourmet", "Papas rústicas con especias", 3.75, 2));
        productosMock.add(crearMockProducto(3L, "PROD-PIZZ", "Pizza Familiar Pepperoni", "Pizza artesanal en horno de leña", 15.00, 5));
        productosMock.add(crearMockProducto(4L, "PROD-BEBI", "Gaseosa Gigante", "Vaso de bebida helada de 1L", 2.00, 1));
        productosMock.add(crearMockProducto(5L, "PROD-ENSA", "Ensalada César", "Ensalada fresca con aderezo César", 6.50, 3));
    }

    private Productos crearMockProducto(Long id, String codigo, String nombre, String desc, double precio, int minutosPrep) {
        Productos p = new Productos(id, nombre, BigDecimal.valueOf(precio), true);
        p.setCodigoProducto(codigo);
        p.setDescripcion(desc);
        p.setTiempoPreparacionMinutos(minutosPrep);
        p.setItemsPedidoCollection(new ArrayList<>());
        return p;
    }

    private void cargarSemillasDesdeBD() {
        try {
            registrarLog("Intentando cargar datos semillas de base de datos...");
            dbProductos = productosController.findProductosEntities();
            dbClientes = clientesController.findClientesEntities();
            dbPersonal = personalController.findPersonalEntities();
            dbDirecciones = direccionesController.findDireccionesClienteEntities();
            
            for (int i = 1; i <= 5; i++) {
                dbEstados[i] = estadosController.findEstadosPedido((long) i);
            }
            registrarLog("✅ Semillas cargadas exitosamente desde MySQL.");
        } catch (Exception e) {
            registrarLog("⚠️ No se pudo conectar a MySQL. Usando fallback autónomo en memoria: " + e.getMessage());
            dbProductos = new ArrayList<>(productosMock);
            dbClientes = new ArrayList<>();
            dbPersonal = new ArrayList<>();
            dbDirecciones = new ArrayList<>();
            
            // Crear estados mock autónomos
            dbEstados[1] = crearMockEstado(1L, "PENDIENTE", "Pendiente", 1, false);
            dbEstados[2] = crearMockEstado(2L, "EN_PREPARACION", "En preparación", 2, false);
            dbEstados[3] = crearMockEstado(3L, "LISTO", "Listo para retirar", 3, false);
            dbEstados[4] = crearMockEstado(4L, "EN_CAMINO", "En camino a entrega", 4, false);
            dbEstados[5] = crearMockEstado(5L, "ENTREGADO", "Entregado exitosamente", 5, true);

            // Crear cliente mock
            Clientes c = new Clientes(100L);
            c.setNombreCompleto("Cliente de Simulación");
            c.setTelefono("0999999999");
            c.setCorreoElectronico("fallback@restaurant.com");
            dbClientes.add(c);

            // Crear personal mock
            Personal p1 = new Personal(200L);
            p1.setNombreCompleto("Chef Cocina Autómata");
            dbPersonal.add(p1);
        }
    }

    private EstadosPedido crearMockEstado(Long id, String codigo, String nombre, int orden, boolean finalizado) {
        EstadosPedido ep = new EstadosPedido(id);
        ep.setCodigoEstado(codigo);
        ep.setNombreEstado(nombre);
        ep.setOrdenEstado(orden);
        ep.setFinalizado(finalizado);
        ep.setPedidosClienteCollection(new ArrayList<>());
        return ep;
    }

    /**
     * Inicia la simulación concurrente
     */
    public synchronized void startSimulation(int intervalSeconds, int numCooks) {
        if (simulando) return;
        
        simulando = true;
        produciendo = true;
        
        colaPendientes.clear();
        colaListos.clear();
        pedidosActivos.clear();

        cargarSemillasDesdeBD();

        // 1. Iniciar Hilo del Productor (Generador de Pedidos)
        producerThread = new Thread(() -> loopProductor(intervalSeconds), "Sim-Producer");
        producerThread.start();
        registrarLog("🚀 Productor iniciado: Generación cada " + intervalSeconds + "s.");

        // 2. Iniciar Pool de Cocina (Consumidor de Pendientes)
        poolCocineros = Executors.newFixedThreadPool(numCooks);
        for (int i = 0; i < numCooks; i++) {
            final int idCocinero = i + 1;
            poolCocineros.submit(() -> loopCocinero(idCocinero));
        }
        registrarLog("👨‍🍳 Pool de Cocina iniciado con " + numCooks + " cocineros.");

        // 3. Iniciar Pool de Reparto (Consumidor de Listos)
        poolRepartidores = Executors.newFixedThreadPool(1);
        poolRepartidores.submit(() -> loopRepartidor(1));
        registrarLog("🛵 Repartidor iniciado.");
    }

    /**
     * Detiene la generación de pedidos ("Cerrar cocina")
     * Deja que los pedidos activos y en cola terminen de procesarse.
     */
    public synchronized void stopKitchen() {
        if (!produciendo) return;
        produciendo = false;
        registrarLog("🛑 cocina cerrada: Deteniendo la generación de nuevos pedidos.");
        
        if (producerThread != null) {
            producerThread.interrupt();
        }
        
        // Lanzar un hilo de monitoreo para apagar los pools cuando todo esté procesado
        new Thread(() -> {
            while (!colaPendientes.isEmpty() || !colaListos.isEmpty() || hayPedidosEnCurso()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    break;
                }
            }
            registrarLog("🧹 Todos los pedidos de la cola se han completado. Apagando pools...");
            shutdownPools();
        }, "Sim-Shutdown-Monitor").start();
    }

    private boolean hayPedidosEnCurso() {
        for (PedidosCliente pc : pedidosActivos.values()) {
            String est = pc.getEstadoActualId().getCodigoEstado();
            if (!est.equals("ENTREGADO")) {
                return true;
            }
        }
        return false;
    }

    private synchronized void shutdownPools() {
        simulando = false;
        produciendo = false;
        
        if (poolCocineros != null) {
            poolCocineros.shutdown();
            try {
                if (!poolCocineros.awaitTermination(3, TimeUnit.SECONDS)) poolCocineros.shutdownNow();
            } catch (InterruptedException e) {
                poolCocineros.shutdownNow();
            }
        }
        
        if (poolRepartidores != null) {
            poolRepartidores.shutdown();
            try {
                if (!poolRepartidores.awaitTermination(3, TimeUnit.SECONDS)) poolRepartidores.shutdownNow();
            } catch (InterruptedException e) {
                poolRepartidores.shutdownNow();
            }
        }
        
        registrarLog("🏁 Simulación terminada y pools cerrados correctamente.");
    }

    public synchronized void shutdownImmediately() {
        produciendo = false;
        simulando = false;
        if (producerThread != null) producerThread.interrupt();
        if (poolCocineros != null) poolCocineros.shutdownNow();
        if (poolRepartidores != null) poolRepartidores.shutdownNow();
        colaPendientes.clear();
        colaListos.clear();
        pedidosActivos.clear();
        registrarLog("🚨 Simulación cancelada de inmediato.");
    }

    // --- LOOP DEL PRODUCTOR (HILO) ---
    private void loopProductor(int intervalSeconds) {
        while (produciendo) {
            try {
                // Generar Pedido
                PedidosCliente pedido = generarPedidoAleatorio();
                colaPendientes.put(pedido);
                pedidosActivos.put(pedido.getCodigoPedido(), pedido);
                
                registrarLog(String.format("📝 Pedido %s creado para cliente '%s' (Total: $%s). PENDIENTE", 
                        pedido.getCodigoPedido(), 
                        pedido.getClienteId().getNombreCompleto(),
                        pedido.getTotal()));
                
                notificarActualizacion(pedido);

                // Esperar intervalo
                Thread.sleep(intervalSeconds * 1000L);
            } catch (InterruptedException e) {
                break;
            } catch (Exception e) {
                registrarLog("❌ Error en Productor de Simulación: " + e.getMessage());
            }
        }
    }

    // --- LOOP DEL COCINERO (POOL DE HILOS) ---
    private void loopCocinero(int idCocinero) {
        while (simulando || !colaPendientes.isEmpty()) {
            try {
                // Tomar de la cola (espera con timeout por si la simulación termina)
                PedidosCliente pedido = colaPendientes.poll(1, TimeUnit.SECONDS);
                if (pedido == null) continue;

                String codigo = pedido.getCodigoPedido();
                registrarLog(String.format("🍳 Cocinero #%d toma Pedido %s. Cambiando a: EN PREPARACIÓN", idCocinero, codigo));
                
                // Actualizar estado a EN_PREPARACION
                try {
                    pedidoService.actualizarEstado(pedido.getPedidoId(), "EN_PREPARACION", (long) idCocinero);
                    pedido.setEstadoActualId(dbEstados[2]); // EN_PREPARACION
                    pedido.setEstadoActualCambiadoEn(new Date());
                } catch (Exception e) {
                    pedido.setEstadoActualId(dbEstados[2]); // Fallback memoria
                    pedido.setEstadoActualCambiadoEn(new Date());
                }
                notificarActualizacion(pedido);

                // Calcular tiempo total de preparación (suma de tiempos de productos)
                int totalMinutos = 0;
                for (ItemsPedido item : pedido.getItemsPedidoCollection()) {
                    totalMinutos += (item.getProductoId().getTiempoPreparacionMinutos() != null 
                            ? item.getProductoId().getTiempoPreparacionMinutos() : 2);
                }
                if (totalMinutos == 0) totalMinutos = 3;

                // Simular preparación (1s real por minuto de preparación)
                Thread.sleep(totalMinutos * 1000L);

                // Actualizar estado a LISTO
                registrarLog(String.format("🔔 Cocinero #%d terminó Pedido %s. Cambiando a: LISTO", idCocinero, codigo));
                try {
                    pedidoService.actualizarEstado(pedido.getPedidoId(), "LISTO", (long) idCocinero);
                    pedido.setEstadoActualId(dbEstados[3]); // LISTO
                    pedido.setEstadoActualCambiadoEn(new Date());
                } catch (Exception e) {
                    pedido.setEstadoActualId(dbEstados[3]); // Fallback memoria
                    pedido.setEstadoActualCambiadoEn(new Date());
                }
                notificarActualizacion(pedido);

                colaListos.put(pedido);

            } catch (InterruptedException e) {
                break;
            } catch (Exception e) {
                registrarLog("❌ Error en Cocinero #" + idCocinero + ": " + e.getMessage());
            }
        }
    }

    // --- LOOP DEL REPARTIDOR (POOL/HILO) ---
    private void loopRepartidor(int idRepartidor) {
        while (simulando || !colaListos.isEmpty()) {
            try {
                PedidosCliente pedido = colaListos.poll(1, TimeUnit.SECONDS);
                if (pedido == null) continue;

                String codigo = pedido.getCodigoPedido();
                registrarLog(String.format("🛵 Repartidor #%d tomó Pedido %s. Cambiando a: EN CAMINO", idRepartidor, codigo));

                // Actualizar estado a EN_CAMINO
                try {
                    pedidoService.actualizarEstado(pedido.getPedidoId(), "EN_CAMINO", (long) idRepartidor);
                    pedido.setEstadoActualId(dbEstados[4]); // EN_CAMINO
                    pedido.setEstadoActualCambiadoEn(new Date());
                } catch (Exception e) {
                    pedido.setEstadoActualId(dbEstados[4]); // Fallback memoria
                    pedido.setEstadoActualCambiadoEn(new Date());
                }
                notificarActualizacion(pedido);

                // Simular viaje (3-7s aleatorio)
                int tiempoEntrega = ThreadLocalRandom.current().nextInt(3, 8);
                Thread.sleep(tiempoEntrega * 1000L);

                // Actualizar estado a ENTREGADO
                registrarLog(String.format("🏁 Repartidor #%d entregó Pedido %s. Cambiando a: ENTREGADO", idRepartidor, codigo));
                
                // Simular quién recibe
                String receptor = pedido.getClienteId().getNombreCompleto();
                pedido.setNotasGenerales(String.format("Entregado a las %s. Recibe: %s", 
                        new java.text.SimpleDateFormat("HH:mm").format(new Date()), 
                        receptor));

                try {
                    pedidoService.actualizarEstado(pedido.getPedidoId(), "ENTREGADO", (long) idRepartidor);
                    pedido.setEstadoActualId(dbEstados[5]); // ENTREGADO
                    pedido.setEstadoActualCambiadoEn(new Date());
                    actualizarPedidoEnBD(pedido); // Para guardar notasGenerales
                } catch (Exception e) {
                    pedido.setEstadoActualId(dbEstados[5]); // Fallback memoria
                    pedido.setEstadoActualCambiadoEn(new Date());
                }
                notificarActualizacion(pedido);

            } catch (InterruptedException e) {
                break;
            } catch (Exception e) {
                registrarLog("❌ Error en Repartidor #" + idRepartidor + ": " + e.getMessage());
            }
        }
    }

    private void actualizarPedidoEnBD(PedidosCliente pedido) {
        try {
            pedidosController.edit(pedido);
        } catch (Exception e) {
            // Fallback silencioso en memoria si MySQL está detenido
        }
    }

    // --- GENERADOR DE PEDIDOS ALEATORIOS (HELPER) ---
    private PedidosCliente generarPedidoAleatorio() {
        long idUnico = idGenerator.getAndIncrement();
        String codigo = String.format("PED-%06d", idUnico);

        // Elegir Cliente
        Clientes cliente;
        if (!dbClientes.isEmpty()) {
            cliente = dbClientes.get(ThreadLocalRandom.current().nextInt(dbClientes.size()));
        } else {
            cliente = new Clientes();
            cliente.setNombreCompleto(CLIENTES_MOCK[ThreadLocalRandom.current().nextInt(CLIENTES_MOCK.length)]);
            cliente.setTelefono("09" + ThreadLocalRandom.current().nextInt(10000000, 99999999));
            cliente.setCorreoElectronico("simulado" + idUnico + "@restaurant.com");
            cliente.setActivo(true);
            cliente.setCreadoEn(new Date());
            try {
                clientesController.create(cliente);
                dbClientes.add(cliente);
            } catch (Exception e) {
                // If it fails (e.g. database offline), use memory fallback ID
                cliente.setClienteId(idUnico);
            }
        }

        // Crear Dirección
        DireccionesCliente direccion = null;
        if (cliente.getClienteId() != null && cliente.getClienteId() > 0 && cliente.getDireccionesClienteCollection() != null && !cliente.getDireccionesClienteCollection().isEmpty()) {
            direccion = cliente.getDireccionesClienteCollection().iterator().next();
        } else {
            direccion = new DireccionesCliente();
            direccion.setClienteId(cliente);
            direccion.setAlias("Entrega");
            direccion.setCallePrincipal(DIRECCIONES_MOCK[ThreadLocalRandom.current().nextInt(DIRECCIONES_MOCK.length)]);
            direccion.setCiudad("Quito");
            direccion.setProvincia("Pichincha");
            direccion.setPais("Ecuador");
            direccion.setPrincipal(true);
            direccion.setActiva(true);
            direccion.setNumeroCasa("N/A");

            // Si la base de datos está conectada y el cliente ya está persistido, persistir la dirección primero
            if (cliente.getClienteId() != null && cliente.getClienteId() > 0 && dbClientes.contains(cliente)) {
                try {
                    direccionesController.create(direccion);
                    if (cliente.getDireccionesClienteCollection() == null) {
                        cliente.setDireccionesClienteCollection(new ArrayList<>());
                    }
                    cliente.getDireccionesClienteCollection().add(direccion);
                    dbDirecciones.add(direccion);
                } catch (Exception e) {
                    if (!dbDirecciones.isEmpty()) {
                        direccion = dbDirecciones.get(ThreadLocalRandom.current().nextInt(dbDirecciones.size()));
                    } else {
                        direccion.setDireccionId(idUnico);
                    }
                }
            } else {
                direccion.setDireccionId(idUnico);
            }
        }

        // Elegir Productos (1-3 aleatorios)
        int numItems = ThreadLocalRandom.current().nextInt(1, 4);
        Collection<ItemsPedido> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (int i = 0; i < numItems; i++) {
            Productos prod;
            if (!dbProductos.isEmpty()) {
                prod = dbProductos.get(ThreadLocalRandom.current().nextInt(dbProductos.size()));
            } else {
                prod = productosMock.get(ThreadLocalRandom.current().nextInt(productosMock.size()));
            }

            int cantidad = ThreadLocalRandom.current().nextInt(1, 3);
            BigDecimal totalItem = prod.getPrecioUnitario().multiply(BigDecimal.valueOf(cantidad));
            subtotal = subtotal.add(totalItem);

            ItemsPedido item = new ItemsPedido();
            if (cliente.getClienteId() == null || cliente.getClienteId() == 0) {
                item.setItemPedidoId(idUnico + i * 100);
            }
            item.setProductoId(prod);
            item.setCantidad(cantidad);
            item.setPrecioUnitario(prod.getPrecioUnitario());
            item.setTotalLinea(totalItem);
            item.setNotaEspecial("Simulado");
            item.setSnapshotNombreProducto(prod.getNombreProducto());
            item.setListo(false);
            items.add(item);
        }

        BigDecimal impuesto = subtotal.multiply(BigDecimal.valueOf(0.12)); // 12% IVA
        BigDecimal total = subtotal.add(impuesto);

        // Registrar por Personal
        Personal registrador = null;
        if (!dbPersonal.isEmpty()) {
            registrador = dbPersonal.get(ThreadLocalRandom.current().nextInt(dbPersonal.size()));
        } else {
            registrador = new Personal();
            registrador.setNombreCompleto("Chef Simulado");
            registrador.setNombreUsuario("chef_sim");
            registrador.setActivo(true);
            registrador.setCreadoEn(new Date());
            try {
                Roles rolCocinero = rolesController.findRoles(2L);
                if (rolCocinero != null) {
                    registrador.setRolId(rolCocinero);
                }
                personalController.create(registrador);
                dbPersonal.add(registrador);
            } catch (Exception e) {
                registrador.setPersonalId(1L);
            }
        }

        // Armar Entidad
        PedidosCliente pedido = new PedidosCliente();
        if (cliente.getClienteId() == null || cliente.getClienteId() == 0) {
            pedido.setPedidoId(idUnico);
        }
        pedido.setCodigoPedido(codigo);
        pedido.setClienteId(cliente);
        pedido.setDireccionEntregaId(direccion);
        pedido.setSnapshotDireccionEntrega(direccion.getCallePrincipal());
        pedido.setItemsPedidoCollection(items);
        pedido.setSubtotal(subtotal);
        pedido.setImpuesto(impuesto);
        pedido.setDescuento(BigDecimal.ZERO);
        pedido.setRecargoDireccion(BigDecimal.ZERO);
        pedido.setTotal(total);
        pedido.setPrioritario(ThreadLocalRandom.current().nextBoolean());
        pedido.setNotasGenerales("Pedido creado mediante simulación");
        
        pedido.setEstadoActualId(dbEstados[1]); // PENDIENTE
        pedido.setCreadoEn(new Date());
        pedido.setEstadoActualCambiadoEn(new Date());
        
        // Tiempo estimado de entrega: 15 minutos en el futuro
        pedido.setEntregaEstimadaEn(Date.from(Instant.now().plus(15, ChronoUnit.MINUTES)));
        pedido.setRegistradoPorPersonalId(registrador);

        // Vincular bidireccional
        for (ItemsPedido it : items) {
            it.setPedidoId(pedido);
        }

        // Registrar en base de datos de manera opcional mediante PedidoService
        try {
            pedidoService.crearPedido(pedido);
        } catch (Exception e) {
            registrarLog("⚠️ Fallback memoria: Error al persistir pedido en MySQL: " + e.getMessage());
            if (pedido.getPedidoId() == null) {
                pedido.setPedidoId(idUnico);
            }
        }

        return pedido;
    }
}
