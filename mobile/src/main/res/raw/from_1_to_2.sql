/*
 * Introduced for version 1.1.0
 */
ALTER TABLE `truck_properties` ADD COLUMN `website` TEXT;
ALTER TABLE `truck_properties` ADD COLUMN `is_dirty` INTEGER;

DROP VIEW `truck`;

CREATE VIEW `truck` AS SELECT
    `truck_properties`.`id` AS `id`,
    `name`,
    `image_url`,
    `keywords`,
    `color_primary`,
    `color_secondary`,
    `description`,
    `phone_number`,
    `website`,

    `truck_state`.`_id` AS `_id`,
    `is_serving`,
    `matched_search`,
    `latitude`,
    `longitude`,
    `truck_state`.`is_dirty` AS `is_dirty`,
    `owned_by_current_user`

    FROM
    `truck_properties`INNER JOIN `truck_state`
    ON `truck_properties`.`id` = `truck_state`.`id`
;