-- =========================================================
-- Restaurant Order Management - Test Data
-- MySQL 9.3
--
-- Usa este script después de ejecutar:
-- assets/ROM - ER Diagram.sql
--
-- ATENCIÓN: este seed limpia los datos existentes de las tablas
-- del modelo y vuelve a poblarlas con datos determinísticos.
-- Objetivo:
-- - 3 roles
-- - 5 estados
-- - 4 reglas de transición
-- - 12 staff
-- - 30 clientes/usuarios externos
-- - 45 direcciones
-- - 25 productos
-- - 36 pedidos
-- - 84 ítems de pedido
-- - historial para todos los pedidos
-- - entregas para pedidos en camino y entregados
-- =========================================================

USE first_bimester_project;

-- =========================================================
-- 0. Validación del esquema
-- =========================================================

DELIMITER $$

DROP PROCEDURE IF EXISTS validate_restaurant_seed_schema$$

CREATE PROCEDURE validate_restaurant_seed_schema()
BEGIN
	IF EXISTS (
		SELECT 1
		FROM information_schema.columns
		WHERE table_schema = DATABASE()
			AND table_name = 'customer_orders'
			AND column_name = 'delivery_address'
	) THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'Schema mismatch: customer_orders.delivery_address pertenece al modelo viejo. Ejecutá primero ROM - ER Diagram.sql recreando first_bimester_project.';
	END IF;

	IF NOT EXISTS (
		SELECT 1
		FROM information_schema.columns
		WHERE table_schema = DATABASE()
			AND table_name = 'customer_orders'
			AND column_name = 'delivery_address_id'
	) THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'Schema mismatch: falta customer_orders.delivery_address_id. Ejecutá primero ROM - ER Diagram.sql.';
	END IF;

	IF EXISTS (
		SELECT 1
		FROM information_schema.columns
		WHERE table_schema = DATABASE()
			AND table_name = 'customer_orders'
			AND column_name = 'delivery_address_id'
			AND is_nullable = 'YES'
	) THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'Schema mismatch: customer_orders.delivery_address_id debe ser NOT NULL.';
	END IF;

	IF NOT EXISTS (
		SELECT 1
		FROM information_schema.columns
		WHERE table_schema = DATABASE()
			AND table_name = 'customer_orders'
			AND column_name = 'delivery_address_snapshot'
	) THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'Schema mismatch: falta customer_orders.delivery_address_snapshot. Ejecutá primero ROM - ER Diagram.sql.';
	END IF;

	IF NOT EXISTS (
		SELECT 1
		FROM information_schema.tables
		WHERE table_schema = DATABASE()
			AND table_name = 'customer_addresses'
	) THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'Schema mismatch: falta customer_addresses. Ejecutá primero ROM - ER Diagram.sql.';
	END IF;

	IF NOT EXISTS (
		SELECT 1
		FROM information_schema.columns
		WHERE table_schema = DATABASE()
			AND table_name = 'customer_addresses'
			AND column_name = 'country'
			AND is_nullable = 'NO'
	) THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'Schema mismatch: falta customer_addresses.country NOT NULL.';
	END IF;

	IF EXISTS (
		SELECT 1
		FROM information_schema.columns
		WHERE table_schema = DATABASE()
			AND table_name = 'customer_addresses'
			AND column_name IN ('city', 'province')
			AND is_nullable = 'YES'
	) THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'Schema mismatch: customer_addresses.city y province deben ser NOT NULL.';
	END IF;

	IF NOT EXISTS (
		SELECT 1
		FROM information_schema.columns
		WHERE table_schema = DATABASE()
			AND table_name = 'order_status_histories'
			AND column_name = 'from_status_id'
			AND is_nullable = 'NO'
			AND column_default = '1'
	) THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'Schema mismatch: order_status_histories.from_status_id debe ser NOT NULL DEFAULT 1.';
	END IF;
END$$

DELIMITER ;

CALL validate_restaurant_seed_schema();
DROP PROCEDURE validate_restaurant_seed_schema;

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE deliveries;
TRUNCATE TABLE order_status_histories;
TRUNCATE TABLE order_items;
TRUNCATE TABLE customer_orders;
TRUNCATE TABLE customer_addresses;
TRUNCATE TABLE products;
TRUNCATE TABLE staff;
TRUNCATE TABLE order_status_transition_rules;
TRUNCATE TABLE order_statuses;
TRUNCATE TABLE roles;
TRUNCATE TABLE customers;

SET FOREIGN_KEY_CHECKS = 1;

-- =========================================================
-- 1. Roles
-- =========================================================

INSERT INTO roles (role_id, role_name) VALUES
	(1, 'ADMINISTRATOR'),
	(2, 'COOK'),
	(3, 'COURIER');

