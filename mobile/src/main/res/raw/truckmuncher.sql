/*
 * This script represents the initial state of the database
 */

CREATE TABLE `truck_properties` (
    `_id`	                    INTEGER PRIMARY KEY AUTOINCREMENT,
	`id`	                    TEXT UNIQUE,
	`name`	                    TEXT,
	`image_url`	                TEXT,
	`keywords`	                TEXT,
	`color_primary`	            TEXT,
	`color_secondary`	        TEXT,
	`owned_by_current_user`     INTEGER DEFAULT 0
);

CREATE INDEX `idx_truck_properties_id` ON `truck_properties` (
    `id`
);

CREATE TABLE `truck_state` (
    `_id`	            INTEGER PRIMARY KEY AUTOINCREMENT,
    `id`    	        TEXT UNIQUE,
    `is_serving`        INTEGER DEFAULT 0,
    `matched_search`    INTEGER DEFAULT 1,
    `latitude`          REAL,
    `longitude`         REAL,
    `is_dirty`          INTEGER DEFAULT 0
);

CREATE INDEX `idx_truck_state_id` ON `truck_state` (
    `id`
);

CREATE VIEW `truck` AS SELECT
    `truck_properties`.`id` AS `id`,
    `name`,
    `image_url`,
    `keywords`,
    `color_primary`,
    `color_secondary`,
    `owned_by_current_user`,

    `truck_state`.`_id` AS `_id`,
    `is_serving`,
    `matched_search`,
    `latitude`,
    `longitude`,
    `is_dirty`

    FROM
    `truck_properties`INNER JOIN `truck_state`
    ON `truck_properties`.`id` = `truck_state`.`id`
;

CREATE TABLE `category` (
    `_id`	            INTEGER PRIMARY KEY AUTOINCREMENT,
    `id`	            TEXT UNIQUE,
    `name`              TEXT,
    `notes`             TEXT,
    `order_in_menu`     INTEGER,
    `truck_id`          TEXT,

    FOREIGN KEY(`truck_id`) REFERENCES `truck_properties`(`id`)
);

CREATE TABLE `menu_item` (
    `_id`	                INTEGER PRIMARY KEY AUTOINCREMENT,
    `id`	                TEXT UNIQUE,
    `name`                  TEXT,
    `price`                 REAL,
    `is_available`          INTEGER,
    `notes`                 TEXT,
    `tags`                  TEXT,
    `order_in_category`     INTEGER,
    `category_id`           TEXT,
    `is_dirty`              INTEGER DEFAULT 0,

    FOREIGN KEY(`category_id`) REFERENCES `category`(`id`)
);

CREATE VIEW `menu` AS SELECT
    `menu_item`.`_id` AS `_id`,
    `menu_item`.`id` AS `menu_item_id`,
    `menu_item`.`name` AS `menu_item_name`,
    `price`,
    `menu_item`.`notes` AS `menu_item_notes`,
    `order_in_category`,
    `is_available`,

    `category`.`name` AS `category_name`,
    `category`.`id` AS `category_id`,
    `category`.`notes` AS `category_notes`,
    `order_in_menu`,

    `truck_properties`.`id` AS `truck_id`

    FROM
    `menu_item` INNER JOIN `category`
    ON `menu_item`.`category_id` = `category`.`id`

    INNER JOIN `truck_properties`
    ON `category`.`truck_id` = `truck_properties`.`id`

    ORDER BY `order_in_menu`, `order_in_category`
;
