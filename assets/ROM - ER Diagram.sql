-- =========================================================
-- Sistema de Gestion de Pedidos de Restaurante - Diagrama ER
-- MySQL 9.3
--
-- Este script acompana a:
-- assets/ER Diagram.puml
--
-- Regla: las tablas, columnas y relaciones principales deben
-- coincidir con el diagrama ER documentado en PlantUML.
-- =========================================================

-- Si queres recrear la base desde cero, descomenta estas lineas:
-- DROP DATABASE IF EXISTS proyecto_primer_bimestre;

CREATE DATABASE IF NOT EXISTS proyecto_primer_bimestre
	CHARACTER SET utf8mb4
	COLLATE utf8mb4_0900_ai_ci;

USE proyecto_primer_bimestre;

-- =========================================================
-- 1. Roles
-- =========================================================

CREATE TABLE roles (
	rol_id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del rol',
	codigo_rol VARCHAR(30) NOT NULL COMMENT 'Codigo del rol (ADMINISTRADOR, COCINERO, REPARTIDOR)',

	CONSTRAINT pk_roles PRIMARY KEY (rol_id),
	CONSTRAINT uk_roles_codigo_rol UNIQUE (codigo_rol)
) ENGINE = InnoDB
	COMMENT = 'Catalogo de roles del sistema';

-- =========================================================
-- 2. Personal
-- =========================================================

