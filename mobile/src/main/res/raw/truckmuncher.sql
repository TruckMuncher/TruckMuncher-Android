/*
 * This script represents the initial state of the database
 */

CREATE TABLE `truck_properties` (
    `_id`	                    INTEGER PRIMARY KEY AUTOINCREMENT,
	`id`	                    TEXT UNIQUE,
	`name`	                    TEXT,
	`image_url`	                TEXT,
	`keywords`	                TEXT,
	`owned_by_current_user`	    INTEGER,
	`color_primary`	            TEXT,
	`color_secondary`	        TEXT
);

CREATE INDEX `idx_truck_properties_id` ON `truck_properties` (
    `id`
);