-- =========================================================
-- 2. Staff: 12 usuarios internos
-- =========================================================

INSERT INTO staff (staff_id, role_id, full_name, phone, email, username, is_active, created_at) VALUES
	(1, 1, 'Ana Morales', '0991000001', 'ana.morales@restaurant.local', 'admin01', TRUE, '2026-04-01 08:00:00'),
	(2, 1, 'Bruno Salazar', '0991000002', 'bruno.salazar@restaurant.local', 'admin02', TRUE, '2026-04-01 08:05:00'),
	(3, 1, 'Camila Herrera', '0991000003', 'camila.herrera@restaurant.local', 'admin03', TRUE, '2026-04-01 08:10:00'),
	(4, 1, 'Diego Andrade', '0991000004', 'diego.andrade@restaurant.local', 'admin04', TRUE, '2026-04-01 08:15:00'),
	(5, 2, 'Elena Paredes', '0991000005', 'elena.paredes@restaurant.local', 'cook01', TRUE, '2026-04-01 08:20:00'),
	(6, 2, 'Fernando Rivas', '0991000006', 'fernando.rivas@restaurant.local', 'cook02', TRUE, '2026-04-01 08:25:00'),
	(7, 2, 'Gabriela Núñez', '0991000007', 'gabriela.nunez@restaurant.local', 'cook03', TRUE, '2026-04-01 08:30:00'),
	(8, 2, 'Hugo Benítez', '0991000008', 'hugo.benitez@restaurant.local', 'cook04', TRUE, '2026-04-01 08:35:00'),
	(9, 3, 'Iván Castillo', '0991000009', 'ivan.castillo@restaurant.local', 'courier01', TRUE, '2026-04-01 08:40:00'),
	(10, 3, 'Julieta Gómez', '0991000010', 'julieta.gomez@restaurant.local', 'courier02', TRUE, '2026-04-01 08:45:00'),
	(11, 3, 'Kevin Molina', '0991000011', 'kevin.molina@restaurant.local', 'courier03', TRUE, '2026-04-01 08:50:00'),
	(12, 3, 'Laura Cevallos', '0991000012', 'laura.cevallos@restaurant.local', 'courier04', TRUE, '2026-04-01 08:55:00');

-- =========================================================
-- 3. Clientes: 30 usuarios externos
-- =========================================================

INSERT INTO customers (customer_id, full_name, phone, email, is_active, created_at) VALUES
	(1, 'Mateo Alvarez', '0982000001', 'cliente01@test.local', TRUE, '2026-04-02 09:00:00'),
	(2, 'Sofía Zambrano', '0982000002', 'cliente02@test.local', TRUE, '2026-04-02 09:03:00'),
	(3, 'Valentina Rojas', '0982000003', 'cliente03@test.local', TRUE, '2026-04-02 09:06:00'),
	(4, 'Sebastián Vega', '0982000004', 'cliente04@test.local', TRUE, '2026-04-02 09:09:00'),
	(5, 'Isabella Torres', '0982000005', 'cliente05@test.local', TRUE, '2026-04-02 09:12:00'),
	(6, 'Daniela Castro', '0982000006', 'cliente06@test.local', TRUE, '2026-04-02 09:15:00'),
	(7, 'Nicolás León', '0982000007', 'cliente07@test.local', TRUE, '2026-04-02 09:18:00'),
	(8, 'Lucía Molina', '0982000008', 'cliente08@test.local', TRUE, '2026-04-02 09:21:00'),
	(9, 'Emilia Ortega', '0982000009', 'cliente09@test.local', TRUE, '2026-04-02 09:24:00'),
	(10, 'Tomás Ponce', '0982000010', 'cliente10@test.local', TRUE, '2026-04-02 09:27:00'),
	(11, 'Martina Flores', '0982000011', 'cliente11@test.local', TRUE, '2026-04-02 09:30:00'),
	(12, 'Joaquín Suárez', '0982000012', 'cliente12@test.local', TRUE, '2026-04-02 09:33:00'),
	(13, 'Renata Bravo', '0982000013', 'cliente13@test.local', TRUE, '2026-04-02 09:36:00'),
	(14, 'Alejandro Mena', '0982000014', 'cliente14@test.local', TRUE, '2026-04-02 09:39:00'),
	(15, 'Paula Viteri', '0982000015', 'cliente15@test.local', TRUE, '2026-04-02 09:42:00'),
	(16, 'Carlos Aguilar', '0982000016', 'cliente16@test.local', TRUE, '2026-04-02 09:45:00'),
	(17, 'María José Cárdenas', '0982000017', 'cliente17@test.local', TRUE, '2026-04-02 09:48:00'),
	(18, 'Gabriel Peña', '0982000018', 'cliente18@test.local', TRUE, '2026-04-02 09:51:00'),
	(19, 'Victoria Espinoza', '0982000019', 'cliente19@test.local', TRUE, '2026-04-02 09:54:00'),
	(20, 'Samuel Carrera', '0982000020', 'cliente20@test.local', TRUE, '2026-04-02 09:57:00'),
	(21, 'Antonella Ruiz', '0982000021', 'cliente21@test.local', TRUE, '2026-04-02 10:00:00'),
	(22, 'Felipe Ochoa', '0982000022', 'cliente22@test.local', TRUE, '2026-04-02 10:03:00'),
	(23, 'Carolina Mejía', '0982000023', 'cliente23@test.local', TRUE, '2026-04-02 10:06:00'),
	(24, 'Andrés Villacís', '0982000024', 'cliente24@test.local', TRUE, '2026-04-02 10:09:00'),
	(25, 'Valeria Cordero', '0982000025', 'cliente25@test.local', TRUE, '2026-04-02 10:12:00'),
	(26, 'Emiliano Arias', '0982000026', 'cliente26@test.local', TRUE, '2026-04-02 10:15:00'),
	(27, 'Mía Jaramillo', '0982000027', 'cliente27@test.local', TRUE, '2026-04-02 10:18:00'),
	(28, 'Juan Pablo Serrano', '0982000028', 'cliente28@test.local', TRUE, '2026-04-02 10:21:00'),
	(29, 'Clara Maldonado', '0982000029', 'cliente29@test.local', TRUE, '2026-04-02 10:24:00'),
	(30, 'Rafael Coronel', '0982000030', 'cliente30@test.local', TRUE, '2026-04-02 10:27:00');

