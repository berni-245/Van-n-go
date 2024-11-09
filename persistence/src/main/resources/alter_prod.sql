update app_user
set language = 'SPANISH'
where language is null;

ALTER TABLE vehicle
    ALTER COLUMN plate_number SET NOT NULL,
    ALTER COLUMN volume_m3 SET NOT NULL,
    ALTER COLUMN description SET NOT NULL;


ALTER TABLE message ALTER COLUMN id SET DATA TYPE int;
ALTER SEQUENCE message_id_seq OWNED BY message.id;
ALTER SEQUENCE message_id_seq AS int;

ALTER TABLE message ALTER COLUMN client_id SET DATA TYPE int;
ALTER TABLE message ALTER COLUMN driver_id SET DATA TYPE int;


create view minimal_price as SELECT driver_id, MIN(hourly_rate) from vehicle group by driver_id;
