-- =========================================================
-- Sistema de Gestion de Pedidos de Restaurante - Datos de Prueba
-- MySQL 9.3
--
-- Usa este script despues de ejecutar:
-- assets/ROM - ER Diagram.sql
--
-- ATENCION: este seed limpia los datos existentes de las tablas
-- del modelo y vuelve a poblarlas con datos deterministicos.
-- Objetivo:
-- - 3 roles
-- - 5 estados
-- - 4 reglas de transicion
-- - 12 personas del sistema
-- - 30 clientes
-- - 45 direcciones
-- - 25 productos
-- - 36 pedidos
-- - 84 items de pedido
-- - historial para todos los pedidos
-- - entregas para pedidos en camino y entregados
-- =========================================================

USE proyecto_primer_bimestre;

-- =========================================================
-- 0. Validacion del esquema
-- =========================================================

DELIMITER $$

DROP PROCEDURE IF EXISTS validar_esquema_seed_restaurante$$

CREATE PROCEDURE validar_esquema_seed_restaurante()
BEGIN
	IF EXISTS (SELECT 1
	           FROM information_schema.columns
	           WHERE table_schema = DATABASE()
		          AND table_name = 'pedidos_cliente'
		          AND column_name = 'delivery_address') THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'Schema mismatch: pedidos_cliente.delivery_address pertenece al modelo viejo. Ejecuta primero ROM - ER Diagram.sql recreando proyecto_primer_bimestre.';
	END IF;

	IF NOT EXISTS (SELECT 1
	               FROM information_schema.columns
	               WHERE table_schema = DATABASE()
		              AND table_name = 'pedidos_cliente'
		              AND column_name = 'direccion_entrega_id') THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'Schema mismatch: falta pedidos_cliente.direccion_entrega_id. Ejecuta primero ROM - ER Diagram.sql.';
	END IF;

	IF EXISTS (SELECT 1
	           FROM information_schema.columns
	           WHERE table_schema = DATABASE()
		          AND table_name = 'pedidos_cliente'
		          AND column_name = 'direccion_entrega_id'
		          AND is_nullable = 'YES') THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'Schema mismatch: pedidos_cliente.direccion_entrega_id debe ser NOT NULL.';
	END IF;

	IF NOT EXISTS (SELECT 1
	               FROM information_schema.columns
	               WHERE table_schema = DATABASE()
		              AND table_name = 'pedidos_cliente'
		              AND column_name = 'snapshot_direccion_entrega') THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'Schema mismatch: falta pedidos_cliente.snapshot_direccion_entrega. Ejecuta primero ROM - ER Diagram.sql.';
	END IF;

	IF NOT EXISTS (SELECT 1
	               FROM information_schema.tables
	               WHERE table_schema = DATABASE()
		              AND table_name = 'direcciones_cliente') THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'Schema mismatch: falta direcciones_cliente. Ejecuta primero ROM - ER Diagram.sql.';
	END IF;

	IF NOT EXISTS (SELECT 1
	               FROM information_schema.columns
	               WHERE table_schema = DATABASE()
		              AND table_name = 'direcciones_cliente'
		              AND column_name = 'pais'
		              AND is_nullable = 'NO') THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'Schema mismatch: falta direcciones_cliente.pais NOT NULL.';
	END IF;

	IF EXISTS (SELECT 1
	           FROM information_schema.columns
	           WHERE table_schema = DATABASE()
		          AND table_name = 'direcciones_cliente'
		          AND column_name IN ('ciudad', 'provincia')
		          AND is_nullable = 'YES') THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'Schema mismatch: direcciones_cliente.ciudad y provincia deben ser NOT NULL.';
	END IF;

	IF NOT EXISTS (SELECT 1
	               FROM information_schema.columns
	               WHERE table_schema = DATABASE()
		              AND table_name = 'historial_estados_pedido'
		              AND column_name = 'estado_origen_id'
		              AND is_nullable = 'NO'
		              AND column_default = '1') THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'Schema mismatch: historial_estados_pedido.estado_origen_id debe ser NOT NULL DEFAULT 1.';
	END IF;
END$$

DELIMITER ;

CALL validar_esquema_seed_restaurante();
DROP PROCEDURE validar_esquema_seed_restaurante;

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE entregas;
TRUNCATE TABLE historial_estados_pedido;
TRUNCATE TABLE items_pedido;
TRUNCATE TABLE pedidos_cliente;
TRUNCATE TABLE direcciones_cliente;
TRUNCATE TABLE productos;
TRUNCATE TABLE personal;
TRUNCATE TABLE reglas_transicion_estado_pedido;
TRUNCATE TABLE estados_pedido;
TRUNCATE TABLE roles;
TRUNCATE TABLE clientes;

SET FOREIGN_KEY_CHECKS = 1;

-- =========================================================
-- 1. Roles
-- =========================================================

INSERT INTO roles (rol_id, codigo_rol)
VALUES (1, 'ADMINISTRADOR'),
       (2, 'COCINERO'),
       (3, 'REPARTIDOR');

-- =========================================================
-- 2. Personal: 12 usuarios internos
-- =========================================================

