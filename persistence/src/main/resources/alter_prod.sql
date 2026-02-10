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