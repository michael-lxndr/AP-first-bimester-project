-- =========================================================
-- Sistema de Gestion de Pedidos de Restaurante - Diagrama ER
-- MySQL 9.3
--
-- Este script acompaña a:
-- assets/ER Diagram.puml
--
-- Regla: las tablas, columnas y relaciones principales deben
-- coincidir con el diagrama ER documentado en PlantUML.
--
-- Nota de compatibilidad: los codigos tecnicos de roles,
-- estados y categorias se mantienen en ingles porque la
-- aplicacion Java los persiste con EnumType.STRING.
-- =========================================================

-- Si queres recrear la base desde cero, descomenta estas lineas:
-- DROP DATABASE IF EXISTS first_bimester_project;

CREATE DATABASE IF NOT EXISTS first_bimester_project
	CHARACTER SET utf8mb4
	COLLATE utf8mb4_0900_ai_ci;

USE first_bimester_project;

-- =========================================================
-- 1. Roles
-- Descripcion: roles operativos del sistema.
-- =========================================================

CREATE TABLE roles (
	role_id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del rol',
	role_name VARCHAR(30) NOT NULL COMMENT 'Codigo tecnico del rol (ADMINISTRATOR, COOK, COURIER)',

	CONSTRAINT pk_roles PRIMARY KEY (role_id),
	CONSTRAINT uk_roles_role_name UNIQUE (role_name)
) ENGINE = InnoDB
	COMMENT = 'Catalogo de roles del sistema';

-- =========================================================
-- 2. Personal
-- Descripcion: empleados del restaurante con rol asignado.
-- =========================================================