INSERT INTO personal (personal_id, rol_id, nombre_completo, telefono, correo_electronico, nombre_usuario, activo, creado_en)
VALUES (1, 1, 'Ana Morales', '0991000001', 'ana.morales@restaurant.local', 'admin01', TRUE, '2026-04-01 08:00:00'),
       (2, 1, 'Bruno Salazar', '0991000002', 'bruno.salazar@restaurant.local', 'admin02', TRUE, '2026-04-01 08:05:00'),
       (3, 1, 'Camila Herrera', '0991000003', 'camila.herrera@restaurant.local', 'admin03', TRUE, '2026-04-01 08:10:00'),
       (4, 1, 'Diego Andrade', '0991000004', 'diego.andrade@restaurant.local', 'admin04', TRUE, '2026-04-01 08:15:00'),
       (5, 2, 'Elena Paredes', '0991000005', 'elena.paredes@restaurant.local', 'cocinero01', TRUE, '2026-04-01 08:20:00'),
       (6, 2, 'Fernando Rivas', '0991000006', 'fernando.rivas@restaurant.local', 'cocinero02', TRUE, '2026-04-01 08:25:00'),
       (7, 2, 'Gabriela Nunez', '0991000007', 'gabriela.nunez@restaurant.local', 'cocinero03', TRUE, '2026-04-01 08:30:00'),
       (8, 2, 'Hugo Benitez', '0991000008', 'hugo.benitez@restaurant.local', 'cocinero04', TRUE, '2026-04-01 08:35:00'),
       (9, 3, 'Ivan Castillo', '0991000009', 'ivan.castillo@restaurant.local', 'repartidor01', TRUE, '2026-04-01 08:40:00'),
       (10, 3, 'Julieta Gomez', '0991000010', 'julieta.gomez@restaurant.local', 'repartidor02', TRUE, '2026-04-01 08:45:00'),
       (11, 3, 'Kevin Molina', '0991000011', 'kevin.molina@restaurant.local', 'repartidor03', TRUE, '2026-04-01 08:50:00'),
       (12, 3, 'Laura Cevallos', '0991000012', 'laura.cevallos@restaurant.local', 'repartidor04', TRUE, '2026-04-01 08:55:00');

-- =========================================================
-- 3. Clientes: 30 usuarios externos
-- =========================================================

INSERT INTO clientes (cliente_id, nombre_completo, telefono, correo_electronico, activo, creado_en)
VALUES (1, 'Mateo Alvarez', '0982000001', 'cliente01@test.local', TRUE, '2026-04-02 09:00:00'),
       (2, 'Sofia Zambrano', '0982000002', 'cliente02@test.local', TRUE, '2026-04-02 09:03:00'),
       (3, 'Valentina Rojas', '0982000003', 'cliente03@test.local', TRUE, '2026-04-02 09:06:00'),
       (4, 'Sebastian Vega', '0982000004', 'cliente04@test.local', TRUE, '2026-04-02 09:09:00'),
       (5, 'Isabella Torres', '0982000005', 'cliente05@test.local', TRUE, '2026-04-02 09:12:00'),
       (6, 'Daniela Castro', '0982000006', 'cliente06@test.local', TRUE, '2026-04-02 09:15:00'),
       (7, 'Nicolas Leon', '0982000007', 'cliente07@test.local', TRUE, '2026-04-02 09:18:00'),
       (8, 'Lucia Molina', '0982000008', 'cliente08@test.local', TRUE, '2026-04-02 09:21:00'),
       (9, 'Emilia Ortega', '0982000009', 'cliente09@test.local', TRUE, '2026-04-02 09:24:00'),
       (10, 'Tomas Ponce', '0982000010', 'cliente10@test.local', TRUE, '2026-04-02 09:27:00'),
       (11, 'Martina Flores', '0982000011', 'cliente11@test.local', TRUE, '2026-04-02 09:30:00'),
       (12, 'Joaquin Suarez', '0982000012', 'cliente12@test.local', TRUE, '2026-04-02 09:33:00'),
       (13, 'Renata Bravo', '0982000013', 'cliente13@test.local', TRUE, '2026-04-02 09:36:00'),
       (14, 'Alejandro Mena', '0982000014', 'cliente14@test.local', TRUE, '2026-04-02 09:39:00'),
       (15, 'Paula Viteri', '0982000015', 'cliente15@test.local', TRUE, '2026-04-02 09:42:00'),
       (16, 'Carlos Aguilar', '0982000016', 'cliente16@test.local', TRUE, '2026-04-02 09:45:00'),
       (17, 'Maria Jose Cardenas', '0982000017', 'cliente17@test.local', TRUE, '2026-04-02 09:48:00'),
       (18, 'Gabriel Pena', '0982000018', 'cliente18@test.local', TRUE, '2026-04-02 09:51:00'),
       (19, 'Victoria Espinoza', '0982000019', 'cliente19@test.local', TRUE, '2026-04-02 09:54:00'),
       (20, 'Samuel Carrera', '0982000020', 'cliente20@test.local', TRUE, '2026-04-02 09:57:00'),
       (21, 'Antonella Ruiz', '0982000021', 'cliente21@test.local', TRUE, '2026-04-02 10:00:00'),
       (22, 'Felipe Ochoa', '0982000022', 'cliente22@test.local', TRUE, '2026-04-02 10:03:00'),
       (23, 'Carolina Mejia', '0982000023', 'cliente23@test.local', TRUE, '2026-04-02 10:06:00'),
       (24, 'Andres Villacis', '0982000024', 'cliente24@test.local', TRUE, '2026-04-02 10:09:00'),
       (25, 'Valeria Cordero', '0982000025', 'cliente25@test.local', TRUE, '2026-04-02 10:12:00'),
       (26, 'Emiliano Arias', '0982000026', 'cliente26@test.local', TRUE, '2026-04-02 10:15:00'),
       (27, 'Mia Jaramillo', '0982000027', 'cliente27@test.local', TRUE, '2026-04-02 10:18:00'),
       (28, 'Juan Pablo Serrano', '0982000028', 'cliente28@test.local', TRUE, '2026-04-02 10:21:00'),
       (29, 'Clara Maldonado', '0982000029', 'cliente29@test.local', TRUE, '2026-04-02 10:24:00'),
       (30, 'Rafael Coronel', '0982000030', 'cliente30@test.local', TRUE, '2026-04-02 10:27:00');

