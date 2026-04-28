package org.example.foodflow2.config;

import org.example.foodflow2.model.enums.*;
import org.example.foodflow2.model.entity.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void run(String... args) {
        // Solo insertar si la tabla está vacía
        Long count = em.createQuery("SELECT COUNT(u) FROM Usuario u", Long.class).getSingleResult();
        if (count > 0) {
            System.out.println("✅ Datos ya existen, omitiendo inicialización");
            return;
        }

        System.out.println("🔄 Insertando datos de prueba...");

        // 1. ADMINISTRADOR
        Administrador admin = new Administrador();
        admin.setNombre("Carlos");
        admin.setApellido("Martinez");
        admin.setCedula("1712345678");
        admin.setTelefono("0991234567");
        admin.setEmail("admin@foodflow.com");
        admin.setUsername("admin");
        admin.setPassword("123456");
        admin.setRol(RolUsuario.ADMIN);
        admin.setActivo(true);
        admin.setNivelAcceso("TOTAL");
        em.persist(admin);

        // 2. RECEPCIONISTA
        Usuario recepcionista = new Usuario() {};
        recepcionista.setNombre("Maria");
        recepcionista.setApellido("Garcia");
        recepcionista.setCedula("1712345679");
        recepcionista.setTelefono("0991234568");
        recepcionista.setEmail("maria@foodflow.com");
        recepcionista.setUsername("recepcionista1");
        recepcionista.setPassword("123456");
        recepcionista.setRol(RolUsuario.RECEPCIONISTA);
        recepcionista.setActivo(true);
        em.persist(recepcionista);

        // 3. COCINEROS
        Cocinero cocinero1 = new Cocinero();
        cocinero1.setNombre("Ana");
        cocinero1.setApellido("Castro");
        cocinero1.setCedula("1712345681");
        cocinero1.setTelefono("0991234570");
        cocinero1.setEmail("ana@foodflow.com");
        cocinero1.setUsername("cocinero1");
        cocinero1.setPassword("123456");
        cocinero1.setRol(RolUsuario.COCINERO);
        cocinero1.setDisponible(true);
        cocinero1.setEspecialidad("Parrilla");
        em.persist(cocinero1);

        Cocinero cocinero2 = new Cocinero();
        cocinero2.setNombre("Pedro");
        cocinero2.setApellido("Vera");
        cocinero2.setCedula("1712345682");
        cocinero2.setTelefono("0991234571");
        cocinero2.setEmail("pedro@foodflow.com");
        cocinero2.setUsername("cocinero2");
        cocinero2.setPassword("123456");
        cocinero2.setRol(RolUsuario.COCINERO);
        cocinero2.setDisponible(true);
        cocinero2.setEspecialidad("Pastas");
        em.persist(cocinero2);

        // 4. REPARTIDORES
        Repartidor repartidor1 = new Repartidor();
        repartidor1.setNombre("Juan");
        repartidor1.setApellido("Ruiz");
        repartidor1.setCedula("1712345684");
        repartidor1.setTelefono("0991234573");
        repartidor1.setEmail("juan@foodflow.com");
        repartidor1.setUsername("repartidor1");
        repartidor1.setPassword("123456");
        repartidor1.setRol(RolUsuario.REPARTIDOR);
        repartidor1.setDisponible(true);
        repartidor1.setTipoVehiculo(TipoVehiculo.MOTO);
        repartidor1.setPlacaVehiculo("ABC123");
        em.persist(repartidor1);

        // 5. CLIENTES
        Cliente cliente1 = new Cliente();
        cliente1.setNombre("Roberto");
        cliente1.setApellido("Mendez");
        cliente1.setCedula("1712345687");
        cliente1.setTelefono("0991234576");
        cliente1.setEmail("roberto@gmail.com");
        cliente1.setUsername("cliente1");
        cliente1.setPassword("123456");
        cliente1.setRol(RolUsuario.CLIENTE);
        em.persist(cliente1);

        // Dirección para cliente1
        Direccion dir1 = new Direccion();
        dir1.setAlias("Casa");
        dir1.setCallePrincipal("Av. Amazonas");
        dir1.setCalleSecundaria("Naciones Unidas");
        dir1.setNumeroCasa("123");
        dir1.setReferencia("Frente al parque");
        dir1.setCliente(cliente1);
        dir1.setPrincipal(true);
        em.persist(dir1);

        // 6. PRODUCTOS
        Producto p1 = crearProducto("HAM001", "Hamburguesa Clasica", "Carne 150gr, queso, lechuga, tomate", 5.50, CategoriaProducto.PLATO_FUERTE, 15);
        Producto p2 = crearProducto("PIZ001", "Pizza Margarita", "Pizza personal, tomate, mozzarella", 6.00, CategoriaProducto.PLATO_FUERTE, 25);
        Producto p3 = crearProducto("BEB001", "Coca Cola", "Botella 500ml", 1.50, CategoriaProducto.BEBIDA, 0);
        Producto p4 = crearProducto("ENT001", "Papas Fritas", "Porcion grande con salsas", 3.50, CategoriaProducto.ENTRADA, 10);
        em.persist(p1);
        em.persist(p2);
        em.persist(p3);
        em.persist(p4);

        System.out.println("✅ DATOS DE PRUEBA INSERTADOS EXITOSAMENTE");
        System.out.println("   Usuarios: admin/123456, cocinero1/123456, repartidor1/123456, cliente1/123456");
    }

    private Producto crearProducto(String codigo, String nombre, String descripcion, double precio, CategoriaProducto categoria, int tiempo) {
        Producto p = new Producto();
        p.setCodigo(codigo);
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setPrecio(BigDecimal.valueOf(precio));
        p.setCostoProduccion(BigDecimal.valueOf(precio * 0.4));
        p.setCategoria(categoria);
        p.setTiempoPreparacionMin(tiempo);
        p.setActivo(true);
        return p;
    }
}