-- =========================================================
-- 4. Direcciones: 45 direcciones
-- Cada cliente tiene una dirección principal.
-- Los clientes 1 a 15 tienen además una dirección secundaria.
-- =========================================================

INSERT INTO customer_addresses (
	address_id,
	customer_id,
	alias,
	main_street,
	secondary_street,
	house_number,
	reference,
	postal_code,
	city,
	province,
	country,
	is_primary,
	is_active
) VALUES
	(1, 1, 'Casa', 'Av. Amazonas', 'Naciones Unidas', 'N1-01', 'Edificio azul, timbre 1', '170101', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
	(2, 2, 'Casa', 'Av. 6 de Diciembre', 'Portugal', 'N2-02', 'Frente a farmacia', '170102', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
	(3, 3, 'Casa', 'Av. República', 'Eloy Alfaro', 'N3-03', 'Casa con reja negra', '170103', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
	(4, 4, 'Casa', 'Av. Colón', 'Rábida', 'N4-04', 'Junto al parque', '170104', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
	(5, 5, 'Casa', 'Av. Shyris', 'Suecia', 'N5-05', 'Torre B, piso 5', '170105', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
	(6, 6, 'Casa', 'Av. América', 'Mariana de Jesús', 'N6-06', 'Portón blanco', '170106', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
	(7, 7, 'Casa', 'Av. Patria', '9 de Octubre', 'N7-07', 'Cerca del banco', '170107', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
	(8, 8, 'Casa', 'Av. Universitaria', 'Bolivia', 'N8-08', 'Bloque C', '170108', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
	(9, 9, 'Casa', 'Av. Occidental', 'Machala', 'N9-09', 'Entrada posterior', '170109', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
	(10, 10, 'Casa', 'Av. De la Prensa', 'Florida', 'N10-10', 'Junto a panadería', '170110', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
	(11, 11, 'Casa', 'Av. Maldonado', 'Morán Valverde', 'S11-11', 'Casa esquinera', '170111', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
	(12, 12, 'Casa', 'Av. Simón Bolívar', 'Ruta Viva', 'S12-12', 'Conjunto Los Pinos', '170112', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
	(13, 13, 'Casa', 'Av. Interoceánica', 'Cumbayá', 'E13-13', 'Portería principal', '170113', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
	(14, 14, 'Casa', 'Av. Ilaló', 'San Rafael', 'E14-14', 'Casa amarilla', '170114', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
	(15, 15, 'Casa', 'Av. General Rumiñahui', 'El Triángulo', 'E15-15', 'Frente al redondel', '170115', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
	(16, 16, 'Casa', 'Av. Real Audiencia', 'Luis Tufiño', 'N16-16', 'Timbre 16', '170116', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
	(17, 17, 'Casa', 'Av. Galo Plaza', 'Capitán Ramón Borja', 'N17-17', 'Local planta baja', '170117', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
	(18, 18, 'Casa', 'Av. El Inca', 'Amazonas', 'N18-18', 'Conjunto cerrado', '170118', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
	(19, 19, 'Casa', 'Av. La Gasca', 'Villalengua', 'N19-19', 'Junto a lavandería', '170119', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
	(20, 20, 'Casa', 'Av. América', 'La Gasca', 'N20-20', 'Segundo piso', '170120', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
	(21, 21, 'Casa', 'Av. 10 de Agosto', 'Carrión', 'N21-21', 'Edificio gris', '170121', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
	(22, 22, 'Casa', 'Av. Coruña', 'González Suárez', 'N22-22', 'Suite 302', '170122', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
	(23, 23, 'Casa', 'Av. Eloy Alfaro', 'Granados', 'N23-23', 'Junto al colegio', '170123', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
	(24, 24, 'Casa', 'Av. Granados', 'Río Coca', 'N24-24', 'Casa verde', '170124', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
	(25, 25, 'Casa', 'Av. Río Coca', 'París', 'N25-25', 'Frente a gasolinera', '170125', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
	(26, 26, 'Casa', 'Av. De los Granados', 'Isla Marchena', 'N26-26', 'Bloque 2', '170126', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
	(27, 27, 'Casa', 'Av. República del Salvador', 'Moscú', 'N27-27', 'Piso 7', '170127', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
	(28, 28, 'Casa', 'Av. Portugal', 'Catalina Aldaz', 'N28-28', 'Torre norte', '170128', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
	(29, 29, 'Casa', 'Av. Naciones Unidas', 'Japón', 'N29-29', 'Entrada lateral', '170129', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
	(30, 30, 'Casa', 'Av. Eloy Alfaro', 'Portugal', 'N30-30', 'Casa blanca', '170130', 'Quito', 'Pichincha', 'Ecuador', TRUE, TRUE),
	(31, 1, 'Trabajo', 'Av. República del Salvador', 'Suecia', 'T1-01', 'Oficina 401', '170201', 'Quito', 'Pichincha', 'Ecuador', FALSE, TRUE),
	(32, 2, 'Trabajo', 'Av. Amazonas', 'Colón', 'T2-02', 'Recepción principal', '170202', 'Quito', 'Pichincha', 'Ecuador', FALSE, TRUE),
	(33, 3, 'Trabajo', 'Av. Orellana', '9 de Octubre', 'T3-03', 'Piso 8', '170203', 'Quito', 'Pichincha', 'Ecuador', FALSE, TRUE),
	(34, 4, 'Trabajo', 'Av. Shyris', 'Río Coca', 'T4-04', 'Local 12', '170204', 'Quito', 'Pichincha', 'Ecuador', FALSE, TRUE),
	(35, 5, 'Trabajo', 'Av. Naciones Unidas', 'Amazonas', 'T5-05', 'Torre 1', '170205', 'Quito', 'Pichincha', 'Ecuador', FALSE, TRUE),
	(36, 6, 'Trabajo', 'Av. De los Granados', 'Eloy Alfaro', 'T6-06', 'Edificio empresarial', '170206', 'Quito', 'Pichincha', 'Ecuador', FALSE, TRUE),
	(37, 7, 'Trabajo', 'Av. 12 de Octubre', 'Veintimilla', 'T7-07', 'Oficina 702', '170207', 'Quito', 'Pichincha', 'Ecuador', FALSE, TRUE),
	(38, 8, 'Trabajo', 'Av. Patria', 'Amazonas', 'T8-08', 'Piso 3', '170208', 'Quito', 'Pichincha', 'Ecuador', FALSE, TRUE),
	(39, 9, 'Trabajo', 'Av. Maldonado', 'Recreo', 'T9-09', 'Junto al centro comercial', '170209', 'Quito', 'Pichincha', 'Ecuador', FALSE, TRUE),
	(40, 10, 'Trabajo', 'Av. Simón Bolívar', 'Tumbaco', 'T10-10', 'Bodega 4', '170210', 'Quito', 'Pichincha', 'Ecuador', FALSE, TRUE),
	(41, 11, 'Trabajo', 'Av. Interoceánica', 'Puembo', 'T11-11', 'Entrada sur', '170211', 'Quito', 'Pichincha', 'Ecuador', FALSE, TRUE),
	(42, 12, 'Trabajo', 'Av. Ilaló', 'Conocoto', 'T12-12', 'Local exterior', '170212', 'Quito', 'Pichincha', 'Ecuador', FALSE, TRUE),
	(43, 13, 'Trabajo', 'Av. General Rumiñahui', 'Sangolquí', 'T13-13', 'Planta alta', '170213', 'Quito', 'Pichincha', 'Ecuador', FALSE, TRUE),
	(44, 14, 'Trabajo', 'Av. América', 'Colón', 'T14-14', 'Consultorio 5', '170214', 'Quito', 'Pichincha', 'Ecuador', FALSE, TRUE),
	(45, 15, 'Trabajo', 'Av. De la Prensa', 'La Florida', 'T15-15', 'Oficina 15', '170215', 'Quito', 'Pichincha', 'Ecuador', FALSE, TRUE);

-- =========================================================
-- 5. Productos: 25 productos
-- =========================================================

INSERT INTO products (
	product_id,
	product_code,
	product_name,
	description,
	unit_price,
	production_cost,
	category,
	preparation_time_minutes,
	is_available,
	image_url,
	created_at
) VALUES
	(1, 'START-001', 'Papas bravas', 'Papas crocantes con salsa picante', 3.50, 1.20, 'STARTER', 10, TRUE, '/images/papas-bravas.png', '2026-04-03 08:00:00'),
	(2, 'START-002', 'Empanadas mixtas', 'Tres empanadas con ají de la casa', 4.20, 1.55, 'STARTER', 12, TRUE, '/images/empanadas-mixtas.png', '2026-04-03 08:02:00'),
	(3, 'START-003', 'Nachos completos', 'Nachos con queso, guacamole y pico de gallo', 5.10, 2.00, 'STARTER', 14, TRUE, '/images/nachos-completos.png', '2026-04-03 08:04:00'),
	(4, 'START-004', 'Alitas BBQ', 'Alitas bañadas en salsa BBQ', 6.25, 2.70, 'STARTER', 18, TRUE, '/images/alitas-bbq.png', '2026-04-03 08:06:00'),
	(5, 'START-005', 'Ensalada fresca', 'Lechuga, tomate, pepino y vinagreta', 4.75, 1.85, 'STARTER', 8, TRUE, '/images/ensalada-fresca.png', '2026-04-03 08:08:00'),
	(6, 'MAIN-001', 'Hamburguesa clásica', 'Carne, queso, lechuga, tomate y salsa de la casa', 7.90, 3.10, 'MAIN_COURSE', 18, TRUE, '/images/hamburguesa-clasica.png', '2026-04-03 08:10:00'),
	(7, 'MAIN-002', 'Hamburguesa doble', 'Doble carne, doble queso y tocino', 10.50, 4.60, 'MAIN_COURSE', 22, TRUE, '/images/hamburguesa-doble.png', '2026-04-03 08:12:00'),
	(8, 'MAIN-003', 'Pizza personal', 'Pizza individual de queso y pepperoni', 8.25, 3.20, 'MAIN_COURSE', 20, TRUE, '/images/pizza-personal.png', '2026-04-03 08:14:00'),
	(9, 'MAIN-004', 'Pizza vegetariana', 'Pizza con vegetales frescos y queso mozzarella', 8.75, 3.35, 'MAIN_COURSE', 21, TRUE, '/images/pizza-vegetariana.png', '2026-04-03 08:16:00'),
	(10, 'MAIN-005', 'Pollo grillado', 'Pechuga grillada con arroz y ensalada', 9.90, 4.10, 'MAIN_COURSE', 25, TRUE, '/images/pollo-grillado.png', '2026-04-03 08:18:00'),
	(11, 'MAIN-006', 'Lomo salteado', 'Lomo salteado con papas y arroz', 11.80, 5.10, 'MAIN_COURSE', 28, TRUE, '/images/lomo-salteado.png', '2026-04-03 08:20:00'),
	(12, 'MAIN-007', 'Pasta al pesto', 'Pasta corta con pesto y queso parmesano', 8.60, 3.05, 'MAIN_COURSE', 19, TRUE, '/images/pasta-pesto.png', '2026-04-03 08:22:00'),
	(13, 'DRINK-001', 'Gaseosa personal', 'Bebida gaseosa de 400 ml', 1.50, 0.45, 'DRINK', 2, TRUE, '/images/gaseosa-personal.png', '2026-04-03 08:24:00'),
	(14, 'DRINK-002', 'Agua mineral', 'Agua sin gas de 500 ml', 1.20, 0.35, 'DRINK', 1, TRUE, '/images/agua-mineral.png', '2026-04-03 08:26:00'),
	(15, 'DRINK-003', 'Limonada natural', 'Limonada preparada al momento', 2.25, 0.80, 'DRINK', 4, TRUE, '/images/limonada-natural.png', '2026-04-03 08:28:00'),
	(16, 'DRINK-004', 'Jugo de mora', 'Jugo natural de mora', 2.50, 0.95, 'DRINK', 5, TRUE, '/images/jugo-mora.png', '2026-04-03 08:30:00'),
	(17, 'DESS-001', 'Brownie', 'Brownie de chocolate con nueces', 3.20, 1.20, 'DESSERT', 6, TRUE, '/images/brownie.png', '2026-04-03 08:32:00'),
	(18, 'DESS-002', 'Cheesecake', 'Cheesecake de frutos rojos', 4.50, 1.90, 'DESSERT', 7, TRUE, '/images/cheesecake.png', '2026-04-03 08:34:00'),
	(19, 'DESS-003', 'Helado artesanal', 'Dos bolas de helado artesanal', 3.75, 1.35, 'DESSERT', 3, TRUE, '/images/helado-artesanal.png', '2026-04-03 08:36:00'),
	(20, 'DESS-004', 'Tres leches', 'Postre tres leches individual', 4.10, 1.60, 'DESSERT', 5, TRUE, '/images/tres-leches.png', '2026-04-03 08:38:00'),
	(21, 'COMBO-001', 'Combo clásico', 'Hamburguesa clásica, papas y gaseosa', 11.50, 4.55, 'COMBO', 22, TRUE, '/images/combo-clasico.png', '2026-04-03 08:40:00'),
	(22, 'COMBO-002', 'Combo doble', 'Hamburguesa doble, papas y limonada', 14.25, 5.85, 'COMBO', 26, TRUE, '/images/combo-doble.png', '2026-04-03 08:42:00'),
	(23, 'COMBO-003', 'Combo pizza', 'Pizza personal, ensalada y bebida', 12.75, 4.95, 'COMBO', 24, TRUE, '/images/combo-pizza.png', '2026-04-03 08:44:00'),
	(24, 'COMBO-004', 'Combo familiar', 'Dos pizzas, alitas y dos bebidas', 24.90, 10.20, 'COMBO', 35, TRUE, '/images/combo-familiar.png', '2026-04-03 08:46:00'),
	(25, 'COMBO-005', 'Combo ligero', 'Ensalada, agua y postre pequeño', 9.80, 3.70, 'COMBO', 15, TRUE, '/images/combo-ligero.png', '2026-04-03 08:48:00');

-- =========================================================
-- 6. Estados y reglas de transición
-- =========================================================

INSERT INTO order_statuses (status_id, status_code, status_name, status_order, is_final) VALUES
	(1, 'PENDING', 'Pendiente', 1, FALSE),
	(2, 'IN_PREPARATION', 'En preparación', 2, FALSE),
	(3, 'READY', 'Listo', 3, FALSE),
	(4, 'ON_THE_WAY', 'En camino', 4, FALSE),
	(5, 'DELIVERED', 'Entregado', 5, TRUE);

INSERT INTO order_status_transition_rules (
	transition_rule_id,
	from_status_id,
	to_status_id,
	role_id,
	is_active
) VALUES
	(1, 1, 2, 2, TRUE),
	(2, 2, 3, 2, TRUE),
	(3, 3, 4, 3, TRUE),
	(4, 4, 5, 3, TRUE);

-- =========================================================
-- 7. Pedidos e ítems
-- Se generan 36 pedidos con 2 o 3 ítems por pedido.
-- =========================================================

DELIMITER $$

DROP PROCEDURE IF EXISTS seed_restaurant_orders$$

CREATE PROCEDURE seed_restaurant_orders()
BEGIN
	DECLARE v_order_id INT DEFAULT 1;
	DECLARE v_customer_id INT;
	DECLARE v_status_id INT;
	DECLARE v_admin_staff_id INT;
	DECLARE v_product_id INT;
	DECLARE v_quantity INT;
	DECLARE v_price DECIMAL(10, 2);
	DECLARE v_product_name VARCHAR(120);

	WHILE v_order_id <= 36 DO
		SET v_customer_id = ((v_order_id - 1) MOD 30) + 1;
		SET v_admin_staff_id = ((v_order_id - 1) MOD 4) + 1;

		SET v_status_id = CASE
			WHEN v_order_id BETWEEN 1 AND 8 THEN 1
			WHEN v_order_id BETWEEN 9 AND 15 THEN 2
			WHEN v_order_id BETWEEN 16 AND 22 THEN 3
			WHEN v_order_id BETWEEN 23 AND 29 THEN 4
			ELSE 5
		END;

		INSERT INTO customer_orders (
			order_id,
			order_code,
			customer_id,
			registered_by_staff_id,
			current_status_id,
			delivery_address_id,
			delivery_address_snapshot,
			subtotal_amount,
			tax_amount,
			discount_amount,
			address_surcharge_amount,
			total_amount,
			discount_code,
			is_priority,
			general_notes,
			created_at,
			estimated_delivery_at,
			current_status_changed_at
		) VALUES (
			v_order_id,
			CONCAT('ORD-', LPAD(v_order_id, 5, '0')),
			v_customer_id,
			v_admin_staff_id,
			v_status_id,
			v_customer_id,
			(
				SELECT CONCAT(alias, ': ', main_street, ' y ', COALESCE(secondary_street, 'S/N'), ', ', COALESCE(house_number, 'S/N'), '. Ref: ', COALESCE(reference, 'Sin referencia'))
				FROM customer_addresses
				WHERE address_id = v_customer_id
			),
			0.00,
			0.00,
			0.00,
			0.00,
			0.00,
			CASE WHEN v_order_id MOD 9 = 0 THEN 'PROMO10' ELSE NULL END,
			CASE WHEN v_order_id MOD 6 = 0 THEN TRUE ELSE FALSE END,
			CASE
				WHEN v_order_id MOD 5 = 0 THEN 'Cliente solicita cubiertos y servilletas adicionales.'
				WHEN v_order_id MOD 7 = 0 THEN 'Pedido corporativo, confirmar en recepción.'
				ELSE NULL
			END,
			DATE_ADD('2026-04-10 12:00:00', INTERVAL v_order_id * 7 MINUTE),
			DATE_ADD('2026-04-10 12:45:00', INTERVAL v_order_id * 7 MINUTE),
			DATE_ADD('2026-04-10 12:00:00', INTERVAL v_order_id * 7 MINUTE)
		);

		SET v_product_id = ((v_order_id - 1) MOD 25) + 1;
		SET v_quantity = (v_order_id MOD 3) + 1;
		SELECT product_name, unit_price INTO v_product_name, v_price FROM products WHERE product_id = v_product_id;

		INSERT INTO order_items (
			order_id,
			product_id,
			quantity,
			product_name_snapshot,
			unit_price,
			line_total,
			special_note,
			is_ready
		) VALUES (
			v_order_id,
			v_product_id,
			v_quantity,
			v_product_name,
			v_price,
			ROUND(v_price * v_quantity, 2),
			CASE WHEN v_order_id MOD 4 = 0 THEN 'Sin cebolla, por favor.' ELSE NULL END,
			CASE WHEN v_status_id >= 3 THEN TRUE ELSE FALSE END
		);

		SET v_product_id = ((v_order_id + 6) MOD 25) + 1;
		SET v_quantity = ((v_order_id + 1) MOD 2) + 1;
		SELECT product_name, unit_price INTO v_product_name, v_price FROM products WHERE product_id = v_product_id;

		INSERT INTO order_items (
			order_id,
			product_id,
			quantity,
			product_name_snapshot,
			unit_price,
			line_total,
			special_note,
			is_ready
		) VALUES (
			v_order_id,
			v_product_id,
			v_quantity,
			v_product_name,
			v_price,
			ROUND(v_price * v_quantity, 2),
			CASE WHEN v_order_id MOD 6 = 0 THEN 'Agregar salsa aparte.' ELSE NULL END,
			CASE WHEN v_status_id >= 3 THEN TRUE ELSE FALSE END
		);

		IF v_order_id MOD 3 = 0 THEN
			SET v_product_id = ((v_order_id + 13) MOD 25) + 1;
			SET v_quantity = 1;
			SELECT product_name, unit_price INTO v_product_name, v_price FROM products WHERE product_id = v_product_id;

			INSERT INTO order_items (
				order_id,
				product_id,
				quantity,
				product_name_snapshot,
				unit_price,
				line_total,
				special_note,
				is_ready
			) VALUES (
				v_order_id,
				v_product_id,
				v_quantity,
				v_product_name,
				v_price,
				ROUND(v_price * v_quantity, 2),
				'Producto adicional sugerido por promoción.',
				CASE WHEN v_status_id >= 3 THEN TRUE ELSE FALSE END
			);
		END IF;

		SET v_order_id = v_order_id + 1;
	END WHILE;
END$$

DELIMITER ;

CALL seed_restaurant_orders();
DROP PROCEDURE seed_restaurant_orders;

-- Recalcular importes a partir de los ítems.
UPDATE customer_orders co
	JOIN (
		SELECT
			order_id,
			ROUND(SUM(line_total), 2) AS subtotal
		FROM order_items
		GROUP BY order_id
	) totals ON totals.order_id = co.order_id
SET
	co.subtotal_amount = totals.subtotal,
	co.tax_amount = ROUND(totals.subtotal * 0.12, 2),
	co.discount_amount = CASE
		WHEN co.discount_code = 'PROMO10' THEN ROUND(totals.subtotal * 0.10, 2)
		WHEN co.is_priority = TRUE THEN 0.50
		ELSE 0.00
	END,
	co.address_surcharge_amount = CASE
		WHEN co.customer_id MOD 5 = 0 THEN 1.25
		ELSE 0.00
	END,
	co.total_amount = ROUND(
		totals.subtotal
		+ ROUND(totals.subtotal * 0.12, 2)
		+ CASE WHEN co.customer_id MOD 5 = 0 THEN 1.25 ELSE 0.00 END
		- CASE
			WHEN co.discount_code = 'PROMO10' THEN ROUND(totals.subtotal * 0.10, 2)
			WHEN co.is_priority = TRUE THEN 0.50
			ELSE 0.00
		END,
		2
	);

-- =========================================================
-- 8. Historial de estados
-- =========================================================

INSERT INTO order_status_histories (
	order_id,
	from_status_id,
	to_status_id,
	changed_by_staff_id,
	changed_at,
	notes
)
SELECT
	order_id,
	1,
	1,
	registered_by_staff_id,
	created_at,
	'Pedido creado por administrador.'
FROM customer_orders;

INSERT INTO order_status_histories (
	order_id,
	from_status_id,
	to_status_id,
	changed_by_staff_id,
	changed_at,
	notes
)
SELECT
	order_id,
	1,
	2,
	5 + (order_id MOD 4),
	DATE_ADD(created_at, INTERVAL 8 MINUTE),
	'Pedido tomado por cocina.'
FROM customer_orders
WHERE current_status_id >= 2;

INSERT INTO order_status_histories (
	order_id,
	from_status_id,
	to_status_id,
	changed_by_staff_id,
	changed_at,
	notes
)
SELECT
	order_id,
	2,
	3,
	5 + (order_id MOD 4),
	DATE_ADD(created_at, INTERVAL 24 MINUTE),
	'Pedido listo para despacho.'
FROM customer_orders
WHERE current_status_id >= 3;

INSERT INTO order_status_histories (
	order_id,
	from_status_id,
	to_status_id,
	changed_by_staff_id,
	changed_at,
	notes
)
SELECT
	order_id,
	3,
	4,
	9 + (order_id MOD 4),
	DATE_ADD(created_at, INTERVAL 32 MINUTE),
	'Pedido despachado por repartidor.'
FROM customer_orders
WHERE current_status_id >= 4;

INSERT INTO order_status_histories (
	order_id,
	from_status_id,
	to_status_id,
	changed_by_staff_id,
	changed_at,
	notes
)
SELECT
	order_id,
	4,
	5,
	9 + (order_id MOD 4),
	DATE_ADD(created_at, INTERVAL 52 MINUTE),
	'Entrega confirmada por repartidor.'
FROM customer_orders
WHERE current_status_id >= 5;

UPDATE customer_orders co
	JOIN (
		SELECT
			order_id,
			MAX(changed_at) AS last_changed_at
		FROM order_status_histories
		GROUP BY order_id
	) h ON h.order_id = co.order_id
SET co.current_status_changed_at = h.last_changed_at;

-- =========================================================
-- 9. Entregas
-- Pedidos ON_THE_WAY y DELIVERED tienen Delivery.
-- Solo algunos DELIVERED tienen confirmación del cliente.
-- =========================================================

INSERT INTO deliveries (
	order_id,
	courier_staff_id,
	dispatched_at,
	delivered_at,
	receiver_name,
	customer_confirmed_at,
	customer_confirmation_notes
)
SELECT
	co.order_id,
	9 + (co.order_id MOD 4),
	DATE_ADD(co.created_at, INTERVAL 32 MINUTE),
	CASE
		WHEN co.current_status_id = 5 THEN DATE_ADD(co.created_at, INTERVAL 52 MINUTE)
		ELSE NULL
	END,
	CASE
		WHEN co.current_status_id = 5 THEN c.full_name
		ELSE NULL
	END,
	CASE
		WHEN co.current_status_id = 5 AND co.order_id MOD 2 = 0 THEN DATE_ADD(co.created_at, INTERVAL 60 MINUTE)
		ELSE NULL
	END,
	CASE
		WHEN co.current_status_id = 5 AND co.order_id MOD 2 = 0 THEN 'Cliente confirmó recepción sin novedades.'
		ELSE NULL
	END
FROM customer_orders co
	JOIN customers c ON c.customer_id = co.customer_id
WHERE co.current_status_id >= 4;

-- =========================================================
-- 10. Resumen de validación rápida
-- =========================================================

SELECT 'roles' AS table_name, COUNT(*) AS total_rows FROM roles
UNION ALL SELECT 'staff', COUNT(*) FROM staff
UNION ALL SELECT 'customers', COUNT(*) FROM customers
UNION ALL SELECT 'customer_addresses', COUNT(*) FROM customer_addresses
UNION ALL SELECT 'products', COUNT(*) FROM products
UNION ALL SELECT 'order_statuses', COUNT(*) FROM order_statuses
UNION ALL SELECT 'order_status_transition_rules', COUNT(*) FROM order_status_transition_rules
UNION ALL SELECT 'customer_orders', COUNT(*) FROM customer_orders
UNION ALL SELECT 'order_items', COUNT(*) FROM order_items
UNION ALL SELECT 'order_status_histories', COUNT(*) FROM order_status_histories
UNION ALL SELECT 'deliveries', COUNT(*) FROM deliveries;
