update app_user
set language = 'SPANISH'
where language is null;

-- TODO add constraints to vehicle to make not null plate_number, volume_m3, description

ALTER TABLE vehicle
    ALTER COLUMN plate_number SET NOT NULL,
    ALTER COLUMN volume_m3 SET NOT NULL,
    ALTER COLUMN description SET NOT NULL;

create view minimal_price as SELECT driver_id, MIN(hourly_rate) from vehicle group by driver_id;