-- =========================================================
-- 4. Direcciones: 45 direcciones
-- Cada cliente tiene una direccion principal.
-- Los clientes 1 a 15 tienen ademas una direccion secundaria.
-- =========================================================

INSERT INTO direcciones_cliente (direccion_id,
                                 cliente_id,
                                 alias,
                                 calle_principal,
                                 calle_secundaria,
                                 numero_casa,
                                 referencia,
                                 codigo_postal,
                                 ciudad,
                                 provincia,
                                 pais,
                                 principal,
                                 activa)
VALUES (1, 1, 'Casa', 'Av. Amazonas', 'Naciones Unidas', 'N1-01', 'Edificio azul, timbre 1', '170101', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
       (2, 2, 'Casa', 'Av. 6 de Diciembre', 'Portugal', 'N2-02', 'Frente a farmacia', '170102', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
       (3, 3, 'Casa', 'Av. Republica', 'Eloy Alfaro', 'N3-03', 'Casa con reja negra', '170103', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
       (4, 4, 'Casa', 'Av. Colon', 'Rabida', 'N4-04', 'Junto al parque', '170104', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
       (5, 5, 'Casa', 'Av. Shyris', 'Suecia', 'N5-05', 'Torre B, piso 5', '170105', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
       (6, 6, 'Casa', 'Av. America', 'Mariana de Jesus', 'N6-06', 'Porton blanco', '170106', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
       (7, 7, 'Casa', 'Av. Patria', '9 de Octubre', 'N7-07', 'Cerca del banco', '170107', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
       (8, 8, 'Casa', 'Av. Universitaria', 'Bolivia', 'N8-08', 'Bloque C', '170108', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
       (9, 9, 'Casa', 'Av. Occidental', 'Machala', 'N9-09', 'Entrada posterior', '170109', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
       (10, 10, 'Casa', 'Av. De la Prensa', 'Florida', 'N10-10', 'Junto a panaderia', '170110', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
       (11, 11, 'Casa', 'Av. Maldonado', 'Moran Valverde', 'S11-11', 'Casa esquinera', '170111', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
       (12, 12, 'Casa', 'Av. Simon Bolivar', 'Ruta Viva', 'S12-12', 'Conjunto Los Pinos', '170112', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
       (13, 13, 'Casa', 'Av. Interoceanica', 'Cumbaya', 'E13-13', 'Porteria principal', '170113', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
       (14, 14, 'Casa', 'Av. Ilalo', 'San Rafael', 'E14-14', 'Casa amarilla', '170114', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
       (15, 15, 'Casa', 'Av. General Ruminahui', 'El Triangulo', 'E15-15', 'Frente al redondel', '170115', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
       (16, 16, 'Casa', 'Av. Real Audiencia', 'Luis Tufino', 'N16-16', 'Timbre 16', '170116', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
       (17, 17, 'Casa', 'Av. Galo Plaza', 'Capitan Ramon Borja', 'N17-17', 'Local planta baja', '170117', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
       (18, 18, 'Casa', 'Av. El Inca', 'Amazonas', 'N18-18', 'Conjunto cerrado', '170118', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
       (19, 19, 'Casa', 'Av. La Gasca', 'Villalengua', 'N19-19', 'Junto a lavanderia', '170119', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
       (20, 20, 'Casa', 'Av. America', 'La Gasca', 'N20-20', 'Segundo piso', '170120', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
       (21, 21, 'Casa', 'Av. 10 de Agosto', 'Carrion', 'N21-21', 'Edificio gris', '170121', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
       (22, 22, 'Casa', 'Av. Coruna', 'Gonzalez Suarez', 'N22-22', 'Suite 302', '170122', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
       (23, 23, 'Casa', 'Av. Eloy Alfaro', 'Granados', 'N23-23', 'Junto al colegio', '170123', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
       (24, 24, 'Casa', 'Av. Granados', 'Rio Coca', 'N24-24', 'Casa verde', '170124', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
       (25, 25, 'Casa', 'Av. Rio Coca', 'Paris', 'N25-25', 'Frente a gasolinera', '170125', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
       (26, 26, 'Casa', 'Av. De los Granados', 'Isla Marchena', 'N26-26', 'Bloque 2', '170126', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
       (27, 27, 'Casa', 'Av. Republica del Salvador', 'Moscu', 'N27-27', 'Piso 7', '170127', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
       (28, 28, 'Casa', 'Av. Portugal', 'Catalina Aldaz', 'N28-28', 'Torre norte', '170128', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
       (29, 29, 'Casa', 'Av. Naciones Unidas', 'Japon', 'N29-29', 'Entrada lateral', '170129', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
       (30, 30, 'Casa', 'Av. Eloy Alfaro', 'Portugal', 'N30-30', 'Casa blanca', '170130', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
       (31, 1, 'Trabajo', 'Av. Republica del Salvador', 'Suecia', 'T1-01', 'Oficina 401', '170201', 'Quito', 'Pichincha', 'Ecuador', FALSE, TRUE),
       (32, 2, 'Trabajo', 'Av. Amazonas', 'Colon', 'T2-02', 'Recepcion principal', '170202', 'Quito', 'Pichincha', 'Ecuador', FALSE, TRUE),
       (33, 3, 'Trabajo', 'Av. Orellana', '9 de Octubre', 'T3-03', 'Piso 8', '170203', 'Quito', 'Pichincha', 'Ecuador', FALSE, TRUE),
       (34, 4, 'Trabajo', 'Av. Shyris', 'Rio Coca', 'T4-04', 'Local 12', '170204', 'Quito', 'Pichincha', 'Ecuador', FALSE, TRUE),
       (35, 5, 'Trabajo', 'Av. Naciones Unidas', 'Amazonas', 'T5-05', 'Torre 1', '170205', 'Quito', 'Pichincha', 'Ecuador', FALSE, TRUE),
       (36, 6, 'Trabajo', 'Av. De los Granados', 'Eloy Alfaro', 'T6-06', 'Edificio empresarial', '170206', 'Quito', 'Pichincha', 'Ecuador', FALSE, TRUE),
       (37, 7, 'Trabajo', 'Av. 12 de Octubre', 'Veintimilla', 'T7-07', 'Oficina 702', '170207', 'Quito', 'Pichincha', 'Ecuador', FALSE, TRUE),
       (38, 8, 'Trabajo', 'Av. Patria', 'Amazonas', 'T8-08', 'Piso 3', '170208', 'Quito', 'Pichincha', 'Ecuador', FALSE, TRUE),
       (39, 9, 'Trabajo', 'Av. Maldonado', 'Recreo', 'T9-09', 'Junto al centro comercial', '170209', 'Quito', 'Pichincha', 'Ecuador', FALSE, TRUE),
       (40, 10, 'Trabajo', 'Av. Simon Bolivar', 'Tumbaco', 'T10-10', 'Bodega 4', '170210', 'Quito', 'Pichincha', 'Ecuador', FALSE, TRUE),
       (41, 11, 'Trabajo', 'Av. Interoceanica', 'Puembo', 'T11-11', 'Entrada sur', '170211', 'Quito', 'Pichincha', 'Ecuador', FALSE, TRUE),
       (42, 12, 'Trabajo', 'Av. Ilalo', 'Conocoto', 'T12-12', 'Local exterior', '170212', 'Quito', 'Pichincha', 'Ecuador', FALSE, TRUE),
       (43, 13, 'Trabajo', 'Av. General Ruminahui', 'Sangolqui', 'T13-13', 'Planta alta', '170213', 'Quito', 'Pichincha', 'Ecuador', FALSE, TRUE),
       (44, 14, 'Trabajo', 'Av. America', 'Colon', 'T14-14', 'Consultorio 5', '170214', 'Quito', 'Pichincha', 'Ecuador', FALSE, TRUE),
       (45, 15, 'Trabajo', 'Av. De la Prensa', 'La Florida', 'T15-15', 'Oficina 15', '170215', 'Quito', 'Pichincha', 'Ecuador', FALSE, TRUE);

-- =========================================================
-- 5. Productos: 25 productos
-- =========================================================

INSERT INTO productos (producto_id,
                       codigo_producto,
                       nombre_producto,
                       descripcion,
                       precio_unitario,
                       costo_produccion,
                       categoria,
                       tiempo_preparacion_minutos,
                       disponible,
                       url_imagen,
                       creado_en)
VALUES (1, 'ENTRADA-001', 'Papas bravas', 'Papas crocantes con salsa picante', 3.50, 1.20, 'ENTRADA', 10, TRUE, '/images/papas-bravas.png', '2026-04-03 08:00:00'),
       (2, 'ENTRADA-002', 'Empanadas mixtas', 'Tres empanadas con aji de la casa', 4.20, 1.55, 'ENTRADA', 12, TRUE, '/images/empanadas-mixtas.png', '2026-04-03 08:02:00'),
       (3, 'ENTRADA-003', 'Nachos completos', 'Nachos con queso, guacamole y pico de gallo', 5.10, 2.00, 'ENTRADA', 14, TRUE, '/images/nachos-completos.png',
        '2026-04-03 08:04:00'),
       (4, 'ENTRADA-004', 'Alitas BBQ', 'Alitas banadas en salsa BBQ', 6.25, 2.70, 'ENTRADA', 18, TRUE, '/images/alitas-bbq.png', '2026-04-03 08:06:00'),
       (5, 'ENTRADA-005', 'Ensalada fresca', 'Lechuga, tomate, pepino y vinagreta', 4.75, 1.85, 'ENTRADA', 8, TRUE, '/images/ensalada-fresca.png', '2026-04-03 08:08:00'),
       (6, 'PLATO-001', 'Hamburguesa clasica', 'Carne, queso, lechuga, tomate y salsa de la casa', 7.90, 3.10, 'PLATO_FUERTE', 18, TRUE, '/images/hamburguesa-clasica.png',
        '2026-04-03 08:10:00'),
       (7, 'PLATO-002', 'Hamburguesa doble', 'Doble carne, doble queso y tocino', 10.50, 4.60, 'PLATO_FUERTE', 22, TRUE, '/images/hamburguesa-doble.png', '2026-04-03 08:12:00'),
       (8, 'PLATO-003', 'Pizza personal', 'Pizza individual de queso y pepperoni', 8.25, 3.20, 'PLATO_FUERTE', 20, TRUE, '/images/pizza-personal.png', '2026-04-03 08:14:00'),
       (9, 'PLATO-004', 'Pizza vegetariana', 'Pizza con vegetales frescos y queso mozzarella', 8.75, 3.35, 'PLATO_FUERTE', 21, TRUE, '/images/pizza-vegetariana.png',
        '2026-04-03 08:16:00'),
       (10, 'PLATO-005', 'Pollo grillado', 'Pechuga grillada con arroz y ensalada', 9.90, 4.10, 'PLATO_FUERTE', 25, TRUE, '/images/pollo-grillado.png', '2026-04-03 08:18:00'),
       (11, 'PLATO-006', 'Lomo salteado', 'Lomo salteado con papas y arroz', 11.80, 5.10, 'PLATO_FUERTE', 28, TRUE, '/images/lomo-salteado.png', '2026-04-03 08:20:00'),
       (12, 'PLATO-007', 'Pasta al pesto', 'Pasta corta con pesto y queso parmesano', 8.60, 3.05, 'PLATO_FUERTE', 19, TRUE, '/images/pasta-pesto.png', '2026-04-03 08:22:00'),
       (13, 'BEBIDA-001', 'Gaseosa personal', 'Bebida gaseosa de 400 ml', 1.50, 0.45, 'BEBIDA', 2, TRUE, '/images/gaseosa-personal.png', '2026-04-03 08:24:00'),
       (14, 'BEBIDA-002', 'Agua mineral', 'Agua sin gas de 500 ml', 1.20, 0.35, 'BEBIDA', 1, TRUE, '/images/agua-mineral.png', '2026-04-03 08:26:00'),
       (15, 'BEBIDA-003', 'Limonada natural', 'Limonada preparada al momento', 2.25, 0.80, 'BEBIDA', 4, TRUE, '/images/limonada-natural.png', '2026-04-03 08:28:00'),
       (16, 'BEBIDA-004', 'Jugo de mora', 'Jugo natural de mora', 2.50, 0.95, 'BEBIDA', 5, TRUE, '/images/jugo-mora.png', '2026-04-03 08:30:00'),
       (17, 'POSTRE-001', 'Brownie', 'Brownie de chocolate con nueces', 3.20, 1.20, 'POSTRE', 6, TRUE, '/images/brownie.png', '2026-04-03 08:32:00'),
       (18, 'POSTRE-002', 'Cheesecake', 'Cheesecake de frutos rojos', 4.50, 1.90, 'POSTRE', 7, TRUE, '/images/cheesecake.png', '2026-04-03 08:34:00'),
       (19, 'POSTRE-003', 'Helado artesanal', 'Dos bolas de helado artesanal', 3.75, 1.35, 'POSTRE', 3, TRUE, '/images/helado-artesanal.png', '2026-04-03 08:36:00'),
       (20, 'POSTRE-004', 'Tres leches', 'Postre tres leches individual', 4.10, 1.60, 'POSTRE', 5, TRUE, '/images/tres-leches.png', '2026-04-03 08:38:00'),
       (21, 'COMBO-001', 'Combo clasico', 'Hamburguesa clasica, papas y gaseosa', 11.50, 4.55, 'COMBO', 22, TRUE, '/images/combo-clasico.png', '2026-04-03 08:40:00'),
       (22, 'COMBO-002', 'Combo doble', 'Hamburguesa doble, papas y limonada', 14.25, 5.85, 'COMBO', 26, TRUE, '/images/combo-doble.png', '2026-04-03 08:42:00'),
       (23, 'COMBO-003', 'Combo pizza', 'Pizza personal, ensalada y bebida', 12.75, 4.95, 'COMBO', 24, TRUE, '/images/combo-pizza.png', '2026-04-03 08:44:00'),
       (24, 'COMBO-004', 'Combo familiar', 'Dos pizzas, alitas y dos bebidas', 24.90, 10.20, 'COMBO', 35, TRUE, '/images/combo-familiar.png', '2026-04-03 08:46:00'),
       (25, 'COMBO-005', 'Combo ligero', 'Ensalada, agua y postre pequeno', 9.80, 3.70, 'COMBO', 15, TRUE, '/images/combo-ligero.png', '2026-04-03 08:48:00');

-- =========================================================
-- 6. Estados y reglas de transicion
-- =========================================================

INSERT INTO estados_pedido (estado_id, codigo_estado, nombre_estado, orden_estado, finalizado)
VALUES (1, 'PENDIENTE', 'Pendiente', 1, FALSE),
       (2, 'EN_PREPARACION', 'En preparacion', 2, FALSE),
       (3, 'LISTO', 'Listo', 3, FALSE),
       (4, 'EN_CAMINO', 'En camino', 4, FALSE),
       (5, 'ENTREGADO', 'Entregado', 5, TRUE);

INSERT INTO reglas_transicion_estado_pedido (regla_transicion_id,
                                             estado_origen_id,
                                             estado_destino_id,
                                             rol_id,
                                             activa)
VALUES (1, 1, 2, 2, TRUE),
       (2, 2, 3, 2, TRUE),
       (3, 3, 4, 3, TRUE),
       (4, 4, 5, 3, TRUE);

-- =========================================================
-- 7. Pedidos e items
-- Se generan 36 pedidos con 2 o 3 items por pedido.
-- =========================================================

DELIMITER $$

DROP PROCEDURE IF EXISTS seed_pedidos_restaurante$$

CREATE PROCEDURE seed_pedidos_restaurante()
BEGIN
	DECLARE v_pedido_id INT DEFAULT 1;
	DECLARE v_cliente_id INT;
	DECLARE v_estado_id INT;
	DECLARE v_personal_admin_id INT;
	DECLARE v_producto_id INT;
	DECLARE v_cantidad INT;
	DECLARE v_precio DECIMAL(10, 2);
	DECLARE v_nombre_producto VARCHAR(120);

	WHILE v_pedido_id <= 36
		DO
			SET v_cliente_id = ((v_pedido_id - 1) MOD 30) + 1;
			SET v_personal_admin_id = ((v_pedido_id - 1) MOD 4) + 1;

			SET v_estado_id = CASE
										WHEN v_pedido_id BETWEEN 1 AND 8 THEN 1
				                  WHEN v_pedido_id BETWEEN 9 AND 15 THEN 2
				                  WHEN v_pedido_id BETWEEN 16 AND 22 THEN 3
				                  WHEN v_pedido_id BETWEEN 23 AND 29 THEN 4
				                  ELSE 5
				END;

			INSERT INTO pedidos_cliente (pedido_id,
			                             codigo_pedido,
			                             cliente_id,
			                             registrado_por_personal_id,
			                             estado_actual_id,
			                             direccion_entrega_id,
			                             snapshot_direccion_entrega,
			                             instrucciones_entrega,
			                             subtotal,
			                             impuesto,
			                             descuento,
			                             recargo_direccion,
			                             total,
			                             codigo_descuento,
			                             prioritario,
			                             notas_generales,
			                             creado_en,
			                             entrega_estimada_en,
			                             estado_actual_cambiado_en)
			VALUES (v_pedido_id,
			        CONCAT('PED-', LPAD(v_pedido_id, 5, '0')),
			        v_cliente_id,
			        v_personal_admin_id,
			        v_estado_id,
			        v_cliente_id,
			        (SELECT CONCAT(alias, ': ', calle_principal, ' y ', COALESCE(calle_secundaria, 'S/N'), ', ', COALESCE(numero_casa, 'S/N'), '. Ref: ',
			                       COALESCE(referencia, 'Sin referencia'))
			         FROM direcciones_cliente
			         WHERE direccion_id = v_cliente_id),
			        CASE
						  WHEN v_pedido_id MOD 4 = 0 THEN 'Llamar antes de llegar.'
				        WHEN v_pedido_id MOD 5 = 0 THEN 'Entregar en porteria.'
				        ELSE NULL
						  END,
			        0.00,
			        0.00,
			        0.00,
			        0.00,
			        0.00,
			        CASE WHEN v_pedido_id MOD 9 = 0 THEN 'PROMO10' ELSE NULL END,
			        CASE WHEN v_pedido_id MOD 6 = 0 THEN TRUE ELSE FALSE END,
			        CASE
						  WHEN v_pedido_id MOD 5 = 0 THEN 'Cliente solicita cubiertos y servilletas adicionales.'
				        WHEN v_pedido_id MOD 7 = 0 THEN 'Pedido corporativo, confirmar en recepcion.'
				        ELSE NULL
						  END,
			        DATE_ADD('2026-04-10 12:00:00', INTERVAL v_pedido_id * 7 MINUTE),
			        DATE_ADD('2026-04-10 12:45:00', INTERVAL v_pedido_id * 7 MINUTE),
			        DATE_ADD('2026-04-10 12:00:00', INTERVAL v_pedido_id * 7 MINUTE));

			SET v_producto_id = ((v_pedido_id - 1) MOD 25) + 1;
			SET v_cantidad = (v_pedido_id MOD 3) + 1;
			SELECT nombre_producto, precio_unitario INTO v_nombre_producto, v_precio FROM productos WHERE producto_id = v_producto_id;

			INSERT INTO items_pedido (pedido_id,
			                          producto_id,
			                          cantidad,
			                          snapshot_nombre_producto,
			                          precio_unitario,
			                          total_linea,
			                          nota_especial,
			                          listo)
			VALUES (v_pedido_id,
			        v_producto_id,
			        v_cantidad,
			        v_nombre_producto,
			        v_precio,
			        ROUND(v_precio * v_cantidad, 2),
			        CASE WHEN v_pedido_id MOD 4 = 0 THEN 'Sin cebolla, por favor.' ELSE NULL END,
			        CASE WHEN v_estado_id >= 3 THEN TRUE ELSE FALSE END);

			SET v_producto_id = ((v_pedido_id + 6) MOD 25) + 1;
			SET v_cantidad = ((v_pedido_id + 1) MOD 2) + 1;
			SELECT nombre_producto, precio_unitario INTO v_nombre_producto, v_precio FROM productos WHERE producto_id = v_producto_id;

			INSERT INTO items_pedido (pedido_id,
			                          producto_id,
			                          cantidad,
			                          snapshot_nombre_producto,
			                          precio_unitario,
			                          total_linea,
			                          nota_especial,
			                          listo)
			VALUES (v_pedido_id,
			        v_producto_id,
			        v_cantidad,
			        v_nombre_producto,
			        v_precio,
			        ROUND(v_precio * v_cantidad, 2),
			        CASE WHEN v_pedido_id MOD 6 = 0 THEN 'Agregar salsa aparte.' ELSE NULL END,
			        CASE WHEN v_estado_id >= 3 THEN TRUE ELSE FALSE END);

			IF v_pedido_id MOD 3 = 0 THEN
				SET v_producto_id = ((v_pedido_id + 13) MOD 25) + 1;
				SET v_cantidad = 1;
				SELECT nombre_producto, precio_unitario INTO v_nombre_producto, v_precio FROM productos WHERE producto_id = v_producto_id;

				INSERT INTO items_pedido (pedido_id,
				                          producto_id,
				                          cantidad,
				                          snapshot_nombre_producto,
				                          precio_unitario,
				                          total_linea,
				                          nota_especial,
				                          listo)
				VALUES (v_pedido_id,
				        v_producto_id,
				        v_cantidad,
				        v_nombre_producto,
				        v_precio,
				        ROUND(v_precio * v_cantidad, 2),
				        'Producto adicional sugerido por promocion.',
				        CASE WHEN v_estado_id >= 3 THEN TRUE ELSE FALSE END);
			END IF;

			SET v_pedido_id = v_pedido_id + 1;
		END WHILE;
END$$

DELIMITER ;

CALL seed_pedidos_restaurante();
DROP PROCEDURE seed_pedidos_restaurante;

-- Recalcular importes a partir de los items.
UPDATE pedidos_cliente pc
	JOIN (SELECT pedido_id,
	             ROUND(SUM(total_linea), 2) AS subtotal_calculado
	      FROM items_pedido
	      GROUP BY pedido_id) totales ON totales.pedido_id = pc.pedido_id
SET pc.subtotal          = totales.subtotal_calculado,
    pc.impuesto          = ROUND(totales.subtotal_calculado * 0.12, 2),
    pc.descuento         = CASE
										WHEN pc.codigo_descuento = 'PROMO10' THEN ROUND(totales.subtotal_calculado * 0.10, 2)
	                           WHEN pc.prioritario = TRUE THEN 0.50
	                           ELSE 0.00
		 END,
    pc.recargo_direccion = CASE
										WHEN pc.cliente_id MOD 5 = 0 THEN 1.25
	                           ELSE 0.00
		 END,
    pc.total             = ROUND(
		 totales.subtotal_calculado
			 + ROUND(totales.subtotal_calculado * 0.12, 2)
			 + CASE WHEN pc.cliente_id MOD 5 = 0 THEN 1.25 ELSE 0.00 END
			 - CASE
					WHEN pc.codigo_descuento = 'PROMO10' THEN ROUND(totales.subtotal_calculado * 0.10, 2)
			      WHEN pc.prioritario = TRUE THEN 0.50
			      ELSE 0.00
			 END,
	    2
                           );

-- =========================================================
-- 8. Historial de estados
-- =========================================================

INSERT INTO historial_estados_pedido (pedido_id,
                                      estado_origen_id,
                                      estado_destino_id,
                                      cambiado_por_personal_id,
                                      cambiado_en,
                                      notas)
SELECT pedido_id,
       1,
       1,
       registrado_por_personal_id,
       creado_en,
       'Pedido creado por administrador.'
FROM pedidos_cliente;

INSERT INTO historial_estados_pedido (pedido_id,
                                      estado_origen_id,
                                      estado_destino_id,
                                      cambiado_por_personal_id,
                                      cambiado_en,
                                      notas)
SELECT pedido_id,
       1,
       2,
       5 + (pedido_id MOD 4),
       DATE_ADD(creado_en, INTERVAL 8 MINUTE),
       'Pedido tomado por cocina.'
FROM pedidos_cliente
WHERE estado_actual_id >= 2;

INSERT INTO historial_estados_pedido (pedido_id,
                                      estado_origen_id,
                                      estado_destino_id,
                                      cambiado_por_personal_id,
                                      cambiado_en,
                                      notas)
SELECT pedido_id,
       2,
       3,
       5 + (pedido_id MOD 4),
       DATE_ADD(creado_en, INTERVAL 24 MINUTE),
       'Pedido listo para despacho.'
FROM pedidos_cliente
WHERE estado_actual_id >= 3;

INSERT INTO historial_estados_pedido (pedido_id,
                                      estado_origen_id,
                                      estado_destino_id,
                                      cambiado_por_personal_id,
                                      cambiado_en,
                                      notas)
SELECT pedido_id,
       3,
       4,
       9 + (pedido_id MOD 4),
       DATE_ADD(creado_en, INTERVAL 32 MINUTE),
       'Pedido despachado por repartidor.'
FROM pedidos_cliente
WHERE estado_actual_id >= 4;

INSERT INTO historial_estados_pedido (pedido_id,
                                      estado_origen_id,
                                      estado_destino_id,
                                      cambiado_por_personal_id,
                                      cambiado_en,
                                      notas)
SELECT pedido_id,
       4,
       5,
       9 + (pedido_id MOD 4),
       DATE_ADD(creado_en, INTERVAL 52 MINUTE),
       'Entrega confirmada por repartidor.'
FROM pedidos_cliente
WHERE estado_actual_id >= 5;

UPDATE pedidos_cliente pc
	JOIN (SELECT pedido_id,
	             MAX(cambiado_en) AS ultimo_cambio
	      FROM historial_estados_pedido
	      GROUP BY pedido_id) historial ON historial.pedido_id = pc.pedido_id
SET pc.estado_actual_cambiado_en = historial.ultimo_cambio;

-- =========================================================
-- 9. Entregas
-- Pedidos EN_CAMINO y ENTREGADO tienen entrega.
-- Solo algunos ENTREGADO tienen confirmacion del cliente.
-- =========================================================

INSERT INTO entregas (pedido_id,
                      repartidor_personal_id,
                      despachado_en,
                      entregado_en,
                      nombre_receptor,
                      confirmado_por_cliente_en,
                      notas_confirmacion_cliente,
                      estado_entrega,
                      notas_repartidor,
                      numero_intento)
SELECT pc.pedido_id,
       9 + (pc.pedido_id MOD 4),
       DATE_ADD(pc.creado_en, INTERVAL 32 MINUTE),
       CASE
			 WHEN pc.estado_actual_id = 5 THEN DATE_ADD(pc.creado_en, INTERVAL 52 MINUTE)
	       ELSE NULL
			 END,
       CASE
			 WHEN pc.estado_actual_id = 5 THEN c.nombre_completo
	       ELSE NULL
			 END,
       CASE
			 WHEN pc.estado_actual_id = 5 AND pc.pedido_id MOD 2 = 0 THEN DATE_ADD(pc.creado_en, INTERVAL 60 MINUTE)
	       ELSE NULL
			 END,
       CASE
			 WHEN pc.estado_actual_id = 5 AND pc.pedido_id MOD 2 = 0 THEN 'Cliente confirmo recepcion sin novedades.'
	       ELSE NULL
			 END,
       CASE
			 WHEN pc.estado_actual_id = 5 THEN 'ENTREGADO'
	       ELSE 'EN_TRANSITO'
			 END,
       CASE
			 WHEN pc.estado_actual_id = 5 THEN 'Entrega completada sin incidentes.'
	       ELSE 'Repartidor en ruta.'
			 END,
       1
FROM pedidos_cliente pc
		  JOIN clientes c ON c.cliente_id = pc.cliente_id
WHERE pc.estado_actual_id >= 4;

-- =========================================================
-- 10. Resumen de validacion rapida
-- =========================================================

SELECT 'roles' AS nombre_tabla, COUNT(*) AS total_filas
FROM roles
UNION ALL
SELECT 'personal', COUNT(*)
FROM personal
UNION ALL
SELECT 'clientes', COUNT(*)
FROM clientes
UNION ALL
SELECT 'direcciones_cliente', COUNT(*)
FROM direcciones_cliente
UNION ALL
SELECT 'productos', COUNT(*)
FROM productos
UNION ALL
SELECT 'estados_pedido', COUNT(*)
FROM estados_pedido
UNION ALL
SELECT 'reglas_transicion_estado_pedido', COUNT(*)
FROM reglas_transicion_estado_pedido
UNION ALL
SELECT 'pedidos_cliente', COUNT(*)
FROM pedidos_cliente
UNION ALL
SELECT 'items_pedido', COUNT(*)
FROM items_pedido
UNION ALL
SELECT 'historial_estados_pedido', COUNT(*)
FROM historial_estados_pedido
UNION ALL
SELECT 'entregas', COUNT(*)
FROM entregas;
