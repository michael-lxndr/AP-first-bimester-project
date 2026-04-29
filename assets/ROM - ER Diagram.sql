-- =========================================================
-- Restaurant Order Management - ER Diagram
-- MySQL 9.3
--
-- Este script acompaña a:
-- assets/ER Diagram.puml
--
-- Regla: las tablas, columnas y relaciones principales deben
-- coincidir con el diagrama ER documentado en PlantUML.
-- =========================================================

-- Si querés recrear la base desde cero, descomentá estas líneas:
-- DROP DATABASE IF EXISTS first_bimester_project;

CREATE DATABASE IF NOT EXISTS first_bimester_project
	CHARACTER SET utf8mb4
	COLLATE utf8mb4_0900_ai_ci;

USE first_bimester_project;

-- =========================================================
-- 1. Role
-- =========================================================

CREATE TABLE roles (
	role_id BIGINT NOT NULL AUTO_INCREMENT,
	role_name VARCHAR(30) NOT NULL,

	CONSTRAINT pk_roles PRIMARY KEY (role_id),
	CONSTRAINT uk_roles_role_name UNIQUE (role_name)
) ENGINE = InnoDB;

-- =========================================================
-- 2. Staff
-- =========================================================

CREATE TABLE staff (
	staff_id BIGINT NOT NULL AUTO_INCREMENT,
	role_id BIGINT NOT NULL,
	full_name VARCHAR(120) NOT NULL,
	phone VARCHAR(20),
	email VARCHAR(120) NOT NULL,
	username VARCHAR(50) NOT NULL,
	is_active BOOLEAN NOT NULL DEFAULT TRUE,
	created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

	CONSTRAINT pk_staff PRIMARY KEY (staff_id),
	CONSTRAINT uk_staff_email UNIQUE (email),
	CONSTRAINT uk_staff_username UNIQUE (username),

	CONSTRAINT fk_staff_role
		FOREIGN KEY (role_id)
		REFERENCES roles (role_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT
) ENGINE = InnoDB;

CREATE INDEX idx_staff_role_id ON staff (role_id);

-- =========================================================
-- 3. Customer
-- =========================================================

CREATE TABLE customers (
	customer_id BIGINT NOT NULL AUTO_INCREMENT,
	full_name VARCHAR(120) NOT NULL,
	phone VARCHAR(20),
	email VARCHAR(120),
	is_active BOOLEAN NOT NULL DEFAULT TRUE,
	created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

	CONSTRAINT pk_customers PRIMARY KEY (customer_id),
	CONSTRAINT uk_customers_email UNIQUE (email)
) ENGINE = InnoDB;

-- =========================================================
-- 4. CustomerAddress
-- =========================================================

CREATE TABLE customer_addresses (
	address_id BIGINT NOT NULL AUTO_INCREMENT,
	customer_id BIGINT NOT NULL,
	alias VARCHAR(50) NOT NULL,
	main_street VARCHAR(150) NOT NULL,
	secondary_street VARCHAR(150),
	house_number VARCHAR(10),
	reference VARCHAR(255),
	postal_code VARCHAR(10),
	city VARCHAR(50) NOT NULL,
	province VARCHAR(50) NOT NULL,
	country VARCHAR(50) NOT NULL DEFAULT 'Ecuador',
	is_primary BOOLEAN NOT NULL DEFAULT FALSE,
	is_active BOOLEAN NOT NULL DEFAULT TRUE,

	CONSTRAINT pk_customer_addresses PRIMARY KEY (address_id),

	CONSTRAINT fk_customer_address_customer
		FOREIGN KEY (customer_id)
		REFERENCES customers (customer_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT
) ENGINE = InnoDB;

CREATE INDEX idx_customer_addresses_customer_id ON customer_addresses (customer_id);
CREATE INDEX idx_customer_addresses_customer_active ON customer_addresses (customer_id, is_active);
CREATE INDEX idx_customer_addresses_customer_primary ON customer_addresses (customer_id, is_primary, is_active);

-- =========================================================
-- 5. Product
-- =========================================================

CREATE TABLE products (
	product_id BIGINT NOT NULL AUTO_INCREMENT,
	product_code VARCHAR(20),
	product_name VARCHAR(120) NOT NULL,
	description VARCHAR(255),
	unit_price DECIMAL(10, 2) NOT NULL,
	production_cost DECIMAL(10, 2) DEFAULT 0.00,
	category VARCHAR(30),
	preparation_time_minutes INT DEFAULT 15,
	is_available BOOLEAN NOT NULL DEFAULT TRUE,
	image_url VARCHAR(255),
	created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),

	CONSTRAINT pk_products PRIMARY KEY (product_id),
	CONSTRAINT uk_products_product_code UNIQUE (product_code),
	CONSTRAINT chk_products_unit_price CHECK (unit_price >= 0),
	CONSTRAINT chk_products_production_cost CHECK (production_cost IS NULL OR production_cost >= 0),
	CONSTRAINT chk_products_preparation_time CHECK (preparation_time_minutes IS NULL OR preparation_time_minutes > 0),
	CONSTRAINT chk_products_category CHECK (
		category IS NULL
			OR category IN ('STARTER', 'MAIN_COURSE', 'DRINK', 'DESSERT', 'COMBO')
	)
) ENGINE = InnoDB;

CREATE INDEX idx_products_category ON products (category);
CREATE INDEX idx_products_is_available ON products (is_available);

-- =========================================================
-- 6. OrderStatus
-- =========================================================

CREATE TABLE order_statuses (
	status_id BIGINT NOT NULL AUTO_INCREMENT,
	status_code VARCHAR(30) NOT NULL,
	status_name VARCHAR(50) NOT NULL,
	status_order INT NOT NULL,
	is_final BOOLEAN NOT NULL DEFAULT FALSE,

	CONSTRAINT pk_order_statuses PRIMARY KEY (status_id),
	CONSTRAINT uk_order_statuses_status_code UNIQUE (status_code),
	CONSTRAINT uk_order_statuses_status_name UNIQUE (status_name),
	CONSTRAINT uk_order_statuses_status_order UNIQUE (status_order),
	CONSTRAINT chk_order_statuses_status_order CHECK (status_order > 0)
) ENGINE = InnoDB;

-- =========================================================
-- 7. OrderStatusTransitionRule
-- =========================================================

CREATE TABLE order_status_transition_rules (
	transition_rule_id BIGINT NOT NULL AUTO_INCREMENT,
	from_status_id BIGINT NOT NULL,
	to_status_id BIGINT NOT NULL,
	role_id BIGINT NOT NULL,
	is_active BOOLEAN NOT NULL DEFAULT TRUE,

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
) ENGINE = InnoDB;

CREATE INDEX idx_rule_from_status_id ON order_status_transition_rules (from_status_id);
CREATE INDEX idx_rule_to_status_id ON order_status_transition_rules (to_status_id);
CREATE INDEX idx_rule_role_id ON order_status_transition_rules (role_id);

-- =========================================================
-- 8. CustomerOrder
-- =========================================================

CREATE TABLE customer_orders (
	order_id BIGINT NOT NULL AUTO_INCREMENT,
	order_code VARCHAR(30) NOT NULL,
	customer_id BIGINT NOT NULL,
	registered_by_staff_id BIGINT NOT NULL,
	current_status_id BIGINT NOT NULL,
	delivery_address_id BIGINT NOT NULL,
	delivery_address_snapshot VARCHAR(255),
	subtotal_amount DECIMAL(10, 2) DEFAULT 0.00,
	tax_amount DECIMAL(10, 2) DEFAULT 0.00,
	discount_amount DECIMAL(10, 2) DEFAULT 0.00,
	address_surcharge_amount DECIMAL(10, 2) DEFAULT 0.00,
	total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
	discount_code VARCHAR(20),
	is_priority BOOLEAN NOT NULL DEFAULT FALSE,
	general_notes VARCHAR(255),
	created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	estimated_delivery_at DATETIME(6),
	current_status_changed_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

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
) ENGINE = InnoDB;

CREATE INDEX idx_customer_orders_customer_id ON customer_orders (customer_id);
CREATE INDEX idx_customer_orders_registered_staff_id ON customer_orders (registered_by_staff_id);
CREATE INDEX idx_customer_orders_current_status_id ON customer_orders (current_status_id);
CREATE INDEX idx_customer_orders_delivery_address_id ON customer_orders (delivery_address_id);
CREATE INDEX idx_customer_orders_created_at ON customer_orders (created_at);
CREATE INDEX idx_customer_orders_estimated_delivery_at ON customer_orders (estimated_delivery_at);

-- =========================================================
-- 9. OrderItem
-- =========================================================

CREATE TABLE order_items (
	order_item_id BIGINT NOT NULL AUTO_INCREMENT,
	order_id BIGINT NOT NULL,
	product_id BIGINT NOT NULL,
	quantity INT NOT NULL,
	product_name_snapshot VARCHAR(120) NOT NULL,
	unit_price DECIMAL(10, 2) NOT NULL,
	line_total DECIMAL(10, 2) NOT NULL,
	special_note VARCHAR(150),
	is_ready BOOLEAN NOT NULL DEFAULT FALSE,

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
) ENGINE = InnoDB;

CREATE INDEX idx_order_items_order_id ON order_items (order_id);
CREATE INDEX idx_order_items_product_id ON order_items (product_id);
CREATE INDEX idx_order_items_is_ready ON order_items (is_ready);

-- =========================================================
-- 10. OrderStatusHistory
-- =========================================================

CREATE TABLE order_status_histories (
	history_id BIGINT NOT NULL AUTO_INCREMENT,
	order_id BIGINT NOT NULL,
	from_status_id BIGINT NOT NULL DEFAULT 1, -- TODO: 1 = PENDING
	to_status_id BIGINT NOT NULL,
	changed_by_staff_id BIGINT NOT NULL,
	changed_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	notes VARCHAR(255),

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
) ENGINE = InnoDB;

CREATE INDEX idx_histories_order_id ON order_status_histories (order_id);
CREATE INDEX idx_histories_from_status_id ON order_status_histories (from_status_id);
CREATE INDEX idx_histories_to_status_id ON order_status_histories (to_status_id);
CREATE INDEX idx_histories_changed_by_staff_id ON order_status_histories (changed_by_staff_id);
CREATE INDEX idx_histories_changed_at ON order_status_histories (changed_at);

-- =========================================================
-- 11. Delivery
-- =========================================================

CREATE TABLE deliveries (
	delivery_id BIGINT NOT NULL AUTO_INCREMENT,
	order_id BIGINT NOT NULL,
	courier_staff_id BIGINT NOT NULL,
	dispatched_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
	delivered_at DATETIME(6) NULL,
	receiver_name VARCHAR(120) NULL,
	customer_confirmed_at DATETIME(6) NULL,
	customer_confirmation_notes VARCHAR(255) NULL,

	CONSTRAINT pk_deliveries PRIMARY KEY (delivery_id),
	CONSTRAINT uk_deliveries_order_id UNIQUE (order_id),

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
) ENGINE = InnoDB;

CREATE INDEX idx_deliveries_courier_staff_id ON deliveries (courier_staff_id);
CREATE INDEX idx_deliveries_dispatched_at ON deliveries (dispatched_at);
CREATE INDEX idx_deliveries_delivered_at ON deliveries (delivered_at);
CREATE INDEX idx_deliveries_customer_confirmed_at ON deliveries (customer_confirmed_at);

-- =========================================================
-- 12. Initial Data
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
	(2, 'IN_PREPARATION', 'En preparación', 2, FALSE),
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
-- 13. Optional Demo Staff
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
	'System Administrator',
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
	'Main Cook',
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
	'Main Courier',
	'0000000002',
	'courier@restaurant.local',
	'courier',
	TRUE
FROM roles
WHERE role_name = 'COURIER';

-- =========================================================
-- 14. Optional Demo Products
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
	('MAIN-001', 'Hamburguesa clásica', 'Hamburguesa con carne, queso y vegetales', 5.50, 2.25, 'MAIN_COURSE', 18, TRUE),
	('MAIN-002', 'Pizza personal', 'Pizza individual de queso y pepperoni', 6.75, 2.80, 'MAIN_COURSE', 20, TRUE),
	('START-001', 'Papas fritas', 'Porción personal de papas fritas', 2.25, 0.80, 'STARTER', 10, TRUE),
	('DRINK-001', 'Gaseosa', 'Bebida gaseosa personal', 1.50, 0.45, 'DRINK', 2, TRUE);
