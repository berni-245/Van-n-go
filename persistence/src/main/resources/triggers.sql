-- Doesn't work. See https://github.com/spring-projects/spring-framework/issues/19999

CREATE OR REPLACE FUNCTION check_time_range()
    RETURNS TRIGGER AS
$$
BEGIN
    IF NEW.t_start >= NEW.t_end THEN
        RAISE EXCEPTION 't_start must be before t_end. Provided: t_start = %, t_end = %', NEW.t_start, NEW.t_end;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER check_time_before_insert_update
    BEFORE INSERT OR UPDATE
    ON weekly_availability
    FOR EACH ROW
EXECUTE FUNCTION check_time_range();