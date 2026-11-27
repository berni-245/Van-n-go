-- If you are seeing this after 8 of february, then you NEED to run this

ALTER TABLE vehicle
    ALTER COLUMN plate_number DROP NOT NULL;

ALTER TABLE vehicle
    ADD COLUMN old_plate_number TEXT;

ALTER TABLE vehicle
    ADD CONSTRAINT vehicle_plate_present
        CHECK (
            plate_number IS NOT NULL OR old_plate_number IS NOT NULL
            );

-- after 10 of february

UPDATE client
SET zone_id = 1
WHERE zone_id IS NULL;

ALTER TABLE client
    ALTER COLUMN zone_id SET NOT NULL;

--After 14 of february
UPDATE image
SET type = CASE
    -- Si los primeros 2 bytes son 'FF D8' (en hex), es un JPEG
               WHEN encode(substring(bin from 1 for 2), 'hex') = 'ffd8' THEN 'image/jpeg'

    -- Si los primeros 4 bytes son '89 50 4E 47', es un PNG
               WHEN encode(substring(bin from 1 for 4), 'hex') = '89504e47' THEN 'image/png'

    -- Si los primeros 4 bytes son '%PDF' (en hex: '25 50 44 46'), es un PDF
               WHEN encode(substring(bin from 1 for 4), 'hex') = '25504446' THEN 'application/pdf'

    -- Fallback genérico por si hay algún formato desconocido
               ELSE 'application/octet-stream'
    END
WHERE type IS NULL;

-- After 21 of february
UPDATE booking
set destination_zone_id = 1
where destination_zone_id is null;

alter table booking
    alter column destination_zone_id set not null;

UPDATE booking
set job_description = 'Lo organizamos por chat'
where job_description is null;

alter table booking
    alter column job_description set not null;