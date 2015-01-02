/*
 * This script represents the initial state of the database
 */

CREATE TABLE `truck_properties` (
    `_id`	                    INTEGER PRIMARY KEY AUTOINCREMENT,
	`id`	                    TEXT UNIQUE,
	`name`	                    TEXT,
	`image_url`	                TEXT,
	`keywords`	                TEXT,
	`owned_by_current_user`	    INTEGER DEFAULT 0,
	`color_primary`	            TEXT,
	`color_secondary`	        TEXT
);

CREATE INDEX `idx_truck_properties_id` ON `truck_properties` (
    `id`
);

CREATE TABLE `truck_state` (
    `_id`	        INTEGER PRIMARY KEY AUTOINCREMENT,
    `id`	        TEXT UNIQUE,
    `is_selected`   INTEGER DEFAULT 0,
    `is_serving`    INTEGER DEFAULT 0,
    `latitude`      REAL,
    `longitude`     REAL,
    `is_dirty`      INTEGER DEFAULT 0
);

CREATE INDEX `idx_truck_state_id` ON `truck_state` (
    `id`
);
