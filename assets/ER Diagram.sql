-- Restaurant Order Management - MySQL schema
-- Based on assets/ER Diagram.puml
-- Target: MySQL 8.0+ / InnoDB / utf8mb4

CREATE DATABASE IF NOT EXISTS `restaurant_order_management`
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `restaurant_order_management`;

CREATE TABLE IF NOT EXISTS `Role` (
  `role_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `role_name` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`role_id`),
  CONSTRAINT `uk_role_role_name` UNIQUE (`role_name`)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `Staff` (
  `staff_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `role_id` BIGINT UNSIGNED NOT NULL,
  `full_name` VARCHAR(120) NOT NULL,
  `phone` VARCHAR(20) NULL,
  `email` VARCHAR(120) NOT NULL,
  `username` VARCHAR(50) NOT NULL,
  `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`staff_id`),
  CONSTRAINT `uk_staff_email` UNIQUE (`email`),
  CONSTRAINT `uk_staff_username` UNIQUE (`username`),
  INDEX `idx_staff_role_id` (`role_id`),
  CONSTRAINT `fk_staff_role`
    FOREIGN KEY (`role_id`) REFERENCES `Role` (`role_id`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `Customer` (
  `customer_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `full_name` VARCHAR(120) NOT NULL,
  `phone` VARCHAR(20) NULL,
  `email` VARCHAR(120) NOT NULL,
  `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`customer_id`),
  CONSTRAINT `uk_customer_email` UNIQUE (`email`)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `Product` (
  `product_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `product_name` VARCHAR(120) NOT NULL,
  `description` VARCHAR(255) NULL,
  `unit_price` DECIMAL(10,2) NOT NULL,
  `is_available` BOOLEAN NOT NULL DEFAULT TRUE,
  PRIMARY KEY (`product_id`),
  CONSTRAINT `chk_product_unit_price_non_negative` CHECK (`unit_price` >= 0)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `OrderStatus` (
  `status_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `status_code` VARCHAR(30) NOT NULL,
  `status_name` VARCHAR(50) NOT NULL,
  `status_order` INT NOT NULL,
  `is_final` BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (`status_id`),
  CONSTRAINT `uk_order_status_code` UNIQUE (`status_code`),
  CONSTRAINT `uk_order_status_name` UNIQUE (`status_name`),
  CONSTRAINT `chk_order_status_order_positive` CHECK (`status_order` > 0)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `OrderStatusTransitionRule` (
  `transition_rule_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `from_status_id` BIGINT UNSIGNED NOT NULL,
  `to_status_id` BIGINT UNSIGNED NOT NULL,
  `role_id` BIGINT UNSIGNED NOT NULL,
  `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
  PRIMARY KEY (`transition_rule_id`),
  CONSTRAINT `uk_transition_rule_from_to_role` UNIQUE (`from_status_id`, `to_status_id`, `role_id`),
  INDEX `idx_transition_rule_from_status_id` (`from_status_id`),
  INDEX `idx_transition_rule_to_status_id` (`to_status_id`),
  INDEX `idx_transition_rule_role_id` (`role_id`),
  CONSTRAINT `chk_transition_rule_different_status` CHECK (`from_status_id` <> `to_status_id`),
  CONSTRAINT `fk_transition_rule_from_status`
    FOREIGN KEY (`from_status_id`) REFERENCES `OrderStatus` (`status_id`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT `fk_transition_rule_to_status`
    FOREIGN KEY (`to_status_id`) REFERENCES `OrderStatus` (`status_id`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT `fk_transition_rule_role`
    FOREIGN KEY (`role_id`) REFERENCES `Role` (`role_id`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `CustomerOrder` (
  `order_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `order_code` VARCHAR(30) NOT NULL,
  `customer_id` BIGINT UNSIGNED NOT NULL,
  `registered_by_staff_id` BIGINT UNSIGNED NOT NULL,
  `current_status_id` BIGINT UNSIGNED NOT NULL,
  `delivery_address` VARCHAR(255) NOT NULL,
  `total_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `current_status_changed_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`order_id`),
  CONSTRAINT `uk_customer_order_order_code` UNIQUE (`order_code`),
  INDEX `idx_customer_order_customer_id` (`customer_id`),
  INDEX `idx_customer_order_registered_by_staff_id` (`registered_by_staff_id`),
  INDEX `idx_customer_order_current_status_id` (`current_status_id`),
  CONSTRAINT `chk_customer_order_total_amount_non_negative` CHECK (`total_amount` >= 0),
  CONSTRAINT `fk_customer_order_customer`
    FOREIGN KEY (`customer_id`) REFERENCES `Customer` (`customer_id`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT `fk_customer_order_registered_by_staff`
    FOREIGN KEY (`registered_by_staff_id`) REFERENCES `Staff` (`staff_id`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT `fk_customer_order_current_status`
    FOREIGN KEY (`current_status_id`) REFERENCES `OrderStatus` (`status_id`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `OrderItem` (
  `order_item_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT UNSIGNED NOT NULL,
  `product_id` BIGINT UNSIGNED NOT NULL,
  `quantity` INT NOT NULL,
  `product_name_snapshot` VARCHAR(120) NOT NULL,
  `unit_price` DECIMAL(10,2) NOT NULL,
  `line_total` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`order_item_id`),
  INDEX `idx_order_item_order_id` (`order_id`),
  INDEX `idx_order_item_product_id` (`product_id`),
  CONSTRAINT `chk_order_item_quantity_positive` CHECK (`quantity` > 0),
  CONSTRAINT `chk_order_item_unit_price_non_negative` CHECK (`unit_price` >= 0),
  CONSTRAINT `chk_order_item_line_total_non_negative` CHECK (`line_total` >= 0),
  CONSTRAINT `fk_order_item_order`
    FOREIGN KEY (`order_id`) REFERENCES `CustomerOrder` (`order_id`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT `fk_order_item_product`
    FOREIGN KEY (`product_id`) REFERENCES `Product` (`product_id`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `OrderStatusHistory` (
  `history_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT UNSIGNED NOT NULL,
  `from_status_id` BIGINT UNSIGNED NULL,
  `to_status_id` BIGINT UNSIGNED NOT NULL,
  `changed_by_staff_id` BIGINT UNSIGNED NOT NULL,
  `changed_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `notes` VARCHAR(255) NULL,
  PRIMARY KEY (`history_id`),
  INDEX `idx_order_status_history_order_id` (`order_id`),
  INDEX `idx_order_status_history_from_status_id` (`from_status_id`),
  INDEX `idx_order_status_history_to_status_id` (`to_status_id`),
  INDEX `idx_order_status_history_changed_by_staff_id` (`changed_by_staff_id`),
  CONSTRAINT `fk_order_status_history_order`
    FOREIGN KEY (`order_id`) REFERENCES `CustomerOrder` (`order_id`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT `fk_order_status_history_from_status`
    FOREIGN KEY (`from_status_id`) REFERENCES `OrderStatus` (`status_id`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT `fk_order_status_history_to_status`
    FOREIGN KEY (`to_status_id`) REFERENCES `OrderStatus` (`status_id`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT `fk_order_status_history_changed_by_staff`
    FOREIGN KEY (`changed_by_staff_id`) REFERENCES `Staff` (`staff_id`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `Delivery` (
  `delivery_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT UNSIGNED NOT NULL,
  `courier_staff_id` BIGINT UNSIGNED NOT NULL,
  `dispatched_at` DATETIME NULL,
  `delivered_at` DATETIME NULL,
  `receiver_name` VARCHAR(120) NULL,
  `customer_confirmed_at` DATETIME NULL,
  `customer_confirmation_notes` VARCHAR(255) NULL,
  PRIMARY KEY (`delivery_id`),
  CONSTRAINT `uk_delivery_order_id` UNIQUE (`order_id`),
  INDEX `idx_delivery_courier_staff_id` (`courier_staff_id`),
  CONSTRAINT `chk_delivery_delivered_after_dispatched`
    CHECK (`delivered_at` IS NULL OR `dispatched_at` IS NULL OR `delivered_at` >= `dispatched_at`),
  CONSTRAINT `chk_delivery_customer_confirmation_after_delivery`
    CHECK (`customer_confirmed_at` IS NULL OR `delivered_at` IS NOT NULL),
  CONSTRAINT `fk_delivery_order`
    FOREIGN KEY (`order_id`) REFERENCES `CustomerOrder` (`order_id`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT `fk_delivery_courier_staff`
    FOREIGN KEY (`courier_staff_id`) REFERENCES `Staff` (`staff_id`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE = InnoDB;

-- Suggested seed data from the ER diagram notes.
INSERT INTO `Role` (`role_name`)
VALUES ('COOK'), ('COURIER')
ON DUPLICATE KEY UPDATE `role_name` = VALUES(`role_name`);

INSERT INTO `OrderStatus` (`status_code`, `status_name`, `status_order`, `is_final`)
VALUES
  ('PENDING', 'Pendiente', 1, FALSE),
  ('IN_PREPARATION', 'En preparación', 2, FALSE),
  ('READY', 'Listo', 3, FALSE),
  ('ON_THE_WAY', 'En camino', 4, FALSE),
  ('DELIVERED', 'Entregado', 5, TRUE)
ON DUPLICATE KEY UPDATE
  `status_name` = VALUES(`status_name`),
  `status_order` = VALUES(`status_order`),
  `is_final` = VALUES(`is_final`);

INSERT INTO `OrderStatusTransitionRule` (`from_status_id`, `to_status_id`, `role_id`, `is_active`)
SELECT `from_status`.`status_id`, `to_status`.`status_id`, `role`.`role_id`, TRUE
FROM (
  SELECT 'PENDING' AS `from_code`, 'IN_PREPARATION' AS `to_code`, 'COOK' AS `role_name`
  UNION ALL SELECT 'IN_PREPARATION', 'READY', 'COOK'
  UNION ALL SELECT 'READY', 'ON_THE_WAY', 'COURIER'
  UNION ALL SELECT 'ON_THE_WAY', 'DELIVERED', 'COURIER'
) AS `rule_data`
JOIN `OrderStatus` AS `from_status`
  ON `from_status`.`status_code` = `rule_data`.`from_code`
JOIN `OrderStatus` AS `to_status`
  ON `to_status`.`status_code` = `rule_data`.`to_code`
JOIN `Role` AS `role`
  ON `role`.`role_name` = `rule_data`.`role_name`
ON DUPLICATE KEY UPDATE `is_active` = VALUES(`is_active`);

-- Business rules that are documented in the ER diagram but should be enforced
-- by application logic, transactions, or triggers depending on the final design:
-- 1. A CustomerOrder must contain at least one OrderItem.
-- 2. Creating a CustomerOrder must also create the first OrderStatusHistory row.
-- 3. Operational status changes must follow OrderStatusTransitionRule.
-- 4. Customer confirmation requires DELIVERED status, an existing Delivery,
--    delivered_at not null, matching order ownership, and no previous confirmation.