CREATE TABLE personal (
	personal_id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del personal',
	rol_id BIGINT NOT NULL COMMENT 'Rol asignado al miembro del personal',
	nombre_completo VARCHAR(120) NOT NULL COMMENT 'Nombre completo del empleado',
	telefono VARCHAR(20) COMMENT 'Numero de telefono',
	correo_electronico VARCHAR(120) NOT NULL COMMENT 'Correo electronico unico',
	nombre_usuario VARCHAR(50) NOT NULL COMMENT 'Nombre de usuario para inicio de sesion',
	activo BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Indica si el empleado esta activo',
	creado_en DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Fecha de creacion del registro',

	CONSTRAINT pk_personal PRIMARY KEY (personal_id),
	CONSTRAINT uk_personal_correo_electronico UNIQUE (correo_electronico),
	CONSTRAINT uk_personal_nombre_usuario UNIQUE (nombre_usuario),
	CONSTRAINT fk_personal_rol FOREIGN KEY (rol_id)
		REFERENCES roles (rol_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT
) ENGINE = InnoDB
	COMMENT = 'Personal del restaurante: administradores, cocineros y repartidores';

CREATE INDEX idx_personal_rol_id ON personal (rol_id);
CREATE INDEX idx_personal_activo ON personal (activo);

-- =========================================================
-- 3. Clientes
-- =========================================================

CREATE TABLE clientes (
	cliente_id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del cliente',
	nombre_completo VARCHAR(120) NOT NULL COMMENT 'Nombre completo del cliente',
	telefono VARCHAR(20) COMMENT 'Numero de telefono',
	correo_electronico VARCHAR(120) COMMENT 'Correo electronico',
	activo BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Indica si el cliente esta activo',
	creado_en DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Fecha de registro',

	CONSTRAINT pk_clientes PRIMARY KEY (cliente_id),
	CONSTRAINT uk_clientes_correo_electronico UNIQUE (correo_electronico)
) ENGINE = InnoDB
	COMMENT = 'Clientes registrados del restaurante';

-- =========================================================
-- 4. Direcciones de cliente
-- =========================================================

CREATE TABLE direcciones_cliente (
	direccion_id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de direccion',
	cliente_id BIGINT NOT NULL COMMENT 'Cliente propietario de la direccion',
	alias VARCHAR(50) NOT NULL COMMENT 'Alias descriptivo, por ejemplo Casa o Trabajo',
	calle_principal VARCHAR(150) NOT NULL COMMENT 'Calle principal',
	calle_secundaria VARCHAR(150) COMMENT 'Calle secundaria o referencia cruzada',
	numero_casa VARCHAR(10) COMMENT 'Numero de casa o edificio',
	referencia VARCHAR(255) COMMENT 'Referencia adicional, por ejemplo frente al parque',
	codigo_postal VARCHAR(10) COMMENT 'Codigo postal',
	ciudad VARCHAR(50) NOT NULL COMMENT 'Ciudad',
	provincia VARCHAR(50) NOT NULL COMMENT 'Provincia o estado',
	pais VARCHAR(50) NOT NULL DEFAULT 'Ecuador' COMMENT 'Pais',
	principal BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Indica si es la direccion principal',
	activa BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Indica si la direccion esta activa',

	CONSTRAINT pk_direcciones_cliente PRIMARY KEY (direccion_id),
	CONSTRAINT fk_direccion_cliente_cliente FOREIGN KEY (cliente_id)
		REFERENCES clientes (cliente_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT
) ENGINE = InnoDB
	COMMENT = 'Direcciones de entrega de los clientes';

CREATE INDEX idx_direcciones_cliente_cliente_id ON direcciones_cliente (cliente_id);
CREATE INDEX idx_direcciones_cliente_cliente_activa ON direcciones_cliente (cliente_id, activa);
CREATE INDEX idx_direcciones_cliente_principal ON direcciones_cliente (cliente_id, principal);

-- =========================================================
-- 5. Productos
-- =========================================================

CREATE TABLE productos (
	producto_id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del producto',
	codigo_producto VARCHAR(20) COMMENT 'Codigo interno del producto',
	nombre_producto VARCHAR(120) NOT NULL COMMENT 'Nombre del producto',
	descripcion VARCHAR(255) COMMENT 'Descripcion detallada',
	precio_unitario DECIMAL(10, 2) NOT NULL COMMENT 'Precio de venta unitario',
	costo_produccion DECIMAL(10, 2) DEFAULT 0.00 COMMENT 'Costo de produccion',
	categoria VARCHAR(30) COMMENT 'Categoria (ENTRADA, PLATO_FUERTE, BEBIDA, POSTRE, COMBO)',
	tiempo_preparacion_minutos INT DEFAULT 15 COMMENT 'Tiempo estimado de preparacion en minutos',
	disponible BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Indica si el producto esta disponible',
	url_imagen VARCHAR(255) COMMENT 'URL de imagen del producto',
	creado_en DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Fecha de creacion',

	CONSTRAINT pk_productos PRIMARY KEY (producto_id),
	CONSTRAINT uk_productos_codigo_producto UNIQUE (codigo_producto),
	CONSTRAINT chk_productos_precio_unitario CHECK (precio_unitario >= 0),
	CONSTRAINT chk_productos_costo_produccion CHECK (costo_produccion IS NULL OR costo_produccion >= 0),
	CONSTRAINT chk_productos_tiempo_preparacion CHECK (tiempo_preparacion_minutos IS NULL OR tiempo_preparacion_minutos > 0),
	CONSTRAINT chk_productos_categoria CHECK (
		categoria IS NULL
			OR categoria IN ('ENTRADA', 'PLATO_FUERTE', 'BEBIDA', 'POSTRE', 'COMBO')
	)
) ENGINE = InnoDB
	COMMENT = 'Catalogo de productos del menu';

CREATE INDEX idx_productos_categoria ON productos (categoria);
CREATE INDEX idx_productos_disponible ON productos (disponible);

-- =========================================================
-- 6. Estados de pedido
-- =========================================================

CREATE TABLE estados_pedido (
	estado_id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del estado',
	codigo_estado VARCHAR(30) NOT NULL COMMENT 'Codigo del estado (PENDIENTE, EN_PREPARACION, etc.)',
	nombre_estado VARCHAR(50) NOT NULL COMMENT 'Nombre descriptivo del estado',
	orden_estado INT NOT NULL COMMENT 'Orden secuencial del estado en el flujo',
	finalizado BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Indica si es un estado final',

	CONSTRAINT pk_estados_pedido PRIMARY KEY (estado_id),
	CONSTRAINT uk_estados_pedido_codigo_estado UNIQUE (codigo_estado),
	CONSTRAINT uk_estados_pedido_nombre_estado UNIQUE (nombre_estado),
	CONSTRAINT uk_estados_pedido_orden_estado UNIQUE (orden_estado),
	CONSTRAINT chk_estados_pedido_orden_estado CHECK (orden_estado > 0)
) ENGINE = InnoDB
	COMMENT = 'Estados posibles del flujo de pedidos';

-- =========================================================
-- 7. Reglas de transicion de estados
-- =========================================================

CREATE TABLE reglas_transicion_estado_pedido (
	regla_transicion_id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de la regla',
	estado_origen_id BIGINT NOT NULL COMMENT 'Estado de origen',
	estado_destino_id BIGINT NOT NULL COMMENT 'Estado de destino',
	rol_id BIGINT NOT NULL COMMENT 'Rol autorizado para realizar esta transicion',
	activa BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Indica si la regla esta activa',

	CONSTRAINT pk_reglas_transicion_estado_pedido PRIMARY KEY (regla_transicion_id),
	CONSTRAINT uk_regla_transicion UNIQUE (estado_origen_id, estado_destino_id, rol_id),
	CONSTRAINT fk_regla_estado_origen FOREIGN KEY (estado_origen_id)
		REFERENCES estados_pedido (estado_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT,
	CONSTRAINT fk_regla_estado_destino FOREIGN KEY (estado_destino_id)
		REFERENCES estados_pedido (estado_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT,
	CONSTRAINT fk_regla_rol FOREIGN KEY (rol_id)
		REFERENCES roles (rol_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT
) ENGINE = InnoDB
	COMMENT = 'Reglas de transicion entre estados de pedido por rol';

CREATE INDEX idx_reglas_estado_origen_id ON reglas_transicion_estado_pedido (estado_origen_id);
CREATE INDEX idx_reglas_estado_destino_id ON reglas_transicion_estado_pedido (estado_destino_id);
CREATE INDEX idx_reglas_rol_id ON reglas_transicion_estado_pedido (rol_id);

-- =========================================================
-- 8. Pedidos de cliente
-- =========================================================

CREATE TABLE pedidos_cliente (
	pedido_id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del pedido',
	codigo_pedido VARCHAR(30) NOT NULL COMMENT 'Codigo unico del pedido, por ejemplo PED-2024-001',
	cliente_id BIGINT NOT NULL COMMENT 'Cliente que realiza el pedido',
	registrado_por_personal_id BIGINT NOT NULL COMMENT 'Personal que registro el pedido',
	estado_actual_id BIGINT NOT NULL COMMENT 'Estado actual del pedido',
	direccion_entrega_id BIGINT NOT NULL COMMENT 'Direccion de entrega seleccionada',
	snapshot_direccion_entrega VARCHAR(500) NOT NULL COMMENT 'Copia de la direccion al momento del pedido',
	instrucciones_entrega VARCHAR(500) COMMENT 'Instrucciones especiales de entrega',
	subtotal DECIMAL(10, 2) DEFAULT 0.00 COMMENT 'Subtotal sin impuestos ni descuentos',
	impuesto DECIMAL(10, 2) DEFAULT 0.00 COMMENT 'Monto de impuestos',
	descuento DECIMAL(10, 2) DEFAULT 0.00 COMMENT 'Monto de descuento aplicado',
	recargo_direccion DECIMAL(10, 2) DEFAULT 0.00 COMMENT 'Recargo por direccion o zona de entrega',
	total DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT 'Monto total del pedido',
	codigo_descuento VARCHAR(20) COMMENT 'Codigo de descuento aplicado',
	prioritario BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Indica si es un pedido prioritario',
	notas_generales VARCHAR(255) COMMENT 'Notas generales del pedido',
	creado_en DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Fecha y hora de creacion',
	entrega_estimada_en DATETIME(6) COMMENT 'Fecha y hora estimada de entrega',
	estado_actual_cambiado_en DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Ultimo cambio de estado',

	CONSTRAINT pk_pedidos_cliente PRIMARY KEY (pedido_id),
	CONSTRAINT uk_pedidos_cliente_codigo_pedido UNIQUE (codigo_pedido),
	CONSTRAINT chk_pedidos_cliente_subtotal CHECK (subtotal IS NULL OR subtotal >= 0),
	CONSTRAINT chk_pedidos_cliente_impuesto CHECK (impuesto IS NULL OR impuesto >= 0),
	CONSTRAINT chk_pedidos_cliente_descuento CHECK (descuento IS NULL OR descuento >= 0),
	CONSTRAINT chk_pedidos_cliente_recargo_direccion CHECK (recargo_direccion IS NULL OR recargo_direccion >= 0),
	CONSTRAINT chk_pedidos_cliente_total CHECK (total >= 0),
	CONSTRAINT fk_pedido_cliente FOREIGN KEY (cliente_id)
		REFERENCES clientes (cliente_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT,
	CONSTRAINT fk_pedido_personal_registro FOREIGN KEY (registrado_por_personal_id)
		REFERENCES personal (personal_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT,
	CONSTRAINT fk_pedido_estado_actual FOREIGN KEY (estado_actual_id)
		REFERENCES estados_pedido (estado_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT,
	CONSTRAINT fk_pedido_direccion_entrega FOREIGN KEY (direccion_entrega_id)
		REFERENCES direcciones_cliente (direccion_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT
) ENGINE = InnoDB
	COMMENT = 'Pedidos realizados por los clientes';

CREATE INDEX idx_pedidos_cliente_cliente_id ON pedidos_cliente (cliente_id);
CREATE INDEX idx_pedidos_cliente_personal_registro_id ON pedidos_cliente (registrado_por_personal_id);
CREATE INDEX idx_pedidos_cliente_estado_actual_id ON pedidos_cliente (estado_actual_id);
CREATE INDEX idx_pedidos_cliente_direccion_entrega_id ON pedidos_cliente (direccion_entrega_id);
CREATE INDEX idx_pedidos_cliente_creado_en ON pedidos_cliente (creado_en);
CREATE INDEX idx_pedidos_cliente_entrega_estimada_en ON pedidos_cliente (entrega_estimada_en);
CREATE INDEX idx_pedidos_cliente_estado_actual_cambiado_en ON pedidos_cliente (estado_actual_cambiado_en);

-- =========================================================
-- 9. Items de pedido
-- =========================================================

CREATE TABLE items_pedido (
	item_pedido_id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del item',
	pedido_id BIGINT NOT NULL COMMENT 'Pedido al que pertenece',
	producto_id BIGINT NOT NULL COMMENT 'Producto solicitado',
	cantidad INT NOT NULL COMMENT 'Cantidad solicitada',
	snapshot_nombre_producto VARCHAR(120) NOT NULL COMMENT 'Nombre del producto al momento del pedido',
	precio_unitario DECIMAL(10, 2) NOT NULL COMMENT 'Precio unitario al momento del pedido',
	total_linea DECIMAL(10, 2) NOT NULL COMMENT 'Total del item, cantidad por precio',
	nota_especial VARCHAR(150) COMMENT 'Nota especial del cliente para este item',
	listo BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Indica si el item esta listo',

	CONSTRAINT pk_items_pedido PRIMARY KEY (item_pedido_id),
	CONSTRAINT chk_items_pedido_cantidad CHECK (cantidad > 0),
	CONSTRAINT chk_items_pedido_precio_unitario CHECK (precio_unitario >= 0),
	CONSTRAINT chk_items_pedido_total_linea CHECK (total_linea >= 0),
	CONSTRAINT fk_item_pedido_pedido FOREIGN KEY (pedido_id)
		REFERENCES pedidos_cliente (pedido_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT,
	CONSTRAINT fk_item_pedido_producto FOREIGN KEY (producto_id)
		REFERENCES productos (producto_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT
) ENGINE = InnoDB
	COMMENT = 'Detalles de productos en cada pedido';

CREATE INDEX idx_items_pedido_pedido_id ON items_pedido (pedido_id);
CREATE INDEX idx_items_pedido_producto_id ON items_pedido (producto_id);
CREATE INDEX idx_items_pedido_listo ON items_pedido (listo);

-- =========================================================
-- 10. Historial de estados
-- =========================================================

CREATE TABLE historial_estados_pedido (
	historial_id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico del historial',
	pedido_id BIGINT NOT NULL COMMENT 'Pedido afectado',
	estado_origen_id BIGINT NOT NULL DEFAULT 1 COMMENT 'Estado anterior; por defecto PENDIENTE',
	estado_destino_id BIGINT NOT NULL COMMENT 'Estado nuevo',
	cambiado_por_personal_id BIGINT NOT NULL COMMENT 'Personal que realizo el cambio',
	cambiado_en DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Fecha y hora del cambio',
	notas VARCHAR(255) COMMENT 'Notas adicionales del cambio',

	CONSTRAINT pk_historial_estados_pedido PRIMARY KEY (historial_id),
	CONSTRAINT fk_historial_pedido FOREIGN KEY (pedido_id)
		REFERENCES pedidos_cliente (pedido_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT,
	CONSTRAINT fk_historial_estado_origen FOREIGN KEY (estado_origen_id)
		REFERENCES estados_pedido (estado_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT,
	CONSTRAINT fk_historial_estado_destino FOREIGN KEY (estado_destino_id)
		REFERENCES estados_pedido (estado_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT,
	CONSTRAINT fk_historial_personal_cambio FOREIGN KEY (cambiado_por_personal_id)
		REFERENCES personal (personal_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT
) ENGINE = InnoDB
	COMMENT = 'Historial de auditoria de cambios de estado';

CREATE INDEX idx_historial_pedido_id ON historial_estados_pedido (pedido_id);
CREATE INDEX idx_historial_estado_origen_id ON historial_estados_pedido (estado_origen_id);
CREATE INDEX idx_historial_estado_destino_id ON historial_estados_pedido (estado_destino_id);
CREATE INDEX idx_historial_personal_cambio_id ON historial_estados_pedido (cambiado_por_personal_id);
CREATE INDEX idx_historial_cambiado_en ON historial_estados_pedido (cambiado_en);

-- =========================================================
-- 11. Entregas
-- =========================================================

CREATE TABLE entregas (
	entrega_id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico de entrega',
	pedido_id BIGINT NOT NULL COMMENT 'Pedido asociado',
	repartidor_personal_id BIGINT NOT NULL COMMENT 'Repartidor asignado',
	despachado_en DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Fecha y hora de despacho',
	entregado_en DATETIME(6) NULL COMMENT 'Fecha y hora de entrega completada',
	nombre_receptor VARCHAR(120) NULL COMMENT 'Nombre de quien recibio el pedido',
	confirmado_por_cliente_en DATETIME(6) NULL COMMENT 'Fecha de confirmacion del cliente',
	notas_confirmacion_cliente VARCHAR(255) NULL COMMENT 'Notas de confirmacion del cliente',
	estado_entrega VARCHAR(30) NOT NULL DEFAULT 'DESPACHADO' COMMENT 'Estado de la entrega',
	notas_repartidor VARCHAR(500) COMMENT 'Notas del repartidor sobre la entrega',
	numero_intento INT NOT NULL DEFAULT 1 COMMENT 'Numero de intento de entrega',

	CONSTRAINT pk_entregas PRIMARY KEY (entrega_id),
	CONSTRAINT uk_entregas_pedido_id UNIQUE (pedido_id),
	CONSTRAINT chk_entregas_estado CHECK (estado_entrega IN ('DESPACHADO', 'EN_TRANSITO', 'ENTREGADO', 'FALLIDO', 'DEVUELTO')),
	CONSTRAINT chk_entregas_entregado_despues_despacho CHECK (entregado_en IS NULL OR entregado_en >= despachado_en),
	CONSTRAINT chk_entregas_receptor_si_entregado CHECK (entregado_en IS NULL OR nombre_receptor IS NOT NULL),
	CONSTRAINT chk_entregas_confirmacion_despues_entrega CHECK (
		confirmado_por_cliente_en IS NULL
			OR (entregado_en IS NOT NULL AND confirmado_por_cliente_en >= entregado_en)
	),
	CONSTRAINT fk_entrega_pedido FOREIGN KEY (pedido_id)
		REFERENCES pedidos_cliente (pedido_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT,
	CONSTRAINT fk_entrega_repartidor FOREIGN KEY (repartidor_personal_id)
		REFERENCES personal (personal_id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT
) ENGINE = InnoDB
	COMMENT = 'Informacion logistica de entregas a domicilio';

CREATE INDEX idx_entregas_repartidor_personal_id ON entregas (repartidor_personal_id);
CREATE INDEX idx_entregas_despachado_en ON entregas (despachado_en);
CREATE INDEX idx_entregas_entregado_en ON entregas (entregado_en);
CREATE INDEX idx_entregas_confirmado_por_cliente_en ON entregas (confirmado_por_cliente_en);
CREATE INDEX idx_entregas_estado_entrega ON entregas (estado_entrega);

-- =========================================================
-- 12. Datos iniciales
-- =========================================================

INSERT INTO roles (codigo_rol) VALUES
	('ADMINISTRADOR'),
	('COCINERO'),
	('REPARTIDOR');

-- estado_id = 1 se reserva para PENDIENTE porque
-- historial_estados_pedido.estado_origen_id lo usa como default.
INSERT INTO estados_pedido (estado_id, codigo_estado, nombre_estado, orden_estado, finalizado) VALUES
	(1, 'PENDIENTE', 'Pendiente', 1, FALSE),
	(2, 'EN_PREPARACION', 'En preparacion', 2, FALSE),
	(3, 'LISTO', 'Listo', 3, FALSE),
	(4, 'EN_CAMINO', 'En camino', 4, FALSE),
	(5, 'ENTREGADO', 'Entregado', 5, TRUE);

INSERT INTO reglas_transicion_estado_pedido (estado_origen_id, estado_destino_id, rol_id, activa)
SELECT origen.estado_id, destino.estado_id, rol.rol_id, TRUE
FROM estados_pedido origen
	JOIN estados_pedido destino
	JOIN roles rol
WHERE origen.codigo_estado = 'PENDIENTE'
	AND destino.codigo_estado = 'EN_PREPARACION'
	AND rol.codigo_rol = 'COCINERO';

INSERT INTO reglas_transicion_estado_pedido (estado_origen_id, estado_destino_id, rol_id, activa)
SELECT origen.estado_id, destino.estado_id, rol.rol_id, TRUE
FROM estados_pedido origen
	JOIN estados_pedido destino
	JOIN roles rol
WHERE origen.codigo_estado = 'EN_PREPARACION'
	AND destino.codigo_estado = 'LISTO'
	AND rol.codigo_rol = 'COCINERO';

INSERT INTO reglas_transicion_estado_pedido (estado_origen_id, estado_destino_id, rol_id, activa)
SELECT origen.estado_id, destino.estado_id, rol.rol_id, TRUE
FROM estados_pedido origen
	JOIN estados_pedido destino
	JOIN roles rol
WHERE origen.codigo_estado = 'LISTO'
	AND destino.codigo_estado = 'EN_CAMINO'
	AND rol.codigo_rol = 'REPARTIDOR';

INSERT INTO reglas_transicion_estado_pedido (estado_origen_id, estado_destino_id, rol_id, activa)
SELECT origen.estado_id, destino.estado_id, rol.rol_id, TRUE
FROM estados_pedido origen
	JOIN estados_pedido destino
	JOIN roles rol
WHERE origen.codigo_estado = 'EN_CAMINO'
	AND destino.codigo_estado = 'ENTREGADO'
	AND rol.codigo_rol = 'REPARTIDOR';

-- =========================================================
-- 13. Personal demo opcional
-- =========================================================

INSERT INTO personal (rol_id, nombre_completo, telefono, correo_electronico, nombre_usuario, activo)
SELECT rol_id, 'Administrador del sistema', '0000000000', 'admin@restaurant.local', 'admin', TRUE
FROM roles
WHERE codigo_rol = 'ADMINISTRADOR';

INSERT INTO personal (rol_id, nombre_completo, telefono, correo_electronico, nombre_usuario, activo)
SELECT rol_id, 'Cocinero principal', '0000000001', 'cook@restaurant.local', 'cook', TRUE
FROM roles
WHERE codigo_rol = 'COCINERO';

INSERT INTO personal (rol_id, nombre_completo, telefono, correo_electronico, nombre_usuario, activo)
SELECT rol_id, 'Repartidor principal', '0000000002', 'courier@restaurant.local', 'courier', TRUE
FROM roles
WHERE codigo_rol = 'REPARTIDOR';

-- =========================================================
-- 14. Productos demo opcionales
-- =========================================================

INSERT INTO productos (
	codigo_producto,
	nombre_producto,
	descripcion,
	precio_unitario,
	costo_produccion,
	categoria,
	tiempo_preparacion_minutos,
	disponible
) VALUES
	('PLATO-001', 'Hamburguesa clasica', 'Hamburguesa con carne, queso y vegetales', 5.50, 2.25, 'PLATO_FUERTE', 18, TRUE),
	('PLATO-002', 'Pizza personal', 'Pizza individual de queso y pepperoni', 6.75, 2.80, 'PLATO_FUERTE', 20, TRUE),
	('ENTRADA-001', 'Papas fritas', 'Porcion personal de papas fritas', 2.25, 0.80, 'ENTRADA', 10, TRUE),
	('BEBIDA-001', 'Gaseosa', 'Bebida gaseosa personal', 1.50, 0.45, 'BEBIDA', 2, TRUE);
