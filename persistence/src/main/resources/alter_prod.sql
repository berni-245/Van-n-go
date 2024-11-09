create view minimal_price as SELECT driver_id, MIN(hourly_rate) from vehicle group by driver_id;

update app_user
set language = 'SPANISH'
where language is null;

-- TODO add constraints to vehicle to make not null plate_number, volume_m3, description

/*

 SELECT distinct
    c.relname AS table_name,
    a.attname AS column_name
FROM
    pg_class c
JOIN
    pg_attribute a ON c.oid = a.attrelid
JOIN
    pg_type t ON a.atttypid = t.oid
JOIN
    pg_constraint con ON con.conrelid = c.oid
JOIN
    unnest(con.conkey) AS conkey_pos ON conkey_pos = a.attnum
WHERE
    t.typname = 'int4'  -- serial es un alias de int4
    AND c.relkind = 'r'  -- solo tablas regulares
    AND c.relname NOT like 'pg_%' ;

 */