CREATE TABLE staff (
	staff_id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del personal',
	role_id BIGINT NOT NULL COMMENT 'Rol asignado al miembro del personal',
	full_name VARCHAR(120) NOT NULL COMMENT 'Nombre completo del empleado',
	phone VARCHAR(20) COMMENT 'Numero de telefono',
	email VARCHAR(120) NOT NULL COMMENT 'Correo electronico unico',
	username VARCHAR(50) NOT NULL COMMENT 'Nombre de usuario para inicio de sesion',
	is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Indica si el empleado esta activo',
	created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Fecha de creacion del registro',

	CONSTRAINT pk_staff PRIMARY KEY (staff_id),
	CONSTRAINT uk_staff_email UNIQUE (email),
	CONSTRAINT uk_staff_username UNIQUE (username),

	CONSTRAINT fk_staff_role
		FOREIGN KEY (role_id)
		REFERENCES roles (role_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT
) ENGINE = InnoDB
	COMMENT = 'Personal del restaurante: administradores, cocineros y repartidores';

CREATE INDEX idx_staff_role_id ON staff (role_id);
CREATE INDEX idx_staff_is_active ON staff (is_active);

-- =========================================================
-- 3. Clientes
-- Descripcion: clientes registrados en el sistema.
-- =========================================================

CREATE TABLE customers (
	customer_id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del cliente',
	full_name VARCHAR(120) NOT NULL COMMENT 'Nombre completo del cliente',
	phone VARCHAR(20) COMMENT 'Numero de telefono',
	email VARCHAR(120) COMMENT 'Correo electronico',
	is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Indica si el cliente esta activo',
	created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Fecha de registro',

	CONSTRAINT pk_customers PRIMARY KEY (customer_id),
	CONSTRAINT uk_customers_email UNIQUE (email)
) ENGINE = InnoDB
	COMMENT = 'Clientes registrados del restaurante';

-- =========================================================
-- 4. Direcciones de cliente
-- Descripcion: varias direcciones por cliente.
-- =========================================================

CREATE TABLE customer_addresses (
	address_id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de direccion',
	customer_id BIGINT NOT NULL COMMENT 'Cliente propietario de la direccion',
	alias VARCHAR(50) NOT NULL COMMENT 'Alias descriptivo, por ejemplo Casa o Trabajo',
	main_street VARCHAR(150) NOT NULL COMMENT 'Calle principal',
	secondary_street VARCHAR(150) COMMENT 'Calle secundaria o referencia cruzada',
	house_number VARCHAR(10) COMMENT 'Numero de casa o edificio',
	reference VARCHAR(255) COMMENT 'Referencia adicional, por ejemplo frente al parque',
	postal_code VARCHAR(10) COMMENT 'Codigo postal',
	city VARCHAR(50) NOT NULL COMMENT 'Ciudad',
	province VARCHAR(50) NOT NULL COMMENT 'Provincia o estado',
	country VARCHAR(50) NOT NULL DEFAULT 'Ecuador' COMMENT 'Pais',
	is_primary BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Indica si es la direccion principal',
	is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Indica si la direccion esta activa',

	CONSTRAINT pk_customer_addresses PRIMARY KEY (address_id),

	CONSTRAINT fk_customer_address_customer
		FOREIGN KEY (customer_id)
		REFERENCES customers (customer_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT
) ENGINE = InnoDB
	COMMENT = 'Direcciones de entrega de los clientes';

CREATE INDEX idx_customer_addresses_customer_id ON customer_addresses (customer_id);
CREATE INDEX idx_customer_addresses_customer_active ON customer_addresses (customer_id, is_active);
CREATE INDEX idx_customer_addresses_primary ON customer_addresses (customer_id, is_primary);

-- =========================================================
-- 5. Productos
-- Descripcion: catalogo de productos del menu.
-- =========================================================

CREATE TABLE products (
	product_id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del producto',
	product_code VARCHAR(20) COMMENT 'Codigo interno del producto',
	product_name VARCHAR(120) NOT NULL COMMENT 'Nombre del producto',
	description VARCHAR(255) COMMENT 'Descripcion detallada',
	unit_price DECIMAL(10, 2) NOT NULL COMMENT 'Precio de venta unitario',
	production_cost DECIMAL(10, 2) DEFAULT 0.00 COMMENT 'Costo de produccion',
	category VARCHAR(30) COMMENT 'Categoria tecnica (STARTER, MAIN_COURSE, DRINK, DESSERT, COMBO)',
	preparation_time_minutes INT DEFAULT 15 COMMENT 'Tiempo estimado de preparacion en minutos',
	is_available BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Indica si el producto esta disponible',
	image_url VARCHAR(255) COMMENT 'URL de imagen del producto',
	created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Fecha de creacion',

	CONSTRAINT pk_products PRIMARY KEY (product_id),
	CONSTRAINT uk_products_product_code UNIQUE (product_code),
	CONSTRAINT chk_products_unit_price CHECK (unit_price >= 0),
	CONSTRAINT chk_products_production_cost CHECK (production_cost IS NULL OR production_cost >= 0),
	CONSTRAINT chk_products_preparation_time CHECK (preparation_time_minutes IS NULL OR preparation_time_minutes > 0),
	CONSTRAINT chk_products_category CHECK (
		category IS NULL
			OR category IN ('STARTER', 'MAIN_COURSE', 'DRINK', 'DESSERT', 'COMBO')
	)
) ENGINE = InnoDB
	COMMENT = 'Catalogo de productos del menu';

CREATE INDEX idx_products_category ON products (category);
CREATE INDEX idx_products_is_available ON products (is_available);

-- =========================================================
-- 6. Estados de pedido
-- Descripcion: estados posibles del flujo de pedidos.
-- =========================================================

CREATE TABLE order_statuses (
	status_id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del estado',
	status_code VARCHAR(30) NOT NULL COMMENT 'Codigo tecnico del estado (PENDING, IN_PREPARATION, etc.)',
	status_name VARCHAR(50) NOT NULL COMMENT 'Nombre descriptivo del estado en espanol',
	status_order INT NOT NULL COMMENT 'Orden secuencial del estado en el flujo',
	is_final BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Indica si es un estado final',

	CONSTRAINT pk_order_statuses PRIMARY KEY (status_id),
	CONSTRAINT uk_order_statuses_status_code UNIQUE (status_code),
	CONSTRAINT uk_order_statuses_status_name UNIQUE (status_name),
	CONSTRAINT uk_order_statuses_status_order UNIQUE (status_order),
	CONSTRAINT chk_order_statuses_status_order CHECK (status_order > 0)
) ENGINE = InnoDB
	COMMENT = 'Estados posibles del flujo de pedidos';

-- =========================================================
-- 7. Reglas de transicion de estados
-- Descripcion: roles autorizados para cambiar entre estados.
-- =========================================================

CREATE TABLE order_status_transition_rules (
	transition_rule_id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la regla',
	from_status_id BIGINT NOT NULL COMMENT 'Estado de origen',
	to_status_id BIGINT NOT NULL COMMENT 'Estado de destino',
	role_id BIGINT NOT NULL COMMENT 'Rol autorizado para realizar esta transicion',
	is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Indica si la regla esta activa',

	CONSTRAINT pk_order_status_transition_rules PRIMARY KEY (transition_rule_id),

	CONSTRAINT uk_transition_rule UNIQUE (
		from_status_id,
		to_status_id,
		role_id
	),

	CONSTRAINT fk_rule_from_status
		FOREIGN KEY (from_status_id)
		REFERENCES order_statuses (status_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT,

	CONSTRAINT fk_rule_to_status
		FOREIGN KEY (to_status_id)
		REFERENCES order_statuses (status_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT,

	CONSTRAINT fk_rule_role
		FOREIGN KEY (role_id)
		REFERENCES roles (role_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT
) ENGINE = InnoDB
	COMMENT = 'Reglas de transicion entre estados de pedido por rol';

CREATE INDEX idx_rule_from_status_id ON order_status_transition_rules (from_status_id);
CREATE INDEX idx_rule_to_status_id ON order_status_transition_rules (to_status_id);
CREATE INDEX idx_rule_role_id ON order_status_transition_rules (role_id);

-- =========================================================
-- 8. Pedidos de cliente
-- Descripcion: pedidos realizados por clientes.
-- =========================================================

CREATE TABLE customer_orders (
	order_id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del pedido',
	order_code VARCHAR(30) NOT NULL COMMENT 'Codigo unico del pedido, por ejemplo PED-2024-001',
	customer_id BIGINT NOT NULL COMMENT 'Cliente que realiza el pedido',
	registered_by_staff_id BIGINT NOT NULL COMMENT 'Personal que registro el pedido',
	current_status_id BIGINT NOT NULL COMMENT 'Estado actual del pedido',
	delivery_address_id BIGINT NOT NULL COMMENT 'Direccion de entrega seleccionada',
	delivery_address_snapshot VARCHAR(500) NOT NULL COMMENT 'Copia de la direccion al momento del pedido',
	delivery_instructions VARCHAR(500) COMMENT 'Instrucciones especiales de entrega',
	subtotal_amount DECIMAL(10, 2) DEFAULT 0.00 COMMENT 'Subtotal sin impuestos ni descuentos',
	tax_amount DECIMAL(10, 2) DEFAULT 0.00 COMMENT 'Monto de impuestos',
	discount_amount DECIMAL(10, 2) DEFAULT 0.00 COMMENT 'Monto de descuento aplicado',
	address_surcharge_amount DECIMAL(10, 2) DEFAULT 0.00 COMMENT 'Recargo por direccion o zona de entrega',
	total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT 'Monto total del pedido',
	discount_code VARCHAR(20) COMMENT 'Codigo de descuento aplicado',
	is_priority BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Indica si es un pedido prioritario',
	general_notes VARCHAR(255) COMMENT 'Notas generales del pedido',
	created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Fecha y hora de creacion',
	estimated_delivery_at DATETIME(6) COMMENT 'Fecha y hora estimada de entrega',
	current_status_changed_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Ultimo cambio de estado',

	CONSTRAINT pk_customer_orders PRIMARY KEY (order_id),
	CONSTRAINT uk_customer_orders_order_code UNIQUE (order_code),
	CONSTRAINT chk_customer_orders_subtotal_amount CHECK (subtotal_amount IS NULL OR subtotal_amount >= 0),
	CONSTRAINT chk_customer_orders_tax_amount CHECK (tax_amount IS NULL OR tax_amount >= 0),
	CONSTRAINT chk_customer_orders_discount_amount CHECK (discount_amount IS NULL OR discount_amount >= 0),
	CONSTRAINT chk_customer_orders_address_surcharge CHECK (address_surcharge_amount IS NULL OR address_surcharge_amount >= 0),
	CONSTRAINT chk_customer_orders_total_amount CHECK (total_amount >= 0),

	CONSTRAINT fk_order_customer
		FOREIGN KEY (customer_id)
		REFERENCES customers (customer_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT,

	CONSTRAINT fk_order_registered_staff
		FOREIGN KEY (registered_by_staff_id)
		REFERENCES staff (staff_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT,

	CONSTRAINT fk_order_current_status
		FOREIGN KEY (current_status_id)
		REFERENCES order_statuses (status_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT,

	CONSTRAINT fk_order_delivery_address
		FOREIGN KEY (delivery_address_id)
		REFERENCES customer_addresses (address_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT
) ENGINE = InnoDB
	COMMENT = 'Pedidos realizados por los clientes';

CREATE INDEX idx_customer_orders_customer_id ON customer_orders (customer_id);
CREATE INDEX idx_customer_orders_registered_staff_id ON customer_orders (registered_by_staff_id);
CREATE INDEX idx_customer_orders_current_status_id ON customer_orders (current_status_id);
CREATE INDEX idx_customer_orders_delivery_address_id ON customer_orders (delivery_address_id);
CREATE INDEX idx_customer_orders_created_at ON customer_orders (created_at);
CREATE INDEX idx_customer_orders_estimated_delivery_at ON customer_orders (estimated_delivery_at);
CREATE INDEX idx_customer_orders_current_status_changed_at ON customer_orders (current_status_changed_at);

-- =========================================================
-- 9. Items de pedido
-- Descripcion: productos individuales dentro de un pedido.
-- =========================================================

CREATE TABLE order_items (
	order_item_id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del item',
	order_id BIGINT NOT NULL COMMENT 'Pedido al que pertenece',
	product_id BIGINT NOT NULL COMMENT 'Producto solicitado',
	quantity INT NOT NULL COMMENT 'Cantidad solicitada',
	product_name_snapshot VARCHAR(120) NOT NULL COMMENT 'Nombre del producto al momento del pedido',
	unit_price DECIMAL(10, 2) NOT NULL COMMENT 'Precio unitario al momento del pedido',
	line_total DECIMAL(10, 2) NOT NULL COMMENT 'Total del item, cantidad por precio',
	special_note VARCHAR(150) COMMENT 'Nota especial del cliente para este item',
	is_ready BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Indica si el item esta listo',

	CONSTRAINT pk_order_items PRIMARY KEY (order_item_id),

	CONSTRAINT chk_order_items_quantity CHECK (quantity > 0),
	CONSTRAINT chk_order_items_unit_price CHECK (unit_price >= 0),
	CONSTRAINT chk_order_items_line_total CHECK (line_total >= 0),

	CONSTRAINT fk_order_item_order
		FOREIGN KEY (order_id)
		REFERENCES customer_orders (order_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT,

	CONSTRAINT fk_order_item_product
		FOREIGN KEY (product_id)
		REFERENCES products (product_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT
) ENGINE = InnoDB
	COMMENT = 'Detalles de productos en cada pedido';

CREATE INDEX idx_order_items_order_id ON order_items (order_id);
CREATE INDEX idx_order_items_product_id ON order_items (product_id);
CREATE INDEX idx_order_items_is_ready ON order_items (is_ready);

-- =========================================================
-- 10. Historial de estados
-- Descripcion: auditoria de cambios de estado de pedidos.
-- =========================================================

CREATE TABLE order_status_histories (
	history_id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del historial',
	order_id BIGINT NOT NULL COMMENT 'Pedido afectado',
	from_status_id BIGINT NOT NULL DEFAULT 1 COMMENT 'Estado anterior; por defecto PENDING',
	to_status_id BIGINT NOT NULL COMMENT 'Estado nuevo',
	changed_by_staff_id BIGINT NOT NULL COMMENT 'Personal que realizo el cambio',
	changed_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Fecha y hora del cambio',
	notes VARCHAR(255) COMMENT 'Notas adicionales del cambio',

	CONSTRAINT pk_order_status_histories PRIMARY KEY (history_id),

	CONSTRAINT fk_history_order
		FOREIGN KEY (order_id)
		REFERENCES customer_orders (order_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT,

	CONSTRAINT fk_history_from_status
		FOREIGN KEY (from_status_id)
		REFERENCES order_statuses (status_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT,

	CONSTRAINT fk_history_to_status
		FOREIGN KEY (to_status_id)
		REFERENCES order_statuses (status_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT,

	CONSTRAINT fk_history_changed_by_staff
		FOREIGN KEY (changed_by_staff_id)
		REFERENCES staff (staff_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT
) ENGINE = InnoDB
	COMMENT = 'Historial de auditoria de cambios de estado';

CREATE INDEX idx_histories_order_id ON order_status_histories (order_id);
CREATE INDEX idx_histories_from_status_id ON order_status_histories (from_status_id);
CREATE INDEX idx_histories_to_status_id ON order_status_histories (to_status_id);
CREATE INDEX idx_histories_changed_by_staff_id ON order_status_histories (changed_by_staff_id);
CREATE INDEX idx_histories_changed_at ON order_status_histories (changed_at);

-- =========================================================
-- 11. Entregas
-- Descripcion: informacion logistica de entregas a domicilio.
-- =========================================================

CREATE TABLE deliveries (
	delivery_id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de entrega',
	order_id BIGINT NOT NULL COMMENT 'Pedido asociado',
	courier_staff_id BIGINT NOT NULL COMMENT 'Repartidor asignado',
	dispatched_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Fecha y hora de despacho',
	delivered_at DATETIME(6) NULL COMMENT 'Fecha y hora de entrega completada',
	receiver_name VARCHAR(120) NULL COMMENT 'Nombre de quien recibio el pedido',
	customer_confirmed_at DATETIME(6) NULL COMMENT 'Fecha de confirmacion del cliente',
	customer_confirmation_notes VARCHAR(255) NULL COMMENT 'Notas de confirmacion del cliente',
	delivery_status VARCHAR(30) NOT NULL DEFAULT 'DISPATCHED' COMMENT 'Estado tecnico de la entrega: DISPATCHED, IN_TRANSIT, DELIVERED, FAILED, RETURNED',
	courier_notes VARCHAR(500) COMMENT 'Notas del repartidor sobre la entrega',
	attempt_number INT NOT NULL DEFAULT 1 COMMENT 'Numero de intento de entrega',

	CONSTRAINT pk_deliveries PRIMARY KEY (delivery_id),
	CONSTRAINT uk_deliveries_order_id UNIQUE (order_id),
	CONSTRAINT chk_delivery_status CHECK (delivery_status IN ('DISPATCHED', 'IN_TRANSIT', 'DELIVERED', 'FAILED', 'RETURNED')),

	CONSTRAINT chk_deliveries_delivered_after_dispatched
		CHECK (delivered_at IS NULL OR delivered_at >= dispatched_at),

	CONSTRAINT chk_deliveries_receiver_when_delivered
		CHECK (delivered_at IS NULL OR receiver_name IS NOT NULL),

	CONSTRAINT chk_deliveries_customer_confirm_after_delivery
		CHECK (
			customer_confirmed_at IS NULL
				OR (
				delivered_at IS NOT NULL
					AND customer_confirmed_at >= delivered_at
				)
			),

	CONSTRAINT fk_delivery_order
		FOREIGN KEY (order_id)
		REFERENCES customer_orders (order_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT,

	CONSTRAINT fk_delivery_courier_staff
		FOREIGN KEY (courier_staff_id)
		REFERENCES staff (staff_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT
) ENGINE = InnoDB
	COMMENT = 'Informacion logistica de entregas a domicilio';

CREATE INDEX idx_deliveries_courier_staff_id ON deliveries (courier_staff_id);
CREATE INDEX idx_deliveries_dispatched_at ON deliveries (dispatched_at);
CREATE INDEX idx_deliveries_delivered_at ON deliveries (delivered_at);
CREATE INDEX idx_deliveries_customer_confirmed_at ON deliveries (customer_confirmed_at);
CREATE INDEX idx_deliveries_delivery_status ON deliveries (delivery_status);

-- =========================================================
-- 12. Datos iniciales
-- =========================================================

INSERT INTO roles (role_name) VALUES
	('ADMINISTRATOR'),
	('COOK'),
	('COURIER');

-- status_id = 1 se reserva para PENDING porque
-- order_status_histories.from_status_id lo usa como default.
INSERT INTO order_statuses (
	status_id,
	status_code,
	status_name,
	status_order,
	is_final
) VALUES
	(1, 'PENDING', 'Pendiente', 1, FALSE),
	(2, 'IN_PREPARATION', 'En preparacion', 2, FALSE),
	(3, 'READY', 'Listo', 3, FALSE),
	(4, 'ON_THE_WAY', 'En camino', 4, FALSE),
	(5, 'DELIVERED', 'Entregado', 5, TRUE);

INSERT INTO order_status_transition_rules (
	from_status_id,
	to_status_id,
	role_id,
	is_active
)
SELECT
	from_status.status_id,
	to_status.status_id,
	role.role_id,
	TRUE
FROM order_statuses from_status
	JOIN order_statuses to_status
	JOIN roles role
WHERE from_status.status_code = 'PENDING'
	AND to_status.status_code = 'IN_PREPARATION'
	AND role.role_name = 'COOK';

INSERT INTO order_status_transition_rules (
	from_status_id,
	to_status_id,
	role_id,
	is_active
)
SELECT
	from_status.status_id,
	to_status.status_id,
	role.role_id,
	TRUE
FROM order_statuses from_status
	JOIN order_statuses to_status
	JOIN roles role
WHERE from_status.status_code = 'IN_PREPARATION'
	AND to_status.status_code = 'READY'
	AND role.role_name = 'COOK';

INSERT INTO order_status_transition_rules (
	from_status_id,
	to_status_id,
	role_id,
	is_active
)
SELECT
	from_status.status_id,
	to_status.status_id,
	role.role_id,
	TRUE
FROM order_statuses from_status
	JOIN order_statuses to_status
	JOIN roles role
WHERE from_status.status_code = 'READY'
	AND to_status.status_code = 'ON_THE_WAY'
	AND role.role_name = 'COURIER';

INSERT INTO order_status_transition_rules (
	from_status_id,
	to_status_id,
	role_id,
	is_active
)
SELECT
	from_status.status_id,
	to_status.status_id,
	role.role_id,
	TRUE
FROM order_statuses from_status
	JOIN order_statuses to_status
	JOIN roles role
WHERE from_status.status_code = 'ON_THE_WAY'
	AND to_status.status_code = 'DELIVERED'
	AND role.role_name = 'COURIER';

-- =========================================================
-- 13. Personal demo opcional
-- =========================================================

INSERT INTO staff (
	role_id,
	full_name,
	phone,
	email,
	username,
	is_active
)
SELECT
	role_id,
	'Administrador del sistema',
	'0000000000',
	'admin@restaurant.local',
	'admin',
	TRUE
FROM roles
WHERE role_name = 'ADMINISTRATOR';

INSERT INTO staff (
	role_id,
	full_name,
	phone,
	email,
	username,
	is_active
)
SELECT
	role_id,
	'Cocinero principal',
	'0000000001',
	'cook@restaurant.local',
	'cook',
	TRUE
FROM roles
WHERE role_name = 'COOK';

INSERT INTO staff (
	role_id,
	full_name,
	phone,
	email,
	username,
	is_active
)
SELECT
	role_id,
	'Repartidor principal',
	'0000000002',
	'courier@restaurant.local',
	'courier',
	TRUE
FROM roles
WHERE role_name = 'COURIER';

-- =========================================================
-- 14. Productos demo opcionales
-- =========================================================

INSERT INTO products (
	product_code,
	product_name,
	description,
	unit_price,
	production_cost,
	category,
	preparation_time_minutes,
	is_available
) VALUES
	('MAIN-001', 'Hamburguesa clasica', 'Hamburguesa con carne, queso y vegetales', 5.50, 2.25, 'MAIN_COURSE', 18, TRUE),
	('MAIN-002', 'Pizza personal', 'Pizza individual de queso y pepperoni', 6.75, 2.80, 'MAIN_COURSE', 20, TRUE),
	('START-001', 'Papas fritas', 'Porcion personal de papas fritas', 2.25, 0.80, 'STARTER', 10, TRUE),
	('DRINK-001', 'Gaseosa', 'Bebida gaseosa personal', 1.50, 0.45, 'DRINK', 2, TRUE);
