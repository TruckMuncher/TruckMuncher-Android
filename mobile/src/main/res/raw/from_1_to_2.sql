ALTER TABLE `truck_properties` ADD COLUMN `phone_number`    TEXT;
ALTER TABLE `truck_properties` ADD COLUMN `description`     TEXT;

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

    `truck_state`.`_id` AS `_id`,
    `is_serving`,
    `matched_search`,
    `latitude`,
    `longitude`,
    `is_dirty`,
    `owned_by_current_user`

    FROM
    `truck_properties`INNER JOIN `truck_state`
    ON `truck_properties`.`id` = `truck_state`.`id`
;